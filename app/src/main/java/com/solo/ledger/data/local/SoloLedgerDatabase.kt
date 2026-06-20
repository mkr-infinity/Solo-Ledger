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
import com.solo.ledger.data.local.entity.SavingsGoalEntity

@Database(
    entities = [
        ExpenseEntity::class,
        CategoryEntity::class,
        SavingsGoalEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class SoloLedgerDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun categoryDao(): CategoryDao
    abstract fun goalDao(): GoalDao

    companion object {
        @Volatile
        private var instance: SoloLedgerDatabase? = null

        fun create(context: Context): SoloLedgerDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                SoloLedgerDatabase::class.java,
                "solo_ledger.db",
            ).build().also { instance = it }
        }
    }
}
