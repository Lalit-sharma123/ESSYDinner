import { Injectable, NotFoundException } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { CheckOrderAllergiesDto, ConfirmAllergyWarningDto, CreateIngredientDto, SetUserAllergiesDto } from './dto/allergens.dto';

export const STANDARD_ALLERGENS = [
  { code: 'PEANUTS', name: 'Peanuts', description: 'Peanuts and peanut derivatives' },
  { code: 'MILK', name: 'Milk / Dairy', description: 'Lactose, butter, cheese, cream, milk' },
  { code: 'EGG', name: 'Egg', description: 'Egg whites, egg yolks, mayonnaise' },
  { code: 'FISH', name: 'Fish', description: 'Fresh, saltwater fish, fish sauces' },
  { code: 'SHELLFISH', name: 'Shellfish / Crustaceans', description: 'Shrimp, crab, lobster, prawns' },
  { code: 'SOY', name: 'Soy / Soybeans', description: 'Soy sauce, tofu, edamame, lecithin' },
  { code: 'GLUTEN', name: 'Gluten / Wheat', description: 'Wheat flour, barley, rye, bread, pasta' },
  { code: 'SESAME', name: 'Sesame', description: 'Sesame seeds, tahini, sesame oil' },
  { code: 'TREE_NUTS', name: 'Tree Nuts', description: 'Almonds, walnuts, cashews, pistachios, hazelnuts' },
  { code: 'MUSTARD', name: 'Mustard', description: 'Mustard seeds, mustard powder, sauces' },
  { code: 'CELERY', name: 'Celery', description: 'Celery stalks, seeds, celery salt' },
  { code: 'SULPHITES', name: 'Sulphites', description: 'Preservatives found in wine, dried fruit' },
  { code: 'LUPIN', name: 'Lupin', description: 'Lupin flour, seeds' },
  { code: 'MOLLUSCS', name: 'Molluscs', description: 'Clams, mussels, oysters, squid, octopus' },
];

@Injectable()
export class AllergensService {
  constructor(private prisma: PrismaService) {}

  async getAllergenTaxonomy() {
    return STANDARD_ALLERGENS;
  }

  async getUserAllergies(userId: string) {
    return this.prisma.userAllergy.findMany({
      where: { userId },
    });
  }

  async setUserAllergies(userId: string, dto: SetUserAllergiesDto) {
    // Delete previous user allergy profiles
    await this.prisma.userAllergy.deleteMany({
      where: { userId },
    });

    // Create new allergy entries
    const entries = dto.allergens.map((alg) => ({
      userId,
      allergenName: alg,
      severity: 'HIGH',
    }));

    await this.prisma.userAllergy.createMany({
      data: entries,
    });

    return this.getUserAllergies(userId);
  }

  async createIngredient(dto: CreateIngredientDto) {
    return this.prisma.ingredient.upsert({
      where: { name: dto.name },
      create: {
        name: dto.name,
        allergensCsv: dto.allergensCsv || '',
      },
      update: {
        allergensCsv: dto.allergensCsv || '',
      },
    });
  }

  async validateOrderAllergies(userId: string, dto: CheckOrderAllergiesDto) {
    const userAllergies = await this.prisma.userAllergy.findMany({
      where: { userId },
    });

    if (userAllergies.length === 0) {
      return {
        safe: true,
        warnings: [],
        conflictingItems: [],
      };
    }

    const userAllergenNames = userAllergies.map((a) => a.allergenName.toUpperCase());

    const menuItems = await this.prisma.menuItem.findMany({
      where: { id: { in: dto.menuItemIds } },
      include: {
        ingredients: {
          include: { ingredient: true },
        },
      },
    });

    const warnings: { menuItemId: string; itemName: string; matchedAllergens: string[] }[] = [];

    for (const item of menuItems) {
      const itemAllergensStr = item.allergensCsv || '';
      const ingredientAllergensStr = item.ingredients
        .map((i) => i.ingredient.allergensCsv)
        .join(', ');

      const combinedAllergenStr = `${itemAllergensStr}, ${ingredientAllergensStr}`.toUpperCase();

      const matched: string[] = [];
      for (const alg of userAllergenNames) {
        if (combinedAllergenStr.includes(alg)) {
          matched.push(alg);
        }
      }

      if (matched.length > 0) {
        warnings.push({
          menuItemId: item.id,
          itemName: item.name,
          matchedAllergens: Array.from(new Set(matched)),
        });
      }
    }

    const hasConflict = warnings.length > 0;

    return {
      safe: !hasConflict,
      hasConflict,
      userAllergies: userAllergenNames,
      warnings,
      conflictingItemsCount: warnings.length,
      warningMessage: hasConflict
        ? `ALLERGY WARNING: ${warnings.length} item(s) in your order match your registered health allergy profiles!`
        : 'All items are safe based on your registered allergy profile.',
    };
  }

  async confirmAllergyWarning(userId: string, dto: ConfirmAllergyWarningDto) {
    const logEntries = dto.menuItemIds.map((itemId) => ({
      userId,
      menuItemId: itemId,
      allergensDetected: 'USER_ACKNOWLEDGED_ALLERGY_MATCH',
      confirmedByUser: dto.confirmedByUser,
    }));

    await this.prisma.allergyWarningLog.createMany({
      data: logEntries,
    });

    return {
      success: true,
      loggedCount: logEntries.length,
      timestamp: new Date(),
    };
  }
}
