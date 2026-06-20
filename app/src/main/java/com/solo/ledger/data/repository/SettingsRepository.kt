package com.solo.ledger.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.solo.ledger.data.model.BudgetTemplate
import com.solo.ledger.data.model.DashboardWidget
import com.solo.ledger.data.model.QuickAddField
import com.solo.ledger.data.model.UserSettings
import com.solo.ledger.ui.theme.LedgerTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "solo_ledger_settings")

class SettingsRepository(
    context: Context,
) {
    private val dataStore = context.applicationContext.settingsDataStore

    val settings: Flow<UserSettings> = dataStore.data.map { preferences ->
        UserSettings(
            onboardingCompleted = preferences[Keys.onboardingCompleted] ?: false,
            selectedBudgetTemplate = preferences[Keys.selectedBudgetTemplate]?.let(::budgetTemplateFromName),
            name = preferences[Keys.name].orEmpty(),
            avatarPath = preferences[Keys.avatarPath],
            monthlyBudgetMinor = preferences[Keys.monthlyBudgetMinor] ?: 0L,
            currencyCode = preferences[Keys.currencyCode] ?: "INR",
            theme = preferences[Keys.theme]?.let(::themeFromName) ?: LedgerTheme.LedgerDark,
            fontScale = preferences[Keys.fontScale] ?: 1f,
            animationsEnabled = preferences[Keys.animationsEnabled] ?: true,
            reducedMotion = preferences[Keys.reducedMotion] ?: false,
            highContrast = preferences[Keys.highContrast] ?: false,
            borderRadiusDp = preferences[Keys.borderRadiusDp] ?: 28,
            dashboardWidgets = preferences[Keys.dashboardWidgets]?.decodeDashboardWidgets() ?: defaultDashboardWidgets,
            quickAddFields = preferences[Keys.quickAddFields]?.decodeQuickAddFields() ?: defaultQuickAddFields,
        )
    }

    suspend fun updateTheme(theme: LedgerTheme) {
        dataStore.edit { it[Keys.theme] = theme.name }
    }

    suspend fun completeOnboarding(template: BudgetTemplate?) {
        dataStore.edit {
            it[Keys.onboardingCompleted] = true
            if (template == null) {
                it.remove(Keys.selectedBudgetTemplate)
            } else {
                it[Keys.selectedBudgetTemplate] = template.name
            }
        }
    }

    suspend fun updateProfile(name: String, avatarPath: String?, monthlyBudgetMinor: Long, currencyCode: String) {
        dataStore.edit {
            it[Keys.name] = name.trim()
            if (avatarPath.isNullOrBlank()) {
                it.remove(Keys.avatarPath)
            } else {
                it[Keys.avatarPath] = avatarPath
            }
            it[Keys.monthlyBudgetMinor] = monthlyBudgetMinor
            it[Keys.currencyCode] = currencyCode.trim().uppercase()
        }
    }

    suspend fun updateAppearance(
        fontScale: Float,
        animationsEnabled: Boolean,
        reducedMotion: Boolean,
        highContrast: Boolean,
        borderRadiusDp: Int,
    ) {
        dataStore.edit {
            it[Keys.fontScale] = fontScale
            it[Keys.animationsEnabled] = animationsEnabled
            it[Keys.reducedMotion] = reducedMotion
            it[Keys.highContrast] = highContrast
            it[Keys.borderRadiusDp] = borderRadiusDp
        }
    }

    suspend fun updateDashboardWidgets(widgets: List<DashboardWidget>) {
        dataStore.edit { it[Keys.dashboardWidgets] = widgets.joinToString(separator = ",") { widget -> widget.name } }
    }

    suspend fun updateQuickAddFields(fields: Set<QuickAddField>) {
        dataStore.edit { it[Keys.quickAddFields] = fields.joinToString(separator = ",") { field -> field.name } }
    }

    private fun themeFromName(name: String): LedgerTheme = LedgerTheme.entries.firstOrNull { it.name == name }
        ?: LedgerTheme.LedgerDark

    private fun budgetTemplateFromName(name: String): BudgetTemplate? = BudgetTemplate.entries.firstOrNull { it.name == name }

    private fun String.decodeDashboardWidgets(): List<DashboardWidget> = split(',')
        .filter { it.isNotBlank() }
        .mapNotNull { encoded -> DashboardWidget.entries.firstOrNull { it.name == encoded } }

    private fun String.decodeQuickAddFields(): Set<QuickAddField> = split(',')
        .filter { it.isNotBlank() }
        .mapNotNull { encoded -> QuickAddField.entries.firstOrNull { it.name == encoded } }
        .toSet()

    private object Keys {
        val name = stringPreferencesKey("name")
        val onboardingCompleted = booleanPreferencesKey("onboarding_completed")
        val selectedBudgetTemplate = stringPreferencesKey("selected_budget_template")
        val avatarPath = stringPreferencesKey("avatar_path")
        val monthlyBudgetMinor = longPreferencesKey("monthly_budget_minor")
        val currencyCode = stringPreferencesKey("currency_code")
        val theme = stringPreferencesKey("theme")
        val fontScale = floatPreferencesKey("font_scale")
        val animationsEnabled = booleanPreferencesKey("animations_enabled")
        val reducedMotion = booleanPreferencesKey("reduced_motion")
        val highContrast = booleanPreferencesKey("high_contrast")
        val borderRadiusDp = intPreferencesKey("border_radius_dp")
        val dashboardWidgets = stringPreferencesKey("dashboard_widgets")
        val quickAddFields = stringPreferencesKey("quick_add_fields")
    }
}

private val defaultDashboardWidgets = listOf(
    DashboardWidget.MonthlyBudget,
    DashboardWidget.DailySpending,
    DashboardWidget.SavingsGoalProgress,
    DashboardWidget.Insights,
    DashboardWidget.RecentTransactions,
    DashboardWidget.CategoryBreakdown,
    DashboardWidget.MonthlyGraph,
)

private val defaultQuickAddFields = QuickAddField.entries.toSet()
