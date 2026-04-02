package com.mkr.soloLedger.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mkr.soloLedger.data.entities.UserProfile

@Dao
interface UserProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userProfile: UserProfile): Long

    @Update
    suspend fun update(userProfile: UserProfile)

    @Query("SELECT * FROM user_profile ORDER BY id DESC LIMIT 1")
    fun getUserProfile(): LiveData<UserProfile?>

    @Query("SELECT * FROM user_profile ORDER BY id DESC LIMIT 1")
    suspend fun getUserProfileOnce(): UserProfile?

    @Query("DELETE FROM user_profile")
    suspend fun deleteAll()
}
