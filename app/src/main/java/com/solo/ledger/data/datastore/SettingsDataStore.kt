package com.solo.ledger.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.solo.ledger.util.AppLogger
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// ── DataStore extension ───────────────────────────────────────────────────────

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "solo_ledger_settings")

// ── Settings data class ───────────────────────────────────────────────────────

data class AppSettings(
    val onboardingCompleted: Boolean = false,
    val userName: String = "",
    val activeThemeId: String = "dark_cred",
    val navigationStyle: String = "bottom_standard",
    val currencySymbol: String = "₹",
    val currencyName: String = "Indian Rupee",
    val showTitleField: Boolean = true,
    val showCategoryField: Boolean = true,
    val showNotesField: Boolean = true,
    val showDateField: Boolean = true,
    val showTimeField: Boolean = true,
    val defaultCategoryId: String = "",
    val monthlyBudgetLimit: Double = 0.0,
    val savingsTarget: Double = 0.0,
    val trackBudgetCycles: Boolean = true,
    val fontScale: Float = 1.0f,
    val reducedMotion: Boolean = false,
    val highContrast: Boolean = false,
    val largerTouchTargets: Boolean = false,
    val dashboardSectionOrder: String = "balance,insights,budget,recent",
    val preferredChartType: String = "bar",
    val animationIntensity: String = "normal",
    val iconStyle: String = "outline",
    val cardStyle: String = "elevated",
    val borderRadiusStyle: String = "default",
    val swipeLeftAction: String = "delete",
    val swipeRightAction: String = "edit",
    val autoCheckUpdates: Boolean = true,
    val lastUpdateCheckDate: Long = 0L,
    val dismissedUpdateVersion: String = "",
    val firstInstallDate: Long = 0L,
    val lastSupportPopupDate: Long = 0L,
    val storagePermissionDeferred: Boolean = false,
    val logsEnabled: Boolean = false
)

