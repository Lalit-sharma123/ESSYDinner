import { Injectable, NotFoundException, BadRequestException, Logger } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { EventsGateway } from '../sockets/events.gateway';
import { CheckoutQrDto, GenerateQrDto, ScanAndValidateQrDto } from './dto/qr-generator.dto';
import * as crypto from 'crypto';

export interface QrPayload {
  bookingId: string;
  restaurantId: string;
  tableNumber: string;
  userId: string;
  nonce: string;
  iat: number;
  exp: number;
}

@Injectable()
export class QrGeneratorService {
  private readonly logger = new Logger(QrGeneratorService.name);
  private readonly algorithm = 'aes-256-gcm';
  private readonly secretKey: Buffer;

  constructor(
    private prisma: PrismaService,
    private eventsGateway: EventsGateway,
  ) {
    // Derive a fixed 32-byte key from JWT_SECRET or fallback secret
    const secret = process.env.JWT_SECRET || 'dinereserve-secure-qr-secret-key-2026';
    this.secretKey = crypto.scryptSync(secret, 'qr_salt_dinereserve', 32);
  }

  /**
   * Encrypts plain payload object using AES-256-GCM
   */
  private encryptPayload(payload: QrPayload): string {
    const iv = crypto.randomBytes(12);
    const cipher = crypto.createCipheriv(this.algorithm, this.secretKey, iv);
    
    const jsonStr = JSON.stringify(payload);
    let encrypted = cipher.update(jsonStr, 'utf8', 'hex');
    encrypted += cipher.final('hex');
    
    const authTag = cipher.getAuthTag().toString('hex');
    // Format: iv:authTag:encryptedCiphertext
    return `${iv.toString('hex')}:${authTag}:${encrypted}`;
  }

  /**
   * Decrypts AES-256-GCM encrypted token string
   */
  private decryptToken(encryptedQr: string): QrPayload {
    try {
      const parts = encryptedQr.split(':');
      if (parts.length !== 3) {
        throw new BadRequestException('Malformed QR token structure');
      }

      const [ivHex, authTagHex, encryptedHex] = parts;
      const iv = Buffer.from(ivHex, 'hex');
      const authTag = Buffer.from(authTagHex, 'hex');

      const decipher = crypto.createDecipheriv(this.algorithm, this.secretKey, iv);
      decipher.setAuthTag(authTag);

      let decrypted = decipher.update(encryptedHex, 'hex', 'utf8');
      decrypted += decipher.final('utf8');

      return JSON.parse(decrypted) as QrPayload;
    } catch (err: any) {
      this.logger.error(`Failed to decrypt QR token: ${err.message}`);
      throw new BadRequestException('Invalid or tampered QR code token');
    }
  }

  /**
   * Generate secure, time-sensitive encrypted QR code for a booking
   */
  async generateEncryptedQr(dto: GenerateQrDto) {
    const booking = await this.prisma.booking.findUnique({
      where: { id: dto.bookingId },
      include: { restaurant: true },
    });

    if (!booking) {
      throw new NotFoundException(`Booking with ID ${dto.bookingId} not found`);
    }

    // Check if QR session record already exists
    const existingRecord = await this.prisma.qrCodeSessionRecord.findUnique({
      where: { bookingId: dto.bookingId },
    });

    if (existingRecord?.isCheckedOut) {
      throw new BadRequestException('This reservation has already been checked out. Cannot regenerate or reuse QR code.');
    }

    const ttlMinutes = dto.ttlMinutes || 240; // Default 4 hours expiration window
    const nowMs = Date.now();
    const expMs = nowMs + ttlMinutes * 60 * 1000;
    const nonce = crypto.randomUUID();

    const payload: QrPayload = {
      bookingId: booking.id,
      restaurantId: booking.restaurantId,
      tableNumber: dto.tableNumber,
      userId: booking.userId,
      nonce,
      iat: nowMs,
      exp: expMs,
    };

    const encryptedQr = this.encryptPayload(payload);
    const expiresAt = new Date(expMs);

    // Persist or update QR code session record in DB
    const qrRecord = await this.prisma.qrCodeSessionRecord.upsert({
      where: { bookingId: dto.bookingId },
      create: {
        bookingId: dto.bookingId,
        restaurantId: booking.restaurantId,
        tableNumber: dto.tableNumber,
        encryptedQr,
        secretNonce: nonce,
        issuedAt: new Date(nowMs),
        expiresAt,
        isUsed: false,
        isCheckedOut: false,
      },
      update: {
        tableNumber: dto.tableNumber,
        encryptedQr,
        secretNonce: nonce,
        issuedAt: new Date(nowMs),
        expiresAt,
        isUsed: false,
        isCheckedOut: false,
      },
    });

    // Update booking record with encrypted QR payload
    await this.prisma.booking.update({
      where: { id: dto.bookingId },
      data: { qrCodeData: encryptedQr },
    });

    return {
      success: true,
      bookingId: booking.id,
      restaurantName: booking.restaurant.name,
      tableNumber: dto.tableNumber,
      encryptedQr,
      expiresAt,
      qrDataUrl: `dinereserve://dining?qr=${encodeURIComponent(encryptedQr)}`,
    };
  }

