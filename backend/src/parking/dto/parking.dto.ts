import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';
import { IsEnum, IsInt, IsNotEmpty, IsOptional, IsString, Min } from 'class-validator';
import { ParkingSlotState } from '@prisma/client';

export class CreateParkingLotDto {
  @ApiProperty({ example: 'rest_1' })
  @IsString()
  @IsNotEmpty()
  restaurantId: string;

  @ApiProperty({ example: 'Valet & Guest Parking' })
  @IsString()
  @IsNotEmpty()
  lotName: string;

  @ApiPropertyOptional({ example: 25 })
  @IsOptional()
  @IsInt()
  @Min(1)
  totalCapacity?: number;
}

export class CreateParkingSlotDto {
  @ApiProperty({ example: 'lot_uuid' })
  @IsString()
  @IsNotEmpty()
  lotId: string;

  @ApiProperty({ example: 'P-01' })
  @IsString()
  @IsNotEmpty()
  slotNumber: string;

  @ApiPropertyOptional({ example: 'EV_CHARGING', enum: ['STANDARD', 'EV_CHARGING', 'ACCESSIBLE', 'VIP'] })
  @IsOptional()
  @IsString()
  slotType?: string;
}

export class UpdateParkingSlotStateDto {
  @ApiProperty({ enum: ParkingSlotState, example: ParkingSlotState.Occupied })
  @IsEnum(ParkingSlotState)
  state: ParkingSlotState;

  @ApiPropertyOptional({ example: 'John Doe - BMW X5' })
  @IsOptional()
  @IsString()
  occupiedBy?: string;
}
