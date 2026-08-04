import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';
import { IsEnum, IsInt, IsNotEmpty, IsNumber, IsOptional, IsString, Min } from 'class-validator';
import { TableStatus } from '@prisma/client';

export class CreateFloorDto {
  @ApiProperty({ example: 'rest_1' })
  @IsString()
  @IsNotEmpty()
  restaurantId: string;

  @ApiProperty({ example: 'Main Dining Floor' })
  @IsString()
  @IsNotEmpty()
  floorName: string;

  @ApiPropertyOptional({ example: 1 })
  @IsOptional()
  @IsInt()
  level?: number;
}

export class CreateTableDto {
  @ApiProperty({ example: 'rest_1' })
  @IsString()
  @IsNotEmpty()
  restaurantId: string;

  @ApiProperty({ example: 'floor_uuid' })
  @IsString()
  @IsNotEmpty()
  floorId: string;

  @ApiProperty({ example: 'T-101' })
  @IsString()
  @IsNotEmpty()
  tableNumber: string;

  @ApiPropertyOptional({ example: 4 })
  @IsOptional()
  @IsInt()
  @Min(1)
  capacity?: number;

  @ApiPropertyOptional({ example: 120.5 })
  @IsOptional()
  @IsNumber()
  positionX?: number;

  @ApiPropertyOptional({ example: 250.0 })
  @IsOptional()
  @IsNumber()
  positionY?: number;

  @ApiPropertyOptional({ example: 'RECTANGLE', enum: ['RECTANGLE', 'CIRCLE', 'SQUARE', 'BOOTH'] })
  @IsOptional()
  @IsString()
  shape?: string;
}

export class UpdateTableStatusDto {
  @ApiProperty({ enum: TableStatus, example: TableStatus.DINING })
  @IsEnum(TableStatus)
  status: TableStatus;

  @ApiPropertyOptional({ example: 'John Manager' })
  @IsOptional()
  @IsString()
  changedBy?: string;
}

export class UpdateTablePositionDto {
  @ApiProperty({ example: 150.0 })
  @IsNumber()
  positionX: number;

  @ApiProperty({ example: 300.0 })
  @IsNumber()
  positionY: number;
}

export class MergeTablesDto {
  @ApiProperty({ example: 'table_1_id' })
  @IsString()
  @IsNotEmpty()
  primaryTableId: string;

  @ApiProperty({ example: 'table_2_id' })
  @IsString()
  @IsNotEmpty()
  secondaryTableId: string;
}

export class CompleteCleaningDto {
  @ApiProperty({ example: 'Cleaner Sarah' })
  @IsString()
  @IsNotEmpty()
  cleanedBy: string;
}
