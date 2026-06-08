package com.solo.ledger.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.solo.ledger.data.model.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories ORDER BY sortOrder ASC, name ASC")
    fun getAll(): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): Category?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category)

    @Update
    suspend fun update(category: Category)

    @Delete
    suspend fun delete(category: Category)

    @Query("SELECT * FROM categories WHERE isDefault = 1 ORDER BY sortOrder ASC")
    fun getDefaults(): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE type = :type OR type = 'BOTH' ORDER BY sortOrder ASC, name ASC")
    fun getByType(type: String): Flow<List<Category>>

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun getCount(): Int
}
