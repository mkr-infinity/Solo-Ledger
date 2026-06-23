package com.solo.ledger.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.solo.ledger.core.ServiceLocator
import com.solo.ledger.ui.navigation.Routes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.solo.ledger.ui.AppViewModel
import com.solo.ledger.ui.components.LedgerCard
import com.solo.ledger.ui.components.SectionHeader
import com.solo.ledger.ui.theme.LedgerTheme

private val NAV_STYLES = listOf(
    "capsule" to "Capsule", "floating" to "Floating", "minimal" to "Minimal",
    "elevated" to "Elevated", "compact" to "Compact", "standard" to "Standard"
)

// Themes: only Ledger is confirmed/active. Others are locked pending user confirmation (PRD STOP rule).
private data class ThemeOption(val id: String, val label: String, val confirmed: Boolean)
private val THEMES = listOf(
    ThemeOption("ledger", "Ledger", true),
    ThemeOption("emerald", "Emerald", true),
    ThemeOption("anime", "Anime", true),
    ThemeOption("spider", "Spider", true)
)

private val COMING_SOON = listOf(
    "Login", "Cloud Sync", "Online Backup", "Multi Device Sync", "Shared Budgets",
    "Family Accounts", "AI Insights", "Bank Integration", "UPI Integration", "OCR Receipt Scanner"
)

@Composable
fun SettingsScreen(nav: NavController, appVm: AppViewModel) {
    val c = LedgerTheme.colors
    val s by appVm.settings.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }
    val backup = ServiceLocator.backupManager

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) scope.launch {
            withContext(Dispatchers.IO) {
                val json = backup.exportJson()
                context.contentResolver.openOutputStream(uri)?.use { it.write(json.toByteArray()) }
            }
            snackbar.showSnackbar("Backup exported")
        }
    }
    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) scope.launch {
            val count = withContext(Dispatchers.IO) {
                val text = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() } ?: ""
                if (text.isNotBlank()) backup.importJson(text) else 0
            }
            snackbar.showSnackbar("Imported $count transactions")
        }
    }

  Scaffold(containerColor = c.background, snackbarHost = { SnackbarHost(snackbar) }) { pad ->
    LazyColumn(Modifier.fillMaxSize().padding(pad), contentPadding = PaddingValues(16.dp, 12.dp, 16.dp, 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {

        item { Text("Settings", style = MaterialTheme.typography.headlineSmall, color = c.textPrimary, fontWeight = FontWeight.SemiBold) }

        item {
            LedgerCard(Modifier.fillMaxWidth()) {
                SectionHeader("Account")
                Spacer(Modifier.height(8.dp))
                NavRow(Icons.Rounded.Person, "Profile") { nav.navigate(Routes.PROFILE) }
                NavRow(Icons.Rounded.Category, "Categories") { nav.navigate(Routes.CATEGORIES) }
            }
        }
        item {
            LedgerCard(Modifier.fillMaxWidth()) {
                SectionHeader("Manage")
                Spacer(Modifier.height(8.dp))
                NavRow(Icons.Rounded.QueryStats, "Analytics") { nav.navigate(Routes.ANALYTICS) }
                NavRow(Icons.Rounded.Savings, "Savings Goals") { nav.navigate(Routes.GOALS) }
                NavRow(Icons.Rounded.DeleteOutline, "Bin") { nav.navigate(Routes.BIN) }
            }
        }

        // Appearance
        item {
            LedgerCard(Modifier.fillMaxWidth()) {
                SectionHeader("Appearance")
                Spacer(Modifier.height(12.dp))
                ToggleRow("Dark mode", s.darkMode) { appVm.setDark(it) }
                ToggleRow("Animations", s.animationsEnabled) { appVm.setAnimations(it) }
                ToggleRow("Reduced motion", s.reducedMotion) { appVm.setReducedMotion(it) }
                ToggleRow("High contrast", s.highContrast) { appVm.setHighContrast(it) }
                Spacer(Modifier.height(8.dp))
                Text("Font size", style = MaterialTheme.typography.labelLarge, color = c.muted)
                Slider(value = s.fontScale, onValueChange = { appVm.setFontScale(it) }, valueRange = 0.85f..1.3f, steps = 8,
                    colors = SliderDefaults.colors(thumbColor = c.primary, activeTrackColor = c.primary))
                Text("Corner radius", style = MaterialTheme.typography.labelLarge, color = c.muted)
                Slider(value = s.cornerRadius.toFloat(), onValueChange = { appVm.setCornerRadius(it.toInt()) }, valueRange = 6f..32f, steps = 12,
                    colors = SliderDefaults.colors(thumbColor = c.primary, activeTrackColor = c.primary))
                Spacer(Modifier.height(12.dp))
                Text("Theme", style = MaterialTheme.typography.labelLarge, color = c.muted)
                Spacer(Modifier.height(8.dp))
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    THEMES.forEach { t ->
                        val sel = s.themeId == t.id
                        FilterChip(
                            selected = sel,
                            onClick = { if (t.confirmed) appVm.setTheme(t.id) },
                            enabled = t.confirmed,
                            label = { Text(if (t.confirmed) t.label else "${t.label} · soon") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = c.primary, selectedLabelColor = c.onPrimary)
                        )
                    }
                }
                Text("Switch themes anytime. Palettes are fully editable.",
                    style = MaterialTheme.typography.bodyMedium, color = c.muted, modifier = Modifier.padding(top = 8.dp))
            }
        }

        // Navigation style
        item {
            LedgerCard(Modifier.fillMaxWidth()) {
                SectionHeader("Navigation style")
                Spacer(Modifier.height(12.dp))
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NAV_STYLES.forEach { (id, label) ->
                        val sel = s.navStyle == id
                        FilterChip(selected = sel, onClick = { appVm.setNavStyle(id) }, label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = c.primary, selectedLabelColor = c.onPrimary))
                    }
                }
            }
        }

        // Dashboard
        item {
            LedgerCard(Modifier.fillMaxWidth()) {
                SectionHeader("Dashboard widgets")
                Spacer(Modifier.height(8.dp))
                listOf("insights" to "Insights", "graph" to "Monthly graph", "categories" to "Category breakdown", "recent" to "Recent transactions").forEach { (key, label) ->
                    ToggleRow(label, key !in s.hiddenWidgets) { show -> appVm.toggleWidget(key, !show) }
                }
            }
        }

        // Data
        item {
            LedgerCard(Modifier.fillMaxWidth()) {
                SectionHeader("Data")
                Spacer(Modifier.height(8.dp))
                NavRow(Icons.Rounded.Upload, "Export JSON") { exportLauncher.launch("solo_ledger_backup.json") }
                NavRow(Icons.Rounded.Download, "Import JSON") { importLauncher.launch(arrayOf("application/json")) }
                NavRow(Icons.Rounded.PictureAsPdf, "Export PDF report") {
                    scope.launch {
                        val path = withContext(Dispatchers.IO) { com.solo.ledger.core.PdfExporter.export(context, backup) }
                        snackbar.showSnackbar(if (path != null) "PDF saved to Documents" else "Export failed")
                    }
                }
            }
        }

        // Quick Add fields
        item {
            LedgerCard(Modifier.fillMaxWidth()) {
                SectionHeader("Quick Add fields")
                Spacer(Modifier.height(8.dp))
                ToggleRow("Notes field", s.quickAddNotes) { appVm.setQuickAddNotes(it) }
                ToggleRow("Time field", s.quickAddTime) { appVm.setQuickAddTime(it) }
            }
        }

        // Coming soon
        item {
            LedgerCard(Modifier.fillMaxWidth()) {
                SectionHeader("Coming soon")
                Spacer(Modifier.height(8.dp))
                COMING_SOON.forEach { item ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(item, style = MaterialTheme.typography.bodyLarge, color = c.muted)
                        Surface(color = c.primary.copy(alpha = 0.12f), shape = RoundedCornerShape(8.dp)) {
                            Text("COMING SOON", color = c.primary, style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }
                }
            }
        }

        // Support
        item {
            LedgerCard(Modifier.fillMaxWidth()) {
                SectionHeader("Support")
                Spacer(Modifier.height(8.dp))
                NavRow(Icons.Rounded.Favorite, "Support this project") { openUrl(context, "https://buymeacoffee.com/mkr_infinity") }
                NavRow(Icons.Rounded.Lightbulb, "Request a feature") { openUrl(context, "https://github.com/mkr-infinity/Solo-Ledger/issues") }
                NavRow(Icons.Rounded.BugReport, "Report a bug") { openUrl(context, "https://github.com/mkr-infinity/Solo-Ledger/issues") }
            }
        }

        // Architect
        item { ArchitectCard() }
    }
  }
}

