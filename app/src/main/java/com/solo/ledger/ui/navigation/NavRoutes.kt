package com.solo.ledger.ui.navigation

object NavRoutes {
    const val HOME = "home"
    const val HISTORY = "history"
    const val QUICK_ADD = "quick_add"
    const val CALENDAR = "calendar"
    const val SETTINGS = "settings"
    const val ANALYTICS = "analytics"
    const val ABOUT = "about"
    const val ARCHITECT = "architect"
    const val SUPPORT = "support"
    const val COMING_SOON = "coming_soon"
    const val DELETED = "deleted"
    const val CHECK_UPDATES = "check_updates"
    const val ONBOARDING = "onboarding"
    const val LOGS = "logs"
    const val EDIT_TRANSACTION = "edit_transaction/{transactionId}"
    fun editTransaction(id: String) = "edit_transaction/$id"
}

val bottomNavRoutes = listOf(
    NavRoutes.HOME,
    NavRoutes.HISTORY,
    NavRoutes.QUICK_ADD,
    NavRoutes.CALENDAR,
    NavRoutes.SETTINGS
)
