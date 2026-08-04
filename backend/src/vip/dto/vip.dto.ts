import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';
import { IsNotEmpty, IsNumber, IsOptional, IsString } from 'class-validator';

export class CreateVipProfileDto {
  @ApiProperty({ example: 'user_uuid' })
  @IsString()
  @IsNotEmpty()
  userId: string;

  @ApiPropertyOptional({ example: 'PLATINUM_VIP' })
  @IsOptional()
  @IsString()
  tier?: string;

  @ApiPropertyOptional({ example: 'Truffle Pasta' })
  @IsOptional()
  @IsString()
  favoriteDish?: string;

  @ApiPropertyOptional({ example: 'Booth #4' })
  @IsOptional()
  @IsString()
  favoriteTable?: string;

  @ApiPropertyOptional({ example: 'Peanuts, Shellfish' })
  @IsOptional()
  @IsString()
  allergies?: string;

  @ApiPropertyOptional({ example: 'Prefers sparkling water upon seating' })
  @IsOptional()
  @IsString()
  specialRequests?: string;
}

export class RecordVipCheckInDto {
  @ApiProperty({ example: 'user_uuid' })
  @IsString()
  @IsNotEmpty()
  userId: string;

  @ApiProperty({ example: 'rest_1' })
  @IsString()
  @IsNotEmpty()
  restaurantId: string;

  @ApiPropertyOptional({ example: 'Table 4' })
  @IsOptional()
  @IsString()
  tableNumber?: string;
}

export class AddPreferenceDto {
  @ApiProperty({ example: 'SEATING', enum: ['SEATING', 'DIET', 'DRINK', 'STAFF'] })
  @IsString()
  @IsNotEmpty()
  category: string;

  @ApiProperty({ example: 'preferred_water' })
  @IsString()
  @IsNotEmpty()
  preferenceKey: string;

  @ApiProperty({ example: 'San Pellegrino Sparkling Water' })
  @IsString()
  @IsNotEmpty()
  preferenceValue: string;
}
