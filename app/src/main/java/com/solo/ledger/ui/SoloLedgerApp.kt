package com.solo.ledger.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.solo.ledger.ui.theme.LedgerTheme
import com.solo.ledger.ui.theme.LocalLedgerColors
import com.solo.ledger.ui.theme.SoloLedgerTheme

@Composable
fun SoloLedgerApp() {
    SoloLedgerTheme(theme = LedgerTheme.LedgerDark) {
        var selectedTab by remember { mutableStateOf(LedgerDestination.Home) }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            containerColor = MaterialTheme.colorScheme.background,
            floatingActionButton = {
                QuickAddButton(onClick = { selectedTab = LedgerDestination.QuickAdd })
            },
            bottomBar = {
                LedgerBottomBar(
                    selected = selectedTab,
                    onSelected = { selectedTab = it },
                )
            },
        ) { innerPadding ->
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    (fadeIn(spring()) + scaleIn(initialScale = 0.98f)) togetherWith
                        (fadeOut(spring()) + scaleOut(targetScale = 0.98f))
                },
                label = "ledger-screen",
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding),
            ) { destination ->
                ScreenShell(destination = destination)
            }
        }
    }
}

@Composable
private fun ScreenShell(destination: LedgerDestination) {
    val ledgerColors = LocalLedgerColors.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = destination.title,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = destination.description,
                style = MaterialTheme.typography.bodyMedium,
                color = ledgerColors.muted,
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = ledgerColors.card),
            border = BorderStroke(1.dp, ledgerColors.outline),
            shape = RoundedCornerShape(28.dp),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = destination.emptyTitle,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = destination.emptyBody,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun QuickAddButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = Color.White,
        shape = CircleShape,
    ) {
        Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = "Quick Add",
        )
    }
}

@Composable
private fun LedgerBottomBar(
    selected: LedgerDestination,
    onSelected: (LedgerDestination) -> Unit,
) {
    val ledgerColors = LocalLedgerColors.current
    val destinations = LedgerDestination.entries.filterNot { it == LedgerDestination.QuickAdd }

    Row(
        modifier = Modifier
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(ledgerColors.card)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        destinations.forEach { destination ->
            LedgerNavItem(
                destination = destination,
                selected = destination == selected,
                onClick = { onSelected(destination) },
            )
        }
    }
}

@Composable
private fun LedgerNavItem(
    destination: LedgerDestination,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val ledgerColors = LocalLedgerColors.current

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(22.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .background(if (selected) ledgerColors.navSelected else Color.Transparent)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = destination.icon,
            contentDescription = destination.title,
            tint = if (selected) MaterialTheme.colorScheme.primary else ledgerColors.muted,
            modifier = Modifier.size(22.dp),
        )
        AnimatedVisibility(
            visible = selected,
            enter = fadeIn() + scaleIn(initialScale = 0.92f),
            exit = fadeOut() + scaleOut(targetScale = 0.92f),
        ) {
            Text(
                text = destination.title,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

private enum class LedgerDestination(
    val title: String,
    val description: String,
    val emptyTitle: String,
    val emptyBody: String,
    val icon: ImageVector,
) {
    Home(
        title = "Home",
        description = "Monthly budget, spending awareness, savings goals, and insights will live here.",
        emptyTitle = "No Expenses Yet",
        emptyBody = "Add your first expense from Quick Add to begin tracking your monthly budget offline.",
        icon = Icons.Outlined.Home,
    ),
    History(
        title = "History",
        description = "Searchable, grouped transactions will appear here after expenses are saved.",
        emptyTitle = "No Transaction History",
        emptyBody = "Deleted, edited, and restored expense records will be tracked locally in later slices.",
        icon = Icons.Outlined.History,
    ),
    QuickAdd(
        title = "Quick Add",
        description = "Expense capture is the next implementation slice after the foundation is committed.",
        emptyTitle = "Expense Form Not Built Yet",
        emptyBody = "This slice establishes app identity, theme rules, and navigation before persistence work begins.",
        icon = Icons.Outlined.Add,
    ),
    Calendar(
        title = "Calendar",
        description = "Spending days, date details, and range filters will use local transaction data.",
        emptyTitle = "No Calendar Spending",
        emptyBody = "Calendar analytics will activate when Room-backed expenses are implemented.",
        icon = Icons.Outlined.CalendarMonth,
    ),
    Settings(
        title = "Settings",
        description = "Appearance, dashboard controls, data export, accessibility, and support options.",
        emptyTitle = "Settings Foundation",
        emptyBody = "Theme selection, font scaling, radius, and data controls will be wired to DataStore next.",
        icon = Icons.Outlined.Settings,
    ),
}

@Preview(showBackground = true)
@Composable
private fun SoloLedgerAppPreview() {
    SoloLedgerApp()
}
