package com.solo.ledger.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    val borderRadius by viewModel.borderRadius.collectAsStateWithLifecycle()
    val logsEnabled by viewModel.logsEnabled.collectAsStateWithLifecycle()
    val currencySymbol by viewModel.currencySymbol.collectAsStateWithLifecycle()
    val currencyCode by viewModel.currencyCode.collectAsStateWithLifecycle()
    val monthlyBudget by viewModel.monthlyBudget.collectAsStateWithLifecycle()

    val cardShape = RoundedCornerShape(borderRadius.dp)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Logo + Header
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.AccountBalanceWallet,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Solo Ledger",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            Column {
                Text(
                    text = "WORKSPACE PREFERENCES",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // Appearance - Theme horizontal scroll
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Appearance", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text("${AppTheme.entries.size} THEMES", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 1.sp)
            }
        }

        // Horizontally scrollable themes
        item {
            val scrollState = androidx.compose.foundation.rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AppTheme.entries.forEach { theme ->
                    val themeKey = AppTheme.toKey(theme)
                    val isSelected = themeKey == currentThemeKey
                    val colorScheme = com.solo.ledger.ui.theme.getColorScheme(theme)
                    val r = if (theme.isSquare) 0.dp else 16.dp
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Card(
                            modifier = Modifier
                                .width(110.dp)
                                .height(120.dp)
                                .clickable { viewModel.setTheme(themeKey) }
                                .then(if (isSelected) Modifier.border(2.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(r)) else Modifier),
                            colors = CardDefaults.cardColors(containerColor = colorScheme.background),
                            shape = RoundedCornerShape(r)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Column(modifier = Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Box(Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp)).background(colorScheme.primary))
                                    Box(Modifier.fillMaxWidth(0.6f).height(8.dp).clip(RoundedCornerShape(4.dp)).background(colorScheme.onBackground.copy(alpha = 0.3f)))
                                    Spacer(Modifier.weight(1f))
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Box(Modifier.weight(1f).height(24.dp).clip(RoundedCornerShape(6.dp)).background(colorScheme.surface))
                                        Box(Modifier.weight(1f).height(24.dp).clip(RoundedCornerShape(6.dp)).background(colorScheme.surfaceVariant))
                                    }
                                    Box(Modifier.fillMaxWidth().height(14.dp).clip(RoundedCornerShape(7.dp)).background(colorScheme.primaryContainer))
                                }
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier.align(Alignment.TopEnd).padding(6.dp).size(20.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Filled.Check, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(12.dp))
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            theme.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // Navigation style header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Navigation Style", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text("${com.solo.ledger.ui.navigation.NavigationStyle.entries.size} STYLES", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 1.sp)
            }
        }

        // Horizontally scrollable nav styles with preview
        item {
            val currentNavStyle by viewModel.navigationStyle.collectAsStateWithLifecycle()
            val scrollState = androidx.compose.foundation.rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                com.solo.ledger.ui.navigation.NavigationStyle.entries.forEach { style ->
                    val isSelected = style.key == currentNavStyle
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Card(
                            modifier = Modifier
                                .width(130.dp)
                                .height(70.dp)
                                .clickable { viewModel.setNavigationStyle(style.key) }
                                .then(if (isSelected) Modifier.border(2.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp)) else Modifier),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize().padding(10.dp), contentAlignment = Alignment.Center) {
                                NavStylePreview(style, isSelected)
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier.align(Alignment.TopEnd).size(18.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Filled.Check, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(11.dp))
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            style.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // Bin
        item {
            SettingsNavRow(icon = Icons.Filled.DeleteSweep, title = "Bin", subtitle = "Manage deleted transactions", onClick = onNavigateToBin, shape = cardShape)
        }

        // Base Currency
        item {
            Card(modifier = Modifier.fillMaxWidth().clickable { onNavigateToProfile() }, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = cardShape) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Filled.Payment, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Text("Base Currency", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(12.dp))
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), shape = RoundedCornerShape(10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("$currencyCode \u2013 ${getCurrencyName(currencyCode)} ($currencySymbol)", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("All your transactions will be converted to this currency for global reports.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        // Budget Customization
        item {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = cardShape) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Filled.TrendingUp, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Budget Customization", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text("Set your financial limits and track spending efficiency.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                    Spacer(Modifier.height(12.dp))
                    // Monthly target
                    Text("MONTHLY TARGET", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 1.sp)
                    Spacer(Modifier.height(4.dp))
                    Text("$currencySymbol${formatBudgetAmount(monthlyBudget)} /month", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.height(8.dp))
                    Text("CALCULATED DAILY", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 1.sp)
                    Spacer(Modifier.height(4.dp))
                    Text("$currencySymbol${formatBudgetAmount(monthlyBudget / 30)} /day", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(14.dp))
                    // Links
                    SettingsInlineRow(icon = Icons.Filled.Category, title = "Categories", onClick = onNavigateToCategories)
                    SettingsInlineRow(icon = Icons.Filled.Description, title = "Budget Templates", onClick = onNavigateToBudgetTemplates)
                    SettingsInlineRow(icon = Icons.Filled.Savings, title = "Savings Goals", onClick = onNavigateToSavingsGoals)
                }
            }
        }

        // Quick Add Customization
        item {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = cardShape) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Quick Add Customization", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text("Toggle which fields appear when creating a new record.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(14.dp))
                    QuickToggleRow(icon = Icons.Filled.Animation, title = "Animations", checked = animationsEnabled, onCheckedChange = { viewModel.updateAnimationsEnabled(it) })
                    QuickToggleRow(icon = Icons.Filled.History, title = "Activity Logging", checked = logsEnabled, onCheckedChange = { viewModel.setLogsEnabled(it) })
                }
            }
        }

        // Data Management
        item {
            Text("Data Management", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 4.dp))
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = cardShape) {
                Column {
                    SettingsCardRow(icon = Icons.Filled.FileDownload, title = "Import JSON", subtitle = "Restore your data from a file", onClick = onNavigateToData)
                    Divider(Modifier.padding(horizontal = 18.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.15f))
                    SettingsCardRow(icon = Icons.Filled.FileUpload, title = "Export JSON", subtitle = "Backup your workspace data", onClick = onNavigateToData)
                }
            }
        }

        // Support - Coffee button (unique purple pill)
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Spacer(Modifier.height(8.dp))
                Box(Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.LocalCafe, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(24.dp))
                }
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = onNavigateToSupport,
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Buy me a coffee", fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.height(6.dp))
                Text("\"Buy me a coffee to fuel future development.\"", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            }
        }

        // Feedback rows
        item {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = cardShape) {
                Column {
                    SettingsCardRow(icon = Icons.Filled.Lightbulb, title = "Request feature", subtitle = "Suggest the next big addition to your workspace.", onClick = onNavigateToSupport)
                    Divider(Modifier.padding(horizontal = 18.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.15f))
                    SettingsCardRow(icon = Icons.Filled.BugReport, title = "Report bug", subtitle = "Encountered an issue? Our team is on standby.", onClick = onNavigateToSupport)
                }
            }
        }

        // Other
        item {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = cardShape) {
                Column {
                    SettingsCardRow(icon = Icons.Filled.Article, title = "Logs", subtitle = "View activity history", onClick = onNavigateToLogs)
                    Divider(Modifier.padding(horizontal = 18.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.15f))
                    SettingsCardRow(icon = Icons.Filled.Update, title = "Updates", subtitle = "Check for new versions", onClick = onNavigateToUpdates)
                }
            }
        }

        // Developer Profile
        item {
            Text("Developer Profile", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 4.dp))
            SettingsNavRow(icon = Icons.Filled.Person, title = "The Architect", subtitle = "Mohammad Kaif Raja", onClick = onNavigateToAbout, shape = cardShape)
        }

        // Version
        item {
            VersionEasterEgg(viewModel = viewModel)
        }
    }
}

