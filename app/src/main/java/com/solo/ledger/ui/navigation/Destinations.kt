package com.solo.ledger.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class Dest(val route: String, val label: String, val icon: ImageVector) {
    Home("home", "Home", Icons.Outlined.Home),
    History("history", "History", Icons.Outlined.History),
    Calendar("calendar", "Calendar", Icons.Outlined.CalendarMonth),
    Settings("settings", "Settings", Icons.Outlined.Settings);

    companion object {
        // Order in the bar; Quick Add FAB sits in the visual center.
        val barItems = listOf(Home, History, Calendar, Settings)
    }
}

object Routes {
    const val QUICK_ADD = "quick_add"
    const val BIN = "bin"
    const val ANALYTICS = "analytics"
    const val GOALS = "goals"
    const val PROFILE = "profile"
    const val CATEGORIES = "categories"
}
