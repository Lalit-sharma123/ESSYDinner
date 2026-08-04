import { Module } from '@nestjs/common';
import { CrowdHeatmapService } from './crowd-heatmap.service';
import { CrowdHeatmapController } from './crowd-heatmap.controller';

@Module({
  controllers: [CrowdHeatmapController],
  providers: [CrowdHeatmapService],
  exports: [CrowdHeatmapService],
})
export class AnalyticsModule {}
