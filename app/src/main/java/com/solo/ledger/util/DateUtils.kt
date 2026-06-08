package com.solo.ledger.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

object DateUtils {
    /** "Monday, 8 Jun 2026" */
    fun formatDateFull(date: LocalDate): String {
        val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
        val day = date.dayOfMonth
        val month = date.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
        val year = date.year
        return "$dayOfWeek, $day $month $year"
    }

    /** "8 Jun" */
    fun formatDateShort(date: LocalDate): String {
        val day = date.dayOfMonth
        val month = date.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
        return "$day $month"
    }

    /** "3:45:22 PM" */
    fun formatTime(time: LocalTime): String =
        time.format(DateTimeFormatter.ofPattern("h:mm:ss a", Locale.ENGLISH))

    /** "Jun 2026" */
    fun formatMonthYear(date: LocalDate): String {
        val month = date.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
        return "$month ${date.year}"
    }

    /** "YYYY-MM" */
    fun toMonthYear(date: LocalDate): String =
        String.format("%04d-%02d", date.year, date.monthValue)

    fun getDayOfWeek(date: LocalDate): String =
        date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)

    fun today(): LocalDate = LocalDate.now()
    fun now(): LocalDateTime = LocalDateTime.now()
    fun currentTime(): LocalTime = LocalTime.now()

    /** Number of days between [from] and [to] (positive if [to] is after [from]). */
    fun daysUntil(from: LocalDate, to: LocalDate): Long =
        ChronoUnit.DAYS.between(from, to)
}
