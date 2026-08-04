import { Injectable, NotFoundException } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { CreateBookingDto } from './dto/booking.dto';
import { QrGeneratorService } from '../qr-dining/qr-generator.service';

@Injectable()
export class BookingsService {
  constructor(
    private prisma: PrismaService,
    private qrGeneratorService: QrGeneratorService,
  ) {}

  async createBooking(userId: string, dto: CreateBookingDto) {
    const restaurant = await this.prisma.restaurant.findUnique({
      where: { id: dto.restaurantId },
    });
    if (!restaurant) {
      throw new NotFoundException('Restaurant not found');
    }

    const booking = await this.prisma.booking.create({
      data: {
        userId,
        restaurantId: dto.restaurantId,
        partySize: dto.partySize,
        bookingDate: dto.bookingDate,
        timeSlot: dto.timeSlot,
        seatingArea: dto.seatingArea || 'Main Dining',
        discountPercent: restaurant.maxDiscountPercent,
        qrCodeData: '',
      },
      include: {
        restaurant: true,
      },
    });

    // Generate secure time-sensitive encrypted QR code for booking
    const qrResult = await this.qrGeneratorService.generateEncryptedQr({
      bookingId: booking.id,
      tableNumber: 'Table 1',
      ttlMinutes: 240,
    });

    return {
      ...booking,
      qrCodeData: qrResult.encryptedQr,
    };
  }

  async getUserBookings(userId: string) {
    return this.prisma.booking.findMany({
      where: { userId },
      include: { restaurant: true },
      orderBy: { createdAt: 'desc' },
    });
  }

  async cancelBooking(bookingId: string, userId: string) {
    const booking = await this.prisma.booking.findFirst({
      where: { id: bookingId, userId },
    });
    if (!booking) {
      throw new NotFoundException('Booking not found');
    }

    return this.prisma.booking.update({
      where: { id: bookingId },
      data: { status: 'CANCELLED' },
    });
  }
}
