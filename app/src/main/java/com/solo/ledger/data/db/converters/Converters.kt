package com.solo.ledger.data.db.converters

import androidx.room.TypeConverter
import com.solo.ledger.data.model.CategoryType
import com.solo.ledger.data.model.TransactionType
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class Converters {

    // ── LocalDate ──────────────────────────────────────────────────────────────

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? = date?.toString()

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? =
        value?.let { LocalDate.parse(it) }

    // ── LocalTime ──────────────────────────────────────────────────────────────

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

    @TypeConverter
    fun fromLocalTime(time: LocalTime?): String? =
        time?.format(timeFormatter)

    @TypeConverter
    fun toLocalTime(value: String?): LocalTime? =
        value?.let { LocalTime.parse(it, timeFormatter) }

    // ── LocalDateTime ─────────────────────────────────────────────────────────

    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime?): String? = dateTime?.toString()

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? =
        value?.let { LocalDateTime.parse(it) }

    // ── TransactionType ───────────────────────────────────────────────────────

    @TypeConverter
    fun fromTransactionType(type: TransactionType?): String? = type?.name

    @TypeConverter
    fun toTransactionType(value: String?): TransactionType? =
        value?.let { TransactionType.valueOf(it) }

    // ── CategoryType ──────────────────────────────────────────────────────────

    @TypeConverter
    fun fromCategoryType(type: CategoryType?): String? = type?.name

    @TypeConverter
    fun toCategoryType(value: String?): CategoryType? =
        value?.let { CategoryType.valueOf(it) }
}
