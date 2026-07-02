package com.solo.ledger.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.solo.ledger.SoloLedgerApp
import com.solo.ledger.ui.navigation.*
import com.solo.ledger.ui.screens.about.AboutScreen
import com.solo.ledger.ui.screens.analytics.AnalyticsScreen
import com.solo.ledger.ui.screens.bin.BinScreen
import com.solo.ledger.ui.screens.budget.BudgetTemplatesScreen
import com.solo.ledger.ui.screens.calendar.CalendarScreen
import com.solo.ledger.ui.screens.categories.CategoriesScreen
import com.solo.ledger.ui.screens.comingsoon.ComingSoonScreen
import com.solo.ledger.ui.screens.data.DataManagementScreen
import com.solo.ledger.ui.screens.expense.AddExpenseScreen
import com.solo.ledger.ui.screens.expense.EditExpenseScreen
import com.solo.ledger.ui.screens.history.HistoryScreen
import com.solo.ledger.ui.screens.home.HomeScreen
import com.solo.ledger.ui.screens.onboarding.OnboardingScreen
import com.solo.ledger.ui.screens.profile.ProfileScreen
import com.solo.ledger.ui.screens.settings.SettingsScreen
import com.solo.ledger.ui.screens.settings.NavigationStyleScreen
import com.solo.ledger.ui.screens.settings.ThemeSelectorScreen
import com.solo.ledger.ui.screens.support.SupportScreen
import com.solo.ledger.ui.theme.AppTheme
import com.solo.ledger.ui.theme.SoloLedgerTheme
import com.solo.ledger.ui.viewmodel.MainViewModel
import com.solo.ledger.ui.viewmodel.MainViewModelFactory

