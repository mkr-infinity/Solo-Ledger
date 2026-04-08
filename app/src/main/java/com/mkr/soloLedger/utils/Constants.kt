package com.mkr.soloLedger.utils

object Constants {
    const val PREFS_NAME = "solo_ledger_prefs"
    const val PREF_ONBOARDING_DONE = "onboarding_done"
    const val PREF_CURRENCY = "currency"
    const val PREF_THEME_MODE = "theme_mode"

    const val RECURRING_NONE = "NONE"
    const val RECURRING_WEEKLY = "WEEKLY"
    const val RECURRING_MONTHLY = "MONTHLY"
    const val RECURRING_YEARLY = "YEARLY"

    const val EXPORT_FILE_NAME = "solo_ledger_backup.json"

    val CURRENCIES = listOf("USD", "EUR", "GBP", "INR", "JPY", "CAD", "AUD", "CHF", "CNY", "BRL")

    val CURRENCY_SYMBOLS = mapOf(
        "USD" to "$",
        "EUR" to "€",
        "GBP" to "£",
        "INR" to "₹",
        "JPY" to "¥",
        "CAD" to "CA$",
        "AUD" to "A$",
        "CHF" to "CHF",
        "CNY" to "¥",
        "BRL" to "R$"
    )
}
