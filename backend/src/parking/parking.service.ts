import { Injectable, NotFoundException } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { EventsGateway } from '../sockets/events.gateway';
import { CreateParkingLotDto, CreateParkingSlotDto, UpdateParkingSlotStateDto } from './dto/parking.dto';
import { ParkingSlotState } from '@prisma/client';

@Injectable()
export class ParkingService {
  constructor(
    private prisma: PrismaService,
    private eventsGateway: EventsGateway,
  ) {}

  async createParkingLot(dto: CreateParkingLotDto) {
    const lot = await this.prisma.parkingLot.create({
      data: {
        restaurantId: dto.restaurantId,
        lotName: dto.lotName,
        totalCapacity: dto.totalCapacity || 20,
      },
      include: { slots: true },
    });

    // Auto-generate default parking slots if needed
    const slotsData = Array.from({ length: dto.totalCapacity || 10 }).map((_, i) => ({
      lotId: lot.id,
      slotNumber: `P-${(i + 1).toString().padStart(2, '0')}`,
      slotType: i === 0 ? 'ACCESSIBLE' : i === 1 ? 'EV_CHARGING' : i === 2 ? 'VIP' : 'STANDARD',
      state: ParkingSlotState.Available,
    }));

    await this.prisma.parkingSlot.createMany({
      data: slotsData,
    });

    return this.prisma.parkingLot.findUnique({
      where: { id: lot.id },
      include: { slots: true },
    });
  }

  async getParkingLots(restaurantId: string) {
    const lots = await this.prisma.parkingLot.findMany({
      where: { restaurantId },
      include: {
        slots: {
          orderBy: { slotNumber: 'asc' },
        },
      },
    });

    if (lots.length === 0) {
      // Auto create a default parking lot for seamless UX
      return [await this.createParkingLot({ restaurantId, lotName: 'Restaurant Valet & Guest Parking', totalCapacity: 12 })];
    }

    return lots.map((lot) => {
      const availableCount = lot.slots.filter((s) => s.state === ParkingSlotState.Available).length;
      const evSlotsCount = lot.slots.filter((s) => s.slotType === 'EV_CHARGING').length;
      const evAvailableCount = lot.slots.filter((s) => s.slotType === 'EV_CHARGING' && s.state === ParkingSlotState.Available).length;
      const accessibleSlotsCount = lot.slots.filter((s) => s.slotType === 'ACCESSIBLE').length;

      return {
        ...lot,
        availableCount,
        occupiedCount: lot.slots.length - availableCount,
        evSlotsCount,
        evAvailableCount,
        accessibleSlotsCount,
        isFull: availableCount === 0,
      };
    });
  }

  async addParkingSlot(dto: CreateParkingSlotDto) {
    const lot = await this.prisma.parkingLot.findUnique({ where: { id: dto.lotId } });
    if (!lot) throw new NotFoundException('Parking lot not found');

    const slot = await this.prisma.parkingSlot.create({
      data: {
        lotId: dto.lotId,
        slotNumber: dto.slotNumber,
        slotType: dto.slotType || 'STANDARD',
        state: ParkingSlotState.Available,
      },
    });

    this.eventsGateway.notifyParkingUpdated(lot.restaurantId, 'parking.updated', slot);
    return slot;
  }

  async updateSlotState(slotId: string, dto: UpdateParkingSlotStateDto) {
    const slot = await this.prisma.parkingSlot.findUnique({
      where: { id: slotId },
      include: { lot: true },
    });

    if (!slot) throw new NotFoundException(`Parking slot ${slotId} not found`);

    const previousState = slot.state;
    const newState = dto.state;

    const updatedSlot = await this.prisma.parkingSlot.update({
      where: { id: slotId },
      data: {
        state: newState,
        occupiedBy: dto.occupiedBy !== undefined ? dto.occupiedBy : slot.occupiedBy,
        history: {
          create: {
            previousState,
            newState,
          },
        },
      },
      include: { lot: true },
    });

    // Check overall lot state
    const allSlots = await this.prisma.parkingSlot.findMany({ where: { lotId: slot.lotId } });
    const availableCount = allSlots.filter((s) => s.state === ParkingSlotState.Available).length;

    let socketEvent = 'parking.updated';
    if (availableCount === 0) socketEvent = 'parking.full';
    else if (previousState !== ParkingSlotState.Available && newState === ParkingSlotState.Available) socketEvent = 'parking.available';

    this.eventsGateway.notifyParkingUpdated(slot.lot.restaurantId, socketEvent, {
      updatedSlot,
      availableCount,
      totalSlots: allSlots.length,
    });

    return updatedSlot;
  }
}
