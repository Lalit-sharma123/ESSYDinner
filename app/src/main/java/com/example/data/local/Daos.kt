package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BookingEntity
import com.example.data.model.CompanyEntity
import com.example.data.model.CorporateApprovalEntity
import com.example.data.model.CorporateDepartmentEntity
import com.example.data.model.CorporateEmployeeEntity
import com.example.data.model.CustomerCrmEntity
import com.example.data.model.DiningOrderItemEntity
import com.example.data.model.DiningSessionEntity
import com.example.data.model.FavoriteEntity
import com.example.data.model.MenuItemEntity
import com.example.data.model.MenuAddonEntity
import com.example.data.model.MenuMediaEntity
import com.example.data.model.MenuNutritionEntity
import com.example.data.model.MenuVariantEntity
import com.example.data.model.OfferEntity
import com.example.data.model.RestaurantEntity
import com.example.data.model.ReviewEntity
import com.example.data.model.ServiceRequestEntity
import com.example.data.model.UserNotificationEntity
import com.example.data.model.WaitlistEntity
import com.example.data.model.WalletTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RestaurantDao {
    @Query("SELECT * FROM restaurants")
    fun getAllRestaurants(): Flow<List<RestaurantEntity>>

    @Query("SELECT * FROM restaurants WHERE id = :id")
    fun getRestaurantById(id: String): Flow<RestaurantEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRestaurants(restaurants: List<RestaurantEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRestaurant(restaurant: RestaurantEntity)

    @Update
    suspend fun updateRestaurant(restaurant: RestaurantEntity)
}

@Dao
interface MenuItemDao {
    @Query("SELECT * FROM menu_items WHERE restaurantId = :restaurantId")
    fun getMenuItemsByRestaurant(restaurantId: String): Flow<List<MenuItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuItems(items: List<MenuItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuItem(item: MenuItemEntity)

    @Query("DELETE FROM menu_items WHERE id = :itemId")
    suspend fun deleteMenuItem(itemId: String)
}

@Dao
interface BookingDao {
    @Query("SELECT * FROM bookings ORDER BY createdAtTimestamp DESC")
    fun getAllBookings(): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE id = :id")
    fun getBookingById(id: String): Flow<BookingEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity)

    @Query("UPDATE bookings SET status = :status WHERE id = :id")
    suspend fun updateBookingStatus(id: String, status: String)

    @Query("UPDATE bookings SET bookingDate = :newDate, bookingTimeSlot = :newSlot, guestCount = :newGuests WHERE id = :id")
    suspend fun rescheduleBooking(id: String, newDate: String, newSlot: String, newGuests: Int)
}

@Dao
interface OfferDao {
    @Query("SELECT * FROM offers WHERE isActive = 1")
    fun getActiveOffers(): Flow<List<OfferEntity>>

    @Query("SELECT * FROM offers WHERE restaurantId = :restaurantId AND isActive = 1")
    fun getOffersForRestaurant(restaurantId: String): Flow<List<OfferEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOffers(offers: List<OfferEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOffer(offer: OfferEntity)
}

@Dao
interface WalletDao {
    @Query("SELECT * FROM user_wallet_transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<WalletTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: WalletTransactionEntity)
}

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews WHERE restaurantId = :restaurantId ORDER BY dateString DESC")
    fun getReviewsForRestaurant(restaurantId: String): Flow<List<ReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<ReviewEntity>)

    @Query("UPDATE reviews SET ownerReply = :reply WHERE id = :reviewId")
    suspend fun addOwnerReply(reviewId: String, reply: String)
}

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(fav: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE restaurantId = :restaurantId")
    suspend fun removeFavorite(restaurantId: String)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY id DESC")
    fun getAllNotifications(): Flow<List<UserNotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: UserNotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)
}

@Dao
interface WaitlistDao {
    @Query("SELECT * FROM waitlist_entries ORDER BY createdAtTimestamp DESC")
    fun getAllWaitlistEntries(): Flow<List<WaitlistEntity>>

    @Query("SELECT * FROM waitlist_entries WHERE restaurantId = :restaurantId AND status IN ('WAITING', 'NOTIFIED') ORDER BY queuePosition ASC")
    fun getActiveWaitlistForRestaurant(restaurantId: String): Flow<List<WaitlistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaitlistEntry(entry: WaitlistEntity)

