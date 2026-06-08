package com.solo.ledger.ui.navigation.navbars

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.solo.ledger.ui.theme.NavigationStyle

@Composable
fun NavBarHost(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    navigationStyle: NavigationStyle,
    modifier: Modifier = Modifier
) {
    when (navigationStyle) {
        NavigationStyle.BOTTOM_STANDARD -> BottomNavBar(
            items = navItems,
            selectedIndex = selectedIndex,
            onItemSelected = onItemSelected,
            modifier = modifier
        )
        NavigationStyle.FLOATING_PILL -> FloatingNavBar(
            items = navItems,
            selectedIndex = selectedIndex,
            onItemSelected = onItemSelected,
            modifier = modifier
        )
        NavigationStyle.CAPSULE -> CapsuleNavBar(
            items = navItems,
            selectedIndex = selectedIndex,
            onItemSelected = onItemSelected,
            modifier = modifier
        )
        NavigationStyle.TELEGRAM -> TelegramNavBar(
            items = navItems,
            selectedIndex = selectedIndex,
            onItemSelected = onItemSelected,
            modifier = modifier
        )
        NavigationStyle.MATERIAL3 -> MaterialNavBar(
            items = navItems,
            selectedIndex = selectedIndex,
            onItemSelected = onItemSelected,
            modifier = modifier
        )
        NavigationStyle.COMPACT -> CompactNavBar(
            items = navItems,
            selectedIndex = selectedIndex,
            onItemSelected = onItemSelected,
            modifier = modifier
        )
        NavigationStyle.GLASS -> GlassNavBar(
            items = navItems,
            selectedIndex = selectedIndex,
            onItemSelected = onItemSelected,
            modifier = modifier
        )
    }
}
