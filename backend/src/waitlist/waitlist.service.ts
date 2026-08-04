import { Injectable, NotFoundException, BadRequestException } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { JoinWaitlistDto } from './dto/waitlist.dto';
import { EventsGateway } from '../sockets/events.gateway';

@Injectable()
export class WaitlistService {
  constructor(
    private prisma: PrismaService,
    private eventsGateway: EventsGateway,
  ) {}

  async joinWaitlist(userId: string, dto: JoinWaitlistDto) {
    const restaurant = await this.prisma.restaurant.findUnique({
      where: { id: dto.restaurantId },
    });
    if (!restaurant) {
      throw new NotFoundException('Restaurant not found');
    }

    const user = await this.prisma.user.findUnique({
      where: { id: userId },
    });

    const isPriorityUser = dto.isPriority || user?.membershipTier === 'VIP' || user?.membershipTier === 'GOLD';
    const priorityScore = isPriorityUser ? 10 : 0;

    const activeEntries = await this.prisma.waitlistEntry.findMany({
      where: {
        restaurantId: dto.restaurantId,
        status: 'QUEUED',
      },
      orderBy: [
        { priorityScore: 'desc' },
        { createdAt: 'asc' },
      ],
    });

    const queuePosition = activeEntries.length + 1;
    const estWaitMins = queuePosition * 10;

    const entry = await this.prisma.waitlistEntry.create({
      data: {
        userId,
        restaurantId: dto.restaurantId,
        restaurantName: restaurant.name,
        partySize: dto.partySize,
        queuePosition,
        estWaitMins,
        isPriority: isPriorityUser,
        priorityScore,
        status: 'QUEUED',
      },
    });

    // Notify WebSocket subscribers of queue update
    this.eventsGateway.notifyWaitlistUpdate(dto.restaurantId, {
      action: 'JOINED',
      entryId: entry.id,
      queuePosition,
    });

    return entry;
  }

  async getUserWaitlists(userId: string) {
    await this.processExpiries();
    return this.prisma.waitlistEntry.findMany({
      where: { userId },
      include: { restaurant: true },
      orderBy: { createdAt: 'desc' },
    });
  }

  async promoteNextInQueue(restaurantId: string) {
    const nextEntry = await this.prisma.waitlistEntry.findFirst({
      where: {
        restaurantId,
        status: 'QUEUED',
      },
      orderBy: [
        { priorityScore: 'desc' },
        { createdAt: 'asc' },
      ],
    });

    if (!nextEntry) {
      return null;
    }

    const expiresAt = new Date(Date.now() + 5 * 60 * 1000); // 5-minute claim expiry window

    const updated = await this.prisma.waitlistEntry.update({
      where: { id: nextEntry.id },
      data: {
        status: 'OFFERED',
        offeredAt: new Date(),
        expiresAt,
      },
    });

    this.eventsGateway.notifyWaitlistUpdate(restaurantId, {
      action: 'OFFERED',
      entryId: updated.id,
      userId: updated.userId,
      expiresAt,
    });

    return updated;
  }

  async claimTable(entryId: string, userId: string) {
    const entry = await this.prisma.waitlistEntry.findFirst({
      where: { id: entryId, userId },
    });

    if (!entry) {
      throw new NotFoundException('Waitlist entry not found');
    }

    if (entry.status !== 'OFFERED') {
      throw new BadRequestException('Table offer is not active or has expired');
    }

    if (entry.expiresAt && new Date() > entry.expiresAt) {
      await this.prisma.waitlistEntry.update({
        where: { id: entryId },
        data: { status: 'EXPIRED' },
      });
      throw new BadRequestException('Table claim window (5 minutes) has expired');
    }

    const updated = await this.prisma.waitlistEntry.update({
      where: { id: entryId },
      data: { status: 'ACCEPTED' },
    });

    this.eventsGateway.notifyWaitlistUpdate(entry.restaurantId, {
      action: 'ACCEPTED',
      entryId: updated.id,
    });

    return updated;
  }

  async leaveWaitlist(entryId: string, userId: string) {
    const entry = await this.prisma.waitlistEntry.findFirst({
      where: { id: entryId, userId },
    });
    if (!entry) {
      throw new NotFoundException('Waitlist entry not found');
    }

    const updated = await this.prisma.waitlistEntry.update({
      where: { id: entryId },
      data: { status: 'CANCELLED' },
    });

    this.eventsGateway.notifyWaitlistUpdate(entry.restaurantId, {
      action: 'CANCELLED',
      entryId: updated.id,
    });

    return updated;
  }

  async processExpiries() {
    const now = new Date();
    const expiredEntries = await this.prisma.waitlistEntry.findMany({
      where: {
        status: 'OFFERED',
        expiresAt: { lt: now },
      },
    });

    for (const entry of expiredEntries) {
      await this.prisma.waitlistEntry.update({
        where: { id: entry.id },
        data: { status: 'EXPIRED' },
      });

      this.eventsGateway.notifyWaitlistUpdate(entry.restaurantId, {
        action: 'EXPIRED',
        entryId: entry.id,
      });

      // Automatically promote the next guest in line
      await this.promoteNextInQueue(entry.restaurantId);
    }
  }
}
