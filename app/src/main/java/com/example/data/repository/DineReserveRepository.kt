package com.example.data.repository

import com.example.data.local.BookingDao
import com.example.data.local.FavoriteDao
import com.example.data.local.MenuItemDao
import com.example.data.local.NotificationDao
import com.example.data.local.OfferDao
import com.example.data.local.RestaurantDao
import com.example.data.local.ReviewDao
import com.example.data.local.WalletDao
import com.example.data.model.BookingEntity
import com.example.data.model.FavoriteEntity
import com.example.data.model.MenuItemEntity
import com.example.data.model.OfferEntity
import com.example.data.model.RestaurantEntity
import com.example.data.model.ReviewEntity
import com.example.data.model.UserNotificationEntity
import com.example.data.model.WalletTransactionEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class DineReserveRepository(
    private val restaurantDao: RestaurantDao,
    private val menuItemDao: MenuItemDao,
    private val bookingDao: BookingDao,
    private val offerDao: OfferDao,
    private val walletDao: WalletDao,
    private val reviewDao: ReviewDao,
    private val favoriteDao: FavoriteDao,
    private val notificationDao: NotificationDao
) {
    val allRestaurants: Flow<List<RestaurantEntity>> = restaurantDao.getAllRestaurants()
    val allBookings: Flow<List<BookingEntity>> = bookingDao.getAllBookings()
    val activeOffers: Flow<List<OfferEntity>> = offerDao.getActiveOffers()
    val walletTransactions: Flow<List<WalletTransactionEntity>> = walletDao.getAllTransactions()
    val favorites: Flow<List<FavoriteEntity>> = favoriteDao.getAllFavorites()
    val notifications: Flow<List<UserNotificationEntity>> = notificationDao.getAllNotifications()

    fun getRestaurantById(id: String): Flow<RestaurantEntity?> = restaurantDao.getRestaurantById(id)
    fun getMenuItemsByRestaurant(restaurantId: String): Flow<List<MenuItemEntity>> = menuItemDao.getMenuItemsByRestaurant(restaurantId)
    fun getOffersForRestaurant(restaurantId: String): Flow<List<OfferEntity>> = offerDao.getOffersForRestaurant(restaurantId)
    fun getReviewsForRestaurant(restaurantId: String): Flow<List<ReviewEntity>> = reviewDao.getReviewsForRestaurant(restaurantId)

    suspend fun createBooking(booking: BookingEntity) {
        bookingDao.insertBooking(booking)
        // Auto create confirmation notification
        notificationDao.insertNotification(
            UserNotificationEntity(
                id = UUID.randomUUID().toString(),
                title = "Booking Confirmed! ${booking.bookingCode}",
                message = "Your table for ${booking.guestCount} guests at ${booking.restaurantName} is confirmed for ${booking.bookingDate} at ${booking.bookingTimeSlot}.",
                category = "BOOKING",
                timestampString = "Just Now"
            )
        )
        // Add cashback reward
        walletDao.insertTransaction(
            WalletTransactionEntity(
                id = UUID.randomUUID().toString(),
                title = "Cashback for Booking ${booking.bookingCode}",
                amount = 15.0,
                type = "CASHBACK",
                dateString = "Today"
            )
        )
    }

    suspend fun cancelBooking(bookingId: String) {
        bookingDao.updateBookingStatus(bookingId, "CANCELLED")
    }

    suspend fun rescheduleBooking(bookingId: String, newDate: String, newSlot: String, newGuests: Int) {
        bookingDao.rescheduleBooking(bookingId, newDate, newSlot, newGuests)
    }

    suspend fun toggleFavorite(restaurantId: String, isFav: Boolean) {
        if (isFav) {
            favoriteDao.addFavorite(FavoriteEntity(restaurantId = restaurantId))
        } else {
            favoriteDao.removeFavorite(restaurantId)
        }
    }

    suspend fun addReview(review: ReviewEntity) {
        reviewDao.insertReview(review)
    }

    suspend fun addOwnerReply(reviewId: String, reply: String) {
        reviewDao.addOwnerReply(reviewId, reply)
    }

    suspend fun insertMenuItem(item: MenuItemEntity) {
        menuItemDao.insertMenuItem(item)
    }

    suspend fun deleteMenuItem(itemId: String) {
        menuItemDao.deleteMenuItem(itemId)
    }

    suspend fun createOffer(offer: OfferEntity) {
        offerDao.insertOffer(offer)
    }

    suspend fun updateRestaurant(restaurant: RestaurantEntity) {
        restaurantDao.updateRestaurant(restaurant)
    }

    suspend fun addWalletFunds(amount: Double) {
        walletDao.insertTransaction(
            WalletTransactionEntity(
                id = UUID.randomUUID().toString(),
                title = "Added to DineWallet",
                amount = amount,
                type = "CREDIT",
                dateString = "Today"
            )
        )
    }

    suspend fun markNotificationRead(id: String) {
        notificationDao.markAsRead(id)
    }

    suspend fun seedInitialDataIfEmpty() {
        // Pre-populate if restaurants are empty
        val seedRestaurants = listOf(
            RestaurantEntity(
                id = "rest_1",
                name = "Aura Fine Dining & Lounge",
                cuisine = "Modern European & Fusion",
                area = "Financial District",
                city = "New York",
                rating = 4.9f,
                reviewCount = 384,
                avgCostForTwo = 120,
                imageUrl = "img_hero_restaurant_1785489385239",
                tagsCsv = "Fine Dining,Rooftop,Outdoor,Pet Friendly",
                openHours = "11:30 AM - 11:30 PM",
                contactPhone = "+1 (212) 555-0198",
                address = "120 Wall Street, 24th Floor, New York, NY",
                lat = 40.7061,
                lng = -74.0089,
                isFeatured = true,
                isPopular = true,
                isTrending = true,
                pureVeg = false,
                kidFriendly = true,
                parkingAvailable = true,
                maxDiscountPercent = 50
            ),
            RestaurantEntity(
                id = "rest_2",
                name = "Rustic Oven Pizza & Craft Bar",
                cuisine = "Italian & Artisanal Pizza",
                area = "West Village",
                city = "New York",
                rating = 4.7f,
                reviewCount = 512,
                avgCostForTwo = 65,
                imageUrl = "img_dining_offer_1785489398911",
                tagsCsv = "Cafe,Outdoor,Kid Friendly,Parking",
                openHours = "12:00 PM - 10:30 PM",
                contactPhone = "+1 (212) 555-0241",
                address = "45 Bleecker St, New York, NY",
                lat = 40.7251,
                lng = -73.9942,
                isFeatured = true,
                isPopular = true,
                isTrending = false,
                pureVeg = false,
                kidFriendly = true,
                parkingAvailable = true,
                maxDiscountPercent = 35
            ),
            RestaurantEntity(
                id = "rest_3",
                name = "Saffron & Spice Pure Veg",
                cuisine = "Authentic Indian & Tandoori",
                area = "Midtown East",
                city = "New York",
                rating = 4.8f,
                reviewCount = 290,
                avgCostForTwo = 50,
                imageUrl = "img_hero_restaurant_1785489385239",
                tagsCsv = "Pure Veg,Fine Dining,Buffet,Kid Friendly",
                openHours = "11:00 AM - 10:00 PM",
                contactPhone = "+1 (212) 555-0377",
                address = "310 Lexington Ave, New York, NY",
                lat = 40.7484,
                lng = -73.9782,
                isFeatured = false,
                isPopular = true,
                isTrending = true,
                pureVeg = true,
                kidFriendly = true,
                parkingAvailable = false,
                maxDiscountPercent = 40
            ),
            RestaurantEntity(
                id = "rest_4",
                name = "The Glasshouse Rooftop & Sushi",
                cuisine = "Pan-Asian & Teppanyaki",
                area = "Hudson Yards",
                city = "New York",
                rating = 4.9f,
                reviewCount = 640,
                avgCostForTwo = 140,
                imageUrl = "img_dining_offer_1785489398911",
                tagsCsv = "Rooftop,Fine Dining,Outdoor,Pet Friendly",
                openHours = "05:00 PM - 01:00 AM",
                contactPhone = "+1 (212) 555-0812",
                address = "500 W 33rd St, 35th Floor, New York, NY",
                lat = 40.7538,
                lng = -74.0022,
                isFeatured = true,
                isPopular = false,
                isTrending = true,
                pureVeg = false,
                kidFriendly = false,
                parkingAvailable = true,
                maxDiscountPercent = 25
            ),
            RestaurantEntity(
                id = "rest_5",
                name = "The Velvet Garden Cafe & Bistro",
                cuisine = "Continental & French Pastries",
                area = "SoHo",
                city = "New York",
                rating = 4.6f,
                reviewCount = 180,
                avgCostForTwo = 45,
                imageUrl = "img_membership_gold_1785489413941",
                tagsCsv = "Cafe,Outdoor,Pet Friendly,Kid Friendly",
                openHours = "08:00 AM - 09:00 PM",
                contactPhone = "+1 (212) 555-0955",
                address = "88 Spring St, New York, NY",
                lat = 40.7233,
                lng = -73.9981,
                isFeatured = false,
                isPopular = true,
                isTrending = false,
                pureVeg = false,
                kidFriendly = true,
                parkingAvailable = false,
                maxDiscountPercent = 20
            )
        )

        val seedMenu = listOf(
            MenuItemEntity("m_1", "rest_1", "Truffle Mushroom Risotto", "Arborio rice cooked with wild forest mushrooms, black truffle oil, and aged parmesan crisp.", "Main Course", 28.0, true, isChefSpecial = true, isRecommended = true),
            MenuItemEntity("m_2", "rest_1", "Charred Wagyu Ribeye", "Grass-fed Wagyu beef served with smoked bone marrow butter and roasted baby carrots.", "Main Course", 48.0, false, isChefSpecial = true, isRecommended = true),
            MenuItemEntity("m_3", "rest_1", "Burrata & Heritage Tomato Tart", "Creamy burrata cheese on puff pastry with basil pesto drizzle.", "Starters", 18.0, true, isChefSpecial = false, isRecommended = true),
            MenuItemEntity("m_4", "rest_1", "Gold Leaf Molten Lava Cake", "70% dark Belgian chocolate molten cake topped with edible 24k gold leaf and vanilla bean gelato.", "Desserts", 22.0, true, isChefSpecial = true, isRecommended = true),
            MenuItemEntity("m_5", "rest_2", "Neapolitan Margherita Pizza", "San Marzano tomatoes, fresh buffalo mozzarella, fresh basil, extra virgin olive oil.", "Main Course", 22.0, true, isChefSpecial = false, isRecommended = true),
            MenuItemEntity("m_6", "rest_2", "Truffle Prosciutto Di Parma", "White truffle cream base, prosciutto, wild arugula, shaved parmesan.", "Main Course", 28.0, false, isChefSpecial = true, isRecommended = true),
            MenuItemEntity("m_7", "rest_3", "Paneer Tikka Butter Masala", "Cottage cheese marinated in clay oven spices cooked in rich velvety tomato gravy.", "Main Course", 20.0, true, isChefSpecial = true, isRecommended = true),
            MenuItemEntity("m_8", "rest_3", "Dal Makhani Royal", "Slow cooked black lentils with cream and organic butter served with garlic naan.", "Main Course", 18.0, true, isChefSpecial = false, isRecommended = true),
            MenuItemEntity("m_9", "rest_4", "Dragon Roll Sushi Platter", "Eel, avocado, tempura shrimp with tobiko caviar and spicy mayo.", "Starters", 32.0, false, isChefSpecial = true, isRecommended = true)
        )

        val seedOffers = listOf(
            OfferEntity("off_1", "rest_1", "FLAT 50% OFF on Food & Soft Beverages", "FLAT", 50, 60.0, 50.0, "Mon-Sun", "12:00 PM - 04:00 PM", "Valid on dining bill only. Excludes alcohol."),
            OfferEntity("off_2", "rest_2", "Buy 1 Get 1 Free Large Woodfired Pizza", "BOGO", 50, 30.0, 30.0, "Tue-Thu", "06:00 PM - 10:00 PM", "Buy any signature pizza and get second pizza of equal/lesser value free."),
            OfferEntity("off_3", "rest_3", "30% OFF Unlimited Grand Vegetarian Buffet", "BUFFET", 30, 25.0, 40.0, "Mon-Fri", "12:00 PM - 03:30 PM", "Valid per person for lunch buffet entry."),
            OfferEntity("off_4", "rest_4", "Free Signature Chef's Special Dessert", "FREE_DESSERT", 100, 20.0, 80.0, "Mon-Sun", "07:00 PM - 11:00 PM", "Minimum spend of $80 required.")
        )

        val seedBookings = listOf(
            BookingEntity(
                id = "b_101",
                bookingCode = "#DR-9812",
                restaurantId = "rest_1",
                restaurantName = "Aura Fine Dining & Lounge",
                restaurantAddress = "120 Wall Street, 24th Floor, New York, NY",
                guestCount = 2,
                bookingDate = "2026-08-02",
                bookingTimeSlot = "07:30 PM",
                occasion = "Anniversary",
                seatingPref = "Rooftop Window",
                specialRequests = "Candlelight setup on table please.",
                offerTitle = "FLAT 50% OFF on Total Bill",
                discountAmount = 60.0,
                totalEstimatedBill = 120.0,
                status = "UPCOMING",
                qrCodePayload = "DINERESERVE-B101-AURA-NY"
            ),
            BookingEntity(
                id = "b_102",
                bookingCode = "#DR-4421",
                restaurantId = "rest_2",
                restaurantName = "Rustic Oven Pizza & Craft Bar",
                restaurantAddress = "45 Bleecker St, New York, NY",
                guestCount = 4,
                bookingDate = "2026-07-28",
                bookingTimeSlot = "08:00 PM",
                occasion = "Casual Catchup",
                seatingPref = "Outdoor Patio",
                specialRequests = "Kid high chair needed.",
                offerTitle = "Buy 1 Get 1 Free Large Pizza",
                discountAmount = 25.0,
                totalEstimatedBill = 75.0,
                status = "COMPLETED",
                qrCodePayload = "DINERESERVE-B102-RUSTIC-NY"
            )
        )

        val seedTransactions = listOf(
            WalletTransactionEntity("tx_1", "Welcome Sign-up Bonus", 50.0, "CREDIT", "2026-07-20"),
            WalletTransactionEntity("tx_2", "Completed Booking Cashback", 15.0, "CASHBACK", "2026-07-28"),
            WalletTransactionEntity("tx_3", "Friend Referral Reward", 25.0, "CREDIT", "2026-07-29")
        )

        val seedReviews = listOf(
            ReviewEntity(
                id = "r_1",
                restaurantId = "rest_1",
                userName = "Sophia Martinez",
                userAvatar = "",
                rating = 5.0f,
                foodRating = 5.0f,
                serviceRating = 5.0f,
                ambienceRating = 5.0f,
                valueRating = 4.8f,
                comment = "Breathtaking rooftop view of New York harbor! The Wagyu ribeye melted in my mouth, and the 50% discount via DineReserve made it an incredible luxury value.",
                ownerReply = "Thank you Sophia! We look forward to hosting your next special occasion at Aura.",
                dateString = "2 days ago"
            ),
            ReviewEntity(
                id = "r_2",
                restaurantId = "rest_1",
                userName = "David Chen",
                userAvatar = "",
                rating = 4.8f,
                foodRating = 4.9f,
                serviceRating = 4.5f,
                ambienceRating = 5.0f,
                valueRating = 4.7f,
                comment = "Excellent wine list and attentive sommelier. The molten lava cake is a must-try!",
                ownerReply = "",
                dateString = "1 week ago"
            )
        )

        val seedNotifications = listOf(
            UserNotificationEntity("n_1", "Gold VIP Status Active!", "Congratulations! You've unlocked Gold VIP dining status with 2x reward points.", "MEMBERSHIP", "1 hr ago", false),
            UserNotificationEntity("n_2", "Weekend Offer Unlocked!", "Get up to 50% OFF at top rooftop lounge restaurants this weekend.", "OFFER", "3 hrs ago", false)
        )

        restaurantDao.insertRestaurants(seedRestaurants)
        menuItemDao.insertMenuItems(seedMenu)
        offerDao.insertOffers(seedOffers)
        seedBookings.forEach { bookingDao.insertBooking(it) }
        seedTransactions.forEach { walletDao.insertTransaction(it) }
        reviewDao.insertReviews(seedReviews)
        favoriteDao.addFavorite(FavoriteEntity("rest_1"))
        favoriteDao.addFavorite(FavoriteEntity("rest_4"))
        seedNotifications.forEach { notificationDao.insertNotification(it) }
    }
}
