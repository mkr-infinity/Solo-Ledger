package com.solo.ledger.core

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object Money {
    fun format(amount: Double, currencyCode: String = "INR"): String {
        return runCatching {
            val nf = NumberFormat.getCurrencyInstance(Locale.getDefault())
            nf.currency = Currency.getInstance(currencyCode)
            nf.maximumFractionDigits = if (amount % 1.0 == 0.0) 0 else 2
            nf.format(amount)
        }.getOrElse { "%.2f".format(amount) }
    }
}
