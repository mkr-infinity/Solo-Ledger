package com.kaif.ledger.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kaif.ledger.ui.screens.SplashScreen
import com.kaif.ledger.ui.screens.OnboardingScreen1
import com.kaif.ledger.ui.screens.OnboardingScreen2
import com.kaif.ledger.ui.screens.HomeScreen
import com.kaif.ledger.ui.screens.SettingsScreen
import com.kaif.ledger.ui.screens.TrashScreen
import com.kaif.ledger.ui.screens.EditProfileScreen
import com.kaif.ledger.ui.screens.TransactionDetailScreen
import com.kaif.ledger.ui.screens.EditExpenseScreen
import com.kaif.ledger.ui.screens.CalendarDetailScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding1 : Screen("onboarding1")
    object Onboarding2 : Screen("onboarding2")
    object Home : Screen("home")
    object History : Screen("home?tab=1") // Tab within Home
    object Report : Screen("home?tab=2") // Tab within Home
    object Profile : Screen("home?tab=3") // Tab within Home
    object Settings : Screen("settings")
    object Trash : Screen("trash")
    object EditProfile : Screen("editProfile")
    object TransactionDetail : Screen("transactionDetail/{id}") {
        fun createRoute(id: Long) = "transactionDetail/$id"
    }
    object EditExpense : Screen("editExpense/{id}") {
        fun createRoute(id: Long) = "editExpense/$id"
    }
    object CalendarDetail : Screen("calendarDetail/{date}") {
        fun createRoute(date: String) = "calendarDetail/$date"
    }
}

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }
        composable(Screen.Onboarding1.route) {
            OnboardingScreen1(navController = navController)
        }
        composable(Screen.Onboarding2.route) {
            OnboardingScreen2(navController = navController)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
        composable(Screen.Trash.route) {
            TrashScreen(navController = navController)
        }
        composable(Screen.EditProfile.route) {
            EditProfileScreen(navController = navController)
        }
        composable(
            route = Screen.TransactionDetail.route,
            arguments = listOf(
                androidx.navigation.navArgument("id") { type = androidx.navigation.NavType.LongType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            TransactionDetailScreen(navController = navController, expenseId = id)
        }
        composable(
            route = Screen.EditExpense.route,
            arguments = listOf(
                androidx.navigation.navArgument("id") { type = androidx.navigation.NavType.LongType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            EditExpenseScreen(navController = navController, expenseId = id)
        }
        composable(
            route = Screen.CalendarDetail.route,
            arguments = listOf(
                androidx.navigation.navArgument("date") { type = androidx.navigation.NavType.StringType }
            )
        ) { backStackEntry ->
            val date = backStackEntry.arguments?.getString("date") ?: ""
            CalendarDetailScreen(navController = navController, date = date)
        }
    }
}
