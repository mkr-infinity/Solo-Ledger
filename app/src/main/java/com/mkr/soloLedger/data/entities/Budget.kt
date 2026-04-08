package com.mkr.soloLedger.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budget")
data class Budget(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val monthlyBudget: Double = 0.0,
    val currency: String = "USD",
    val updatedAt: Long = System.currentTimeMillis()
)
