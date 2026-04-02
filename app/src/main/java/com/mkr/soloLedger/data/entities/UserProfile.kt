package com.mkr.soloLedger.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String = "User",
    val avatarPath: String = "avatars/avatar_1.svg",
    val currency: String = "USD",
    val monthlyBudget: Double = 0.0,
    val themeMode: Int = 0,
    val isPinEnabled: Boolean = false,
    val pin: String = "",
    val isFingerprintEnabled: Boolean = false
)
