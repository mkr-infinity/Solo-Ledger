package com.solo.ledger.ui.screens.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Notes
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Title
import androidx.compose.material.icons.outlined.Upcoming
import androidx.compose.material.icons.outlined.Upload
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.solo.ledger.ui.navigation.NavRoutes
import com.solo.ledger.util.AppLogger
import com.solo.ledger.ui.components.shared.SectionHeader
import com.solo.ledger.ui.components.shared.SettingInfoRow
import com.solo.ledger.ui.components.shared.SettingNavRow
import com.solo.ledger.ui.components.shared.SettingToggleRow
import com.solo.ledger.ui.components.shared.ThemedCard
import com.solo.ledger.ui.components.shared.ThemePreviewCard
import com.solo.ledger.ui.components.shared.NavStylePreviewCard
import com.solo.ledger.ui.theme.NavigationStyle
import com.solo.ledger.ui.theme.ThemeRegistry
import java.time.YearMonth

private fun navigationStyleToString(style: NavigationStyle): String = when (style) {
    NavigationStyle.BOTTOM_STANDARD -> "bottom_standard"
    NavigationStyle.FLOATING_PILL -> "floating_pill"
    NavigationStyle.CAPSULE -> "capsule"
    NavigationStyle.TELEGRAM -> "telegram"
    NavigationStyle.MATERIAL3 -> "material3"
    NavigationStyle.COMPACT -> "compact"
    NavigationStyle.GLASS -> "glass"
}

private fun navigationStyleDisplayName(style: NavigationStyle): String = when (style) {
    NavigationStyle.BOTTOM_STANDARD -> "Standard"
    NavigationStyle.FLOATING_PILL -> "Floating Pill"
    NavigationStyle.CAPSULE -> "Capsule"
    NavigationStyle.TELEGRAM -> "Top Strip"
    NavigationStyle.MATERIAL3 -> "Material"
    NavigationStyle.COMPACT -> "Compact"
    NavigationStyle.GLASS -> "Glass"
}

private val CURRENCY_OPTIONS = listOf(
    "INR" to "₹",
    "USD" to "$",
    "EUR" to "€",
    "GBP" to "£",
    "JPY" to "¥",
    "AUD" to "A$",
    "CAD" to "C$",
    "CHF" to "Fr",
    "SGD" to "S$"
)

