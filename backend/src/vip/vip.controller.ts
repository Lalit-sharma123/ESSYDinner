import { Body, Controller, Get, Param, Post, Put, UseGuards } from '@nestjs/common';
import { ApiTags, ApiOperation, ApiBearerAuth } from '@nestjs/swagger';
import { VipService } from './vip.service';
import { AddPreferenceDto, CreateVipProfileDto, RecordVipCheckInDto } from './dto/vip.dto';
import { JwtAuthGuard } from '../common/guards/jwt-auth.guard';
import { CurrentUser } from '../common/decorators/current-user.decorator';
import { User } from '@prisma/client';

@ApiTags('VIP Customer Recognition')
@Controller('api/v1/vip')
@UseGuards(JwtAuthGuard)
@ApiBearerAuth()
export class VipController {
  constructor(private readonly vipService: VipService) {}

  @Get('profile/:userId')
  @ApiOperation({ summary: 'Get complete VIP profile, preferences, and visit history' })
  async getVipProfile(@Param('userId') userId: string) {
    return this.vipService.getVipProfile(userId);
  }

  @Put('profile')
  @ApiOperation({ summary: 'Create or update VIP customer profile' })
  async upsertVipProfile(@Body() dto: CreateVipProfileDto) {
    return this.vipService.upsertVipProfile(dto);
  }

  @Post('preference')
  @ApiOperation({ summary: 'Add customer preference (Seating, Diet, Drink, Staff)' })
  async addPreference(@CurrentUser() user: User, @Body() dto: AddPreferenceDto) {
    return this.vipService.addPreference(user.id, dto);
  }

  @Post('check-in')
  @ApiOperation({ summary: 'Process customer check-in and trigger automated VIP recognition & staff alerts' })
  async recordVipArrivalAndRecognize(@Body() dto: RecordVipCheckInDto) {
    return this.vipService.recordVipArrivalAndRecognize(dto);
  }
}
