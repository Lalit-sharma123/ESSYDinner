import { Injectable, NotFoundException, BadRequestException } from '@nestjs/common';
import { ServiceRequestType } from '@prisma/client';
import { PrismaService } from '../prisma/prisma.service';
import { AddOrderItemDto, CalculateSplitDto, ServiceRequestDto, StartSessionDto } from './dto/qr-dining.dto';
import { EventsGateway } from '../sockets/events.gateway';

@Injectable()
export class QrDiningService {
  constructor(
    private prisma: PrismaService,
    private eventsGateway: EventsGateway,
  ) {}

  async startSession(userId: string, dto: StartSessionDto) {
    return this.prisma.diningSession.create({
      data: {
        userId,
        restaurantId: dto.restaurantId,
        tableNumber: dto.tableNumber,
        status: 'ACTIVE',
      },
    });
  }

  async getActiveSession(userId: string) {
    return this.prisma.diningSession.findFirst({
      where: { userId, status: 'ACTIVE' },
      include: {
        restaurant: true,
        orders: true,
        serviceRequests: true,
        splits: {
          include: { shares: true },
          orderBy: { createdAt: 'desc' },
        },
      },
    });
  }

  async addOrderItem(sessionId: string, dto: AddOrderItemDto) {
    const item = await this.prisma.diningOrderItem.create({
      data: {
        sessionId,
        menuItemId: dto.menuItemId,
        itemName: dto.itemName,
        unitPrice: dto.unitPrice,
        quantity: dto.quantity,
        notes: dto.notes || '',
      },
    });

    // Recalculate totals
    const session = await this.prisma.diningSession.findUnique({
      where: { id: sessionId },
      include: { orders: true },
    });

    if (session) {
      const subtotal = session.orders.reduce((acc, curr) => acc + curr.unitPrice * curr.quantity, 0);
      const tax = subtotal * 0.08;
      const total = subtotal + tax;

      await this.prisma.diningSession.update({
        where: { id: sessionId },
        data: { subtotal, tax, total },
      });

      this.eventsGateway.notifyTableOrderUpdate(sessionId, {
        action: 'ORDER_ADDED',
        item,
        subtotal,
        total,
      });
    }

    return item;
  }

  async requestService(sessionId: string, dto: ServiceRequestDto) {
    return this.prisma.serviceRequest.create({
      data: {
        sessionId,
        requestType: dto.requestType as ServiceRequestType,
        note: dto.note || '',
      },
    });
  }

  async calculateAndCreateSplit(sessionId: string, dto: CalculateSplitDto) {
    const session = await this.prisma.diningSession.findUnique({
      where: { id: sessionId },
      include: { orders: true },
    });

    if (!session) {
      throw new NotFoundException('Dining session not found');
    }

    if (session.orders.length === 0) {
      throw new BadRequestException('Cannot split a bill with no order items');
    }

    const subtotal = session.orders.reduce((sum, o) => sum + o.unitPrice * o.quantity, 0);
    const tax = subtotal * 0.08;
    const tipPercent = dto.tipPercent ?? 15.0;
    const tip = subtotal * (tipPercent / 100);
    const grandTotal = subtotal + tax + tip;

    const sharesData: {
      attendeeName: string;
      itemIdsJson: string;
      subtotal: number;
      tax: number;
      tip: number;
      totalShare: number;
    }[] = [];

    if (dto.splitType === 'EQUAL') {
      const numAttendees = Math.max(1, dto.numAttendees || 2);
      const shareSubtotal = Math.round((subtotal / numAttendees) * 100) / 100;
      const shareTax = Math.round((tax / numAttendees) * 100) / 100;
      const shareTip = Math.round((tip / numAttendees) * 100) / 100;
      const shareTotal = Math.round((grandTotal / numAttendees) * 100) / 100;

      for (let i = 1; i <= numAttendees; i++) {
        sharesData.push({
          attendeeName: `Guest ${i}`,
          itemIdsJson: JSON.stringify(session.orders.map((o) => o.id)),
          subtotal: shareSubtotal,
          tax: shareTax,
          tip: shareTip,
          totalShare: shareTotal,
        });
      }
    } else if (dto.splitType === 'ITEMIZED') {
      if (!dto.attendees || dto.attendees.length === 0) {
        throw new BadRequestException('Attendee item assignments are required for itemized split');
      }

      const orderItemMap = new Map(session.orders.map((o) => [o.id, o]));

      for (const att of dto.attendees) {
        let attSubtotal = 0;
        const validItemIds: string[] = [];

        for (const itemId of att.itemIds || []) {
          const item = orderItemMap.get(itemId);
          if (item) {
            attSubtotal += item.unitPrice * item.quantity;
            validItemIds.push(itemId);
          }
        }

        const proportion = subtotal > 0 ? attSubtotal / subtotal : 0;
        const attTax = Math.round(tax * proportion * 100) / 100;
        const attTip = Math.round(tip * proportion * 100) / 100;
        const attTotal = Math.round((attSubtotal + attTax + attTip) * 100) / 100;

        sharesData.push({
          attendeeName: att.attendeeName,
          itemIdsJson: JSON.stringify(validItemIds),
          subtotal: Math.round(attSubtotal * 100) / 100,
          tax: attTax,
          tip: attTip,
          totalShare: attTotal,
        });
      }
    }

    const split = await this.prisma.billSplit.create({
      data: {
        sessionId,
        splitType: dto.splitType,
        numAttendees: sharesData.length,
        tipPercent,
        subtotal: Math.round(subtotal * 100) / 100,
        tax: Math.round(tax * 100) / 100,
        tip: Math.round(tip * 100) / 100,
        grandTotal: Math.round(grandTotal * 100) / 100,
        shares: {
          create: sharesData,
        },
      },
      include: {
        shares: true,
      },
    });

    await this.prisma.diningSession.update({
      where: { id: sessionId },
      data: { status: 'BILL_REQUESTED' },
    });

    this.eventsGateway.notifyBillSplitUpdate(sessionId, {
      action: 'SPLIT_CREATED',
      split,
    });

    return split;
  }

  async getLatestSplit(sessionId: string) {
    return this.prisma.billSplit.findFirst({
      where: { sessionId },
      include: { shares: true },
      orderBy: { createdAt: 'desc' },
    });
  }

  async payShare(shareId: string) {
    const share = await this.prisma.billShare.findUnique({
      where: { id: shareId },
      include: { split: { include: { shares: true } } },
    });

    if (!share) {
      throw new NotFoundException('Bill share not found');
    }

    const updatedShare = await this.prisma.billShare.update({
      where: { id: shareId },
      data: {
        isPaid: true,
        paidAt: new Date(),
      },
    });

    const allShares = await this.prisma.billShare.findMany({
      where: { splitId: share.splitId },
    });

    const allPaid = allShares.every((s) => s.isPaid);

    if (allPaid) {
      await this.prisma.diningSession.update({
        where: { id: share.split.sessionId },
        data: { status: 'COMPLETED' },
      });
    }

    this.eventsGateway.notifyBillSplitUpdate(share.split.sessionId, {
      action: 'SHARE_PAID',
      shareId,
      allPaid,
    });

    return { updatedShare, allPaid };
  }
}
