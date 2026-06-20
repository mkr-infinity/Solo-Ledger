package com.solo.ledger.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.solo.ledger.ui.navigation.navbars.NavBarHost
import com.solo.ledger.ui.screens.about.AboutScreen
import com.solo.ledger.ui.screens.analytics.AnalyticsScreen
import com.solo.ledger.ui.screens.architect.ArchitectScreen
import com.solo.ledger.ui.screens.calendar.CalendarScreen
import com.solo.ledger.ui.screens.checkupdates.CheckUpdatesScreen
import com.solo.ledger.ui.screens.comingsoon.ComingSoonScreen
import com.solo.ledger.ui.screens.deleted.DeletedScreen
import com.solo.ledger.ui.screens.quickadd.EditTransactionScreen
import com.solo.ledger.ui.screens.history.HistoryScreen
import com.solo.ledger.ui.screens.home.HomeScreen
import com.solo.ledger.ui.screens.quickadd.QuickAddScreen
import com.solo.ledger.ui.screens.settings.SettingsScreen
import com.solo.ledger.ui.screens.support.SupportScreen
import com.solo.ledger.ui.screens.logs.LogsScreen
import com.solo.ledger.ui.theme.NavigationStyle

@Composable
fun AppNavigation(
    navController: NavHostController,
    navigationStyle: NavigationStyle,
    onQuickAddClick: () -> Unit
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val selectedIndex = bottomNavRoutes.indexOf(currentRoute).let { index ->
        if (index == -1) -1 else index
    }

    val showBottomNav = currentRoute in bottomNavRoutes || selectedIndex != -1
    val isTelegramStyle = navigationStyle == NavigationStyle.TELEGRAM

    val handleItemSelected: (Int) -> Unit = { index ->
        val route = bottomNavRoutes.getOrNull(index)
        if (route != null) {
            if (route == NavRoutes.QUICK_ADD) {
                onQuickAddClick()
            } else if (route != currentRoute) {
                navController.navigate(route) {
                    popUpTo(NavRoutes.HOME) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    }

    Scaffold(
        topBar = {
            if (isTelegramStyle && showBottomNav) {
                NavBarHost(
                    selectedIndex = selectedIndex.coerceAtLeast(0),
                    onItemSelected = handleItemSelected,
                    navigationStyle = navigationStyle
                )
            }
        },
        bottomBar = {
            if (!isTelegramStyle && showBottomNav) {
                NavBarHost(
                    selectedIndex = selectedIndex.coerceAtLeast(0),
                    onItemSelected = handleItemSelected,
                    navigationStyle = navigationStyle
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavRoutes.HOME,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(NavRoutes.HOME) {
                HomeScreen(navController = navController, onQuickAddClick = onQuickAddClick)
            }
            composable(NavRoutes.HISTORY) {
                HistoryScreen(navController = navController, onQuickAddClick = onQuickAddClick)
            }
            composable(NavRoutes.QUICK_ADD) {
                QuickAddScreen(navController = navController)
            }
            composable(NavRoutes.CALENDAR) {
                CalendarScreen(navController = navController, onQuickAddClick = onQuickAddClick)
            }
            composable(NavRoutes.SETTINGS) {
                SettingsScreen(navController = navController)
            }
            composable(NavRoutes.ANALYTICS) {
                AnalyticsScreen(navController = navController)
            }
            composable(NavRoutes.ABOUT) {
                AboutScreen(navController = navController)
            }
            composable(NavRoutes.ARCHITECT) {
                ArchitectScreen(navController = navController)
            }
            composable(NavRoutes.SUPPORT) {
                SupportScreen(navController = navController)
            }
            composable(NavRoutes.COMING_SOON) {
                ComingSoonScreen(navController = navController)
            }
            composable(NavRoutes.DELETED) {
                DeletedScreen(navController = navController)
            }
            composable(NavRoutes.CHECK_UPDATES) {
                CheckUpdatesScreen(navController = navController)
            }
            composable(NavRoutes.LOGS) {
                LogsScreen(navController = navController)
            }
            composable(
                route = NavRoutes.EDIT_TRANSACTION,
                arguments = listOf(
                    navArgument("transactionId") { type = NavType.StringType }
                )
            ) { backStack ->
                val transactionId = backStack.arguments?.getString("transactionId") ?: ""
                EditTransactionScreen(
                    navController = navController,
                    transactionId = transactionId
                )
            }
        }

    }
}