// ── SettingsDataStore ─────────────────────────────────────────────────────────

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {

    // ── Preference keys ───────────────────────────────────────────────────────

    private object Keys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val USER_NAME = stringPreferencesKey("user_name")
        val ACTIVE_THEME_ID = stringPreferencesKey("active_theme_id")
        val NAVIGATION_STYLE = stringPreferencesKey("navigation_style")
        val CURRENCY_SYMBOL = stringPreferencesKey("currency_symbol")
        val CURRENCY_NAME = stringPreferencesKey("currency_name")
        val SHOW_TITLE_FIELD = booleanPreferencesKey("show_title_field")
        val SHOW_CATEGORY_FIELD = booleanPreferencesKey("show_category_field")
        val SHOW_NOTES_FIELD = booleanPreferencesKey("show_notes_field")
        val SHOW_DATE_FIELD = booleanPreferencesKey("show_date_field")
        val SHOW_TIME_FIELD = booleanPreferencesKey("show_time_field")
        val DEFAULT_CATEGORY_ID = stringPreferencesKey("default_category_id")
        val MONTHLY_BUDGET_LIMIT = doublePreferencesKey("monthly_budget_limit")
        val SAVINGS_TARGET = doublePreferencesKey("savings_target")
        val TRACK_BUDGET_CYCLES = booleanPreferencesKey("track_budget_cycles")
        val FONT_SCALE = floatPreferencesKey("font_scale")
        val REDUCED_MOTION = booleanPreferencesKey("reduced_motion")
        val HIGH_CONTRAST = booleanPreferencesKey("high_contrast")
        val LARGER_TOUCH_TARGETS = booleanPreferencesKey("larger_touch_targets")
        val DASHBOARD_SECTION_ORDER = stringPreferencesKey("dashboard_section_order")
        val PREFERRED_CHART_TYPE = stringPreferencesKey("preferred_chart_type")
        val ANIMATION_INTENSITY = stringPreferencesKey("animation_intensity")
        val ICON_STYLE = stringPreferencesKey("icon_style")
        val CARD_STYLE = stringPreferencesKey("card_style")
        val BORDER_RADIUS_STYLE = stringPreferencesKey("border_radius_style")
        val SWIPE_LEFT_ACTION = stringPreferencesKey("swipe_left_action")
        val SWIPE_RIGHT_ACTION = stringPreferencesKey("swipe_right_action")
        val AUTO_CHECK_UPDATES = booleanPreferencesKey("auto_check_updates")
        val LAST_UPDATE_CHECK_DATE = longPreferencesKey("last_update_check_date")
        val DISMISSED_UPDATE_VERSION = stringPreferencesKey("dismissed_update_version")
        val FIRST_INSTALL_DATE = longPreferencesKey("first_install_date")
        val LAST_SUPPORT_POPUP_DATE = longPreferencesKey("last_support_popup_date")
        val STORAGE_PERMISSION_DEFERRED = booleanPreferencesKey("storage_permission_deferred")
        val LOGS_ENABLED = booleanPreferencesKey("logs_enabled")
    }

    // ── Exposed flow ──────────────────────────────────────────────────────────

    val settings: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            onboardingCompleted = prefs[Keys.ONBOARDING_COMPLETED] ?: false,
            userName = prefs[Keys.USER_NAME] ?: "",
            activeThemeId = prefs[Keys.ACTIVE_THEME_ID] ?: "dark_cred",
            navigationStyle = prefs[Keys.NAVIGATION_STYLE] ?: "bottom_standard",
            currencySymbol = prefs[Keys.CURRENCY_SYMBOL] ?: "₹",
            currencyName = prefs[Keys.CURRENCY_NAME] ?: "Indian Rupee",
            showTitleField = prefs[Keys.SHOW_TITLE_FIELD] ?: true,
            showCategoryField = prefs[Keys.SHOW_CATEGORY_FIELD] ?: true,
            showNotesField = prefs[Keys.SHOW_NOTES_FIELD] ?: true,
            showDateField = prefs[Keys.SHOW_DATE_FIELD] ?: true,
            showTimeField = prefs[Keys.SHOW_TIME_FIELD] ?: true,
            defaultCategoryId = prefs[Keys.DEFAULT_CATEGORY_ID] ?: "",
            monthlyBudgetLimit = prefs[Keys.MONTHLY_BUDGET_LIMIT] ?: 0.0,
            savingsTarget = prefs[Keys.SAVINGS_TARGET] ?: 0.0,
            trackBudgetCycles = prefs[Keys.TRACK_BUDGET_CYCLES] ?: true,
            fontScale = prefs[Keys.FONT_SCALE] ?: 1.0f,
            reducedMotion = prefs[Keys.REDUCED_MOTION] ?: false,
            highContrast = prefs[Keys.HIGH_CONTRAST] ?: false,
            largerTouchTargets = prefs[Keys.LARGER_TOUCH_TARGETS] ?: false,
            dashboardSectionOrder = prefs[Keys.DASHBOARD_SECTION_ORDER] ?: "balance,insights,budget,recent",
            preferredChartType = prefs[Keys.PREFERRED_CHART_TYPE] ?: "bar",
            animationIntensity = prefs[Keys.ANIMATION_INTENSITY] ?: "normal",
            iconStyle = prefs[Keys.ICON_STYLE] ?: "outline",
            cardStyle = prefs[Keys.CARD_STYLE] ?: "elevated",
            borderRadiusStyle = prefs[Keys.BORDER_RADIUS_STYLE] ?: "default",
            swipeLeftAction = prefs[Keys.SWIPE_LEFT_ACTION] ?: "delete",
            swipeRightAction = prefs[Keys.SWIPE_RIGHT_ACTION] ?: "edit",
            autoCheckUpdates = prefs[Keys.AUTO_CHECK_UPDATES] ?: true,
            lastUpdateCheckDate = prefs[Keys.LAST_UPDATE_CHECK_DATE] ?: 0L,
            dismissedUpdateVersion = prefs[Keys.DISMISSED_UPDATE_VERSION] ?: "",
            firstInstallDate = prefs[Keys.FIRST_INSTALL_DATE] ?: 0L,
            lastSupportPopupDate = prefs[Keys.LAST_SUPPORT_POPUP_DATE] ?: 0L,
            storagePermissionDeferred = prefs[Keys.STORAGE_PERMISSION_DEFERRED] ?: false,
            logsEnabled = prefs[Keys.LOGS_ENABLED] ?: false
        )
    }

    // ── Update functions ──────────────────────────────────────────────────────

    suspend fun setOnboardingCompleted(value: Boolean) {
        context.dataStore.edit { it[Keys.ONBOARDING_COMPLETED] = value }
    }

    suspend fun setUserName(value: String) {
        context.dataStore.edit { it[Keys.USER_NAME] = value }
    }

    suspend fun setActiveThemeId(value: String) {
        context.dataStore.edit { it[Keys.ACTIVE_THEME_ID] = value }
    }

    suspend fun setNavigationStyle(value: String) {
        context.dataStore.edit { it[Keys.NAVIGATION_STYLE] = value }
    }

    suspend fun setCurrencySymbol(value: String) {
        context.dataStore.edit { it[Keys.CURRENCY_SYMBOL] = value }
    }

    suspend fun setCurrencyName(value: String) {
        context.dataStore.edit { it[Keys.CURRENCY_NAME] = value }
    }

    suspend fun setShowTitleField(value: Boolean) {
        context.dataStore.edit { it[Keys.SHOW_TITLE_FIELD] = value }
    }

    suspend fun setShowCategoryField(value: Boolean) {
        context.dataStore.edit { it[Keys.SHOW_CATEGORY_FIELD] = value }
    }

    suspend fun setShowNotesField(value: Boolean) {
        context.dataStore.edit { it[Keys.SHOW_NOTES_FIELD] = value }
    }

    suspend fun setShowDateField(value: Boolean) {
        context.dataStore.edit { it[Keys.SHOW_DATE_FIELD] = value }
    }

    suspend fun setShowTimeField(value: Boolean) {
        context.dataStore.edit { it[Keys.SHOW_TIME_FIELD] = value }
    }

    suspend fun setDefaultCategoryId(value: String) {
        context.dataStore.edit { it[Keys.DEFAULT_CATEGORY_ID] = value }
    }

    suspend fun setMonthlyBudgetLimit(value: Double) {
        context.dataStore.edit { it[Keys.MONTHLY_BUDGET_LIMIT] = value }
    }

    suspend fun setSavingsTarget(value: Double) {
        context.dataStore.edit { it[Keys.SAVINGS_TARGET] = value }
    }

    suspend fun setTrackBudgetCycles(value: Boolean) {
        context.dataStore.edit { it[Keys.TRACK_BUDGET_CYCLES] = value }
    }

    suspend fun setFontScale(value: Float) {
        context.dataStore.edit { it[Keys.FONT_SCALE] = value }
    }

    suspend fun setReducedMotion(value: Boolean) {
        context.dataStore.edit { it[Keys.REDUCED_MOTION] = value }
    }

    suspend fun setHighContrast(value: Boolean) {
        context.dataStore.edit { it[Keys.HIGH_CONTRAST] = value }
    }

    suspend fun setLargerTouchTargets(value: Boolean) {
        context.dataStore.edit { it[Keys.LARGER_TOUCH_TARGETS] = value }
    }

    suspend fun setDashboardSectionOrder(value: String) {
        context.dataStore.edit { it[Keys.DASHBOARD_SECTION_ORDER] = value }
    }

    suspend fun setPreferredChartType(value: String) {
        context.dataStore.edit { it[Keys.PREFERRED_CHART_TYPE] = value }
    }

    suspend fun setAnimationIntensity(value: String) {
        context.dataStore.edit { it[Keys.ANIMATION_INTENSITY] = value }
    }

    suspend fun setIconStyle(value: String) {
        context.dataStore.edit { it[Keys.ICON_STYLE] = value }
    }

    suspend fun setCardStyle(value: String) {
        context.dataStore.edit { it[Keys.CARD_STYLE] = value }
    }

    suspend fun setBorderRadiusStyle(value: String) {
        context.dataStore.edit { it[Keys.BORDER_RADIUS_STYLE] = value }
    }

    suspend fun setSwipeLeftAction(value: String) {
        context.dataStore.edit { it[Keys.SWIPE_LEFT_ACTION] = value }
    }

    suspend fun setSwipeRightAction(value: String) {
        context.dataStore.edit { it[Keys.SWIPE_RIGHT_ACTION] = value }
    }

    suspend fun setAutoCheckUpdates(value: Boolean) {
        context.dataStore.edit { it[Keys.AUTO_CHECK_UPDATES] = value }
    }

    suspend fun setLastUpdateCheckDate(value: Long) {
        context.dataStore.edit { it[Keys.LAST_UPDATE_CHECK_DATE] = value }
    }

    suspend fun setDismissedUpdateVersion(value: String) {
        context.dataStore.edit { it[Keys.DISMISSED_UPDATE_VERSION] = value }
    }

    suspend fun setFirstInstallDate(value: Long) {
        context.dataStore.edit { it[Keys.FIRST_INSTALL_DATE] = value }
    }

    suspend fun setLastSupportPopupDate(value: Long) {
        context.dataStore.edit { it[Keys.LAST_SUPPORT_POPUP_DATE] = value }
    }

    suspend fun setStoragePermissionDeferred(value: Boolean) {
        context.dataStore.edit { it[Keys.STORAGE_PERMISSION_DEFERRED] = value }
    }

    // Restore AppLogger.isEnabled from persisted settings during app startup.
    suspend fun syncAppLoggerState() {
        AppLogger.isEnabled = context.dataStore.data.map { prefs ->
            prefs[Keys.LOGS_ENABLED] ?: false
        }.first()
    }

    suspend fun setLogsEnabled(value: Boolean) {
        AppLogger.isEnabled = value
        context.dataStore.edit { it[Keys.LOGS_ENABLED] = value }
    }
}
