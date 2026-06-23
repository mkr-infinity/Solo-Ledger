package com.solo.ledger.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.*
import com.solo.ledger.ui.AppViewModel
import com.solo.ledger.ui.components.LedgerBottomBar
import com.solo.ledger.ui.screens.analytics.AnalyticsScreen
import com.solo.ledger.ui.screens.bin.BinScreen
import com.solo.ledger.ui.screens.calendar.CalendarScreen
import com.solo.ledger.ui.screens.edit.EditExpenseScreen
import com.solo.ledger.ui.screens.categories.CategoriesScreen
import com.solo.ledger.ui.screens.goals.GoalsScreen
import com.solo.ledger.ui.screens.profile.ProfileScreen
import com.solo.ledger.ui.screens.history.HistoryScreen
import com.solo.ledger.ui.screens.home.HomeScreen
import com.solo.ledger.ui.screens.quickadd.QuickAddScreen
import com.solo.ledger.ui.screens.settings.SettingsScreen
import com.solo.ledger.ui.theme.LedgerTheme

@Composable
fun SoloLedgerApp(appVm: AppViewModel) {
    val nav = rememberNavController()
    val settings by appVm.settings.collectAsState()
    val backStack by nav.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route ?: Dest.Home.route
    val c = LedgerTheme.colors

    val showBar = currentRoute in Dest.barItems.map { it.route }

    Scaffold(
        containerColor = c.background,
        bottomBar = {
            if (showBar) LedgerBottomBar(
                style = settings.navStyle,
                current = currentRoute,
                onSelect = { dest ->
                    nav.navigate(dest.route) {
                        popUpTo(nav.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true; restoreState = true
                    }
                },
                onQuickAdd = { nav.navigate(Routes.QUICK_ADD) }
            )
        }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = Dest.Home.route,
            modifier = Modifier.padding(padding).fillMaxSize()
        ) { ledgerGraph(nav, appVm) }
    }
}

private fun NavGraphBuilder.ledgerGraph(nav: androidx.navigation.NavController, appVm: AppViewModel) {
    composable(Dest.Home.route) { HomeScreen(nav, appVm) }
    composable(Dest.History.route) { HistoryScreen(nav) }
    composable(Dest.Calendar.route) { CalendarScreen(nav) }
    composable(Dest.Settings.route) { SettingsScreen(nav, appVm) }
    composable(Routes.QUICK_ADD) { QuickAddScreen(nav) }
    composable(Routes.ANALYTICS) { AnalyticsScreen(nav) }
    composable(Routes.GOALS) { GoalsScreen(nav) }
    composable(Routes.BIN) { BinScreen(nav) }
    composable(Routes.PROFILE) { ProfileScreen(nav, appVm) }
    composable(Routes.CATEGORIES) { CategoriesScreen(nav) }
    composable(
        route = "edit/{id}",
        arguments = listOf(androidx.navigation.navArgument("id") { type = androidx.navigation.NavType.LongType })
    ) { entry -> EditExpenseScreen(nav, entry.arguments?.getLong("id") ?: 0L) }
}
