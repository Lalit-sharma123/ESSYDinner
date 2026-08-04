import { PrismaClient, Role } from '@prisma/client';
import * as bcrypt from 'bcrypt';

const prisma = new PrismaClient();

async function main() {
  console.log('🌱 Seeding DineReserve Database...');

  // Create Users
  const passwordHash = await bcrypt.hash('Password123!', 10);
  const userCustomer = await prisma.user.upsert({
    where: { email: 'john.doe@example.com' },
    update: {},
    create: {
      id: 'usr_customer_1',
      email: 'john.doe@example.com',
      phone: '+1 555-0192',
      passwordHash,
      fullName: 'John Doe',
      role: Role.CUSTOMER,
      membershipTier: 'GOLD',
      rewardPoints: 750,
    },
  });

  const userOwner = await prisma.user.upsert({
    where: { email: 'owner@lumina.com' },
    update: {},
    create: {
      id: 'usr_owner_1',
      email: 'owner@lumina.com',
      phone: '+1 555-0188',
      passwordHash,
      fullName: 'Marco Rossi',
      role: Role.RESTAURANT_OWNER,
    },
  });

  // Create Restaurant
  const restaurant = await prisma.restaurant.upsert({
    where: { id: 'rest_1' },
    update: {},
    create: {
      id: 'rest_1',
      name: 'Lumina Grand Dining',
      tagline: 'Modern Italian Fine Dining & Wine Cellar',
      cuisineType: 'Italian • Fine Dining',
      rating: 4.9,
      reviewCount: 320,
      priceRange: '$$$$',
      address: '742 Evergreen Terrace, Downtown',
      city: 'Metropolis',
      area: 'Downtown Financial District',
      latitude: 37.7749,
      longitude: -122.4194,
      heroImageUrl: 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4',
      galleryImages: [
        'https://images.unsplash.com/photo-1550966871-3ed3cdb5ed0c',
        'https://images.unsplash.com/photo-1544025162-d76694265947',
      ],
      maxDiscountPercent: 25,
      hasTableBooking: true,
      hasWaitlist: true,
      hasQrDining: true,
    },
  });

  // Create Menu Items
  await prisma.menuItem.createMany({
    data: [
      {
        id: 'mi_1',
        restaurantId: restaurant.id,
        name: 'Truffle & Porcini Tagliatelle',
        description: 'Fresh egg pasta hand-rolled with shaved black winter truffles and creamy porcini butter.',
        category: 'Mains',
        price: 34.0,
        isVegetarian: true,
        calories: 580,
        proteinGrams: 18.5,
        carbsGrams: 64.0,
        fatGrams: 24.0,
        spicyLevel: 1,
        prepTimeMins: 20,
        allergensCsv: 'Dairy, Gluten',
      },
      {
        id: 'mi_2',
        restaurantId: restaurant.id,
        name: 'Dry-Aged Wagyu Ribeye 12oz',
        description: '45-day dry-aged A5 Wagyu served with roasted bone marrow & rosemary jus.',
        category: 'Mains',
        price: 85.0,
        isVegetarian: false,
        calories: 820,
        proteinGrams: 52.0,
        carbsGrams: 4.0,
        fatGrams: 62.0,
        spicyLevel: 0,
        prepTimeMins: 25,
        allergensCsv: 'None',
      },
    ],
    skipDuplicates: true,
  });

  // Seed CRM Profile
  await prisma.customerCrm.upsert({
    where: { userId: userCustomer.id },
    update: {},
    create: {
      userId: userCustomer.id,
      visitCount: 14,
      totalSpend: 1890.0,
      avgBill: 135.0,
      foodAllergies: 'Gluten, Shellfish',
      favoriteDishes: 'Truffle Tagliatelle, Barolo Reserve',
      preferredTable: 'Table 12 (Window Corner)',
      specialNotes: 'Prefers sparkling water with lemon upon seating.',
      membershipLevel: 'VIP Gold',
      segmentTag: 'High Spender',
    },
  });

  // Seed Company & Corporate Dining
  const company = await prisma.company.upsert({
    where: { id: 'comp_1' },
    update: {},
    create: {
      id: 'comp_1',
      companyName: 'Nexus Global Tech',
      corporateWalletBalance: 25000.0,
      monthlyBudget: 30000.0,
    },
  });

  await prisma.corporateDepartment.createMany({
    data: [
      { id: 'dept_1', companyId: company.id, departmentName: 'Engineering', allocatedBudget: 10000.0, spentAmount: 2400.0 },
      { id: 'dept_2', companyId: company.id, departmentName: 'Sales & Leadership', allocatedBudget: 15000.0, spentAmount: 8500.0 },
    ],
    skipDuplicates: true,
  });

  console.log('✅ Seeding completed successfully!');
}

main()
  .catch((e) => {
    console.error('❌ Seeding error:', e);
    process.exit(1);
  })
  .finally(async () => {
    await prisma.$disconnect();
  });
