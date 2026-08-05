import { Module } from '@nestjs/common';
import { StaffTaskController } from './staff-task.controller';
import { ServiceRequestModule } from '../service-requests/service-request.module';

@Module({
  imports: [ServiceRequestModule],
  controllers: [StaffTaskController],
})
export class StaffTaskModule {}