  /**
   * Decrypt, validate, and activate QR code for in-dining session
   */
  async scanAndValidateQr(dto: ScanAndValidateQrDto) {
    // 1. Decrypt AES token payload
    const payload = this.decryptToken(dto.encryptedQr);

    // 2. Validate Time-sensitive expiration
    if (Date.now() > payload.exp) {
      throw new BadRequestException('QR code token has expired. Please request a new code at the host stand.');
    }

    // 3. Look up DB record to check checkout status
    const record = await this.prisma.qrCodeSessionRecord.findUnique({
      where: { encryptedQr: dto.encryptedQr },
      include: { booking: { include: { user: true, restaurant: true } } },
    });

    if (!record) {
      throw new NotFoundException('QR session record not found or token unregistered.');
    }

    // 4. Verify that QR cannot be reused after checkout!
    if (record.isCheckedOut) {
      throw new BadRequestException('This QR code session has already been checked out and finalized. It cannot be reused.');
    }

    // Mark as scanned / used
    await this.prisma.qrCodeSessionRecord.update({
      where: { id: record.id },
      data: {
        isUsed: true,
        usedAt: new Date(),
      },
    });

    // Ensure active DiningSession exists or create one
    let activeSession = await this.prisma.diningSession.findFirst({
      where: {
        restaurantId: payload.restaurantId,
        tableNumber: payload.tableNumber,
        userId: payload.userId,
        status: 'ACTIVE',
      },
      include: { orders: true, serviceRequests: true },
    });

    if (!activeSession) {
      activeSession = await this.prisma.diningSession.create({
        data: {
          userId: payload.userId,
          restaurantId: payload.restaurantId,
          tableNumber: payload.tableNumber,
          status: 'ACTIVE',
        },
        include: { orders: true, serviceRequests: true },
      });
    }

    return {
      valid: true,
      message: 'QR code verified successfully. Dining session active.',
      payload,
      session: activeSession,
      booking: record.booking,
    };
  }

  /**
   * Finalize dining session, checkout, and permanently invalidate QR code from reuse
   */
  async checkoutAndInvalidateQr(dto: CheckoutQrDto) {
    const record = await this.prisma.qrCodeSessionRecord.findUnique({
      where: { bookingId: dto.bookingId },
    });

    if (!record) {
      throw new NotFoundException(`No QR code session record found for booking ID ${dto.bookingId}`);
    }

    if (record.isCheckedOut) {
      return {
        alreadyCheckedOut: true,
        message: 'QR session was already checked out and invalidated.',
      };
    }

    // Update DB record as permanently checked out
    const updatedRecord = await this.prisma.qrCodeSessionRecord.update({
      where: { bookingId: dto.bookingId },
      data: {
        isCheckedOut: true,
        checkedOutAt: new Date(),
      },
    });

    // Update any active dining session status to COMPLETED
    const activeSession = await this.prisma.diningSession.findFirst({
      where: {
        restaurantId: record.restaurantId,
        tableNumber: record.tableNumber,
        status: { in: ['ACTIVE', 'BILL_REQUESTED'] },
      },
    });

    if (activeSession) {
      await this.prisma.diningSession.update({
        where: { id: activeSession.id },
        data: { status: 'COMPLETED' },
      });

      this.eventsGateway.notifyTableOrderUpdate(activeSession.id, {
        action: 'SESSION_COMPLETED',
        bookingId: dto.bookingId,
      });
    }

    return {
      success: true,
      message: 'Dining session checked out successfully. QR code has been permanently invalidated.',
      bookingId: dto.bookingId,
      checkedOutAt: updatedRecord.checkedOutAt,
    };
  }
}