    @Query("UPDATE waitlist_entries SET status = :status, notifiedAtTimestamp = :notifiedAt, expiresAtTimestamp = :expiresAt WHERE id = :id")
    suspend fun notifyWaitlistCustomer(id: String, status: String, notifiedAt: Long, expiresAt: Long)

    @Query("UPDATE waitlist_entries SET status = :status, acceptedAtTimestamp = :acceptedAt WHERE id = :id")
    suspend fun updateWaitlistStatus(id: String, status: String, acceptedAt: Long = System.currentTimeMillis())
}

@Dao
interface DiningSessionDao {
    @Query("SELECT * FROM dining_sessions WHERE status = 'ACTIVE' LIMIT 1")
    fun getActiveSession(): Flow<DiningSessionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: DiningSessionEntity)

    @Query("UPDATE dining_sessions SET status = 'CHECKED_OUT', isPaid = 1 WHERE id = :sessionId")
    suspend fun checkoutSession(sessionId: String)
}

@Dao
interface DiningOrderDao {
    @Query("SELECT * FROM dining_order_items WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getOrdersForSession(sessionId: String): Flow<List<DiningOrderItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItem(item: DiningOrderItemEntity)

    @Query("UPDATE dining_order_items SET status = :status WHERE id = :id")
    suspend fun updateOrderStatus(id: String, status: String)
}

@Dao
interface ServiceRequestDao {
    @Query("SELECT * FROM service_requests WHERE sessionId = :sessionId ORDER BY timestamp DESC")
    fun getRequestsForSession(sessionId: String): Flow<List<ServiceRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServiceRequest(request: ServiceRequestEntity)

    @Query("UPDATE service_requests SET status = 'FULFILLED' WHERE id = :id")
    suspend fun fulfillRequest(id: String)
}

@Dao
interface DigitalMenuDao {
    @Query("SELECT * FROM menu_media WHERE menuItemId = :menuItemId")
    fun getMediaForMenuItem(menuItemId: String): Flow<List<MenuMediaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedia(media: MenuMediaEntity)

    @Query("SELECT * FROM menu_nutrition WHERE menuItemId = :menuItemId")
    fun getNutritionForMenuItem(menuItemId: String): Flow<MenuNutritionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNutrition(nutrition: MenuNutritionEntity)

    @Query("SELECT * FROM menu_variants WHERE menuItemId = :menuItemId")
    fun getVariantsForMenuItem(menuItemId: String): Flow<List<MenuVariantEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVariant(variant: MenuVariantEntity)

    @Query("SELECT * FROM menu_addons WHERE menuItemId = :menuItemId")
    fun getAddonsForMenuItem(menuItemId: String): Flow<List<MenuAddonEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddon(addon: MenuAddonEntity)
}

@Dao
interface CrmDao {
    @Query("SELECT * FROM customer_crm ORDER BY totalSpend DESC")
    fun getAllCustomerProfiles(): Flow<List<CustomerCrmEntity>>

    @Query("SELECT * FROM customer_crm WHERE userId = :userId")
    fun getCustomerProfile(userId: String): Flow<CustomerCrmEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomerProfile(profile: CustomerCrmEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomerProfiles(profiles: List<CustomerCrmEntity>)

    @Query("UPDATE customer_crm SET specialNotes = :notes WHERE userId = :userId")
    suspend fun updateSpecialNotes(userId: String, notes: String)
}

@Dao
interface CorporateDao {
    @Query("SELECT * FROM corporate_companies LIMIT 1")
    fun getCompany(): Flow<CompanyEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompany(company: CompanyEntity)

    @Query("SELECT * FROM corporate_departments")
    fun getDepartments(): Flow<List<CorporateDepartmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDepartments(departments: List<CorporateDepartmentEntity>)

    @Query("SELECT * FROM corporate_employees")
    fun getEmployees(): Flow<List<CorporateEmployeeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployees(employees: List<CorporateEmployeeEntity>)

    @Query("SELECT * FROM corporate_approvals ORDER BY createdAt DESC")
    fun getApprovals(): Flow<List<CorporateApprovalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApproval(approval: CorporateApprovalEntity)

    @Query("UPDATE corporate_approvals SET status = :status WHERE id = :id")
    suspend fun updateApprovalStatus(id: String, status: String)

    @Query("UPDATE corporate_companies SET corporateWalletBalance = corporateWalletBalance + :amount WHERE id = :companyId")
    suspend fun addCorporateWalletBalance(companyId: String, amount: Double)
}

