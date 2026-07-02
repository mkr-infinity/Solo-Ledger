package com.solo.ledger.data.dao

import androidx.room.*
import com.solo.ledger.data.model.BudgetTemplate
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetTemplateDao {

    @Query("SELECT * FROM budget_templates ORDER BY name ASC")
    fun getAllTemplates(): Flow<List<BudgetTemplate>>

    @Query("SELECT * FROM budget_templates WHERE id = :id")
    suspend fun getTemplateById(id: Long): BudgetTemplate?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: BudgetTemplate): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(templates: List<BudgetTemplate>)

    @Query("SELECT COUNT(*) FROM budget_templates")
    suspend fun getTemplateCount(): Int
}
