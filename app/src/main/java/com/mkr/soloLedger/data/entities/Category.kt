package com.mkr.soloLedger.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val iconName: String,
    val isDefault: Boolean = false,
    val color: String = "#6750A4"
)