@Composable
private fun VersionEasterEgg(viewModel: MainViewModel) {
    var tapCount by remember { mutableIntStateOf(0) }
    val messages = listOf("Stop poking me!", "I am just a version number.", "Okay, you found me.", "Do you expect a trick?", "Fine, I will sit here.", "Still going? Respect.", "Alright, you win.", "Go track expenses!", "Achievement: Version Tapper.")
    Box(Modifier.fillMaxWidth().clickable {
        tapCount++
        if (tapCount == 5) viewModel.showSupportPopup.value = true
        else if (tapCount > 5) viewModel.showToast(messages[(tapCount - 6) % messages.size], com.solo.ledger.ui.components.ToastType.FUN)
    }.padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
        Text("v1.0.0", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
    }
}

@Composable
private fun SettingsNavRow(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit, shape: RoundedCornerShape) {
    Card(modifier = Modifier.fillMaxWidth().clickable { onClick() }, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = shape) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f), modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun SettingsInlineRow(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).clickable { onClick() }.padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(30.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(title, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f), modifier = Modifier.size(16.dp))
    }
}

@Composable
private fun QuickToggleRow(icon: ImageVector, title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(30.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(title, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SettingsCardRow(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 18.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(34.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f), modifier = Modifier.size(16.dp))
    }
}

@Composable
private fun NavStylePreview(style: com.solo.ledger.ui.navigation.NavigationStyle, isSelected: Boolean) {
    val activeColor = MaterialTheme.colorScheme.primary
    val dotColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
    val pillBg = MaterialTheme.colorScheme.surface
    when (style) {
        com.solo.ledger.ui.navigation.NavigationStyle.CAPSULE -> {
            Row(
                modifier = Modifier.fillMaxWidth().height(24.dp).clip(RoundedCornerShape(12.dp)).background(pillBg).padding(horizontal = 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(5) { i ->
                    if (i == 2) Box(Modifier.size(16.dp).clip(CircleShape).background(activeColor))
                    else Box(Modifier.size(if (i == 0) 14.dp else 6.dp, 6.dp).clip(RoundedCornerShape(3.dp)).background(if (i == 0) activeColor.copy(alpha = 0.4f) else dotColor))
                }
            }
        }
        com.solo.ledger.ui.navigation.NavigationStyle.ROUNDED_PILL -> {
            Row(
                modifier = Modifier.fillMaxWidth().height(24.dp).clip(RoundedCornerShape(50)).background(pillBg).padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(5) { i ->
                    if (i == 2) Box(Modifier.size(16.dp).clip(CircleShape).background(activeColor))
                    else Box(Modifier.size(if (i == 1) 18.dp else 6.dp, 6.dp).clip(RoundedCornerShape(50)).background(if (i == 1) activeColor else dotColor))
                }
            }
        }
        com.solo.ledger.ui.navigation.NavigationStyle.FLOATING -> {
            Row(
                modifier = Modifier.width(90.dp).height(22.dp).clip(RoundedCornerShape(11.dp)).background(pillBg).padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(5) { i ->
                    if (i == 2) Box(Modifier.size(14.dp).clip(CircleShape).background(activeColor))
                    else Box(Modifier.size(5.dp).clip(CircleShape).background(if (i == 0) activeColor else dotColor))
                }
            }
        }
        com.solo.ledger.ui.navigation.NavigationStyle.COMPACT -> {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                repeat(5) { i ->
                    Box(Modifier.size(if (i == 2) 18.dp else 12.dp).clip(CircleShape).background(if (i == 2) activeColor else if (i == 0) activeColor.copy(alpha = 0.3f) else dotColor.copy(alpha = 0.2f)))
                }
            }
        }
        com.solo.ledger.ui.navigation.NavigationStyle.MINIMAL_FLAT -> {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                repeat(5) { i ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(Modifier.size(6.dp).clip(CircleShape).background(if (i == 0) activeColor else dotColor))
                        Spacer(Modifier.height(2.dp))
                        Box(Modifier.size(width = 8.dp, height = 2.dp).background(if (i == 0) activeColor else dotColor))
                    }
                }
            }
        }
        com.solo.ledger.ui.navigation.NavigationStyle.ELEVATED -> {
            Row(
                modifier = Modifier.fillMaxWidth().height(26.dp).clip(RoundedCornerShape(8.dp)).background(pillBg).padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(5) { i ->
                    Box(
                        Modifier.clip(RoundedCornerShape(6.dp)).background(if (i == 0) activeColor.copy(alpha = 0.25f) else Color.Transparent).padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) { Box(Modifier.size(6.dp).clip(CircleShape).background(if (i == 0) activeColor else dotColor)) }
                }
            }
        }
        com.solo.ledger.ui.navigation.NavigationStyle.MATERIAL_STANDARD -> {
            Row(
                modifier = Modifier.fillMaxWidth().height(26.dp).background(pillBg),
                horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(5) { i ->
                    Box(
                        Modifier.clip(RoundedCornerShape(50)).background(if (i == 0) activeColor.copy(alpha = 0.3f) else Color.Transparent).padding(horizontal = 8.dp, vertical = 3.dp),
                        contentAlignment = Alignment.Center
                    ) { Box(Modifier.size(6.dp).clip(CircleShape).background(if (i == 0) activeColor else dotColor)) }
                }
            }
        }
    }
}

private fun formatBudgetAmount(amount: Double): String {
    return if (amount == amount.toLong().toDouble()) amount.toLong().toString()
    else String.format("%.0f", amount)
}

private fun getCurrencyName(code: String): String {
    return when (code) {
        "INR" -> "Indian Rupee"
        "USD" -> "US Dollar"
        "EUR" -> "Euro"
        "GBP" -> "British Pound"
        "JPY" -> "Japanese Yen"
        "AUD" -> "Australian Dollar"
        "CAD" -> "Canadian Dollar"
        else -> code
    }
}

// Keep public for other files that reference them
@Composable
fun SettingsSectionHeader(title: String) {
    Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
}

@Composable
fun SettingsItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit, cardShape: RoundedCornerShape = RoundedCornerShape(14.dp)) {
    Card(modifier = Modifier.fillMaxWidth().clickable { onClick() }, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = cardShape) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun SettingsToggleItem(icon: ImageVector, title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit, cardShape: RoundedCornerShape = RoundedCornerShape(14.dp)) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = cardShape) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}