@Composable
fun SoloLedgerMainApp() {
    val app = SoloLedgerApp.instance
    val viewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory(
            app.expenseRepository,
            app.categoryRepository,
            app.savingsGoalRepository,
            app.userPreferences
        )
    )

    val themeKey by viewModel.currentTheme.collectAsStateWithLifecycle()
    val navStyle by viewModel.navigationStyle.collectAsStateWithLifecycle()
    val onboardingComplete by viewModel.onboardingComplete.collectAsStateWithLifecycle()

    val appTheme = AppTheme.fromKey(themeKey)
    val navigationStyle = NavigationStyle.fromKey(navStyle)

    SoloLedgerTheme(appTheme = appTheme) {
        val navController = rememberNavController()
        val startDestination = if (onboardingComplete) Screen.Home.route else Screen.Onboarding.route

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        // Log navigation changes
        LaunchedEffect(currentRoute) {
            currentRoute?.let { route ->
                viewModel.log(
                    com.solo.ledger.data.model.LogType.SCREEN_OPENED,
                    "Screen: $route"
                )
            }
        }

        val bottomNavRoutes = listOf(
            Screen.Home.route,
            Screen.History.route,
            Screen.AddExpense.route,
            Screen.Calendar.route,
            Screen.Settings.route
        )

        val showBottomBar = currentRoute in bottomNavRoutes

        // Use Box instead of Scaffold bottomBar to overlay nav on content
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background
            ) { paddingValues ->
                NavHost(
                    navController = navController,
                    startDestination = startDestination,
                    modifier = Modifier.padding(paddingValues),
                    enterTransition = { fadeIn(animationSpec = tween(300)) },
                    exitTransition = { fadeOut(animationSpec = tween(300)) },
                    popEnterTransition = { fadeIn(animationSpec = tween(300)) },
                    popExitTransition = { fadeOut(animationSpec = tween(300)) }
                ) {
                composable(Screen.Onboarding.route) {
                    OnboardingScreen(
                        viewModel = viewModel,
                        onComplete = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Onboarding.route) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Screen.Home.route) {
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigateToAnalytics = { navController.navigate(Screen.Analytics.route) },
                        onNavigateToSavings = { navController.navigate(Screen.SavingsGoals.route) },
                        onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                        onNavigateToHistory = {
                            navController.navigate(Screen.History.route) {
                                popUpTo(Screen.Home.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }

                composable(Screen.History.route) {
                    HistoryScreen(
                        viewModel = viewModel,
                        onEditExpense = { id ->
                            navController.navigate(Screen.EditExpense.createRoute(id))
                        },
                        onNavigateToBin = { navController.navigate(Screen.Bin.route) }
                    )
                }

                composable(Screen.AddExpense.route) {
                    AddExpenseScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(
                    route = Screen.EditExpense.route,
                    arguments = listOf(navArgument("expenseId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val expenseId = backStackEntry.arguments?.getLong("expenseId") ?: 0L
                    EditExpenseScreen(
                        viewModel = viewModel,
                        expenseId = expenseId,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.Calendar.route) {
                    CalendarScreen(viewModel = viewModel)
                }

                composable(Screen.Settings.route) {
                    SettingsScreen(
                        viewModel = viewModel,
                        onNavigateToTheme = { navController.navigate(Screen.ThemeSelector.route) },
                        onNavigateToNavStyle = { navController.navigate(Screen.NavigationStyleSelector.route) },
                        onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                        onNavigateToCategories = { navController.navigate(Screen.Categories.route) },
                        onNavigateToData = { navController.navigate(Screen.DataManagement.route) },
                        onNavigateToSupport = { navController.navigate(Screen.Support.route) },
                        onNavigateToAbout = { navController.navigate(Screen.About.route) },
                        onNavigateToComingSoon = { navController.navigate(Screen.ComingSoon.route) },
                        onNavigateToBudgetTemplates = { navController.navigate(Screen.BudgetTemplates.route) },
                        onNavigateToSavingsGoals = { navController.navigate(Screen.SavingsGoals.route) },
                        onNavigateToBin = { navController.navigate(Screen.Bin.route) },
                        onNavigateToLogs = { navController.navigate(Screen.Logs.route) },
                        onNavigateToUpdates = { navController.navigate(Screen.Updates.route) }
                    )
                }

                composable(Screen.Analytics.route) {
                    AnalyticsScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.Categories.route) {
                    CategoriesScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.SavingsGoals.route) {
                    com.solo.ledger.ui.screens.savings.SavingsGoalsScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.Bin.route) {
                    BinScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.Profile.route) {
                    ProfileScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.BudgetTemplates.route) {
                    BudgetTemplatesScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.Support.route) {
                    SupportScreen(onNavigateBack = { navController.popBackStack() })
                }

                composable(Screen.About.route) {
                    AboutScreen(onNavigateBack = { navController.popBackStack() })
                }

                composable(Screen.ComingSoon.route) {
                    ComingSoonScreen(onNavigateBack = { navController.popBackStack() })
                }

                composable(Screen.ThemeSelector.route) {
                    ThemeSelectorScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.NavigationStyleSelector.route) {
                    NavigationStyleScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.DataManagement.route) {
                    DataManagementScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.Logs.route) {
                    com.solo.ledger.ui.screens.logs.LogsScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.Updates.route) {
                    com.solo.ledger.ui.screens.update.UpdateScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }

            // Navigation bar overlay - floats on top of content
            if (showBottomBar) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                ) {
                    SoloLedgerBottomBar(
                        items = BottomNavItem.entries.toList(),
                        currentRoute = currentRoute,
                        navigationStyle = navigationStyle,
                        onItemClick = { item ->
                            if (item.route == Screen.AddExpense.route) {
                                navController.navigate(Screen.AddExpense.route) {
                                    launchSingleTop = true
                                }
                            } else {
                                navController.navigate(item.route) {
                                    popUpTo(Screen.Home.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }

        // Toast overlay at bottom
        val toastData by viewModel.currentToast.collectAsStateWithLifecycle()
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            com.solo.ledger.ui.components.AppToast(
                toastData = toastData,
                onDismiss = { viewModel.dismissToast() }
            )
        }

        // Support popup
        val showPopup by viewModel.showSupportPopup.collectAsStateWithLifecycle()
        if (showPopup) {
            com.solo.ledger.ui.components.SupportPopup(
                onDismiss = { viewModel.dismissSupportPopup() },
                onMaybeLater = { viewModel.dismissSupportPopup() }
            )
        }
    }
}
