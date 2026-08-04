import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';
import { IsInt, IsNotEmpty, IsNumber, IsOptional, IsString, Max, Min } from 'class-validator';

export class UpdateHourlyTrafficDto {
  @ApiProperty({ example: 'rest_1' })
  @IsString()
  @IsNotEmpty()
  restaurantId: string;

  @ApiProperty({ example: 1, description: 'Day of week (0=Sun, 1=Mon...6=Sat)' })
  @IsInt()
  @Min(0)
  @Max(6)
  dayOfWeek: number;

  @ApiProperty({ example: 19, description: 'Hour of day (0-23)' })
  @IsInt()
  @Min(0)
  @Max(23)
  hourOfDay: number;

  @ApiProperty({ example: 12 })
  @IsInt()
  @Min(0)
  walkInCount: number;

  @ApiProperty({ example: 28 })
  @IsInt()
  @Min(0)
  bookingCount: number;
}
