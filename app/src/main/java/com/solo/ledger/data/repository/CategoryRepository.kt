package com.solo.ledger.data.repository

import com.solo.ledger.data.db.dao.CategoryDao
import com.solo.ledger.data.model.Category
import com.solo.ledger.data.model.CategoryType
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val dao: CategoryDao
) {

    // ── Read ──────────────────────────────────────────────────────────────────

    fun getAll(): Flow<List<Category>> = dao.getAll()

    fun getDefaults(): Flow<List<Category>> = dao.getDefaults()

    fun getByType(type: CategoryType): Flow<List<Category>> = dao.getByType(type.name)

    suspend fun getById(id: String): Category? = dao.getById(id)

    suspend fun getCount(): Int = dao.getCount()

    // ── Write ─────────────────────────────────────────────────────────────────

    suspend fun insert(category: Category) = dao.insert(category)

    suspend fun update(category: Category) = dao.update(category)

    suspend fun delete(category: Category) = dao.delete(category)

    suspend fun ensureDefaultCategories() {
        if (dao.getCount() > 0) return

        defaultCategories().forEach { category ->
            dao.insert(category)
        }
    }

    private fun defaultCategories(): List<Category> = listOf(
        defaultCategory("Salary", "Work", "#43A047", CategoryType.INCOME, 0),
        defaultCategory("Freelance", "Briefcase", "#00897B", CategoryType.INCOME, 1),
        defaultCategory("Business", "TrendingUp", "#1E88E5", CategoryType.INCOME, 2),
        defaultCategory("Investment", "BarChart2", "#3949AB", CategoryType.INCOME, 3),
        defaultCategory("Gift", "Gift", "#E91E63", CategoryType.INCOME, 4),
        defaultCategory("Other Income", "Plus", "#00ACC1", CategoryType.INCOME, 5),
        defaultCategory("Food", "Utensils", "#FB8C00", CategoryType.EXPENSE, 6),
        defaultCategory("Transport", "Car", "#8E24AA", CategoryType.EXPENSE, 7),
        defaultCategory("Shopping", "ShoppingCart", "#F9A825", CategoryType.EXPENSE, 8),
        defaultCategory("Health", "Heart", "#E53935", CategoryType.EXPENSE, 9),
        defaultCategory("Entertainment", "Film", "#6C63FF", CategoryType.EXPENSE, 10),
        defaultCategory("Education", "BookOpen", "#039BE5", CategoryType.EXPENSE, 11),
        defaultCategory("Bills", "Zap", "#F4511E", CategoryType.EXPENSE, 12),
        defaultCategory("Rent", "Home", "#6D4C41", CategoryType.EXPENSE, 13),
        defaultCategory("Travel", "Plane", "#00BCD4", CategoryType.EXPENSE, 14),
        defaultCategory("Other", "MoreHorizontal", "#757575", CategoryType.EXPENSE, 15)
    )

    private fun defaultCategory(
        name: String,
        iconName: String,
        colorHex: String,
        type: CategoryType,
        sortOrder: Int
    ): Category = Category(
        id = UUID.randomUUID().toString(),
        name = name,
        iconName = iconName,
        colorHex = colorHex,
        type = type,
        isDefault = true,
        sortOrder = sortOrder
    )
}
