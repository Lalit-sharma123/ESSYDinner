import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';
import { IsInt, IsNotEmpty, IsOptional, IsString, Min } from 'class-validator';

export class GenerateQrDto {
  @ApiProperty({ example: 'booking_uuid_123', description: 'Unique reservation booking ID' })
  @IsString()
  @IsNotEmpty()
  bookingId: string;

  @ApiProperty({ example: 'Table 12', description: 'Assigned dining table number' })
  @IsString()
  @IsNotEmpty()
  tableNumber: string;

  @ApiPropertyOptional({ example: 240, description: 'QR Token Time-To-Live in minutes (default: 240 / 4 hrs)' })
  @IsOptional()
  @IsInt()
  @Min(1)
  ttlMinutes?: number;
}

export class ScanAndValidateQrDto {
  @ApiProperty({ example: 'a1f8e2...encrypted_string', description: 'Secure encrypted QR token payload' })
  @IsString()
  @IsNotEmpty()
  encryptedQr: string;
}

export class CheckoutQrDto {
  @ApiProperty({ example: 'booking_uuid_123', description: 'Booking ID to finalize checkout and invalidate QR token' })
  @IsString()
  @IsNotEmpty()
  bookingId: string;
}
