package com.solo.ledger.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.solo.ledger.data.dao.BudgetTemplateDao
import com.solo.ledger.data.dao.CategoryDao
import com.solo.ledger.data.dao.ExpenseDao
import com.solo.ledger.data.dao.SavingsGoalDao
import com.solo.ledger.data.model.BudgetTemplate
import com.solo.ledger.data.model.Category
import com.solo.ledger.data.model.Expense
import com.solo.ledger.data.model.SavingsGoal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Expense::class,
        Category::class,
        SavingsGoal::class,
        BudgetTemplate::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SoloLedgerDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao
    abstract fun categoryDao(): CategoryDao
    abstract fun savingsGoalDao(): SavingsGoalDao
    abstract fun budgetTemplateDao(): BudgetTemplateDao

    companion object {
        @Volatile
        private var INSTANCE: SoloLedgerDatabase? = null

        fun getDatabase(context: Context): SoloLedgerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SoloLedgerDatabase::class.java,
                    "solo_ledger.db"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDefaultCategories(database.categoryDao())
                    populateDefaultTemplates(database.budgetTemplateDao())
                }
            }
        }

        private suspend fun populateDefaultCategories(dao: CategoryDao) {
            val defaults = listOf(
                Category(name = "Food", icon = "restaurant", color = 0xFFE57373, isDefault = true),
                Category(name = "Travel", icon = "directions_car", color = 0xFF81C784, isDefault = true),
                Category(name = "Shopping", icon = "shopping_bag", color = 0xFF64B5F6, isDefault = true),
                Category(name = "Bills", icon = "receipt_long", color = 0xFFFFB74D, isDefault = true),
                Category(name = "Education", icon = "school", color = 0xFF9575CD, isDefault = true),
                Category(name = "Entertainment", icon = "movie", color = 0xFF4DD0E1, isDefault = true),
                Category(name = "Groceries", icon = "local_grocery_store", color = 0xFFA5D6A7, isDefault = true),
                Category(name = "Subscription", icon = "subscriptions", color = 0xFFFF8A65, isDefault = true),
                Category(name = "Other", icon = "more_horiz", color = 0xFF90A4AE, isDefault = true)
            )
            dao.insertAll(defaults)
        }

        private suspend fun populateDefaultTemplates(dao: BudgetTemplateDao) {
            val templates = listOf(
                BudgetTemplate(
                    name = "Student Budget",
                    monthlyBudget = 6000.0,
                    description = "Ideal for college students with limited allowance",
                    isDefault = true
                ),
                BudgetTemplate(
                    name = "Hostel Budget",
                    monthlyBudget = 8000.0,
                    description = "For hostel residents managing food and utilities",
                    isDefault = true
                ),
                BudgetTemplate(
                    name = "Saver Budget",
                    monthlyBudget = 15000.0,
                    description = "Focused on maximizing savings from income",
                    isDefault = true
                ),
                BudgetTemplate(
                    name = "Minimal Budget",
                    monthlyBudget = 3000.0,
                    description = "Bare essentials only, extreme frugality",
                    isDefault = true
                )
            )
            dao.insertAll(templates)
        }
    }
}
