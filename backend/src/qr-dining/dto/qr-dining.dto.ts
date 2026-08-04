import { ApiProperty } from '@nestjs/swagger';
import { IsArray, IsInt, IsNotEmpty, IsNumber, IsOptional, IsString, Min, ValidateNested } from 'class-validator';
import { Type } from 'class-transformer';

export class StartSessionDto {
  @ApiProperty({ example: 'rest_1' })
  @IsString()
  @IsNotEmpty()
  restaurantId: string;

  @ApiProperty({ example: 'Table 14' })
  @IsString()
  @IsNotEmpty()
  tableNumber: string;
}

export class AddOrderItemDto {
  @ApiProperty({ example: 'mi_1' })
  @IsString()
  @IsNotEmpty()
  menuItemId: string;

  @ApiProperty({ example: 'Truffle Tagliatelle' })
  @IsString()
  itemName: string;

  @ApiProperty({ example: 34.0 })
  unitPrice: number;

  @ApiProperty({ example: 2 })
  @IsInt()
  @Min(1)
  quantity: number;

  @ApiProperty({ example: 'Extra parmesan' })
  @IsString()
  notes: string;
}

export class ServiceRequestDto {
  @ApiProperty({ example: 'WATER' })
  @IsString()
  requestType: string;

  @ApiProperty({ example: 'Chilled sparkling water' })
  @IsString()
  note: string;
}

export class ItemAssignmentDto {
  @ApiProperty({ example: 'Alex' })
  @IsString()
  @IsNotEmpty()
  attendeeName: string;

  @ApiProperty({ example: ['item_1_id', 'item_2_id'] })
  itemIds: string[];
}

export class CalculateSplitDto {
  @ApiProperty({ example: 'ITEMIZED', enum: ['EQUAL', 'ITEMIZED'] })
  @IsString()
  @IsNotEmpty()
  splitType: 'EQUAL' | 'ITEMIZED';

  @ApiProperty({ example: 3, required: false })
  @IsOptional()
  @IsInt()
  @Min(1)
  numAttendees?: number;

  @ApiProperty({ example: 18.0, required: false })
  @IsOptional()
  @IsNumber()
  @Min(0)
  tipPercent?: number;

  @ApiProperty({ type: [ItemAssignmentDto], required: false })
  @IsOptional()
  @IsArray()
  @ValidateNested({ each: true })
  @Type(() => ItemAssignmentDto)
  attendees?: ItemAssignmentDto[];
}

export class PayShareDto {
  @ApiProperty({ example: 'share_uuid_here' })
  @IsString()
  @IsNotEmpty()
  shareId: string;
}
