package com.solo.ledger.data.model

data class AppLog(
    val id: Long = System.currentTimeMillis(),
    val timestamp: Long = System.currentTimeMillis(),
    val type: LogType,
    val title: String,
    val details: String
)

enum class LogType {
    EXPENSE_ADDED,
    EXPENSE_EDITED,
    EXPENSE_DELETED,
    EXPENSE_RESTORED,
    CATEGORY_ADDED,
    CATEGORY_DELETED,
    GOAL_ADDED,
    GOAL_UPDATED,
    BUDGET_CHANGED,
    THEME_CHANGED,
    DATA_EXPORTED,
    DATA_IMPORTED,
    SETTINGS_CHANGED,
    APP_OPENED,
    BIN_CLEARED
}
