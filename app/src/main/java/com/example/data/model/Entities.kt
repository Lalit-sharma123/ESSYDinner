package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurants")
data class RestaurantEntity(
    @PrimaryKey val id: String,
    val name: String,
    val cuisine: String,
    val area: String,
    val city: String,
    val rating: Float,
    val reviewCount: Int,
    val avgCostForTwo: Int,
    val imageUrl: String,
    val tagsCsv: String, // e.g. "Fine Dining,Rooftop,Outdoor,Pet Friendly"
    val openHours: String,
    val contactPhone: String,
    val address: String,
    val lat: Double,
    val lng: Double,
    val isFeatured: Boolean = false,
    val isPopular: Boolean = false,
    val isTrending: Boolean = false,
    val pureVeg: Boolean = false,
    val kidFriendly: Boolean = true,
    val parkingAvailable: Boolean = true,
    val maxDiscountPercent: Int = 30,
    val availableSlotsCsv: String = "12:00 PM,12:30 PM,01:00 PM,07:00 PM,07:30 PM,08:00 PM,08:30 PM,09:00 PM",
    val amenitiesCsv: String = "WiFi,Valet Parking,AC,Outdoor Seating,Live Music,Bar"
)

@Entity(tableName = "menu_items")
data class MenuItemEntity(
    @PrimaryKey val id: String,
    val restaurantId: String,
    val name: String,
    val description: String,
    val category: String, // e.g. Starters, Main Course, Desserts, Beverages
    val price: Double,
    val isVeg: Boolean,
    val isChefSpecial: Boolean = false,
    val isRecommended: Boolean = false,
    val isAvailable: Boolean = true,
    val imageUrl: String = "",
    val addonsCsv: String = "Extra Cheese,Spicy Sauce,Garlic Bread"
)

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey val id: String,
    val bookingCode: String, // e.g. #DR-8921
    val restaurantId: String,
    val restaurantName: String,
    val restaurantAddress: String,
    val guestCount: Int,
    val bookingDate: String, // e.g. "2026-08-01"
    val bookingTimeSlot: String, // e.g. "07:30 PM"
    val occasion: String, // e.g. "Birthday", "Date Night", "Casual"
    val seatingPref: String, // e.g. "Rooftop", "Window Seat"
    val specialRequests: String,
    val offerTitle: String,
    val discountAmount: Double,
    val totalEstimatedBill: Double,
    val status: String, // UPCOMING, COMPLETED, CANCELLED, NO_SHOW
    val qrCodePayload: String,
    val createdAtTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "offers")
data class OfferEntity(
    @PrimaryKey val id: String,
    val restaurantId: String,
    val title: String, // e.g. "FLAT 50% OFF on Total Bill"
    val offerType: String, // FLAT, PERCENTAGE, BOGO, FREE_DESSERT, BUFFET, HAPPY_HOURS
    val discountPercent: Int,
    val maxDiscountAmount: Double,
    val minBillAmount: Double,
    val validDays: String, // e.g. "Mon-Sun"
    val validTimes: String, // e.g. "12:00 PM - 11:00 PM"
    val terms: String,
    val isActive: Boolean = true
)

@Entity(tableName = "user_wallet_transactions")
data class WalletTransactionEntity(
    @PrimaryKey val id: String,
    val title: String,
    val amount: Double,
    val type: String, // CREDIT, CASHBACK, REWARD_POINTS, DEBIT
    val dateString: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey val id: String,
    val restaurantId: String,
    val userName: String,
    val userAvatar: String,
    val rating: Float,
    val foodRating: Float,
    val serviceRating: Float,
    val ambienceRating: Float,
    val valueRating: Float,
    val comment: String,
    val ownerReply: String = "",
    val dateString: String
)

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val restaurantId: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class UserNotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val message: String,
    val category: String, // BOOKING, OFFER, MEMBERSHIP, WALLET, WAITLIST, QR_DINING
    val timestampString: String,
    val isRead: Boolean = false
)

// --- MODULE 1: SMART WAITLIST ENTITIES ---
@Entity(tableName = "waitlist_entries")
data class WaitlistEntity(
    @PrimaryKey val id: String,
    val restaurantId: String,
    val restaurantName: String,
    val userId: String,
    val userName: String,
    val guestCount: Int,
    val requestedDate: String,
    val requestedTime: String,
    val priorityLevel: String = "STANDARD", // STANDARD, VIP, GOLD
    val status: String = "WAITING", // WAITING, NOTIFIED, ACCEPTED, EXPIRED, CANCELLED
    val expiresAtTimestamp: Long = 0L,
    val notifiedAtTimestamp: Long = 0L,
    val acceptedAtTimestamp: Long = 0L,
    val createdAtTimestamp: Long = System.currentTimeMillis(),
    val estimatedWaitMinutes: Int = 15,
    val queuePosition: Int = 1
)

// --- MODULE 2: QR DINING EXPERIENCE ENTITIES ---
@Entity(tableName = "dining_sessions")
data class DiningSessionEntity(
    @PrimaryKey val id: String,
    val bookingId: String,
    val restaurantId: String,
    val restaurantName: String,
    val tableNumber: String,
    val status: String = "ACTIVE", // ACTIVE, CHECKED_OUT
    val startTimeTimestamp: Long = System.currentTimeMillis(),
    val totalBillAmount: Double = 0.0,
    val isPaid: Boolean = false
)

