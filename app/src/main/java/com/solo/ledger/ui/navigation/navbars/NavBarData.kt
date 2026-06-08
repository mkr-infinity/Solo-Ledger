package com.solo.ledger.ui.navigation.navbars

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.solo.ledger.ui.navigation.NavRoutes

data class NavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val isQuickAdd: Boolean = false
)

val navItems = listOf(
    NavItem(NavRoutes.HOME, "Home", Icons.Outlined.Home, Icons.Filled.Home),
    NavItem(NavRoutes.HISTORY, "History", Icons.Outlined.Receipt, Icons.Filled.Receipt),
    NavItem(NavRoutes.QUICK_ADD, "Add", Icons.Filled.Add, Icons.Filled.Add, isQuickAdd = true),
    NavItem(NavRoutes.CALENDAR, "Calendar", Icons.Outlined.CalendarMonth, Icons.Filled.CalendarMonth),
    NavItem(NavRoutes.SETTINGS, "Settings", Icons.Outlined.Settings, Icons.Filled.Settings)
)
