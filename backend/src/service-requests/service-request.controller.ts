import { Controller, Post, Get, Param, Query, Body, HttpCode, HttpStatus } from '@nestjs/common';
import { ApiTags, ApiOperation, ApiResponse } from '@nestjs/swagger';
import { ServiceRequestService } from './service-request.service';
import { CreateServiceRequestDto } from './service-request.dto';

@ApiTags('Service Requests')
@Controller('api/v1/service-requests')
export class ServiceRequestController {
  constructor(private readonly serviceRequestService: ServiceRequestService) {}

  @Post()
  @HttpCode(HttpStatus.CREATED)
  @ApiOperation({ summary: 'Create a new service request from customer QR dining session' })
  @ApiResponse({ status: 201, description: 'Service request successfully created and assigned to staff.' })
  async createServiceRequest(@Body() dto: CreateServiceRequestDto) {
    return this.serviceRequestService.createServiceRequest(dto);
  }

  @Get('my')
  @ApiOperation({ summary: 'Get active service requests for current dining session' })
  async getMyRequests(@Query('sessionId') sessionId: string) {
    return this.serviceRequestService.getMyRequests(sessionId || 'session_123');
  }

  @Get(':id')
  @ApiOperation({ summary: 'Get service request status by ID' })
  async getRequestById(@Param('id') id: string) {
    return this.serviceRequestService.getRequestById(id);
  }
}
