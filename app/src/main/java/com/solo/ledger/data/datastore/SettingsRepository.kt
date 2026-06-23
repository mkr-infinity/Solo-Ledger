package com.solo.ledger.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "solo_ledger_settings")

data class AppSettings(
    val onboarded: Boolean = false,
    val userName: String = "",
    val monthlyBudget: Double = 0.0,
    val currency: String = "INR",
    val themeId: String = "ledger",
    val darkMode: Boolean = true,
    val navStyle: String = "capsule",
    val reducedMotion: Boolean = false,
    val fontScale: Float = 1.0f,
    val highContrast: Boolean = false,
    val cornerRadius: Int = 20,
    val avatarUri: String = "",
    val animationsEnabled: Boolean = true,
    val quickAddNotes: Boolean = true,
    val quickAddTime: Boolean = true,
    val hiddenWidgets: Set<String> = emptySet()
)

class SettingsRepository(context: Context) {
    private val ds = context.dataStore

    private object Keys {
        val ONBOARDED = booleanPreferencesKey("onboarded")
        val NAME = stringPreferencesKey("user_name")
        val BUDGET = doublePreferencesKey("monthly_budget")
        val CURRENCY = stringPreferencesKey("currency")
        val THEME = stringPreferencesKey("theme_id")
        val DARK = booleanPreferencesKey("dark_mode")
        val NAV = stringPreferencesKey("nav_style")
        val REDUCED = booleanPreferencesKey("reduced_motion")
        val FONT = floatPreferencesKey("font_scale")
        val CONTRAST = booleanPreferencesKey("high_contrast")
        val RADIUS = intPreferencesKey("corner_radius")
        val AVATAR = stringPreferencesKey("avatar_uri")
        val ANIM = booleanPreferencesKey("animations_enabled")
        val QA_NOTES = booleanPreferencesKey("qa_notes")
        val QA_TIME = booleanPreferencesKey("qa_time")
        val HIDDEN = stringSetPreferencesKey("hidden_widgets")
    }

    val settings: Flow<AppSettings> = ds.data.map { p ->
        AppSettings(
            onboarded = p[Keys.ONBOARDED] ?: false,
            userName = p[Keys.NAME] ?: "",
            monthlyBudget = p[Keys.BUDGET] ?: 0.0,
            currency = p[Keys.CURRENCY] ?: "INR",
            themeId = p[Keys.THEME] ?: "ledger",
            darkMode = p[Keys.DARK] ?: true,
            navStyle = p[Keys.NAV] ?: "capsule",
            reducedMotion = p[Keys.REDUCED] ?: false,
            fontScale = p[Keys.FONT] ?: 1.0f,
            highContrast = p[Keys.CONTRAST] ?: false,
            cornerRadius = p[Keys.RADIUS] ?: 20,
            avatarUri = p[Keys.AVATAR] ?: "",
            animationsEnabled = p[Keys.ANIM] ?: true,
            quickAddNotes = p[Keys.QA_NOTES] ?: true,
            quickAddTime = p[Keys.QA_TIME] ?: true,
            hiddenWidgets = p[Keys.HIDDEN] ?: emptySet()
        )
    }

    suspend fun completeOnboarding(name: String, budget: Double) = ds.edit {
        it[Keys.ONBOARDED] = true; it[Keys.NAME] = name; it[Keys.BUDGET] = budget
    }
    suspend fun setName(v: String) = ds.edit { it[Keys.NAME] = v }
    suspend fun setBudget(v: Double) = ds.edit { it[Keys.BUDGET] = v }
    suspend fun setCurrency(v: String) = ds.edit { it[Keys.CURRENCY] = v }
    suspend fun setTheme(v: String) = ds.edit { it[Keys.THEME] = v }
    suspend fun setDark(v: Boolean) = ds.edit { it[Keys.DARK] = v }
    suspend fun setNavStyle(v: String) = ds.edit { it[Keys.NAV] = v }
    suspend fun setReducedMotion(v: Boolean) = ds.edit { it[Keys.REDUCED] = v }
    suspend fun setFontScale(v: Float) = ds.edit { it[Keys.FONT] = v }
    suspend fun setHighContrast(v: Boolean) = ds.edit { it[Keys.CONTRAST] = v }
    suspend fun setCornerRadius(v: Int) = ds.edit { it[Keys.RADIUS] = v }
    suspend fun setAvatar(v: String) = ds.edit { it[Keys.AVATAR] = v }
    suspend fun setAnimations(v: Boolean) = ds.edit { it[Keys.ANIM] = v }
    suspend fun setQuickAddNotes(v: Boolean) = ds.edit { it[Keys.QA_NOTES] = v }
    suspend fun setQuickAddTime(v: Boolean) = ds.edit { it[Keys.QA_TIME] = v }
    suspend fun toggleWidget(key: String, hidden: Boolean) = ds.edit { p ->
        val cur = (p[Keys.HIDDEN] ?: emptySet()).toMutableSet()
        if (hidden) cur.add(key) else cur.remove(key)
        p[Keys.HIDDEN] = cur
    }
}
