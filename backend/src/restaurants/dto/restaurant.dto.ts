import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';
import { IsNotEmpty, IsNumber, IsOptional, IsString } from 'class-validator';

export class CreateRestaurantDto {
  @ApiProperty({ example: 'Lumina Grand Dining' })
  @IsString()
  @IsNotEmpty()
  name: string;

  @ApiProperty({ example: 'Modern Italian Fine Dining & Wine Cellar' })
  @IsString()
  tagline: string;

  @ApiProperty({ example: 'Italian • Fine Dining' })
  @IsString()
  cuisineType: string;

  @ApiProperty({ example: '$$$$' })
  @IsString()
  priceRange: string;

  @ApiProperty({ example: '742 Evergreen Terrace, Downtown' })
  @IsString()
  address: string;

  @ApiProperty({ example: 'Metropolis' })
  @IsString()
  city: string;

  @ApiProperty({ example: 'Downtown Financial District' })
  @IsString()
  area: string;

  @ApiProperty({ example: 37.7749 })
  @IsNumber()
  latitude: number;

  @ApiProperty({ example: -122.4194 })
  @IsNumber()
  longitude: number;

  @ApiProperty({ example: 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4' })
  @IsString()
  heroImageUrl: string;
}

export class RestaurantQueryDto {
  @ApiPropertyOptional({ example: 'Italian' })
  @IsOptional()
  @IsString()
  cuisine?: string;

  @ApiPropertyOptional({ example: 'Downtown' })
  @IsOptional()
  @IsString()
  search?: string;
}
