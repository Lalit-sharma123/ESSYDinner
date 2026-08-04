import { Body, Controller, Delete, Get, Param, Patch, Post, UseGuards } from '@nestjs/common';
import { ApiTags, ApiOperation, ApiBearerAuth } from '@nestjs/swagger';
import { TablesService } from './tables.service';
import {
  CompleteCleaningDto,
  CreateFloorDto,
  CreateTableDto,
  MergeTablesDto,
  UpdateTablePositionDto,
  UpdateTableStatusDto,
} from './dto/tables.dto';
import { JwtAuthGuard } from '../common/guards/jwt-auth.guard';

@ApiTags('Live Table Occupancy (Digital Twin)')
@Controller('api/v1/tables')
@UseGuards(JwtAuthGuard)
@ApiBearerAuth()
export class TablesController {
  constructor(private readonly tablesService: TablesService) {}

  @Post('floors')
  @ApiOperation({ summary: 'Create a floor plan for a restaurant' })
  async createFloor(@Body() dto: CreateFloorDto) {
    return this.tablesService.createFloor(dto);
  }

  @Get('floors/restaurant/:restaurantId')
  @ApiOperation({ summary: 'Get all floors and digital twin tables for a restaurant' })
  async getRestaurantFloors(@Param('restaurantId') restaurantId: string) {
    return this.tablesService.getRestaurantFloors(restaurantId);
  }

  @Post()
  @ApiOperation({ summary: 'Add a new table to digital twin floor plan' })
  async createTable(@Body() dto: CreateTableDto) {
    return this.tablesService.createTable(dto);
  }

  @Get('restaurant/:restaurantId')
  @ApiOperation({ summary: 'Get all tables for a restaurant' })
  async getTablesByRestaurant(@Param('restaurantId') restaurantId: string) {
    return this.tablesService.getTablesByRestaurant(restaurantId);
  }

  @Patch(':id/status')
  @ApiOperation({ summary: 'Update table occupancy status (AVAILABLE, RESERVED, DINING, CLEANING, etc.)' })
  async updateTableStatus(@Param('id') tableId: string, @Body() dto: UpdateTableStatusDto) {
    return this.tablesService.updateTableStatus(tableId, dto);
  }

  @Patch(':id/position')
  @ApiOperation({ summary: 'Update table floor coordinates (Drag & Drop)' })
  async updateTablePosition(@Param('id') tableId: string, @Body() dto: UpdateTablePositionDto) {
    return this.tablesService.updateTablePosition(tableId, dto);
  }

  @Post('merge')
  @ApiOperation({ summary: 'Merge two adjacent tables for large party' })
  async mergeTables(@Body() dto: MergeTablesDto) {
    return this.tablesService.mergeTables(dto);
  }

  @Post(':id/unmerge')
  @ApiOperation({ summary: 'Unmerge a merged table back to individual capacity' })
  async unmergeTable(@Param('id') tableId: string) {
    return this.tablesService.unmergeTable(tableId);
  }

  @Post(':id/cleaned')
  @ApiOperation({ summary: 'Mark cleaning completed and return table to AVAILABLE status' })
  async completeCleaning(@Param('id') tableId: string, @Body() dto: CompleteCleaningDto) {
    return this.tablesService.completeCleaning(tableId, dto);
  }

  @Delete(':id')
  @ApiOperation({ summary: 'Delete a table' })
  async deleteTable(@Param('id') tableId: string) {
    return this.tablesService.deleteTable(tableId);
  }
}