private val SWIPE_ACTIONS = listOf("delete", "edit", "duplicate")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val settings = uiState.settings

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.operationResult) {
        uiState.operationResult?.let { result ->
            val message = when (result) {
                is OperationResult.Success -> result.message
                is OperationResult.Error -> result.message
            }
            snackbarHostState.showSnackbar(message)
            viewModel.clearOperationResult()
        }
    }

    val importJsonLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { viewModel.importJson(it) }
    }

    val exportJsonLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let { viewModel.exportJson(it) }
    }

    val exportPdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        uri?.let { viewModel.exportPdf(it) }
    }

    var appearanceExpanded by remember { mutableStateOf(false) }
    var currencyDropdownExpanded by remember { mutableStateOf(false) }
    var swipeLeftDropdownExpanded by remember { mutableStateOf(false) }
    var swipeRightDropdownExpanded by remember { mutableStateOf(false) }
    var logsEnabled by remember(settings.logsEnabled) { mutableStateOf(settings.logsEnabled) }

    var customCurrencySymbol by remember(settings.currencySymbol) {
        mutableStateOf(settings.currencySymbol)
    }
    var customCurrencyName by remember(settings.currencyName) {
        mutableStateOf(settings.currencyName)
    }
    var monthlyBudgetText by remember(settings.monthlyBudgetLimit) {
        mutableStateOf(settings.monthlyBudgetLimit.toString())
    }
    var savingsTargetText by remember(settings.savingsTarget) {
        mutableStateOf(settings.savingsTarget.toString())
    }

    val allThemes = remember { ThemeRegistry.getAllThemesList() }
    val allNavStyles = remember { NavigationStyle.values().toList() }

    val daysInMonth = remember { YearMonth.now().lengthOfMonth() }
    val dailyBudget = if (daysInMonth > 0 && settings.monthlyBudgetLimit > 0) {
        settings.monthlyBudgetLimit / daysInMonth
    } else {
        0.0
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {

            // ── HEADER ──────────────────────────────────────────────────────────
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 24.dp, bottom = 8.dp)
                ) {
                    Text(
                        text = "WORKSPACE PREFERENCES",
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 2.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // ── APPEARANCE ──────────────────────────────────────────────────────
            item {
                SectionHeader("APPEARANCE")
            }

            item {
                Text(
                    text = "SELECT THEME",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(top = 12.dp, bottom = 8.dp)
                )
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(allThemes) { theme ->
                        ThemePreviewCard(
                            theme = theme,
                            isSelected = settings.activeThemeId == theme.id,
                            onClick = { viewModel.setTheme(theme.id) }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "NAVIGATION STYLE",
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(top = 8.dp, bottom = 8.dp)
                )
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(allNavStyles) { style ->
                        NavStylePreviewCard(
                            style = style,
                            displayName = navigationStyleDisplayName(style),
                            isSelected = settings.navigationStyle == navigationStyleToString(style),
                            onClick = { viewModel.setNavigationStyle(navigationStyleToString(style)) }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
                SettingNavRow(
                    icon = if (appearanceExpanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                    title = "More Appearance Options",
                    subtitle = if (appearanceExpanded) "Tap to collapse" else "Font, animation, borders & cards",
                    onClick = { appearanceExpanded = !appearanceExpanded }
                )
            }

            item {
                AnimatedVisibility(
                    visible = appearanceExpanded,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    ThemedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Font scale chips
                            Text(
                                text = "FONT SCALE",
                                style = MaterialTheme.typography.labelSmall,
                                letterSpacing = 1.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            val fontScaleOptions = listOf(0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f, 1.4f)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                fontScaleOptions.forEach { scale ->
                                    FilterChip(
                                        selected = settings.fontScale == scale,
                                        onClick = { viewModel.setFontScale(scale) },
                                        label = { Text("${(scale * 100).toInt()}%") }
                                    )
                                }
                            }

                            // Animation intensity chips
                            Text(
                                text = "ANIMATION INTENSITY",
                                style = MaterialTheme.typography.labelSmall,
                                letterSpacing = 1.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            val animOptions = listOf("none", "subtle", "normal", "expressive")
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                animOptions.forEach { anim ->
                                    FilterChip(
                                        selected = settings.animationIntensity == anim,
                                        onClick = { viewModel.setAnimationIntensity(anim) },
                                        label = {
                                            Text(
                                                anim.replaceFirstChar { it.uppercase() }
                                            )
                                        }
                                    )
                                }
                            }

                            // Border radius chips
                            Text(
                                text = "BORDER RADIUS",
                                style = MaterialTheme.typography.labelSmall,
                                letterSpacing = 1.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            val borderOptions = listOf("none", "small", "medium", "large", "full")
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                borderOptions.forEach { border ->
                                    FilterChip(
                                        selected = settings.borderRadiusStyle == border,
                                        onClick = { viewModel.setBorderRadiusStyle(border) },
                                        label = {
                                            Text(
                                                border.replaceFirstChar { it.uppercase() }
                                            )
                                        }
                                    )
                                }
                            }

                            // Card style chips
                            Text(
                                text = "CARD STYLE",
                                style = MaterialTheme.typography.labelSmall,
                                letterSpacing = 1.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            val cardStyleOptions = listOf("flat", "elevated", "outlined", "filled")
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                cardStyleOptions.forEach { cardStyle ->
                                    FilterChip(
                                        selected = settings.cardStyle == cardStyle,
                                        onClick = { viewModel.setCardStyle(cardStyle) },
                                        label = {
                                            Text(
                                                cardStyle.replaceFirstChar { it.uppercase() }
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ── BIN ─────────────────────────────────────────────────────────────
            item {
                SettingNavRow(
                    icon = Icons.Outlined.Delete,
                    title = "Bin",
                    subtitle = "Manage deleted transactions",
                    onClick = { navController.navigate(NavRoutes.DELETED) }
                )
            }

            // ── BASE CURRENCY ───────────────────────────────────────────────────
            item {
                SectionHeader("BASE CURRENCY")
            }

            item {
                ThemedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val selectedCurrencyEntry = CURRENCY_OPTIONS.find { it.second == settings.currencySymbol }
                        val selectedCurrencyCode = selectedCurrencyEntry?.first ?: "Custom"

                        ExposedDropdownMenuBox(
                            expanded = currencyDropdownExpanded,
                            onExpandedChange = { currencyDropdownExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = selectedCurrencyCode,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Currency") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = currencyDropdownExpanded)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = currencyDropdownExpanded,
                                onDismissRequest = { currencyDropdownExpanded = false }
                            ) {
                                CURRENCY_OPTIONS.forEach { (code, symbol) ->
                                    DropdownMenuItem(
                                        text = { Text("$code ($symbol)") },
                                        onClick = {
                                            viewModel.setCurrencySymbol(symbol)
                                            viewModel.setCurrencyName(code)
                                            customCurrencySymbol = symbol
                                            customCurrencyName = code
                                            currencyDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Text(
                            text = "Or enter custom symbol:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedTextField(
                            value = customCurrencySymbol,
                            onValueChange = { value ->
                                customCurrencySymbol = value
                                viewModel.setCurrencySymbol(value)
                            },
                            label = { Text("Currency Symbol") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = customCurrencyName,
                            onValueChange = { value ->
                                customCurrencyName = value
                                viewModel.setCurrencyName(value)
                            },
                            label = { Text("Currency Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Text(
                            text = "Preview: ${settings.currencySymbol}1,500.00",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // ── BUDGET CUSTOMIZATION ────────────────────────────────────────────
            item {
                SectionHeader("BUDGET CUSTOMIZATION")
            }

            item {
                ThemedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SettingToggleRow(
                            title = "Track Budget Cycles",
                            checked = settings.trackBudgetCycles,
                            onCheckedChange = { viewModel.setTrackBudgetCycles(it) }
                        )
                        Text(
                            text = "MONTHLY BUDGET LIMIT",
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 1.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedTextField(
                            value = monthlyBudgetText,
                            onValueChange = { text ->
                                monthlyBudgetText = text
                                text.toDoubleOrNull()?.let { viewModel.setMonthlyBudgetLimit(it) }
                            },
                            label = { Text("Monthly Budget") },
                            prefix = { Text(settings.currencySymbol) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        if (dailyBudget > 0.0) {
                            Text(
                                text = "${settings.currencySymbol}${"%.2f".format(dailyBudget)} / day",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = "MONTHLY TARGET",
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 1.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedTextField(
                            value = savingsTargetText,
                            onValueChange = { text ->
                                savingsTargetText = text
                                text.toDoubleOrNull()?.let { viewModel.setSavingsTarget(it) }
                            },
                            label = { Text("Savings Target") },
                            prefix = { Text(settings.currencySymbol) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            }

            // ── QUICK ADD CUSTOMIZATION ─────────────────────────────────────────
            item {
                SectionHeader("QUICK ADD CUSTOMIZATION")
            }

            item {
                ThemedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        SettingToggleRow(
                            icon = Icons.Outlined.Title,
                            title = "Title",
                            checked = settings.showTitleField,
                            onCheckedChange = { viewModel.setShowTitleField(it) }
                        )
                        SettingToggleRow(
                            icon = Icons.Outlined.Category,
                            title = "Category",
                            checked = settings.showCategoryField,
                            onCheckedChange = { viewModel.setShowCategoryField(it) }
                        )
                        SettingToggleRow(
                            icon = Icons.Outlined.Notes,
                            title = "Notes",
                            checked = settings.showNotesField,
                            onCheckedChange = { viewModel.setShowNotesField(it) }
                        )
                        SettingToggleRow(
                            icon = Icons.Outlined.DateRange,
                            title = "Date",
                            checked = settings.showDateField,
                            onCheckedChange = { viewModel.setShowDateField(it) }
                        )
                        SettingToggleRow(
                            icon = Icons.Outlined.Schedule,
                            title = "Time",
                            checked = settings.showTimeField,
                            onCheckedChange = { viewModel.setShowTimeField(it) }
                        )
                    }
                }
            }

            // ── SWIPE ACTIONS ───────────────────────────────────────────────────
            item {
                SectionHeader("SWIPE ACTIONS")
            }

            item {
                ThemedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Swipe Left",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        ExposedDropdownMenuBox(
                            expanded = swipeLeftDropdownExpanded,
                            onExpandedChange = { swipeLeftDropdownExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = settings.swipeLeftAction.replaceFirstChar { it.uppercase() },
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Swipe Left Action") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = swipeLeftDropdownExpanded)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = swipeLeftDropdownExpanded,
                                onDismissRequest = { swipeLeftDropdownExpanded = false }
                            ) {
                                SWIPE_ACTIONS.forEach { action ->
                                    DropdownMenuItem(
                                        text = { Text(action.replaceFirstChar { it.uppercase() }) },
                                        onClick = {
                                            viewModel.setSwipeLeftAction(action)
                                            swipeLeftDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Text(
                            text = "Swipe Right",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        ExposedDropdownMenuBox(
                            expanded = swipeRightDropdownExpanded,
                            onExpandedChange = { swipeRightDropdownExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = settings.swipeRightAction.replaceFirstChar { it.uppercase() },
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Swipe Right Action") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = swipeRightDropdownExpanded)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = swipeRightDropdownExpanded,
                                onDismissRequest = { swipeRightDropdownExpanded = false }
                            ) {
                                SWIPE_ACTIONS.forEach { action ->
                                    DropdownMenuItem(
                                        text = { Text(action.replaceFirstChar { it.uppercase() }) },
                                        onClick = {
                                            viewModel.setSwipeRightAction(action)
                                            swipeRightDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ── ANALYTICS ───────────────────────────────────────────────────────
            item {
                SectionHeader("ANALYTICS")
            }

            item {
                ThemedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Preferred Chart",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("bar", "line", "donut").forEach { chartType ->
                                FilterChip(
                                    selected = settings.preferredChartType == chartType,
                                    onClick = { viewModel.setPreferredChartType(chartType) },
                                    label = {
                                        Text(chartType.replaceFirstChar { it.uppercase() })
                                    }
                                )
                            }
                        }
                        SettingToggleRow(
                            title = "Graph Animations",
                            checked = settings.animationIntensity != "none",
                            onCheckedChange = { enabled ->
                                viewModel.setAnimationIntensity(if (enabled) "normal" else "none")
                            }
                        )
                    }
                }
            }

            // ── DATA MANAGEMENT ─────────────────────────────────────────────────
            item {
                SectionHeader("DATA MANAGEMENT")
            }

            item {
                ThemedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        SettingNavRow(
                            icon = Icons.Outlined.Upload,
                            title = "Import JSON",
                            subtitle = "Restore data from a JSON file",
                            onClick = {
                                importJsonLauncher.launch(arrayOf("application/json"))
                            }
                        )
                        SettingNavRow(
                            icon = Icons.Outlined.Download,
                            title = "Export JSON",
                            subtitle = "Back up data as a JSON file",
                            onClick = {
                                exportJsonLauncher.launch("solo_ledger_backup.json")
                            }
                        )
                        SettingNavRow(
                            icon = Icons.Outlined.PictureAsPdf,
                            title = "Export PDF",
                            subtitle = "Export transactions as a PDF report",
                            onClick = {
                                exportPdfLauncher.launch("solo_ledger_report.pdf")
                            }
                        )
                        SettingNavRow(
                            icon = Icons.Outlined.Delete,
                            title = "Manage Deleted Items",
                            subtitle = "View and restore deleted transactions",
                            onClick = { navController.navigate(NavRoutes.DELETED) }
                        )
                    }
                }
            }

            // ── ACCESSIBILITY ────────────────────────────────────────────────────
            item {
                SectionHeader("ACCESSIBILITY")
            }

            item {
                ThemedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "FONT SCALE",
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 1.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Slider(
                                value = settings.fontScale,
                                onValueChange = { viewModel.setFontScale(it) },
                                valueRange = 0.8f..1.4f,
                                steps = 5,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${"%.0f".format(settings.fontScale * 100)}%",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        SettingToggleRow(
                            title = "Reduced Motion",
                            checked = settings.reducedMotion,
                            onCheckedChange = { viewModel.setReducedMotion(it) }
                        )
                        SettingToggleRow(
                            title = "High Contrast",
                            checked = settings.highContrast,
                            onCheckedChange = { viewModel.setHighContrast(it) }
                        )
                        SettingToggleRow(
                            title = "Larger Touch Targets",
                            checked = settings.largerTouchTargets,
                            onCheckedChange = { viewModel.setLargerTouchTargets(it) }
                        )
                    }
                }
            }

            // ── UPDATES ──────────────────────────────────────────────────────────
            item {
                SectionHeader("UPDATES")
            }

            item {
                ThemedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        SettingInfoRow(
                            title = "Version",
                            value = "v1.0.0 (Build 1)"
                        )
                        SettingNavRow(
                            title = "Check for Updates",
                            subtitle = "Tap to check for the latest version",
                            onClick = { navController.navigate(NavRoutes.CHECK_UPDATES) }
                        )
                        SettingToggleRow(
                            title = "Auto-check on startup",
                            checked = settings.autoCheckUpdates,
                            onCheckedChange = { viewModel.setAutoCheckUpdates(it) }
                        )
                    }
                }
            }

            // ── COMING SOON ──────────────────────────────────────────────────────
            item {
                SettingNavRow(
                    icon = Icons.Outlined.Upcoming,
                    title = "Coming Soon",
                    subtitle = "Preview upcoming features",
                    onClick = { navController.navigate(NavRoutes.COMING_SOON) }
                )
            }
            item {
                SettingNavRow(
                    icon = Icons.Outlined.Favorite,
                    title = "Support",
                    subtitle = "Buy me a coffee & feedback",
                    onClick = { navController.navigate(NavRoutes.SUPPORT) }
                )
            }

            // ── DEVELOPER LOGS ───────────────────────────────────────────────────
            item {
                SectionHeader("DEVELOPER LOGS")
            }

            item {
                ThemedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        SettingToggleRow(
                            title = "Enable Logging",
                            subtitle = "Record app activity for debugging",
                            icon = Icons.Outlined.BugReport,
                            checked = logsEnabled,
                            onCheckedChange = { enabled ->
                                AppLogger.isEnabled = enabled
                                logsEnabled = enabled
                                viewModel.setLogsEnabled(enabled)
                            }
                        )
                        if (logsEnabled) {
                            SettingNavRow(
                                title = "View Logs",
                                subtitle = "Browse and export app logs",
                                icon = Icons.Outlined.Description,
                                onClick = { navController.navigate(NavRoutes.LOGS) }
                            )
                        }
                    }
                }
            }

            // ── DEVELOPER PROFILE ────────────────────────────────────────────────
            item {
                SectionHeader("DEVELOPER PROFILE")
            }

            item {
                SettingNavRow(
                    icon = Icons.Outlined.Person,
                    title = "The Architect",
                    subtitle = "Meet the developer behind Solo Ledger",
                    onClick = { navController.navigate(NavRoutes.ARCHITECT) }
                )
            }

            // ── ABOUT ────────────────────────────────────────────────────────────
            item {
                SettingNavRow(
                    title = "About Solo Ledger",
                    subtitle = "Licenses, version info & credits",
                    onClick = { navController.navigate(NavRoutes.ABOUT) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(120.dp))
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
