import { Controller, Get, Param, UseGuards } from '@nestjs/common';
import { ApiTags, ApiOperation, ApiBearerAuth } from '@nestjs/swagger';
import { OperationsService } from './operations.service';
import { JwtAuthGuard } from '../common/guards/jwt-auth.guard';

@ApiTags('Restaurant Operations Dashboard')
@Controller('api/v1/operations')
@UseGuards(JwtAuthGuard)
@ApiBearerAuth()
export class OperationsController {
  constructor(private readonly operationsService: OperationsService) {}

  @Get('dashboard/:restaurantId')
  @ApiOperation({ summary: 'Get Live KPIs and Operational Intelligence metrics' })
  async getLiveOperationsDashboard(@Param('restaurantId') restaurantId: string) {
    return this.operationsService.getLiveOperationsDashboard(restaurantId);
  }
}
