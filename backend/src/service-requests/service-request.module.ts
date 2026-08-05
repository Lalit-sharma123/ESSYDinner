import { Module } from '@nestjs/common';
import { ServiceRequestController } from './service-request.controller';
import { ServiceRequestService } from './service-request.service';
import { ServiceRequestRepository } from './service-request.repository';
import { PrismaModule } from '../prisma/prisma.module';
import { SocketsModule } from '../sockets/sockets.module';

@Module({
  imports: [PrismaModule, SocketsModule],
  controllers: [ServiceRequestController],
  providers: [ServiceRequestService, ServiceRequestRepository],
  exports: [ServiceRequestService, ServiceRequestRepository],
})
export class ServiceRequestModule {}
