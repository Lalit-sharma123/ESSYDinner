package com.example.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiResponse<T>(
    @field:Json(name = "status") val status: String,
    @field:Json(name = "message") val message: String?,
    @field:Json(name = "data") val data: T?
)

@JsonClass(generateAdapter = true)
data class RestaurantDto(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "name") val name: String,
    @field:Json(name = "cuisine") val cuisine: String,
    @field:Json(name = "rating") val rating: Float,
    @field:Json(name = "review_count") val reviewCount: Int,
    @field:Json(name = "avg_cost_for_two") val avgCostForTwo: Int,
    @field:Json(name = "area") val area: String,
    @field:Json(name = "address") val address: String,
    @field:Json(name = "image_url") val imageUrl: String,
    @field:Json(name = "max_discount_percent") val maxDiscountPercent: Int,
    @field:Json(name = "pure_veg") val pureVeg: Boolean,
    @field:Json(name = "kid_friendly") val kidFriendly: Boolean,
    @field:Json(name = "parking_available") val parkingAvailable: Boolean,
    @field:Json(name = "tags") val tags: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class MenuItemDto(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "restaurant_id") val restaurantId: String,
    @field:Json(name = "name") val name: String,
    @field:Json(name = "description") val description: String,
    @field:Json(name = "category") val category: String,
    @field:Json(name = "price") val price: Double,
    @field:Json(name = "is_veg") val isVeg: Boolean,
    @field:Json(name = "is_chef_special") val isChefSpecial: Boolean,
    @field:Json(name = "is_recommended") val isRecommended: Boolean
)

@JsonClass(generateAdapter = true)
data class OfferDto(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "restaurant_id") val restaurantId: String,
    @field:Json(name = "title") val title: String,
    @field:Json(name = "offer_type") val offerType: String,
    @field:Json(name = "discount_percent") val discountPercent: Int,
    @field:Json(name = "max_discount_amount") val maxDiscountAmount: Double,
    @field:Json(name = "min_bill_amount") val minBillAmount: Double,
    @field:Json(name = "valid_days") val validDays: String,
    @field:Json(name = "valid_times") val validTimes: String,
    @field:Json(name = "terms") val terms: String
)

@JsonClass(generateAdapter = true)
data class BookingRequestDto(
    @field:Json(name = "restaurant_id") val restaurantId: String,
    @field:Json(name = "guest_count") val guestCount: Int,
    @field:Json(name = "booking_date") val bookingDate: String,
    @field:Json(name = "booking_time_slot") val bookingTimeSlot: String,
    @field:Json(name = "occasion") val occasion: String,
    @field:Json(name = "seating_pref") val seatingPref: String,
    @field:Json(name = "special_requests") val specialRequests: String? = null,
    @field:Json(name = "offer_title") val offerTitle: String? = null
)

@JsonClass(generateAdapter = true)
data class BookingResponseDto(
    @field:Json(name = "booking_id") val bookingId: String,
    @field:Json(name = "booking_code") val bookingCode: String,
    @field:Json(name = "status") val status: String,
    @field:Json(name = "qr_code_payload") val qrCodePayload: String,
    @field:Json(name = "estimated_total") val estimatedTotal: Double
)
