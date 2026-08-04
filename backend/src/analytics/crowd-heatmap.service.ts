import { Injectable, NotFoundException } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { UpdateHourlyTrafficDto } from './dto/crowd.dto';

@Injectable()
export class CrowdHeatmapService {
  constructor(private prisma: PrismaService) {}

  private calculateCrowdLevel(rate: number): string {
    if (rate < 20) return 'Very Quiet';
    if (rate < 40) return 'Quiet';
    if (rate < 65) return 'Moderate';
    if (rate < 85) return 'Busy';
    return 'Very Busy';
  }

  async get24HourHeatmap(restaurantId: string, dayOfWeek: number = 1) {
    const records = await this.prisma.hourlyCrowdAnalytics.findMany({
      where: {
        restaurantId,
        dayOfWeek,
      },
      orderBy: { hourOfDay: 'asc' },
    });

    if (records.length === 0) {
      // Return default 24-hour simulation data if not seeded
      return Array.from({ length: 24 }).map((_, hour) => {
        let rate = 10;
        if (hour >= 12 && hour <= 14) rate = 75; // Lunch peak
        if (hour >= 18 && hour <= 21) rate = 92; // Dinner peak
        if (hour >= 15 && hour <= 17) rate = 35; // Afternoon
        return {
          hourOfDay: hour,
          occupancyRate: rate,
          crowdLevel: this.calculateCrowdLevel(rate),
          walkInCount: Math.round(rate * 0.3),
          bookingCount: Math.round(rate * 0.7),
        };
      });
    }

    return records;
  }

  async recordHourlyTraffic(dto: UpdateHourlyTrafficDto) {
    const totalCapacity = 100; // Estimated max capacity
    const totalPeople = dto.walkInCount + dto.bookingCount;
    const occupancyRate = Math.min(100, Math.round((totalPeople / totalCapacity) * 100));
    const crowdLevel = this.calculateCrowdLevel(occupancyRate);

    const existing = await this.prisma.hourlyCrowdAnalytics.findFirst({
      where: {
        restaurantId: dto.restaurantId,
        dayOfWeek: dto.dayOfWeek,
        hourOfDay: dto.hourOfDay,
      },
    });

    if (existing) {
      return this.prisma.hourlyCrowdAnalytics.update({
        where: { id: existing.id },
        data: {
          occupancyRate,
          crowdLevel,
          walkInCount: dto.walkInCount,
          bookingCount: dto.bookingCount,
        },
      });
    }

    return this.prisma.hourlyCrowdAnalytics.create({
      data: {
        restaurantId: dto.restaurantId,
        dayOfWeek: dto.dayOfWeek,
        hourOfDay: dto.hourOfDay,
        occupancyRate,
        crowdLevel,
        walkInCount: dto.walkInCount,
        bookingCount: dto.bookingCount,
      },
    });
  }

  async getWeeklyPredictions(restaurantId: string) {
    const days = [0, 1, 2, 3, 4, 5, 6]; // Sun to Sat
    const result: Record<number, any[]> = {};

    for (const day of days) {
      result[day] = await this.get24HourHeatmap(restaurantId, day);
    }

    return {
      restaurantId,
      weeklyPredictions: result,
      peakHours: ['12:00 - 14:00', '19:00 - 21:00'],
      recommendedBestTimes: ['15:00 - 17:00', '21:30 - 23:00'],
    };
  }
}
