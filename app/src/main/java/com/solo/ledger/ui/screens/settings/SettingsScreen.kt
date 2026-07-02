package com.solo.ledger.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    onNavigateToBin: () -> Unit
) {
    val animationsEnabled by viewModel.animationsEnabled.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 16.dp)
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
                onClick = onNavigateToProfile
            )
        }

        // Appearance Section
        item {
            SettingsSectionHeader("Appearance")
        }
        item {
            SettingsItem(
                icon = Icons.Filled.Palette,
                title = "Theme",
                subtitle = "Choose app theme",
                onClick = onNavigateToTheme
            )
        }
        item {
            SettingsItem(
                icon = Icons.Filled.Navigation,
                title = "Navigation Style",
                subtitle = "Change navigation bar design",
                onClick = onNavigateToNavStyle
            )
        }
        item {
            SettingsToggleItem(
                icon = Icons.Filled.Animation,
                title = "Animations",
                subtitle = "Enable smooth transitions",
                checked = animationsEnabled,
                onCheckedChange = { viewModel.updateAnimationsEnabled(it) }
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
                onClick = onNavigateToCategories
            )
        }
        item {
            SettingsItem(
                icon = Icons.Filled.Description,
                title = "Budget Templates",
                subtitle = "Pre-made budget plans",
                onClick = onNavigateToBudgetTemplates
            )
        }
        item {
            SettingsItem(
                icon = Icons.Filled.Savings,
                title = "Savings Goals",
                subtitle = "Track your savings targets",
                onClick = onNavigateToSavingsGoals
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
                onClick = onNavigateToData
            )
        }
        item {
            SettingsItem(
                icon = Icons.Filled.DeleteSweep,
                title = "Bin",
                subtitle = "Recover deleted expenses",
                onClick = onNavigateToBin
            )
        }

        // Other Section
        item {
            SettingsSectionHeader("Other")
        }
        item {
            SettingsItem(
                icon = Icons.Filled.RocketLaunch,
                title = "Coming Soon",
                subtitle = "Premium features in development",
                onClick = onNavigateToComingSoon
            )
        }
        item {
            SettingsItem(
                icon = Icons.Filled.Favorite,
                title = "Support",
                subtitle = "Help us grow",
                onClick = onNavigateToSupport
            )
        }
        item {
            SettingsItem(
                icon = Icons.Filled.Info,
                title = "About",
                subtitle = "App info and developer",
                onClick = onNavigateToAbout
            )
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
        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
    )
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(14.dp)
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
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(14.dp)
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
