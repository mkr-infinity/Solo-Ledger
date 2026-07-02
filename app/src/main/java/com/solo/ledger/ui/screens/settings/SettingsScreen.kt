package com.solo.ledger.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.solo.ledger.ui.navigation.NavigationStyle
import com.solo.ledger.ui.theme.AppTheme
import com.solo.ledger.ui.viewmodel.MainViewModel

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onNavigateToTheme: () -> Unit,
    onNavigateToNavStyle: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToCategories: () -> Unit,
    onNavigateToData: () -> Unit,
    onNavigateToSupport: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToComingSoon: () -> Unit,
    onNavigateToBudgetTemplates: () -> Unit,
    onNavigateToSavingsGoals: () -> Unit,
    onNavigateToBin: () -> Unit,
    onNavigateToLogs: () -> Unit,
    onNavigateToUpdates: () -> Unit
) {
    val animationsEnabled by viewModel.animationsEnabled.collectAsStateWithLifecycle()
    val currentThemeKey by viewModel.currentTheme.collectAsStateWithLifecycle()
    val currentNavStyle by viewModel.navigationStyle.collectAsStateWithLifecycle()
    val borderRadius by viewModel.borderRadius.collectAsStateWithLifecycle()

    val cardShape = RoundedCornerShape(borderRadius.dp)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Theme Selection with horizontal preview carousel
        item {
            SettingsSectionHeader("Appearance")
        }

        item {
            Text(
                text = "Theme",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            ThemeCarousel(
                currentThemeKey = currentThemeKey,
                onThemeSelected = { viewModel.setTheme(it) },
                cardShape = cardShape
            )
        }

        // Navigation Style with horizontal preview carousel
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Navigation",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            NavigationStyleCarousel(
                currentStyleKey = currentNavStyle,
                onStyleSelected = { viewModel.setNavigationStyle(it) },
                cardShape = cardShape
            )
        }

        // Animations toggle
        item {
            SettingsToggleItem(
                icon = Icons.Filled.Animation,
                title = "Animations",
                subtitle = "Enable smooth transitions",
                checked = animationsEnabled,
                onCheckedChange = { viewModel.updateAnimationsEnabled(it) },
                cardShape = cardShape
            )
        }

        // Profile Section
        item {
            SettingsSectionHeader("Account")
        }
        item {
            SettingsItem(
                icon = Icons.Filled.Person,
                title = "Profile",
                subtitle = "Name, avatar, currency",
                onClick = onNavigateToProfile,
                cardShape = cardShape
            )
        }

        // Budget Section
        item {
            SettingsSectionHeader("Budget")
        }
        item {
            SettingsItem(
                icon = Icons.Filled.Category,
                title = "Categories",
                subtitle = "Manage expense categories",
                onClick = onNavigateToCategories,
                cardShape = cardShape
            )
        }
        item {
            SettingsItem(
                icon = Icons.Filled.Description,
                title = "Budget Templates",
                subtitle = "Pre-made budget plans",
                onClick = onNavigateToBudgetTemplates,
                cardShape = cardShape
            )
        }
        item {
            SettingsItem(
                icon = Icons.Filled.Savings,
                title = "Savings Goals",
                subtitle = "Track your savings targets",
                onClick = onNavigateToSavingsGoals,
                cardShape = cardShape
            )
        }

        // Data Section
        item {
            SettingsSectionHeader("Data")
        }
        item {
            SettingsItem(
                icon = Icons.Filled.ImportExport,
                title = "Import / Export",
                subtitle = "Backup and restore data",
                onClick = onNavigateToData,
                cardShape = cardShape
            )
        }
        item {
            SettingsItem(
                icon = Icons.Filled.DeleteSweep,
                title = "Bin",
                subtitle = "Recover deleted expenses",
                onClick = onNavigateToBin,
                cardShape = cardShape
            )
        }

        // Other Section
        item {
            SettingsSectionHeader("Other")
        }
        item {
            val logsEnabled by viewModel.logsEnabled.collectAsStateWithLifecycle()
            SettingsToggleItem(
                icon = Icons.Filled.History,
                title = "Activity Logging",
                subtitle = "Record all actions in the app",
                checked = logsEnabled,
                onCheckedChange = { viewModel.setLogsEnabled(it) },
                cardShape = cardShape
            )
        }
        item {
            SettingsItem(
                icon = Icons.Filled.Article,
                title = "Logs",
                subtitle = "View activity history and export",
                onClick = onNavigateToLogs,
                cardShape = cardShape
            )
        }
        item {
            SettingsItem(
                icon = Icons.Filled.Update,
                title = "Updates",
                subtitle = "Check for new versions",
                onClick = onNavigateToUpdates,
                cardShape = cardShape
            )
        }
        item {
            SettingsItem(
                icon = Icons.Filled.Favorite,
                title = "Support",
                subtitle = "Help us grow",
                onClick = onNavigateToSupport,
                cardShape = cardShape
            )
        }
        item {
            SettingsItem(
                icon = Icons.Filled.Info,
                title = "About",
                subtitle = "App info and developer",
                onClick = onNavigateToAbout,
                cardShape = cardShape
            )
        }

        // Version easter egg
        item {
            VersionEasterEgg(cardShape = cardShape, viewModel = viewModel)
        }
    }
}

