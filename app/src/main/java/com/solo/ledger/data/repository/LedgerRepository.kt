package com.solo.ledger.data.repository

import com.solo.ledger.data.local.dao.CategoryDao
import com.solo.ledger.data.local.dao.ExpenseDao
import com.solo.ledger.data.local.dao.GoalDao
import com.solo.ledger.data.local.entity.CategoryEntity
import com.solo.ledger.data.local.entity.ExpenseEntity
import com.solo.ledger.data.local.entity.GoalEntity
import kotlinx.coroutines.flow.Flow

class LedgerRepository(
    private val expenseDao: ExpenseDao,
    private val categoryDao: CategoryDao,
    private val goalDao: GoalDao
) {
    fun activeExpenses(): Flow<List<ExpenseEntity>> = expenseDao.observeActive()
    fun deletedExpenses(): Flow<List<ExpenseEntity>> = expenseDao.observeDeleted()
    fun expensesInRange(start: Long, end: Long) = expenseDao.observeRange(start, end)
    fun categorySummary(start: Long, end: Long) = expenseDao.observeCategorySummary(start, end)
    fun totalInRange(start: Long, end: Long) = expenseDao.observeTotalInRange(start, end)

    suspend fun getExpense(id: Long) = expenseDao.getById(id)
    suspend fun addExpense(e: ExpenseEntity) = expenseDao.insert(e)
    suspend fun updateExpense(e: ExpenseEntity) = expenseDao.update(e)
    suspend fun softDelete(id: Long) = expenseDao.softDelete(id, System.currentTimeMillis())
    suspend fun restore(id: Long) = expenseDao.restore(id)
    suspend fun hardDelete(id: Long) = expenseDao.hardDelete(id)
    suspend fun clearBin() = expenseDao.clearBin()

    fun categories(): Flow<List<CategoryEntity>> = categoryDao.observeAll()
    suspend fun upsertCategory(c: CategoryEntity) = categoryDao.upsert(c)
    suspend fun deleteCategory(c: CategoryEntity) = categoryDao.delete(c)
    suspend fun allExpensesSnapshot() = expenseDao.all()
    suspend fun allGoalsSnapshot() = goalDao.all()
    suspend fun allCategoriesSnapshot() = categoryDao.all()
    suspend fun restoreExpenses(items: List<ExpenseEntity>) = expenseDao.insertAll(items)
    suspend fun restoreGoals(items: List<GoalEntity>) = goalDao.insertAll(items)
    suspend fun restoreCategories(items: List<CategoryEntity>) = categoryDao.insertAll(items)
    suspend fun seedDefaultCategories() {
        if (categoryDao.count() == 0) categoryDao.insertAll(DefaultData.categories)
    }

    fun goals(): Flow<List<GoalEntity>> = goalDao.observeAll()
    suspend fun addGoal(g: GoalEntity) = goalDao.insert(g)
    suspend fun updateGoal(g: GoalEntity) = goalDao.update(g)
    suspend fun deleteGoal(g: GoalEntity) = goalDao.delete(g)
}

object DefaultData {
    val categories = listOf(
        CategoryEntity("Food", "restaurant", 0xFF16A34A, true),
        CategoryEntity("Travel", "flight", 0xFF0EA5A4, true),
        CategoryEntity("Shopping", "shopping_bag", 0xFFCA8A04, true),
        CategoryEntity("Bills", "receipt", 0xFFDC2626, true),
        CategoryEntity("Education", "school", 0xFF7C3AED, true),
        CategoryEntity("Entertainment", "movie", 0xFFDB2777, true),
        CategoryEntity("Groceries", "local_grocery", 0xFF65A30D, true),
        CategoryEntity("Subscription", "subscriptions", 0xFFEA580C, true),
        CategoryEntity("Other", "category", 0xFF64748B, true)
    )
}
