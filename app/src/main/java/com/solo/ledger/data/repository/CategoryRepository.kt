package com.solo.ledger.data.repository

import com.solo.ledger.data.db.dao.CategoryDao
import com.solo.ledger.data.model.Category
import com.solo.ledger.data.model.CategoryType
import kotlinx.coroutines.flow.Flow
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
}
