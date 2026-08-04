import { Module } from '@nestjs/common';
import { QrDiningService } from './qr-dining.service';
import { QrGeneratorService } from './qr-generator.service';
import { QrDiningController } from './qr-dining.controller';

@Module({
  controllers: [QrDiningController],
  providers: [QrDiningService, QrGeneratorService],
  exports: [QrDiningService, QrGeneratorService],
})
export class QrDiningModule {}
