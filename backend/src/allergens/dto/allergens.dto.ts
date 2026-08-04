import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';
import { IsArray, IsBoolean, IsNotEmpty, IsOptional, IsString } from 'class-validator';

export class SetUserAllergiesDto {
  @ApiProperty({
    example: ['PEANUTS', 'MILK', 'GLUTEN', 'TREE_NUTS'],
    description: 'List of allergen codes or names',
  })
  @IsArray()
  allergens: string[];
}

export class CheckOrderAllergiesDto {
  @ApiProperty({ example: ['menu_item_uuid_1', 'menu_item_uuid_2'] })
  @IsArray()
  menuItemIds: string[];
}

export class ConfirmAllergyWarningDto {
  @ApiProperty({ example: ['menu_item_uuid_1'] })
  @IsArray()
  menuItemIds: string[];

  @ApiProperty({ example: true })
  @IsBoolean()
  confirmedByUser: boolean;
}

export class CreateIngredientDto {
  @ApiProperty({ example: 'Garlic Butter Sauce' })
  @IsString()
  @IsNotEmpty()
  name: string;

  @ApiPropertyOptional({ example: 'Milk, Dairy' })
  @IsOptional()
  @IsString()
  allergensCsv?: string;
}
