package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.BookingEntity
import com.example.data.model.FavoriteEntity
import com.example.data.model.MenuItemEntity
import com.example.data.model.OfferEntity
import com.example.data.model.RestaurantEntity
import com.example.data.model.ReviewEntity
import com.example.data.model.UserNotificationEntity
import com.example.data.model.WalletTransactionEntity

@Database(
    entities = [
        RestaurantEntity::class,
        MenuItemEntity::class,
        BookingEntity::class,
        OfferEntity::class,
        WalletTransactionEntity::class,
        ReviewEntity::class,
        FavoriteEntity::class,
        UserNotificationEntity::class
    ],
    version = 1,
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
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
