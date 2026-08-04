import { Controller, Get, Param, UseGuards } from '@nestjs/common';
import { ApiTags, ApiOperation, ApiBearerAuth } from '@nestjs/swagger';
import { PrismaService } from '../prisma/prisma.service';
import { JwtAuthGuard } from '../common/guards/jwt-auth.guard';

@ApiTags('Corporate Dining')
@Controller('api/v1/corporate')
@UseGuards(JwtAuthGuard)
@ApiBearerAuth()
export class CorporateController {
  constructor(private prisma: PrismaService) {}

  @Get('company/:id')
  @ApiOperation({ summary: 'Get Company Budget & Department Balances' })
  async getCompanyDetails(@Param('id') id: string) {
    return this.prisma.company.findUnique({
      where: { id },
      include: {
        departments: true,
        employees: true,
        approvals: true,
      },
    });
  }
}
