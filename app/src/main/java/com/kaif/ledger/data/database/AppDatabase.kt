package com.kaif.ledger.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kaif.ledger.data.database.dao.CategoryDao
import com.kaif.ledger.data.database.dao.ExpenseDao
import com.kaif.ledger.data.database.dao.SettingsDao
import com.kaif.ledger.data.database.entity.AppSettingsEntity
import com.kaif.ledger.data.database.entity.CategoryEntity
import com.kaif.ledger.data.database.entity.ExpenseEntity
import com.kaif.ledger.utils.LocalDateTimeConverter

@Database(
    entities = [ExpenseEntity::class, CategoryEntity::class, AppSettingsEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(LocalDateTimeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun categoryDao(): CategoryDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "solo_ledger_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
