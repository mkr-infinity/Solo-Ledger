package com.solo.ledger.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "savings_goals")
data class SavingsGoal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val targetAmount: Double,
    val savedAmount: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val remainingAmount: Double get() = (targetAmount - savedAmount).coerceAtLeast(0.0)
    val progressPercent: Float get() = if (targetAmount > 0) (savedAmount / targetAmount * 100).toFloat().coerceIn(0f, 100f) else 0f
}
