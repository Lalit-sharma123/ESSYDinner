import { Injectable } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { CreateServiceRequestDto, TaskStatusEnum } from './service-request.dto';

@Injectable()
export class ServiceRequestRepository {
  constructor(private prisma: PrismaService) {}

  async createServiceRequest(dto: CreateServiceRequestDto) {
    const restaurantId = dto.restaurantId || 'rest_1';
    const tableNumber = dto.tableNumber || 'T-01';
    const priority = dto.priority || 'NORMAL';
    const notes = dto.notes || '';

    // Create ServiceRequest
    const req = await this.prisma.serviceRequest.create({
      data: {
        restaurantId,
        bookingId: dto.bookingId || '',
        sessionId: dto.diningSessionId,
        customerId: dto.customerId || '',
        tableId: dto.tableId || '',
        tableNumber,
        requestType: dto.requestType as any,
        status: 'PENDING' as any,
        priority,
        note: notes,
        items: dto.items && dto.items.length > 0
          ? {
              create: dto.items.map(item => ({
                menuItemId: item.menuItemId || '',
                itemName: item.itemName,
                quantity: item.quantity,
                price: item.price || 0.0,
                specialInstructions: item.specialInstructions || '',
              })),
            }
          : undefined,
      },
      include: {
        items: true,
      },
    });

    // Automatically create StaffTask
    const task = await this.prisma.staffTask.create({
      data: {
        restaurantId,
        serviceRequestId: req.id,
        assignedStaffId: 'staff_1',
        assignedStaffName: 'Alex Waiter',
        taskStatus: 'ASSIGNED' as any,
      },
    });

    // Update service request status to ASSIGNED
    const updatedReq = await this.prisma.serviceRequest.update({
      where: { id: req.id },
      data: { status: 'ASSIGNED' as any },
      include: { items: true, staffTasks: true },
    });

    return { request: updatedReq, task };
  }

  async getMyRequests(sessionId: string) {
    return this.prisma.serviceRequest.findMany({
      where: { sessionId },
      include: { items: true, staffTasks: true },
      orderBy: { createdAt: 'desc' },
    });
  }

  async getRequestById(id: string) {
    return this.prisma.serviceRequest.findUnique({
      where: { id },
      include: { items: true, staffTasks: true },
    });
  }

  async getAllStaffTasks(restaurantId: string = 'rest_1') {
    return this.prisma.staffTask.findMany({
      where: { restaurantId },
      include: {
        serviceRequest: {
          include: { items: true },
        },
      },
      orderBy: { createdAt: 'desc' },
    });
  }

  async updateTaskStatus(taskId: string, status: TaskStatusEnum, changedBy = 'STAFF') {
    const task = await this.prisma.staffTask.findUnique({
      where: { id: taskId },
      include: { serviceRequest: true },
    });

    if (!task) return null;

    const now = new Date();
    const updateData: any = { taskStatus: status as any };

    if (status === TaskStatusEnum.ACCEPTED) {
      updateData.acceptedAt = now;
    } else if (status === TaskStatusEnum.IN_PROGRESS) {
      updateData.startedAt = now;
    } else if (status === TaskStatusEnum.COMPLETED) {
      updateData.completedAt = now;
    }

    const updatedTask = await this.prisma.staffTask.update({
      where: { id: taskId },
      data: updateData,
      include: {
        serviceRequest: {
          include: { items: true },
        },
      },
    });

    // Also update parent ServiceRequest status
    await this.prisma.serviceRequest.update({
      where: { id: task.serviceRequestId },
      data: {
        status: status as any,
        isResolved: status === TaskStatusEnum.COMPLETED,
      },
    });

    // Log history
    await this.prisma.taskHistory.create({
      data: {
        taskId,
        previousStatus: task.taskStatus,
        newStatus: status as any,
        changedBy,
      },
    });

    return updatedTask;
  }

  async assignTask(taskId: string, staffId: string, staffName = 'Assigned Waiter') {
    const task = await this.prisma.staffTask.update({
      where: { id: taskId },
      data: {
        assignedStaffId: staffId,
        assignedStaffName: staffName,
        taskStatus: 'ASSIGNED' as any,
      },
      include: {
        serviceRequest: {
          include: { items: true },
        },
      },
    });

    await this.prisma.staffAssignment.create({
      data: {
        taskId,
        staffId,
        role: 'WAITER',
      },
    });

    return task;
  }

  async getStaffStatus(staffId: string) {
    return this.prisma.staffStatus.findUnique({ where: { staffId } });
  }

  async getAllStaffStatuses() {
    return this.prisma.staffStatus.findMany();
  }
}
