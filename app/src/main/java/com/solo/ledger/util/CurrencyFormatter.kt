package com.solo.ledger.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import com.solo.ledger.data.model.TransactionType

object CurrencyFormatter {
    fun format(amount: Double, symbol: String): String {
        val formatter = DecimalFormat("#,##,##0.00", DecimalFormatSymbols(Locale("en", "IN")))
        return "$symbol ${formatter.format(amount)}"
    }

    fun formatCompact(amount: Double, symbol: String): String {
        return when {
            amount >= 1_00_00_000 -> "$symbol ${String.format("%.1fCr", amount / 1_00_00_000)}"
            amount >= 1_00_000    -> "$symbol ${String.format("%.1fL", amount / 1_00_000)}"
            amount >= 1_000       -> "$symbol ${String.format("%.1fK", amount / 1_000)}"
            else                  -> format(amount, symbol)
        }
    }

    fun formatSign(amount: Double, symbol: String, type: TransactionType): String {
        val prefix = if (type == TransactionType.INCOME) "+" else "-"
        return "$prefix ${format(amount, symbol)}"
    }
}
