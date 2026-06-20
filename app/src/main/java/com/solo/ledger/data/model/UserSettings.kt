package com.solo.ledger.data.model

import com.solo.ledger.ui.theme.LedgerTheme

data class UserSettings(
    val onboardingCompleted: Boolean,
    val selectedBudgetTemplate: BudgetTemplate?,
    val name: String,
    val avatarPath: String?,
    val monthlyBudgetMinor: Long,
    val currencyCode: String,
    val theme: LedgerTheme,
    val fontScale: Float,
    val animationsEnabled: Boolean,
    val reducedMotion: Boolean,
    val highContrast: Boolean,
    val borderRadiusDp: Int,
    val dashboardWidgets: List<DashboardWidget>,
    val quickAddFields: Set<QuickAddField>,
)

enum class QuickAddField {
    Title,
    Amount,
    Category,
    Date,
    Time,
    Notes,
    Attachment,
}
