import { Body, Controller, Get, Param, Patch, Post, UseGuards } from '@nestjs/common';
import { ApiTags, ApiOperation, ApiBearerAuth } from '@nestjs/swagger';
import { WaitlistService } from './waitlist.service';
import { JoinWaitlistDto } from './dto/waitlist.dto';
import { JwtAuthGuard } from '../common/guards/jwt-auth.guard';
import { CurrentUser } from '../common/decorators/current-user.decorator';
import { User, Role } from '@prisma/client';
import { RbacGuard } from '../common/guards/rbac.guard';
import { Roles } from '../common/decorators/roles.decorator';

@ApiTags('Live Waitlist')
@Controller('api/v1/waitlist')
@UseGuards(JwtAuthGuard)
@ApiBearerAuth()
export class WaitlistController {
  constructor(private readonly waitlistService: WaitlistService) {}

  @Post()
  @ApiOperation({ summary: 'Join Live Queue Waitlist with Priority Scoring' })
  async joinWaitlist(@CurrentUser() user: User, @Body() dto: JoinWaitlistDto) {
    return this.waitlistService.joinWaitlist(user.id, dto);
  }

  @Get('my-waitlist')
  @ApiOperation({ summary: 'Get current user active waitlist queue entries' })
  async getUserWaitlists(@CurrentUser() user: User) {
    return this.waitlistService.getUserWaitlists(user.id);
  }

  @Post(':id/claim')
  @ApiOperation({ summary: 'Claim offered table within 5-minute expiry window' })
  async claimTable(@Param('id') id: string, @CurrentUser() user: User) {
    return this.waitlistService.claimTable(id, user.id);
  }

  @Patch(':id/leave')
  @ApiOperation({ summary: 'Leave waitlist queue' })
  async leaveWaitlist(@Param('id') id: string, @CurrentUser() user: User) {
    return this.waitlistService.leaveWaitlist(id, user.id);
  }

  @Post('promote/:restaurantId')
  @UseGuards(RbacGuard)
  @Roles(Role.RESTAURANT_OWNER, Role.ADMIN)
  @ApiOperation({ summary: 'Promote next guest in priority queue when table is ready (Owner/Admin)' })
  async promoteNextInQueue(@Param('restaurantId') restaurantId: string) {
    return this.waitlistService.promoteNextInQueue(restaurantId);
  }
}
