package com.solo.ledger.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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

        // Appearance - Theme Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Appearance", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                TextButton(onClick = onNavigateToTheme) {
                    Text("SELECT THEME", style = MaterialTheme.typography.labelSmall, letterSpacing = 1.sp)
                }
            }
        }

        item {
            val themes = AppTheme.entries.take(4)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                for (row in themes.chunked(2)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        row.forEach { theme ->
                            val themeKey = AppTheme.toKey(theme)
                            val isSelected = themeKey == currentThemeKey
                            val colorScheme = com.solo.ledger.ui.theme.getColorScheme(theme)
                            val r = if (theme.isSquare) 0.dp else 14.dp
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(90.dp)
                                    .clickable { viewModel.setTheme(themeKey) }
                                    .then(if (isSelected) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(r)) else Modifier),
                                colors = CardDefaults.cardColors(containerColor = colorScheme.background),
                                shape = RoundedCornerShape(r)
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    Column(modifier = Modifier.fillMaxSize().padding(10.dp), verticalArrangement = Arrangement.SpaceBetween) {
                                        Box(Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)).background(colorScheme.primary))
                                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Box(Modifier.weight(1f).height(20.dp).clip(RoundedCornerShape(4.dp)).background(colorScheme.surface))
                                            Box(Modifier.weight(1f).height(20.dp).clip(RoundedCornerShape(4.dp)).background(colorScheme.surfaceVariant))
                                        }
                                        Box(Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)).background(colorScheme.primaryContainer))
                                    }
                                    if (isSelected) {
                                        Surface(modifier = Modifier.align(Alignment.TopEnd).padding(6.dp), shape = RoundedCornerShape(4.dp), color = MaterialTheme.colorScheme.primary) {
                                            Text("ACTIVE", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 8.sp)
                                        }
                                    }
                                }
                            }
                        }
                        if (row.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    themes.chunked(2).first().forEach { theme ->
                        Text(theme.displayName, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
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
