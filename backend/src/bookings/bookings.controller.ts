import { Body, Controller, Get, Param, Patch, Post, UseGuards } from '@nestjs/common';
import { ApiTags, ApiOperation, ApiBearerAuth } from '@nestjs/swagger';
import { BookingsService } from './bookings.service';
import { CreateBookingDto } from './dto/booking.dto';
import { JwtAuthGuard } from '../common/guards/jwt-auth.guard';
import { CurrentUser } from '../common/decorators/current-user.decorator';
import { User } from '@prisma/client';

@ApiTags('Bookings')
@Controller('api/v1/bookings')
@UseGuards(JwtAuthGuard)
@ApiBearerAuth()
export class BookingsController {
  constructor(private readonly bookingsService: BookingsService) {}

  @Post()
  @ApiOperation({ summary: 'Create new table reservation' })
  async createBooking(@CurrentUser() user: User, @Body() dto: CreateBookingDto) {
    return this.bookingsService.createBooking(user.id, dto);
  }

  @Get('my-bookings')
  @ApiOperation({ summary: 'Get current user table reservations' })
  async getUserBookings(@CurrentUser() user: User) {
    return this.bookingsService.getUserBookings(user.id);
  }

  @Patch(':id/cancel')
  @ApiOperation({ summary: 'Cancel reservation' })
  async cancelBooking(@Param('id') id: string, @CurrentUser() user: User) {
    return this.bookingsService.cancelBooking(id, user.id);
  }
}