@Entity(tableName = "dining_order_items")
data class DiningOrderItemEntity(
    @PrimaryKey val id: String,
    val sessionId: String,
    val menuItemId: String,
    val itemName: String,
    val price: Double,
    val quantity: Int,
    val status: String = "ACCEPTED", // ACCEPTED, PREPARING, READY, SERVED
    val specialNotes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "service_requests")
data class ServiceRequestEntity(
    @PrimaryKey val id: String,
    val sessionId: String,
    val tableNumber: String,
    val requestType: String, // WAITER, WATER, SOFT_DRINK, EXTRA_PLATE, EXTRA_SPOON, NAPKIN, TISSUE, ICE, SAUCE, CALL_WAITER, REQUEST_BILL, SPECIAL_REQUEST, MENU_ITEM
    val status: String = "PENDING", // PENDING, ASSIGNED, ACCEPTED, IN_PROGRESS, READY_TO_SERVE, COMPLETED, CANCELLED
    val priority: String = "NORMAL",
    val note: String = "",
    val itemsSummary: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "staff_tasks")
data class StaffTaskEntity(
    @PrimaryKey val id: String,
    val serviceRequestId: String,
    val restaurantId: String = "rest_1",
    val tableNumber: String = "T-01",
    val customerName: String = "Guest",
    val bookingId: String = "B-101",
    val diningSessionId: String = "session_123",
    val requestType: String = "WATER",
    val requestedItemsSummary: String = "Water x1",
    val quantity: Int = 1,
    val priority: String = "NORMAL",
    val taskStatus: String = "PENDING", // PENDING, ASSIGNED, ACCEPTED, IN_PROGRESS, READY_TO_SERVE, COMPLETED, CANCELLED
    val assignedStaffId: String = "staff_1",
    val assignedStaffName: String = "Alex Waiter",
    val createdAtTimestamp: Long = System.currentTimeMillis(),
    val acceptedAtTimestamp: Long = 0L,
    val startedAtTimestamp: Long = 0L,
    val completedAtTimestamp: Long = 0L,
    val estimatedMinutes: Int = 5
)

data class ServiceRequestItemData(
    val menuItemId: String = "",
    val itemName: String,
    val quantity: Int = 1,
    val price: Double = 0.0,
    val specialInstructions: String = ""
)

// --- MODULE 3: DIGITAL MENU BUILDER EXTENSIONS ---
@Entity(tableName = "menu_media")
data class MenuMediaEntity(
    @PrimaryKey val id: String,
    val menuItemId: String,
    val mediaType: String, // IMAGE, VIDEO, GIF, 360_VIEW
    val mediaUrl: String,
    val title: String = ""
)

@Entity(tableName = "menu_nutrition")
data class MenuNutritionEntity(
    @PrimaryKey val menuItemId: String,
    val calories: Int,
    val proteinGrams: Double,
    val carbsGrams: Double,
    val fatGrams: Double,
    val spicyLevel: Int = 0, // 0 to 3 chili peppers
    val prepTimeMinutes: Int = 15,
    val allergenTagsCsv: String = "Gluten,Dairy,Nuts"
)

@Entity(tableName = "menu_variants")
data class MenuVariantEntity(
    @PrimaryKey val id: String,
    val menuItemId: String,
    val variantName: String, // e.g. "Small", "Large", "Single Shot"
    val priceAdjustment: Double
)

@Entity(tableName = "menu_addons")
data class MenuAddonEntity(
    @PrimaryKey val id: String,
    val menuItemId: String,
    val addonName: String, // e.g. "Extra Cheese", "Truffle Dip"
    val price: Double
)

// --- MODULE 4: RESTAURANT CRM ENTITIES ---
@Entity(tableName = "customer_crm")
data class CustomerCrmEntity(
    @PrimaryKey val userId: String,
    val name: String,
    val phone: String,
    val email: String,
    val favoriteCuisine: String,
    val favoriteDishes: String,
    val foodAllergies: String,
    val preferredTable: String,
    val preferredWaiter: String,
    val visitCount: Int,
    val totalSpend: Double,
    val avgBill: Double,
    val lastVisitDate: String,
    val membershipLevel: String,
    val birthday: String,
    val anniversary: String,
    val specialNotes: String,
    val segmentTag: String = "VIP" // VIP, High Spender, Frequent Visitor, Inactive, Corporate, Birthday
)

// --- MODULE 5: CORPORATE DINING ENTITIES ---
@Entity(tableName = "corporate_companies")
data class CompanyEntity(
    @PrimaryKey val id: String,
    val companyName: String,
    val corporateWalletBalance: Double,
    val monthlyBudget: Double,
    val adminEmail: String
)

@Entity(tableName = "corporate_departments")
data class CorporateDepartmentEntity(
    @PrimaryKey val id: String,
    val companyId: String,
    val departmentName: String,
    val allocatedBudget: Double,
    val spentAmount: Double = 0.0
)

@Entity(tableName = "corporate_employees")
data class CorporateEmployeeEntity(
    @PrimaryKey val id: String,
    val companyId: String,
    val departmentId: String,
    val employeeName: String,
    val employeeEmail: String,
    val monthlyLimit: Double,
    val spentThisMonth: Double = 0.0,
    val isManager: Boolean = false
)

@Entity(tableName = "corporate_approvals")
data class CorporateApprovalEntity(
    @PrimaryKey val id: String,
    val companyId: String,
    val employeeName: String,
    val amount: Double,
    val restaurantName: String,
    val bookingDate: String,
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED
    val createdAt: Long = System.currentTimeMillis()
)

