import { Body, Controller, Get, Post, Put, UseGuards } from '@nestjs/common';
import { ApiTags, ApiOperation, ApiBearerAuth } from '@nestjs/swagger';
import { AllergensService } from './allergens.service';
import { CheckOrderAllergiesDto, ConfirmAllergyWarningDto, CreateIngredientDto, SetUserAllergiesDto } from './dto/allergens.dto';
import { JwtAuthGuard } from '../common/guards/jwt-auth.guard';
import { CurrentUser } from '../common/decorators/current-user.decorator';
import { User } from '@prisma/client';

@ApiTags('Allergy Protection Engine')
@Controller('api/v1/allergens')
@UseGuards(JwtAuthGuard)
@ApiBearerAuth()
export class AllergensController {
  constructor(private readonly allergensService: AllergensService) {}

  @Get('taxonomy')
  @ApiOperation({ summary: 'Get 14 standard global allergens taxonomy' })
  async getAllergenTaxonomy() {
    return this.allergensService.getAllergenTaxonomy();
  }

  @Get('profile')
  @ApiOperation({ summary: 'Get current user registered health allergies' })
  async getUserAllergies(@CurrentUser() user: User) {
    return this.allergensService.getUserAllergies(user.id);
  }

  @Put('profile')
  @ApiOperation({ summary: 'Set or update user allergy profiles' })
  async setUserAllergies(@CurrentUser() user: User, @Body() dto: SetUserAllergiesDto) {
    return this.allergensService.setUserAllergies(user.id, dto);
  }

  @Post('ingredient')
  @ApiOperation({ summary: 'Create or update ingredient allergen mapping' })
  async createIngredient(@Body() dto: CreateIngredientDto) {
    return this.allergensService.createIngredient(dto);
  }

  @Post('check-order')
  @ApiOperation({ summary: 'Validate proposed order items against user registered allergies before checkout' })
  async validateOrderAllergies(@CurrentUser() user: User, @Body() dto: CheckOrderAllergiesDto) {
    return this.allergensService.validateOrderAllergies(user.id, dto);
  }

  @Post('confirm-warning')
  @ApiOperation({ summary: 'Confirm and audit log user override acknowledgement for allergy warning' })
  async confirmAllergyWarning(@CurrentUser() user: User, @Body() dto: ConfirmAllergyWarningDto) {
    return this.allergensService.confirmAllergyWarning(user.id, dto);
  }
}
