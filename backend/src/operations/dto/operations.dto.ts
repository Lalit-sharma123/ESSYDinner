import { ApiProperty } from '@nestjs/swagger';
import { IsNotEmpty, IsString } from 'class-validator';

export class GetDashboardMetricsDto {
  @ApiProperty({ example: 'rest_1' })
  @IsString()
  @IsNotEmpty()
  restaurantId: string;
}
