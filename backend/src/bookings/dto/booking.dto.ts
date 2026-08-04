import { ApiProperty } from '@nestjs/swagger';
import { IsInt, IsNotEmpty, IsString, Min } from 'class-validator';

export class CreateBookingDto {
  @ApiProperty({ example: 'rest_1' })
  @IsString()
  @IsNotEmpty()
  restaurantId: string;

  @ApiProperty({ example: 4 })
  @IsInt()
  @Min(1)
  partySize: number;

  @ApiProperty({ example: '2026-08-10' })
  @IsString()
  @IsNotEmpty()
  bookingDate: string;

  @ApiProperty({ example: '07:30 PM' })
  @IsString()
  @IsNotEmpty()
  timeSlot: string;

  @ApiProperty({ example: 'Window Booth' })
  @IsString()
  seatingArea: string;
}
