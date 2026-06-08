package com.solo.ledger.util

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonSerializer
import com.solo.ledger.data.model.Budget
import com.solo.ledger.data.model.Category
import com.solo.ledger.data.model.Transaction
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class ExportData(
    val version: Int = 1,
    val exportedAt: String,
    val transactions: List<Transaction>,
    val categories: List<Category>,
    val budgets: List<Budget>
)

object JsonSerializer {
    val gson = GsonBuilder()
        .registerTypeAdapter(LocalDate::class.java, JsonSerializer<LocalDate> { src, _, _ ->
            com.google.gson.JsonPrimitive(src.toString())
        })
        .registerTypeAdapter(LocalDate::class.java, JsonDeserializer { json, _, _ ->
            LocalDate.parse(json.asString)
        })
        .registerTypeAdapter(LocalTime::class.java, JsonSerializer<LocalTime> { src, _, _ ->
            com.google.gson.JsonPrimitive(src.toString())
        })
        .registerTypeAdapter(LocalTime::class.java, JsonDeserializer { json, _, _ ->
            LocalTime.parse(json.asString)
        })
        .registerTypeAdapter(LocalDateTime::class.java, JsonSerializer<LocalDateTime> { src, _, _ ->
            com.google.gson.JsonPrimitive(src.toString())
        })
        .registerTypeAdapter(LocalDateTime::class.java, JsonDeserializer { json, _, _ ->
            LocalDateTime.parse(json.asString)
        })
        .setPrettyPrinting()
        .create()

    fun toJson(data: ExportData): String = gson.toJson(data)
    fun fromJson(json: String): ExportData = gson.fromJson(json, ExportData::class.java)
}
