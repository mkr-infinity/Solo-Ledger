package com.solo.ledger.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey val id: String,
    val name: String,
    val iconName: String,
    val colorHex: String,
    val type: CategoryType,
    val isDefault: Boolean = false,
    val sortOrder: Int = 0
)

enum class CategoryType { INCOME, EXPENSE, BOTH }
