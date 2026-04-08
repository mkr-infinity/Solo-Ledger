package com.mkr.soloLedger.utils

import android.content.Context
import android.text.format.DateFormat
import com.mkr.soloLedger.utils.Constants.CURRENCY_SYMBOLS
import java.text.SimpleDateFormat
import java.util.*

fun Double.formatCurrency(currency: String): String {
    val symbol = CURRENCY_SYMBOLS[currency] ?: currency
    return "$symbol%.2f".format(this)
}

fun Long.toFormattedDate(): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return sdf.format(Date(this))
}

fun Long.toFormattedDateTime(): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return sdf.format(Date(this))
}

fun Long.toMonthYear(): String {
    val sdf = SimpleDateFormat("MMM yyyy", Locale.getDefault())
    return sdf.format(Date(this))
}

fun getStartOfMonth(year: Int = -1, month: Int = -1): Long {
    val cal = Calendar.getInstance()
    if (year >= 0) cal.set(Calendar.YEAR, year)
    if (month >= 0) cal.set(Calendar.MONTH, month)
    cal.set(Calendar.DAY_OF_MONTH, 1)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis
}

fun getEndOfMonth(year: Int = -1, month: Int = -1): Long {
    val cal = Calendar.getInstance()
    if (year >= 0) cal.set(Calendar.YEAR, year)
    if (month >= 0) cal.set(Calendar.MONTH, month)
    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
    cal.set(Calendar.HOUR_OF_DAY, 23)
    cal.set(Calendar.MINUTE, 59)
    cal.set(Calendar.SECOND, 59)
    cal.set(Calendar.MILLISECOND, 999)
    return cal.timeInMillis
}

fun Context.isUse24HourFormat(): Boolean = DateFormat.is24HourFormat(this)
