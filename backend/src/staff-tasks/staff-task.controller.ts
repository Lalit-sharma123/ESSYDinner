import { Controller, Get, Patch, Param, Body, Query } from '@nestjs/common';
import { ApiTags, ApiOperation, ApiResponse } from '@nestjs/swagger';
import { ServiceRequestService } from '../service-requests/service-request.service';
import { TaskStatusEnum, AssignStaffTaskDto } from '../service-requests/service-request.dto';

@ApiTags('Staff Tasks')
@Controller('api/v1/staff/tasks')
export class StaffTaskController {
  constructor(private readonly serviceRequestService: ServiceRequestService) {}

  @Get()
  @ApiOperation({ summary: 'Get assigned tasks for restaurant staff' })
  async getStaffTasks(@Query('restaurantId') restaurantId?: string) {
    return this.serviceRequestService.getAllStaffTasks(restaurantId || 'rest_1');
  }

  @Get('all')
  @ApiOperation({ summary: 'Get all live task queue for manager' })
  async getAllTasksManager(@Query('restaurantId') restaurantId?: string) {
    return this.serviceRequestService.getAllStaffTasks(restaurantId || 'rest_1');
  }

  @Patch(':id/accept')
  @ApiOperation({ summary: 'Accept a service request task' })
  async acceptTask(@Param('id') id: string) {
    return this.serviceRequestService.updateTaskStatus(id, TaskStatusEnum.ACCEPTED);
  }

  @Patch(':id/start')
  @ApiOperation({ summary: 'Start working on a service request task' })
  async startTask(@Param('id') id: string) {
    return this.serviceRequestService.updateTaskStatus(id, TaskStatusEnum.IN_PROGRESS);
  }

  @Patch(':id/complete')
  @ApiOperation({ summary: 'Complete a service request task' })
  async completeTask(@Param('id') id: string) {
    return this.serviceRequestService.updateTaskStatus(id, TaskStatusEnum.COMPLETED);
  }

  @Patch(':id/reject')
  @ApiOperation({ summary: 'Reject a service request task' })
  async rejectTask(@Param('id') id: string) {
    return this.serviceRequestService.updateTaskStatus(id, TaskStatusEnum.CANCELLED);
  }

  @Patch(':id/assign')
  @ApiOperation({ summary: 'Assign a task to a specific staff member' })
  async assignTask(@Param('id') id: string, @Body() dto: AssignStaffTaskDto) {
    return this.serviceRequestService.assignTask(id, dto.assignedStaffId, dto.assignedStaffName);
  }

  @Patch(':id/reassign')
  @ApiOperation({ summary: 'Reassign a task to another staff member' })
  async reassignTask(@Param('id') id: string, @Body() dto: AssignStaffTaskDto) {
    return this.serviceRequestService.assignTask(id, dto.assignedStaffId, dto.assignedStaffName);
  }
}
