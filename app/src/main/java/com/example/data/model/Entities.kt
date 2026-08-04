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
    val category: String, // BOOKING, OFFER, MEMBERSHIP, WALLET
    val timestampString: String,
    val isRead: Boolean = false
)
