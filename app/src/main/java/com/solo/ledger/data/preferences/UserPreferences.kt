package com.solo.ledger.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "solo_ledger_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        val KEY_USER_NAME = stringPreferencesKey("user_name")
        val KEY_MONTHLY_BUDGET = doublePreferencesKey("monthly_budget")
        val KEY_CURRENCY_CODE = stringPreferencesKey("currency_code")
        val KEY_CURRENCY_SYMBOL = stringPreferencesKey("currency_symbol")
        val KEY_THEME = stringPreferencesKey("theme")
        val KEY_NAVIGATION_STYLE = stringPreferencesKey("navigation_style")
        val KEY_ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
        val KEY_FONT_SIZE = stringPreferencesKey("font_size")
        val KEY_ANIMATIONS_ENABLED = booleanPreferencesKey("animations_enabled")
        val KEY_BORDER_RADIUS = floatPreferencesKey("border_radius")
        val KEY_DASHBOARD_ORDER = stringPreferencesKey("dashboard_order")
        val KEY_AVATAR_PATH = stringPreferencesKey("avatar_path")
    }

    val userName: Flow<String> = context.dataStore.data.map { it[KEY_USER_NAME] ?: "" }
    val monthlyBudget: Flow<Double> = context.dataStore.data.map { it[KEY_MONTHLY_BUDGET] ?: 10000.0 }
    val currencyCode: Flow<String> = context.dataStore.data.map { it[KEY_CURRENCY_CODE] ?: "INR" }
    val currencySymbol: Flow<String> = context.dataStore.data.map { it[KEY_CURRENCY_SYMBOL] ?: "\u20B9" }
    val theme: Flow<String> = context.dataStore.data.map { it[KEY_THEME] ?: "ledger_dark" }
    val navigationStyle: Flow<String> = context.dataStore.data.map { it[KEY_NAVIGATION_STYLE] ?: "capsule" }
    val onboardingComplete: Flow<Boolean> = context.dataStore.data.map { it[KEY_ONBOARDING_COMPLETE] ?: false }
    val fontSize: Flow<String> = context.dataStore.data.map { it[KEY_FONT_SIZE] ?: "medium" }
    val animationsEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_ANIMATIONS_ENABLED] ?: true }
    val borderRadius: Flow<Float> = context.dataStore.data.map { it[KEY_BORDER_RADIUS] ?: 16f }
    val dashboardOrder: Flow<String> = context.dataStore.data.map { it[KEY_DASHBOARD_ORDER] ?: "budget,daily,savings,recent,category,graph" }
    val avatarPath: Flow<String> = context.dataStore.data.map { it[KEY_AVATAR_PATH] ?: "" }

    suspend fun setUserName(name: String) {
        context.dataStore.edit { it[KEY_USER_NAME] = name }
    }

    suspend fun setMonthlyBudget(budget: Double) {
        context.dataStore.edit { it[KEY_MONTHLY_BUDGET] = budget }
    }

    suspend fun setCurrencyCode(code: String) {
        context.dataStore.edit { it[KEY_CURRENCY_CODE] = code }
    }

    suspend fun setCurrencySymbol(symbol: String) {
        context.dataStore.edit { it[KEY_CURRENCY_SYMBOL] = symbol }
    }

    suspend fun setTheme(theme: String) {
        context.dataStore.edit { it[KEY_THEME] = theme }
    }

    suspend fun setNavigationStyle(style: String) {
        context.dataStore.edit { it[KEY_NAVIGATION_STYLE] = style }
    }

    suspend fun setOnboardingComplete(complete: Boolean) {
        context.dataStore.edit { it[KEY_ONBOARDING_COMPLETE] = complete }
    }

    suspend fun setFontSize(size: String) {
        context.dataStore.edit { it[KEY_FONT_SIZE] = size }
    }

    suspend fun setAnimationsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_ANIMATIONS_ENABLED] = enabled }
    }

    suspend fun setBorderRadius(radius: Float) {
        context.dataStore.edit { it[KEY_BORDER_RADIUS] = radius }
    }

    suspend fun setDashboardOrder(order: String) {
        context.dataStore.edit { it[KEY_DASHBOARD_ORDER] = order }
    }

    suspend fun setAvatarPath(path: String) {
        context.dataStore.edit { it[KEY_AVATAR_PATH] = path }
    }
}
