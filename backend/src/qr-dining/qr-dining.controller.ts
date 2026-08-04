import { Body, Controller, Get, Param, Post, UseGuards } from '@nestjs/common';
import { ApiTags, ApiOperation, ApiBearerAuth } from '@nestjs/swagger';
import { QrDiningService } from './qr-dining.service';
import { QrGeneratorService } from './qr-generator.service';
import { AddOrderItemDto, CalculateSplitDto, ServiceRequestDto, StartSessionDto } from './dto/qr-dining.dto';
import { CheckoutQrDto, GenerateQrDto, ScanAndValidateQrDto } from './dto/qr-generator.dto';
import { JwtAuthGuard } from '../common/guards/jwt-auth.guard';
import { CurrentUser } from '../common/decorators/current-user.decorator';
import { User } from '@prisma/client';

@ApiTags('QR In-Dining')
@Controller('api/v1/qr-dining')
@UseGuards(JwtAuthGuard)
@ApiBearerAuth()
export class QrDiningController {
  constructor(
    private readonly qrDiningService: QrDiningService,
    private readonly qrGeneratorService: QrGeneratorService,
  ) {}

  @Post('generate-qr')
  @ApiOperation({ summary: 'Generate secure, time-sensitive encrypted QR code for booking session' })
  async generateEncryptedQr(@Body() dto: GenerateQrDto) {
    return this.qrGeneratorService.generateEncryptedQr(dto);
  }

  @Post('scan-qr')
  @ApiOperation({ summary: 'Scan, decrypt, and validate encrypted QR code string' })
  async scanAndValidateQr(@Body() dto: ScanAndValidateQrDto) {
    return this.qrGeneratorService.scanAndValidateQr(dto);
  }

  @Post('checkout-qr')
  @ApiOperation({ summary: 'Finalize checkout and permanently invalidate QR code token' })
  async checkoutAndInvalidateQr(@Body() dto: CheckoutQrDto) {
    return this.qrGeneratorService.checkoutAndInvalidateQr(dto);
  }

  @Post('session')
  @ApiOperation({ summary: 'Scan QR & Start Active Dining Session' })
  async startSession(@CurrentUser() user: User, @Body() dto: StartSessionDto) {
    return this.qrDiningService.startSession(user.id, dto);
  }

  @Get('active-session')
  @ApiOperation({ summary: 'Get current table session' })
  async getActiveSession(@CurrentUser() user: User) {
    return this.qrDiningService.getActiveSession(user.id);
  }

  @Post('session/:id/order')
  @ApiOperation({ summary: 'Place order item directly to kitchen' })
  async addOrderItem(@Param('id') sessionId: string, @Body() dto: AddOrderItemDto) {
    return this.qrDiningService.addOrderItem(sessionId, dto);
  }

  @Post('session/:id/service-request')
  @ApiOperation({ summary: 'Call Waiter / Request Water or Bill' })
  async requestService(@Param('id') sessionId: string, @Body() dto: ServiceRequestDto) {
    return this.qrDiningService.requestService(sessionId, dto);
  }

  @Post('session/:id/split')
  @ApiOperation({ summary: 'Calculate & Create Split Bill (Equal or Itemized by Guest)' })
  async calculateAndCreateSplit(@Param('id') sessionId: string, @Body() dto: CalculateSplitDto) {
    return this.qrDiningService.calculateAndCreateSplit(sessionId, dto);
  }

  @Get('session/:id/split')
  @ApiOperation({ summary: 'Get latest bill split and individual attendee shares' })
  async getLatestSplit(@Param('id') sessionId: string) {
    return this.qrDiningService.getLatestSplit(sessionId);
  }

  @Post('share/:shareId/pay')
  @ApiOperation({ summary: 'Pay individual attendee share' })
  async payShare(@Param('shareId') shareId: string) {
    return this.qrDiningService.payShare(shareId);
  }
}
