import { Controller, Get, UseGuards } from '@nestjs/common';
import { ApiTags, ApiOperation, ApiBearerAuth } from '@nestjs/swagger';
import { PrismaService } from '../prisma/prisma.service';
import { JwtAuthGuard } from '../common/guards/jwt-auth.guard';
import { CurrentUser } from '../common/decorators/current-user.decorator';
import { User } from '@prisma/client';

@ApiTags('Guest CRM')
@Controller('api/v1/crm')
@UseGuards(JwtAuthGuard)
@ApiBearerAuth()
export class CrmController {
  constructor(private prisma: PrismaService) {}

  @Get('my-profile')
  @ApiOperation({ summary: 'Get Guest CRM & Preferences Profile' })
  async getMyProfile(@CurrentUser() user: User) {
    let crm = await this.prisma.customerCrm.findUnique({
      where: { userId: user.id },
    });

    if (!crm) {
      crm = await this.prisma.customerCrm.create({
        data: {
          userId: user.id,
          visitCount: 1,
          totalSpend: 0.0,
          avgBill: 0.0,
          membershipLevel: 'Bronze Member',
          segmentTag: 'Regular Guest',
        },
      });
    }

    return crm;
  }
}
