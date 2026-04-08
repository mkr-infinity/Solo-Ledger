package com.mkr.soloLedger.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mkr.soloLedger.data.dao.*
import com.mkr.soloLedger.data.entities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Expense::class, Category::class, Budget::class, UserProfile::class, RecurringExpense::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SoloLedgerDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun recurringExpenseDao(): RecurringExpenseDao

    companion object {
        @Volatile
        private var INSTANCE: SoloLedgerDatabase? = null

        fun getDatabase(context: Context): SoloLedgerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SoloLedgerDatabase::class.java,
                    "solo_ledger_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDefaultCategories(database.categoryDao())
                    populateDefaultProfile(database.userProfileDao())
                }
            }
        }

        suspend fun populateDefaultCategories(categoryDao: CategoryDao) {
            val defaultCategories = listOf(
                Category(name = "Food", iconName = "ic_food", isDefault = true, color = "#FF6B35"),
                Category(name = "Travel", iconName = "ic_travel", isDefault = true, color = "#4ECDC4"),
                Category(name = "Shopping", iconName = "ic_shopping", isDefault = true, color = "#45B7D1"),
                Category(name = "Entertainment", iconName = "ic_entertainment", isDefault = true, color = "#96CEB4"),
                Category(name = "Bills", iconName = "ic_bills", isDefault = true, color = "#FFEAA7"),
                Category(name = "Education", iconName = "ic_education", isDefault = true, color = "#DDA0DD"),
                Category(name = "Groceries", iconName = "ic_groceries", isDefault = true, color = "#98FB98"),
                Category(name = "Subscriptions", iconName = "ic_subscriptions", isDefault = true, color = "#FFA07A"),
                Category(name = "Other", iconName = "ic_other", isDefault = true, color = "#D3D3D3")
            )
            defaultCategories.forEach { categoryDao.insert(it) }
        }

        suspend fun populateDefaultProfile(userProfileDao: UserProfileDao) {
            userProfileDao.insert(UserProfile())
        }
    }
}
