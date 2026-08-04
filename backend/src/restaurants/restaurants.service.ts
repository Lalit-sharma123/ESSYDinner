import { Injectable, NotFoundException } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { RedisService } from '../redis/redis.service';
import { CreateRestaurantDto, RestaurantQueryDto } from './dto/restaurant.dto';

@Injectable()
export class RestaurantsService {
  constructor(
    private prisma: PrismaService,
    private redis: RedisService,
  ) {}

  async findAll(query: RestaurantQueryDto) {
    const cacheKey = `restaurants:list:${query.cuisine || 'all'}:${query.search || 'none'}`;
    const cached = await this.redis.get(cacheKey);
    if (cached) {
      return JSON.parse(cached);
    }

    const where: any = {};
    if (query.cuisine) {
      where.cuisineType = { contains: query.cuisine, mode: 'insensitive' };
    }
    if (query.search) {
      where.OR = [
        { name: { contains: query.search, mode: 'insensitive' } },
        { address: { contains: query.search, mode: 'insensitive' } },
        { area: { contains: query.search, mode: 'insensitive' } },
      ];
    }

    const result = await this.prisma.restaurant.findMany({
      where,
      include: { menuItems: true, offers: true, reviews: true },
    });

    await this.redis.set(cacheKey, JSON.stringify(result), 300); // 5 min cache
    return result;
  }

  async findOne(id: string) {
    const restaurant = await this.prisma.restaurant.findUnique({
      where: { id },
      include: {
        menuItems: {
          include: { variants: true, addons: true },
        },
        offers: true,
        reviews: {
          include: { user: { select: { fullName: true } } },
        },
      },
    });

    if (!restaurant) {
      throw new NotFoundException(`Restaurant with ID ${id} not found`);
    }

    return restaurant;
  }

  async create(dto: CreateRestaurantDto) {
    return this.prisma.restaurant.create({
      data: dto,
    });
  }
}
