import {
  WebSocketGateway,
  WebSocketServer,
  SubscribeMessage,
  OnGatewayConnection,
  OnGatewayDisconnect,
  MessageBody,
  ConnectedSocket,
} from '@nestjs/websockets';
import { Server, Socket } from 'socket.io';
import { Logger } from '@nestjs/common';

@WebSocketGateway({
  cors: {
    origin: '*',
  },
})
export class EventsGateway implements OnGatewayConnection, OnGatewayDisconnect {
  @WebSocketServer()
  server: Server;

  private logger = new Logger('EventsGateway');

  handleConnection(client: Socket) {
    this.logger.log(`Client connected: ${client.id}`);
  }

  handleDisconnect(client: Socket) {
    this.logger.log(`Client disconnected: ${client.id}`);
  }

  @SubscribeMessage('joinRoom')
  handleJoinRoom(@MessageBody() room: string, @ConnectedSocket() client: Socket) {
    client.join(room);
    this.logger.log(`Client ${client.id} joined room ${room}`);
    return { event: 'joinedRoom', room };
  }

  notifyWaitlistUpdate(restaurantId: string, payload: any) {
    this.server.to(`waitlist_${restaurantId}`).emit('waitlistUpdated', payload);
  }

  notifyTableOrderUpdate(sessionId: string, payload: any) {
    this.server.to(`session_${sessionId}`).emit('orderStatusChanged', payload);
  }

  notifyBillSplitUpdate(sessionId: string, payload: any) {
    this.server.to(`session_${sessionId}`).emit('billSplitUpdated', payload);
  }

  // Module 1: Live Table Occupancy Socket Events
  notifyTableUpdated(restaurantId: string, event: string, payload: any) {
    this.server.to(`restaurant_${restaurantId}`).emit(event, payload);
    this.server.emit('table.updated', { restaurantId, event, payload });
  }

  // Module 3: Live Parking Socket Events
  notifyParkingUpdated(restaurantId: string, event: string, payload: any) {
    this.server.to(`restaurant_${restaurantId}`).emit(event, payload);
    this.server.emit('parking.updated', { restaurantId, event, payload });
  }

  // Module 5: Operations Dashboard Socket Events
  notifyDashboardUpdated(restaurantId: string, payload: any) {
    this.server.to(`restaurant_${restaurantId}`).emit('dashboard.updated', payload);
    this.server.to(`restaurant_${restaurantId}`).emit('analytics.updated', payload);
  }

  // Module 6: VIP Recognition Socket Events
  notifyVipArrived(restaurantId: string, payload: any) {
    this.server.to(`restaurant_${restaurantId}`).emit('vip.arrived', payload);
    this.server.to(`restaurant_${restaurantId}`).emit('customer.checkedin', payload);
  }

  // Restaurant Service Request & Waiter Task Socket Events
  notifyServiceRequestCreated(restaurantId: string, payload: any) {
    this.server.to(`restaurant_${restaurantId}`).emit('service_request_created', payload);
    this.server.emit('service_request_created', payload);
  }

  notifyServiceRequestUpdated(restaurantId: string, payload: any) {
    this.server.to(`restaurant_${restaurantId}`).emit('service_request_updated', payload);
    this.server.emit('service_request_updated', payload);
  }

  notifyStaffTaskAssigned(restaurantId: string, payload: any) {
    this.server.to(`restaurant_${restaurantId}`).emit('staff_task_assigned', payload);
    this.server.emit('staff_task_assigned', payload);
  }

  notifyStaffTaskAccepted(restaurantId: string, payload: any) {
    this.server.to(`restaurant_${restaurantId}`).emit('staff_task_accepted', payload);
    this.server.emit('staff_task_accepted', payload);
  }

  notifyStaffTaskStarted(restaurantId: string, payload: any) {
    this.server.to(`restaurant_${restaurantId}`).emit('staff_task_started', payload);
    this.server.emit('staff_task_started', payload);
  }

  notifyStaffTaskCompleted(restaurantId: string, payload: any) {
    this.server.to(`restaurant_${restaurantId}`).emit('staff_task_completed', payload);
    this.server.emit('staff_task_completed', payload);
  }

  notifyStaffStatusChanged(restaurantId: string, payload: any) {
    this.server.to(`restaurant_${restaurantId}`).emit('staff_status_changed', payload);
    this.server.emit('staff_status_changed', payload);
  }
}
