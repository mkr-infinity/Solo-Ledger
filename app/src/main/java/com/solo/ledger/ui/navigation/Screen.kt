package com.solo.ledger.ui.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object History : Screen("history")
    object Calendar : Screen("calendar")
    object Settings : Screen("settings")
    object AddExpense : Screen("add_expense")
    object EditExpense : Screen("edit_expense/{expenseId}") {
        fun createRoute(expenseId: Long) = "edit_expense/$expenseId"
    }
    object Analytics : Screen("analytics")
    object Categories : Screen("categories")
    object SavingsGoals : Screen("savings_goals")
    object Bin : Screen("bin")
    object Profile : Screen("profile")
    object BudgetTemplates : Screen("budget_templates")
    object Support : Screen("support")
    object About : Screen("about")
    object ComingSoon : Screen("coming_soon")
    object ThemeSelector : Screen("theme_selector")
    object NavigationStyleSelector : Screen("navigation_style_selector")
    object DataManagement : Screen("data_management")
}
