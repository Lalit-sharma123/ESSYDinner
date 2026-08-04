import { Body, Controller, Get, Param, Patch, Post, UseGuards } from '@nestjs/common';
import { ApiTags, ApiOperation, ApiBearerAuth } from '@nestjs/swagger';
import { ParkingService } from './parking.service';
import { CreateParkingLotDto, CreateParkingSlotDto, UpdateParkingSlotStateDto } from './dto/parking.dto';
import { JwtAuthGuard } from '../common/guards/jwt-auth.guard';

@ApiTags('Live Parking Availability')
@Controller('api/v1/parking')
@UseGuards(JwtAuthGuard)
@ApiBearerAuth()
export class ParkingController {
  constructor(private readonly parkingService: ParkingService) {}

  @Post('lots')
  @ApiOperation({ summary: 'Create a new parking lot for a restaurant' })
  async createParkingLot(@Body() dto: CreateParkingLotDto) {
    return this.parkingService.createParkingLot(dto);
  }

  @Get('restaurant/:restaurantId')
  @ApiOperation({ summary: 'Get live parking lot summary and slot states' })
  async getParkingLots(@Param('restaurantId') restaurantId: string) {
    return this.parkingService.getParkingLots(restaurantId);
  }

  @Post('slots')
  @ApiOperation({ summary: 'Add a new parking slot to a lot' })
  async addParkingSlot(@Body() dto: CreateParkingSlotDto) {
    return this.parkingService.addParkingSlot(dto);
  }

  @Patch('slots/:id/state')
  @ApiOperation({ summary: 'Update state of a parking slot (Available, Occupied, Reserved, EV_Charging, VIP)' })
  async updateSlotState(@Param('id') slotId: string, @Body() dto: UpdateParkingSlotStateDto) {
    return this.parkingService.updateSlotState(slotId, dto);
  }
}
