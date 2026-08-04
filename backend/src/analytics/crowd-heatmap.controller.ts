import { Body, Controller, Get, Param, Post, Query, UseGuards } from '@nestjs/common';
import { ApiTags, ApiOperation, ApiBearerAuth, ApiQuery } from '@nestjs/swagger';
import { CrowdHeatmapService } from './crowd-heatmap.service';
import { UpdateHourlyTrafficDto } from './dto/crowd.dto';
import { JwtAuthGuard } from '../common/guards/jwt-auth.guard';

@ApiTags('Crowd Heat Map & Traffic Analytics')
@Controller('api/v1/analytics/crowd')
@UseGuards(JwtAuthGuard)
@ApiBearerAuth()
export class CrowdHeatmapController {
  constructor(private readonly crowdService: CrowdHeatmapService) {}

  @Get('heatmap/:restaurantId')
  @ApiOperation({ summary: 'Get 24-Hour Crowd Heatmap for a specific day' })
  @ApiQuery({ name: 'dayOfWeek', required: false, example: 1, description: '0=Sun, 1=Mon, ..., 6=Sat' })
  async get24HourHeatmap(
    @Param('restaurantId') restaurantId: string,
    @Query('dayOfWeek') dayOfWeek?: string,
  ) {
    const day = dayOfWeek !== undefined ? parseInt(dayOfWeek, 10) : 1;
    return this.crowdService.get24HourHeatmap(restaurantId, day);
  }

  @Post('traffic')
  @ApiOperation({ summary: 'Record or update hourly traffic for analytics prediction engine' })
  async recordHourlyTraffic(@Body() dto: UpdateHourlyTrafficDto) {
    return this.crowdService.recordHourlyTraffic(dto);
  }

  @Get('weekly-predictions/:restaurantId')
  @ApiOperation({ summary: 'Get 7-day crowd traffic predictions and peak hour recommendations' })
  async getWeeklyPredictions(@Param('restaurantId') restaurantId: string) {
    return this.crowdService.getWeeklyPredictions(restaurantId);
  }
}
