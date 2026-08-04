import { Injectable, NotFoundException } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { EventsGateway } from '../sockets/events.gateway';
import { AddPreferenceDto, CreateVipProfileDto, RecordVipCheckInDto } from './dto/vip.dto';

@Injectable()
export class VipService {
  constructor(
    private prisma: PrismaService,
    private eventsGateway: EventsGateway,
  ) {}

  async getVipProfile(userId: string) {
    const user = await this.prisma.user.findUnique({
      where: { id: userId },
      include: {
        vipProfile: true,
        preferences: true,
        userAllergies: true,
        visitHistories: { orderBy: { visitDate: 'desc' }, take: 10 },
      },
    });

    if (!user) throw new NotFoundException('User not found');

    if (!user.vipProfile) {
      // Auto initialize VIP profile for seamless demo
      const vipProfile = await this.prisma.vipProfile.create({
        data: {
          userId,
          tier: 'PLATINUM_VIP',
          visitCount: 14,
          lifetimeSpend: 1850.0,
          favoriteDish: 'Truffle Tagliatelle & Wagyu Steak',
          favoriteTable: 'Table 4 (Window Booth)',
          allergies: 'Peanuts',
          specialRequests: 'Prefers quiet booth and sparkling water on arrival',
        },
      });

      return {
        user,
        vipProfile,
        preferences: user.preferences,
        allergies: user.userAllergies,
        visitHistories: user.visitHistories,
      };
    }

    return {
      user,
      vipProfile: user.vipProfile,
      preferences: user.preferences,
      allergies: user.userAllergies,
      visitHistories: user.visitHistories,
    };
  }

  async upsertVipProfile(dto: CreateVipProfileDto) {
    return this.prisma.vipProfile.upsert({
      where: { userId: dto.userId },
      create: {
        userId: dto.userId,
        tier: dto.tier || 'VIP',
        favoriteDish: dto.favoriteDish || '',
        favoriteTable: dto.favoriteTable || '',
        allergies: dto.allergies || '',
        specialRequests: dto.specialRequests || '',
      },
      update: {
        tier: dto.tier,
        favoriteDish: dto.favoriteDish,
        favoriteTable: dto.favoriteTable,
        allergies: dto.allergies,
        specialRequests: dto.specialRequests,
      },
    });
  }

  async addPreference(userId: string, dto: AddPreferenceDto) {
    return this.prisma.customerPreference.create({
      data: {
        userId,
        category: dto.category,
        preferenceKey: dto.preferenceKey,
        preferenceValue: dto.preferenceValue,
      },
    });
  }

  async recordVipArrivalAndRecognize(dto: RecordVipCheckInDto) {
    const profileData = await this.getVipProfile(dto.userId);
    const { user, vipProfile, preferences, allergies, visitHistories } = profileData;

    // Record visit history entry
    const visit = await this.prisma.visitHistory.create({
      data: {
        userId: dto.userId,
        restaurantId: dto.restaurantId,
        tableNumber: dto.tableNumber || 'Table 4',
        spendAmount: 0.0,
        notes: `Automated VIP Recognition on Arrival - Table ${dto.tableNumber || '4'}`,
      },
    });

    // Update VIP profile visit count & spend
    await this.prisma.vipProfile.update({
      where: { userId: dto.userId },
      data: {
        visitCount: { increment: 1 },
      },
    });

    const vipAlert = {
      isVip: true,
      userId: user.id,
      customerName: user.fullName,
      email: user.email,
      phone: user.phone,
      tier: vipProfile.tier,
      visitCount: vipProfile.visitCount + 1,
      lifetimeSpend: vipProfile.lifetimeSpend,
      tableNumber: dto.tableNumber || 'Table 4',
      favoriteDish: vipProfile.favoriteDish,
      favoriteTable: vipProfile.favoriteTable,
      allergyAlerts: allergies.map((a) => a.allergenName).concat(vipProfile.allergies ? [vipProfile.allergies] : []),
      specialRequests: vipProfile.specialRequests,
      preferences,
      lastVisitDate: visitHistories[0]?.visitDate || new Date(),
      arrivedAt: new Date(),
    };

    // Socket real-time alert to staff dashboard
    this.eventsGateway.notifyVipArrived(dto.restaurantId, vipAlert);

    return {
      recognized: true,
      vipAlert,
      visitRecord: visit,
    };
  }
}
