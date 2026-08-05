import { ApiProperty } from '@nestjs/swagger';
import { IsString, IsOptional, IsArray, IsNumber, IsEnum } from 'class-validator';

export enum ServiceRequestTypeEnum {
  MENU_ITEM = 'MENU_ITEM',
  WATER = 'WATER',
  SOFT_DRINK = 'SOFT_DRINK',
  EXTRA_PLATE = 'EXTRA_PLATE',
  EXTRA_SPOON = 'EXTRA_SPOON',
  NAPKIN = 'NAPKIN',
  TISSUE = 'TISSUE',
  ICE = 'ICE',
  SAUCE = 'SAUCE',
  CALL_WAITER = 'CALL_WAITER',
  REQUEST_BILL = 'REQUEST_BILL',
  SPECIAL_REQUEST = 'SPECIAL_REQUEST',
}

export enum TaskStatusEnum {
  PENDING = 'PENDING',
  ASSIGNED = 'ASSIGNED',
  ACCEPTED = 'ACCEPTED',
  IN_PROGRESS = 'IN_PROGRESS',
  READY_TO_SERVE = 'READY_TO_SERVE',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED',
}

export class ServiceRequestItemDto {
  @ApiProperty({ required: false })
  @IsString()
  @IsOptional()
  menuItemId?: string;

  @ApiProperty({ example: 'Water' })
  @IsString()
  itemName: string;

  @ApiProperty({ example: 1 })
  @IsNumber()
  quantity: number;

  @ApiProperty({ example: 0.0 })
  @IsNumber()
  @IsOptional()
  price?: number;

  @ApiProperty({ example: 'No ice', required: false })
  @IsString()
  @IsOptional()
  specialInstructions?: string;
}

export class CreateServiceRequestDto {
  @ApiProperty({ example: 'rest_1', required: false })
  @IsString()
  @IsOptional()
  restaurantId?: string;

  @ApiProperty({ example: 'b_101', required: false })
  @IsString()
  @IsOptional()
  bookingId?: string;

  @ApiProperty({ example: 'session_123' })
  @IsString()
  diningSessionId: string;

  @ApiProperty({ example: 'cust_001', required: false })
  @IsString()
  @IsOptional()
  customerId?: string;

  @ApiProperty({ example: 'table_01', required: false })
  @IsString()
  @IsOptional()
  tableId?: string;

  @ApiProperty({ example: 'T-01', required: false })
  @IsString()
  @IsOptional()
  tableNumber?: string;

  @ApiProperty({ enum: ServiceRequestTypeEnum, example: ServiceRequestTypeEnum.WATER })
  @IsEnum(ServiceRequestTypeEnum)
  requestType: ServiceRequestTypeEnum;

  @ApiProperty({ example: 'NORMAL', required: false })
  @IsString()
  @IsOptional()
  priority?: string;

  @ApiProperty({ example: 'Extra glass please', required: false })
  @IsString()
  @IsOptional()
  notes?: string;

  @ApiProperty({ type: [ServiceRequestItemDto], required: false })
  @IsArray()
  @IsOptional()
  items?: ServiceRequestItemDto[];
}

export class AssignStaffTaskDto {
  @ApiProperty({ example: 'staff_101' })
  @IsString()
  assignedStaffId: string;

  @ApiProperty({ example: 'John Waiter', required: false })
  @IsString()
  @IsOptional()
  assignedStaffName?: string;
}

export class UpdateTaskStatusDto {
  @ApiProperty({ enum: TaskStatusEnum, example: TaskStatusEnum.ACCEPTED })
  @IsEnum(TaskStatusEnum)
  status: TaskStatusEnum;
}
