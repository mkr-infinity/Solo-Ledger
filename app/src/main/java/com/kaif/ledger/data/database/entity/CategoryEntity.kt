package com.kaif.ledger.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val icon: String, // Store as SVG path or resource name
    val colorHex: String, // e.g., "#FF5733"
    val createdDate: LocalDateTime = LocalDateTime.now()
)
