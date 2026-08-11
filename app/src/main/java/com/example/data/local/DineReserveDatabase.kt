package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
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

import com.example.data.model.StaffTaskEntity

@Database(
    entities = [
        RestaurantEntity::class,
        MenuItemEntity::class,
        BookingEntity::class,
        OfferEntity::class,
        WalletTransactionEntity::class,
        ReviewEntity::class,
        FavoriteEntity::class,
        UserNotificationEntity::class,
        WaitlistEntity::class,
        DiningSessionEntity::class,
        DiningOrderItemEntity::class,
        ServiceRequestEntity::class,
        StaffTaskEntity::class,
        MenuMediaEntity::class,
        MenuNutritionEntity::class,
        MenuVariantEntity::class,
        MenuAddonEntity::class,
        CustomerCrmEntity::class,
        CompanyEntity::class,
        CorporateDepartmentEntity::class,
        CorporateEmployeeEntity::class,
        CorporateApprovalEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class DineReserveDatabase : RoomDatabase() {
    abstract fun restaurantDao(): RestaurantDao
    abstract fun menuItemDao(): MenuItemDao
    abstract fun bookingDao(): BookingDao
    abstract fun offerDao(): OfferDao
    abstract fun walletDao(): WalletDao
    abstract fun reviewDao(): ReviewDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun notificationDao(): NotificationDao
    abstract fun waitlistDao(): WaitlistDao
    abstract fun diningSessionDao(): DiningSessionDao
    abstract fun diningOrderDao(): DiningOrderDao
    abstract fun serviceRequestDao(): ServiceRequestDao
    abstract fun staffTaskDao(): StaffTaskDao
    abstract fun digitalMenuDao(): DigitalMenuDao
    abstract fun crmDao(): CrmDao
    abstract fun corporateDao(): CorporateDao

    companion object {
        @Volatile
        private var INSTANCE: DineReserveDatabase? = null

        fun getDatabase(context: Context): DineReserveDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DineReserveDatabase::class.java,
                    "dinereserve_database"
                )
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

