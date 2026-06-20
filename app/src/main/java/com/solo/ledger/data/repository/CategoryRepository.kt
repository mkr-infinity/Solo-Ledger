package com.solo.ledger.data.repository

import com.solo.ledger.data.local.dao.CategoryDao
import com.solo.ledger.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

class CategoryRepository(
    private val categoryDao: CategoryDao,
) {
    fun observeActiveCategories(): Flow<List<CategoryEntity>> = categoryDao.observeActiveCategories()

    fun observeAllCategories(): Flow<List<CategoryEntity>> = categoryDao.observeAllCategories()

    suspend fun ensureDefaultCategories(createdAtMillis: Long) {
        if (categoryDao.countCategories() == 0) {
            categoryDao.insertDefaults(DefaultCategories.create(createdAtMillis))
        }
    }

    suspend fun upsert(category: CategoryEntity) = categoryDao.upsert(category)

    suspend fun archive(id: String, updatedAtMillis: Long) = categoryDao.archive(id, updatedAtMillis)
}

private object DefaultCategories {
    fun create(now: Long): List<CategoryEntity> = listOf(
        category("food", "Food", "restaurant", "#16A34A", now),
        category("travel", "Travel", "directions", "#047857", now),
        category("shopping", "Shopping", "shopping_bag", "#7C3AED", now),
        category("bills", "Bills", "receipt", "#D97706", now),
        category("education", "Education", "school", "#0F766E", now),
        category("entertainment", "Entertainment", "movie", "#DB2777", now),
        category("groceries", "Groceries", "local_grocery_store", "#65A30D", now),
        category("subscription", "Subscription", "subscriptions", "#9333EA", now),
        category("other", "Other", "category", "#64748B", now),
    )

    private fun category(
        id: String,
        name: String,
        iconName: String,
        colorHex: String,
        now: Long,
    ) = CategoryEntity(
        id = id,
        name = name,
        iconName = iconName,
        colorHex = colorHex,
        createdAtMillis = now,
        updatedAtMillis = now,
        isArchived = false,
    )
}
