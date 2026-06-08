package com.solo.ledger.data.repository

import com.solo.ledger.data.db.dao.BudgetDao
import com.solo.ledger.data.model.Budget
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetRepository @Inject constructor(
    private val dao: BudgetDao
) {

    // ── Read ──────────────────────────────────────────────────────────────────

    /**
     * Returns all budget entries for the given month.
     * @param monthYear "YYYY-MM"
     */
    fun getByMonth(monthYear: String): Flow<List<Budget>> = dao.getByMonth(monthYear)

    /**
     * Returns the category-specific budget for the given category and month, or null if none set.
     * @param categoryId the category ID
     * @param monthYear  "YYYY-MM"
     */
    suspend fun getByCategoryAndMonth(categoryId: String, monthYear: String): Budget? =
        dao.getByCategoryAndMonth(categoryId, monthYear)

    /**
     * Returns the global (non-category-specific) budget for the given month, or null if none set.
     * @param monthYear "YYYY-MM"
     */
    suspend fun getGlobalBudget(monthYear: String): Budget? =
        dao.getGlobalBudget(monthYear)

    // ── Write ─────────────────────────────────────────────────────────────────

    suspend fun insert(budget: Budget) = dao.insert(budget)

    suspend fun update(budget: Budget) = dao.update(budget)

    suspend fun delete(budget: Budget) = dao.delete(budget)

    /**
     * Removes the category-specific budget for [categoryId] in [monthYear].
     */
    suspend fun deleteByCategoryAndMonth(categoryId: String, monthYear: String) =
        dao.deleteByCategoryAndMonth(categoryId, monthYear)

    /**
     * Removes the global (overall monthly) budget for [monthYear].
     */
    suspend fun deleteGlobalBudget(monthYear: String) =
        dao.deleteGlobalBudget(monthYear)
}
