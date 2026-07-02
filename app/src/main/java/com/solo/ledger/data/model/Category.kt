package com.solo.ledger.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val icon: String, // Material icon name
    val color: Long, // Color as ARGB long
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