@Composable
private fun VersionEasterEgg(cardShape: RoundedCornerShape, viewModel: MainViewModel) {
    var tapCount by remember { mutableIntStateOf(0) }

    val messages = listOf(
        "Stop poking me!",
        "I am just a version number, relax.",
        "Okay, you found me. Now what?",
        "Do you expect me to do a trick?",
        "Fine, I will just sit here.",
        "You really have nothing better to do?",
        "Still going? Respect.",
        "Alright alright, you win. Happy now?",
        "This is getting out of hand.",
        "Go track some expenses instead!",
        "I am not hiding any secrets, I promise.",
        "You must be fun at parties.",
        "Achievement unlocked: Version Tapper."
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                tapCount++
                if (tapCount == 5) {
                    // First time reaching 5 taps - show support popup
                    viewModel.showSupportPopup.value = true
                } else if (tapCount > 5) {
                    viewModel.showToast(
                        messages[(tapCount - 6) % messages.size],
                        com.solo.ledger.ui.components.ToastType.FUN
                    )
                }
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = cardShape
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "v1.0.0",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ThemeCarousel(
    currentThemeKey: String,
    onThemeSelected: (String) -> Unit,
    cardShape: RoundedCornerShape
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AppTheme.entries.forEach { theme ->
            val themeKey = AppTheme.toKey(theme)
            val isSelected = themeKey == currentThemeKey
            val colorScheme = com.solo.ledger.ui.theme.getColorScheme(theme)
            val previewRadius = if (theme.isSquare) 0.dp else 12.dp

            Column(
                modifier = Modifier
                    .width(100.dp)
                    .clickable { onThemeSelected(themeKey) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Mini preview card
                Card(
                    modifier = Modifier
                        .width(100.dp)
                        .height(70.dp)
                        .then(
                            if (isSelected) Modifier.border(
                                2.dp,
                                MaterialTheme.colorScheme.primary,
                                RoundedCornerShape(previewRadius)
                            ) else Modifier
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.background
                    ),
                    shape = RoundedCornerShape(previewRadius)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Mini header
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(previewRadius / 2))
                                .background(colorScheme.primary)
                        )
                        // Mini cards
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(16.dp)
                                    .clip(RoundedCornerShape(previewRadius / 3))
                                    .background(colorScheme.surface)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(16.dp)
                                    .clip(RoundedCornerShape(previewRadius / 3))
                                    .background(colorScheme.surfaceVariant)
                            )
                        }
                        // Mini nav
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(previewRadius / 2))
                                .background(colorScheme.primaryContainer)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = theme.displayName,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    maxLines = 1,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun NavigationStyleCarousel(
    currentStyleKey: String,
    onStyleSelected: (String) -> Unit,
    cardShape: RoundedCornerShape
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        NavigationStyle.entries.forEach { style ->
            val isSelected = style.key == currentStyleKey

            Column(
                modifier = Modifier
                    .width(110.dp)
                    .clickable { onStyleSelected(style.key) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Nav preview
                Card(
                    modifier = Modifier
                        .width(110.dp)
                        .height(50.dp)
                        .then(
                            if (isSelected) Modifier.border(
                                2.dp,
                                MaterialTheme.colorScheme.primary,
                                cardShape
                            ) else Modifier
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = cardShape
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        // Simulated nav bar preview
                        NavigationPreviewMini(style)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = style.displayName,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    maxLines = 1,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun NavigationPreviewMini(style: NavigationStyle) {
    val dotCount = 5
    when (style) {
        NavigationStyle.CAPSULE -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(dotCount) { i ->
                    Box(
                        modifier = Modifier
                            .size(if (i == 0) 16.dp else 6.dp, 6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                if (i == 0) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                            )
                    )
                }
            }
        }
        NavigationStyle.FLOATING -> {
            Row(
                modifier = Modifier
                    .width(80.dp)
                    .height(18.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(dotCount) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                    )
                }
            }
        }
        NavigationStyle.MINIMAL_FLAT -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(18.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(dotCount) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                    )
                }
            }
        }
        NavigationStyle.ELEVATED -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(dotCount) { i ->
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                if (i == 0) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                            )
                    )
                }
            }
        }
        NavigationStyle.ROUNDED_PILL -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(18.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(dotCount) { i ->
                    Box(
                        modifier = Modifier
                            .size(if (i == 0) 8.dp else 5.dp)
                            .clip(CircleShape)
                            .background(
                                if (i == 0) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                            )
                    )
                }
            }
        }
        NavigationStyle.COMPACT -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(18.dp)
                    .background(MaterialTheme.colorScheme.surface),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(dotCount) { i ->
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(
                                if (i == 0) MaterialTheme.colorScheme.primaryContainer
                                else Color.Transparent
                            )
                            .border(
                                1.dp,
                                if (i == 0) Color.Transparent
                                else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                CircleShape
                            )
                    )
                }
            }
        }
        NavigationStyle.MATERIAL_STANDARD -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(22.dp)
                    .background(MaterialTheme.colorScheme.surface),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(dotCount) { i ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(
                                    if (i == 0) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                )
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        Box(
                            modifier = Modifier
                                .size(width = 10.dp, height = 2.dp)
                                .clip(RoundedCornerShape(1.dp))
                                .background(
                                    if (i == 0) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                    else Color.Transparent
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    cardShape: RoundedCornerShape = RoundedCornerShape(14.dp)
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = cardShape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    cardShape: RoundedCornerShape = RoundedCornerShape(14.dp)
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = cardShape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}
