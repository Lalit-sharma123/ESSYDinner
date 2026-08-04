import { Injectable, NotFoundException, BadRequestException } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { EventsGateway } from '../sockets/events.gateway';
import {
  CompleteCleaningDto,
  CreateFloorDto,
  CreateTableDto,
  MergeTablesDto,
  UpdateTablePositionDto,
  UpdateTableStatusDto,
} from './dto/tables.dto';
import { TableStatus } from '@prisma/client';

@Injectable()
export class TablesService {
  constructor(
    private prisma: PrismaService,
    private eventsGateway: EventsGateway,
  ) {}

  // Floors
  async createFloor(dto: CreateFloorDto) {
    return this.prisma.restaurantFloor.create({
      data: {
        restaurantId: dto.restaurantId,
        floorName: dto.floorName,
        level: dto.level || 1,
      },
      include: { tables: true },
    });
  }

  async getRestaurantFloors(restaurantId: string) {
    return this.prisma.restaurantFloor.findMany({
      where: { restaurantId },
      include: {
        tables: {
          include: {
            history: { orderBy: { timestamp: 'desc' }, take: 3 },
            cleaningLogs: { orderBy: { completedAt: 'desc' }, take: 1 },
          },
        },
      },
      orderBy: { level: 'asc' },
    });
  }

  // Tables
  async createTable(dto: CreateTableDto) {
    const table = await this.prisma.restaurantTable.create({
      data: {
        restaurantId: dto.restaurantId,
        floorId: dto.floorId,
        tableNumber: dto.tableNumber,
        capacity: dto.capacity || 4,
        positionX: dto.positionX || 0.0,
        positionY: dto.positionY || 0.0,
        shape: dto.shape || 'RECTANGLE',
        status: TableStatus.AVAILABLE,
        qrCode: `QR_TABLE_${dto.restaurantId}_${dto.tableNumber}`,
      },
    });

    this.eventsGateway.notifyTableUpdated(dto.restaurantId, 'table.created', table);
    return table;
  }

  async getTablesByRestaurant(restaurantId: string) {
    return this.prisma.restaurantTable.findMany({
      where: { restaurantId },
      include: {
        floor: true,
        history: { orderBy: { timestamp: 'desc' }, take: 5 },
      },
    });
  }

  async updateTableStatus(tableId: string, dto: UpdateTableStatusDto) {
    const table = await this.prisma.restaurantTable.findUnique({ where: { id: tableId } });
    if (!table) {
      throw new NotFoundException(`Table with ID ${tableId} not found`);
    }

    const previousStatus = table.status;
    const newStatus = dto.status;

    const updatedTable = await this.prisma.restaurantTable.update({
      where: { id: tableId },
      data: {
        status: newStatus,
        history: {
          create: {
            previousStatus,
            newStatus,
            changedBy: dto.changedBy || 'SYSTEM',
          },
        },
      },
      include: { floor: true },
    });

    // Map to specific socket event names required
    let socketEvent = 'table.updated';
    if (newStatus === TableStatus.CLEANING) socketEvent = 'table.cleaned';
    if (newStatus === TableStatus.BLOCKED) socketEvent = 'table.blocked';
    if (newStatus === TableStatus.AVAILABLE) socketEvent = 'table.available';

    this.eventsGateway.notifyTableUpdated(table.restaurantId, socketEvent, updatedTable);

    return updatedTable;
  }

  async updateTablePosition(tableId: string, dto: UpdateTablePositionDto) {
    const table = await this.prisma.restaurantTable.findUnique({ where: { id: tableId } });
    if (!table) throw new NotFoundException('Table not found');

    const updatedTable = await this.prisma.restaurantTable.update({
      where: { id: tableId },
      data: {
        positionX: dto.positionX,
        positionY: dto.positionY,
      },
    });

    this.eventsGateway.notifyTableUpdated(table.restaurantId, 'table.updated', updatedTable);
    return updatedTable;
  }

  async mergeTables(dto: MergeTablesDto) {
    const primary = await this.prisma.restaurantTable.findUnique({ where: { id: dto.primaryTableId } });
    const secondary = await this.prisma.restaurantTable.findUnique({ where: { id: dto.secondaryTableId } });

    if (!primary || !secondary) {
      throw new NotFoundException('One or both tables not found for merge');
    }

    const updatedPrimary = await this.prisma.restaurantTable.update({
      where: { id: dto.primaryTableId },
      data: {
        capacity: primary.capacity + secondary.capacity,
        mergedWith: secondary.tableNumber,
      },
    });

    await this.prisma.restaurantTable.update({
      where: { id: dto.secondaryTableId },
      data: {
        status: TableStatus.BLOCKED,
        mergedWith: primary.tableNumber,
      },
    });

    this.eventsGateway.notifyTableUpdated(primary.restaurantId, 'table.updated', updatedPrimary);
    return updatedPrimary;
  }

  async unmergeTable(tableId: string) {
    const table = await this.prisma.restaurantTable.findUnique({ where: { id: tableId } });
    if (!table) throw new NotFoundException('Table not found');

    const updated = await this.prisma.restaurantTable.update({
      where: { id: tableId },
      data: {
        mergedWith: '',
        status: TableStatus.AVAILABLE,
      },
    });

    this.eventsGateway.notifyTableUpdated(table.restaurantId, 'table.updated', updated);
    return updated;
  }

  async completeCleaning(tableId: string, dto: CompleteCleaningDto) {
    const table = await this.prisma.restaurantTable.findUnique({ where: { id: tableId } });
    if (!table) throw new NotFoundException('Table not found');

    const updatedTable = await this.prisma.restaurantTable.update({
      where: { id: tableId },
      data: {
        status: TableStatus.AVAILABLE,
        cleaningLogs: {
          create: {
            cleanedBy: dto.cleanedBy,
            startedAt: table.lastUpdated,
            completedAt: new Date(),
          },
        },
        history: {
          create: {
            previousStatus: TableStatus.CLEANING,
            newStatus: TableStatus.AVAILABLE,
            changedBy: dto.cleanedBy,
          },
        },
      },
    });

    this.eventsGateway.notifyTableUpdated(table.restaurantId, 'table.available', updatedTable);
    return updatedTable;
  }

  async deleteTable(tableId: string) {
    const table = await this.prisma.restaurantTable.findUnique({ where: { id: tableId } });
    if (!table) throw new NotFoundException('Table not found');

    await this.prisma.restaurantTable.delete({ where: { id: tableId } });
    this.eventsGateway.notifyTableUpdated(table.restaurantId, 'table.deleted', { tableId });
    return { success: true };
  }
}
