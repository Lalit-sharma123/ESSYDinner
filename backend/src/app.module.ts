import { Module } from '@nestjs/common';
import { PrismaModule } from './prisma/prisma.module';
import { RedisModule } from './redis/redis.module';
import { AuthModule } from './auth/auth.module';
import { RestaurantsModule } from './restaurants/restaurants.module';
import { BookingsModule } from './bookings/bookings.module';
import { WaitlistModule } from './waitlist/waitlist.module';
import { QrDiningModule } from './qr-dining/qr-dining.module';
import { CrmModule } from './crm/crm.module';
import { CorporateModule } from './corporate/corporate.module';
import { SocketsModule } from './sockets/sockets.module';
import { TablesModule } from './tables/tables.module';
import { AnalyticsModule } from './analytics/analytics.module';
import { ParkingModule } from './parking/parking.module';
import { AllergensModule } from './allergens/allergens.module';
import { OperationsModule } from './operations/operations.module';
import { VipModule } from './vip/vip.module';
import { ServiceRequestModule } from './service-requests/service-request.module';
import { StaffTaskModule } from './staff-tasks/staff-task.module';

@Module({
  imports: [
    PrismaModule,
    RedisModule,
    AuthModule,
    RestaurantsModule,
    BookingsModule,
    WaitlistModule,
    QrDiningModule,
    CrmModule,
    CorporateModule,
    SocketsModule,
    TablesModule,
    AnalyticsModule,
    ParkingModule,
    AllergensModule,
    OperationsModule,
    VipModule,
    ServiceRequestModule,
    StaffTaskModule,
  ],
})
export class AppModule {}