@Composable private fun ToggleRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    val c = LedgerTheme.colors
    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodyLarge, color = c.textPrimary)
        Switch(checked = checked, onCheckedChange = onChange,
            colors = SwitchDefaults.colors(checkedTrackColor = c.primary))
    }
}

@Composable private fun NavRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit = {}) {
    val c = LedgerTheme.colors
    Row(Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = c.primary)
        Spacer(Modifier.width(14.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge, color = c.textPrimary, modifier = Modifier.weight(1f))
        Icon(Icons.Rounded.ChevronRight, null, tint = c.muted)
    }
}

private fun openUrl(context: android.content.Context, url: String) {
    runCatching { context.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))) }
}

@Composable private fun ArchitectCard() {
    val c = LedgerTheme.colors
    var expanded by remember { mutableStateOf(false) }
    LedgerCard(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().clickable { expanded = !expanded },
            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("The Architect", style = MaterialTheme.typography.titleMedium, color = c.textPrimary)
                Text("Mohammad Kaif Raja", style = MaterialTheme.typography.bodyMedium, color = c.textSecondary)
            }
            Icon(Icons.Rounded.ExpandMore, null, tint = c.muted, modifier = Modifier.rotate(if (expanded) 180f else 0f))
        }
        AnimatedVisibility(expanded) {
            Column(Modifier.padding(top = 12.dp)) {
                LinkRow(Icons.Rounded.Code, "GitHub", "https://github.com/mkr-infinity")
                LinkRow(Icons.Rounded.Language, "Website", "https://mkr-infinity.github.io/")
                LinkRow(Icons.Rounded.PhotoCamera, "Instagram", "https://instagram.com/mkr_infinity")
                LinkRow(Icons.Rounded.Send, "Telegram", "https://t.me/mkr_infinity")
            }
        }
    }
}

@Composable private fun LinkRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, url: String) {
    val c = LedgerTheme.colors
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
    Row(Modifier.fillMaxWidth().clickable { runCatching { uriHandler.openUri(url) } }.padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = c.primary)
        Spacer(Modifier.width(14.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge, color = c.textPrimary)
    }
}
