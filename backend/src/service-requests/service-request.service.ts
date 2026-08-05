import { Injectable, NotFoundException } from '@nestjs/common';
import { ServiceRequestRepository } from './service-request.repository';
import { CreateServiceRequestDto, TaskStatusEnum } from './service-request.dto';
import { EventsGateway } from '../sockets/events.gateway';

@Injectable()
export class ServiceRequestService {
  constructor(
    private repository: ServiceRequestRepository,
    private eventsGateway: EventsGateway,
  ) {}

  async createServiceRequest(dto: CreateServiceRequestDto) {
    const result = await this.repository.createServiceRequest(dto);
    const restaurantId = dto.restaurantId || 'rest_1';

    // Broadcast socket events
    this.eventsGateway.server?.to(`restaurant_${restaurantId}`).emit('service_request_created', result.request);
    this.eventsGateway.server?.to(`session_${dto.diningSessionId}`).emit('service_request_created', result.request);
    this.eventsGateway.server?.to(`restaurant_${restaurantId}`).emit('staff_task_assigned', result.task);
    this.eventsGateway.notifyDashboardUpdated(restaurantId, { type: 'SERVICE_REQUEST_NEW', data: result.request });

    return result;
  }

  async getMyRequests(sessionId: string) {
    return this.repository.getMyRequests(sessionId);
  }

  async getRequestById(id: string) {
    const req = await this.repository.getRequestById(id);
    if (!req) {
      throw new NotFoundException(`Service request with ID ${id} not found`);
    }
    return req;
  }

  async getAllStaffTasks(restaurantId = 'rest_1') {
    return this.repository.getAllStaffTasks(restaurantId);
  }

  async updateTaskStatus(taskId: string, status: TaskStatusEnum, changedBy = 'STAFF') {
    const updatedTask = await this.repository.updateTaskStatus(taskId, status, changedBy);
    if (!updatedTask) {
      throw new NotFoundException(`Task with ID ${taskId} not found`);
    }

    const restaurantId = updatedTask.restaurantId || 'rest_1';
    const sessionId = updatedTask.serviceRequest.sessionId;

    // Socket event map
    const eventName =
      status === TaskStatusEnum.ACCEPTED ? 'staff_task_accepted' :
      status === TaskStatusEnum.IN_PROGRESS ? 'staff_task_started' :
      status === TaskStatusEnum.COMPLETED ? 'staff_task_completed' : 'service_request_updated';

    this.eventsGateway.server?.to(`restaurant_${restaurantId}`).emit(eventName, updatedTask);
    this.eventsGateway.server?.to(`session_${sessionId}`).emit('service_request_updated', updatedTask.serviceRequest);
    this.eventsGateway.notifyDashboardUpdated(restaurantId, { type: 'TASK_STATUS_CHANGED', data: updatedTask });

    return updatedTask;
  }

  async assignTask(taskId: string, staffId: string, staffName = 'Assigned Waiter') {
    const task = await this.repository.assignTask(taskId, staffId, staffName);
    const restaurantId = task.restaurantId || 'rest_1';

    this.eventsGateway.server?.to(`restaurant_${restaurantId}`).emit('staff_task_assigned', task);
    this.eventsGateway.server?.to(`session_${task.serviceRequest.sessionId}`).emit('service_request_updated', task.serviceRequest);

    return task;
  }
}
