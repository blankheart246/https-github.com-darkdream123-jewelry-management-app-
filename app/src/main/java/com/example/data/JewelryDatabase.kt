package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        Customer::class,
        InventoryItem::class,
        Transaction::class,
        Branch::class,
        Supplier::class,
        Artisan::class,
        Employee::class,
        BankAccount::class,
        BusinessAccount::class,
        User::class
    ],
    version = 4,
    exportSchema = false
)
abstract class JewelryDatabase : RoomDatabase() {

    abstract fun jewelryDao(): JewelryDao

    companion object {
        @Volatile
        private var INSTANCE: JewelryDatabase? = null

        fun getDatabase(context: Context): JewelryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    JewelryDatabase::class.java,
                    "jewelry_business_database"
                )
                .fallbackToDestructiveMigration() // Simple for local proto/offline app
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
