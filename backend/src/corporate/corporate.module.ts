import { Module } from '@nestjs/common';
import { CorporateController } from './corporate.controller';

@Module({
  controllers: [CorporateController],
})
export class CorporateModule {}
