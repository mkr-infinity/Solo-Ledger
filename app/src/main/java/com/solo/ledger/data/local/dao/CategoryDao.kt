package com.solo.ledger.data.local.dao

import androidx.room.*
import com.solo.ledger.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY isDefault DESC, name ASC")
    fun observeAll(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(category: CategoryEntity)
    @Insert(onConflict = OnConflictStrategy.IGNORE) suspend fun insertAll(items: List<CategoryEntity>)
    @Delete suspend fun delete(category: CategoryEntity)
    @Query("SELECT COUNT(*) FROM categories") suspend fun count(): Int
    @Query("SELECT * FROM categories") suspend fun all(): List<CategoryEntity>
}
