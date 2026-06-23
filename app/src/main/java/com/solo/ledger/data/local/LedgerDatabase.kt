package com.solo.ledger.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.solo.ledger.data.local.dao.CategoryDao
import com.solo.ledger.data.local.dao.ExpenseDao
import com.solo.ledger.data.local.dao.GoalDao
import com.solo.ledger.data.local.entity.CategoryEntity
import com.solo.ledger.data.local.entity.ExpenseEntity
import com.solo.ledger.data.local.entity.GoalEntity

@Database(
    entities = [ExpenseEntity::class, CategoryEntity::class, GoalEntity::class],
    version = 1,
    exportSchema = false
)
abstract class LedgerDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun categoryDao(): CategoryDao
    abstract fun goalDao(): GoalDao

    companion object {
        @Volatile private var INSTANCE: LedgerDatabase? = null
        fun get(context: Context): LedgerDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    LedgerDatabase::class.java,
                    "solo_ledger.db"
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
    }
}
