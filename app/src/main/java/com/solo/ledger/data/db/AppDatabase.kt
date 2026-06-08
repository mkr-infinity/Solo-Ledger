package com.solo.ledger.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.solo.ledger.data.db.converters.Converters
import com.solo.ledger.data.db.dao.BudgetDao
import com.solo.ledger.data.db.dao.CategoryDao
import com.solo.ledger.data.db.dao.TransactionDao
import com.solo.ledger.data.model.Budget
import com.solo.ledger.data.model.Category
import com.solo.ledger.data.model.CategoryType
import com.solo.ledger.data.model.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID

@Database(
    entities = [Transaction::class, Category::class, Budget::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "solo_ledger.db"
            )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        INSTANCE?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch {
                                populateDefaultCategories(database.categoryDao())
                            }
                        }
                    }
                })
                .build()
        }

        private suspend fun populateDefaultCategories(dao: CategoryDao) {
            val defaults = listOf(
                // ── Income ────────────────────────────────────────────────────
                Category(
                    id = UUID.randomUUID().toString(),
                    name = "Salary",
                    iconName = "Work",
                    colorHex = "#43A047",
                    type = CategoryType.INCOME,
                    isDefault = true,
                    sortOrder = 0
                ),
                Category(
                    id = UUID.randomUUID().toString(),
                    name = "Freelance",
                    iconName = "Briefcase",
                    colorHex = "#00897B",
                    type = CategoryType.INCOME,
                    isDefault = true,
                    sortOrder = 1
                ),
                Category(
                    id = UUID.randomUUID().toString(),
                    name = "Business",
                    iconName = "TrendingUp",
                    colorHex = "#1E88E5",
                    type = CategoryType.INCOME,
                    isDefault = true,
                    sortOrder = 2
                ),
                Category(
                    id = UUID.randomUUID().toString(),
                    name = "Investment",
                    iconName = "BarChart2",
                    colorHex = "#3949AB",
                    type = CategoryType.INCOME,
                    isDefault = true,
                    sortOrder = 3
                ),
                Category(
                    id = UUID.randomUUID().toString(),
                    name = "Gift",
                    iconName = "Gift",
                    colorHex = "#E91E63",
                    type = CategoryType.INCOME,
                    isDefault = true,
                    sortOrder = 4
                ),
                Category(
                    id = UUID.randomUUID().toString(),
                    name = "Other Income",
                    iconName = "Plus",
                    colorHex = "#00ACC1",
                    type = CategoryType.INCOME,
                    isDefault = true,
                    sortOrder = 5
                ),
                // ── Expense ───────────────────────────────────────────────────
                Category(
                    id = UUID.randomUUID().toString(),
                    name = "Food",
                    iconName = "Utensils",
                    colorHex = "#FB8C00",
                    type = CategoryType.EXPENSE,
                    isDefault = true,
                    sortOrder = 6
                ),
                Category(
                    id = UUID.randomUUID().toString(),
                    name = "Transport",
                    iconName = "Car",
                    colorHex = "#8E24AA",
                    type = CategoryType.EXPENSE,
                    isDefault = true,
                    sortOrder = 7
                ),
                Category(
                    id = UUID.randomUUID().toString(),
                    name = "Shopping",
                    iconName = "ShoppingCart",
                    colorHex = "#F9A825",
                    type = CategoryType.EXPENSE,
                    isDefault = true,
                    sortOrder = 8
                ),
                Category(
                    id = UUID.randomUUID().toString(),
                    name = "Health",
                    iconName = "Heart",
                    colorHex = "#E53935",
                    type = CategoryType.EXPENSE,
                    isDefault = true,
                    sortOrder = 9
                ),
                Category(
                    id = UUID.randomUUID().toString(),
                    name = "Entertainment",
                    iconName = "Film",
                    colorHex = "#6C63FF",
                    type = CategoryType.EXPENSE,
                    isDefault = true,
                    sortOrder = 10
                ),
                Category(
                    id = UUID.randomUUID().toString(),
                    name = "Education",
                    iconName = "BookOpen",
                    colorHex = "#039BE5",
                    type = CategoryType.EXPENSE,
                    isDefault = true,
                    sortOrder = 11
                ),
                Category(
                    id = UUID.randomUUID().toString(),
                    name = "Bills",
                    iconName = "Zap",
                    colorHex = "#F4511E",
                    type = CategoryType.EXPENSE,
                    isDefault = true,
                    sortOrder = 12
                ),
                Category(
                    id = UUID.randomUUID().toString(),
                    name = "Rent",
                    iconName = "Home",
                    colorHex = "#6D4C41",
                    type = CategoryType.EXPENSE,
                    isDefault = true,
                    sortOrder = 13
                ),
                Category(
                    id = UUID.randomUUID().toString(),
                    name = "Travel",
                    iconName = "Plane",
                    colorHex = "#00BCD4",
                    type = CategoryType.EXPENSE,
                    isDefault = true,
                    sortOrder = 14
                ),
                Category(
                    id = UUID.randomUUID().toString(),
                    name = "Other",
                    iconName = "MoreHorizontal",
                    colorHex = "#757575",
                    type = CategoryType.EXPENSE,
                    isDefault = true,
                    sortOrder = 15
                )
            )

            defaults.forEach { dao.insert(it) }
        }
    }
}
