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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.solo.ledger.R
import com.solo.ledger.data.local.entity.CategoryEntity
import com.solo.ledger.data.local.entity.ExpenseEntity
import com.solo.ledger.data.local.entity.SavingsGoalEntity
import com.solo.ledger.data.model.BudgetTemplate
import com.solo.ledger.data.model.DashboardWidget
import com.solo.ledger.data.model.UserSettings
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.YearMonth
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import com.solo.ledger.ui.theme.LedgerTheme
import com.solo.ledger.ui.theme.LocalLedgerColors
import com.solo.ledger.ui.theme.SoloLedgerTheme

@Composable
fun SoloLedgerApp(ledgerViewModel: SoloLedgerViewModel = viewModel()) {
    val settings by ledgerViewModel.settings.collectAsState()
    val activeSettings = settings

    SoloLedgerTheme(theme = activeSettings?.theme ?: LedgerTheme.LedgerDark) {
        if (activeSettings == null) {
            LoadingSurface()
        } else {
            val navController = rememberNavController()
            val startDestination = if (activeSettings.onboardingCompleted) {
                AppRoute.Main.route
            } else {
                AppRoute.Onboarding.route
            }

            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
            ) {
                composable(AppRoute.Onboarding.route) {
                    OnboardingScreen(
                        onSkip = {
                            ledgerViewModel.completeOnboarding(null)
                            navController.navigate(AppRoute.Main.route) {
                                popUpTo(AppRoute.Onboarding.route) { inclusive = true }
                            }
                        },
                        onComplete = { template ->
                            ledgerViewModel.completeOnboarding(template)
                            navController.navigate(AppRoute.Main.route) {
                                popUpTo(AppRoute.Onboarding.route) { inclusive = true }
                            }
                        },
                    )
                }
                composable(AppRoute.Main.route) {
                    MainLedgerShell(
                        ledgerViewModel = ledgerViewModel,
                        currencyCode = activeSettings.currencyCode,
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingSurface() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Solo Ledger",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun OnboardingScreen(
    onSkip: () -> Unit,
    onComplete: (BudgetTemplate) -> Unit,
) {
    val ledgerColors = LocalLedgerColors.current
    val pages = remember { onboardingPages }
    var pageIndex by remember { mutableIntStateOf(0) }
    var selectedTemplate by remember { mutableStateOf(BudgetTemplate.Student) }
    val page = pages[pageIndex]
    val isLastPage = pageIndex == pages.lastIndex

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(20.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Solo Ledger",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            TextButton(
                onClick = onSkip,
                colors = ButtonDefaults.textButtonColors(contentColor = ledgerColors.muted),
            ) {
                Text(text = "Skip")
            }
        }

        AnimatedContent(
            targetState = page,
            transitionSpec = {
                (fadeIn(spring()) + scaleIn(initialScale = 0.96f)) togetherWith
                    (fadeOut(spring()) + scaleOut(targetScale = 0.96f))
            },
            label = "onboarding-page",
        ) { activePage ->
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(22.dp),
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ledgerColors.card),
                    border = BorderStroke(1.dp, ledgerColors.outline),
                    shape = RoundedCornerShape(36.dp),
                ) {
                    Image(
                        painter = painterResource(activePage.illustrationRes),
                        contentDescription = activePage.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .padding(20.dp),
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        text = activePage.title,
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = activePage.body,
                        color = ledgerColors.muted,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
            if (isLastPage) {
                BudgetTemplateSelector(
                    selectedTemplate = selectedTemplate,
                    onSelected = { selectedTemplate = it },
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                pages.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(width = if (index == pageIndex) 24.dp else 8.dp, height = 8.dp)
                            .clip(CircleShape)
                            .background(if (index == pageIndex) MaterialTheme.colorScheme.primary else ledgerColors.outline),
                    )
                }
            }

            Button(
                onClick = {
                    if (isLastPage) {
                        onComplete(selectedTemplate)
                    } else {
                        pageIndex += 1
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                ),
            ) {
                Text(
                    text = if (isLastPage) "Get Started" else "Next",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun BudgetTemplateSelector(
    selectedTemplate: BudgetTemplate,
    onSelected: (BudgetTemplate) -> Unit,
) {
    val ledgerColors = LocalLedgerColors.current

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Choose A Budget Template",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            BudgetTemplate.entries.take(2).forEach { template ->
                TemplateChip(
                    template = template,
                    selected = selectedTemplate == template,
                    onSelected = onSelected,
                    modifier = Modifier.weight(1f),
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            BudgetTemplate.entries.drop(2).forEach { template ->
                TemplateChip(
                    template = template,
                    selected = selectedTemplate == template,
                    onSelected = onSelected,
                    modifier = Modifier.weight(1f),
                )
            }
        }
        Text(
            text = "Template selection is saved offline. Budget amounts remain editable in profile settings.",
            color = ledgerColors.muted,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun TemplateChip(
    template: BudgetTemplate,
    selected: Boolean,
    onSelected: (BudgetTemplate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val ledgerColors = LocalLedgerColors.current

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onSelected(template) },
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) ledgerColors.navSelected else ledgerColors.card,
        ),
        border = BorderStroke(1.dp, if (selected) MaterialTheme.colorScheme.primary else ledgerColors.outline),
        shape = RoundedCornerShape(18.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = template.displayName,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun MainLedgerShell(
    ledgerViewModel: SoloLedgerViewModel? = null,
    currencyCode: String = "INR",
) {
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
            ScreenShell(
                destination = destination,
                ledgerViewModel = ledgerViewModel,
                currencyCode = currencyCode,
            )
        }
    }
}

@Composable
private fun ScreenShell(
    destination: LedgerDestination,
    ledgerViewModel: SoloLedgerViewModel?,
    currencyCode: String,
) {
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

        when {
            destination == LedgerDestination.Home && ledgerViewModel != null -> HomeDashboard(ledgerViewModel)
            destination == LedgerDestination.History && ledgerViewModel != null -> HistoryScreen(ledgerViewModel)
            destination == LedgerDestination.QuickAdd && ledgerViewModel != null -> QuickAddScreen(
                ledgerViewModel = ledgerViewModel,
                currencyCode = currencyCode,
            )
            destination == LedgerDestination.Calendar && ledgerViewModel != null -> CalendarScreen(ledgerViewModel)
            destination == LedgerDestination.Settings && ledgerViewModel != null -> SettingsScreen(ledgerViewModel)
            else -> {
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
    }
}

@Composable
private fun SettingsScreen(ledgerViewModel: SoloLedgerViewModel) {
    val settings by ledgerViewModel.settings.collectAsState()
    val activeSettings = settings ?: return
    val categories by ledgerViewModel.categories.collectAsState()
    var profileName by remember(activeSettings.name) { mutableStateOf(activeSettings.name) }
    var monthlyBudget by remember(activeSettings.monthlyBudgetMinor) { mutableStateOf((activeSettings.monthlyBudgetMinor / 100.0).toString()) }
    var currencyCode by remember(activeSettings.currencyCode) { mutableStateOf(activeSettings.currencyCode) }
    var message by remember { mutableStateOf<String?>(null) }
    var architectExpanded by remember { mutableStateOf(false) }
    var categoryName by remember { mutableStateOf("") }
    var categoryIcon by remember { mutableStateOf("category") }
    var categoryColor by remember { mutableStateOf("#16A34A") }
    var editingCategory by remember { mutableStateOf<CategoryEntity?>(null) }

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        DashboardCard(title = "Profile") {
            LedgerTextField(
                value = profileName,
                onValueChange = { profileName = it },
                label = "Name",
                singleLine = true,
            )
            LedgerTextField(
                value = monthlyBudget,
                onValueChange = { monthlyBudget = cleanAmountInput(it) },
                label = "Monthly Budget",
                singleLine = true,
                keyboardType = KeyboardType.Decimal,
            )
            LedgerTextField(
                value = currencyCode,
                onValueChange = { currencyCode = it.take(3).uppercase(Locale.US) },
                label = "Currency",
                singleLine = true,
            )
            Button(
                onClick = {
                    ledgerViewModel.updateProfile(
                        name = profileName,
                        monthlyBudgetText = monthlyBudget,
                        currencyCode = currencyCode,
                        onSaved = { message = "Profile saved offline." },
                        onError = { message = it },
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                ),
            ) {
                Text(text = "Save Profile")
            }
            message?.let { currentMessage ->
                Text(
                    text = currentMessage,
                    color = if (currentMessage.contains("saved", ignoreCase = true)) LocalLedgerColors.current.success else LocalLedgerColors.current.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        DashboardCard(title = "Appearance") {
            LedgerTheme.entries.chunked(2).forEach { rowThemes ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    rowThemes.forEach { theme ->
                        SelectionChip(
                            text = theme.name.splitThemeName(),
                            selected = activeSettings.theme == theme,
                            onClick = { ledgerViewModel.updateTheme(theme) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    repeat(2 - rowThemes.size) { Box(modifier = Modifier.weight(1f)) }
                }
            }
        }

        DashboardCard(title = "Dashboard") {
            DashboardWidget.entries.forEach { widget ->
                val enabled = widget in activeSettings.dashboardWidgets
                SelectionChip(
                    text = widget.name.splitCamelName(),
                    selected = enabled,
                    onClick = {
                        val nextWidgets = if (enabled) {
                            activeSettings.dashboardWidgets - widget
                        } else {
                            activeSettings.dashboardWidgets + widget
                        }
                        ledgerViewModel.updateDashboardWidgets(nextWidgets)
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        DashboardCard(title = "Categories") {
            Text(
                text = "Assign concise icon names from Material Symbols and use #RRGGBB colors.",
                color = LocalLedgerColors.current.muted,
                style = MaterialTheme.typography.bodySmall,
            )
            LedgerTextField(
                value = categoryName,
                onValueChange = { categoryName = it },
                label = "Category Name",
                singleLine = true,
            )
            LedgerTextField(
                value = categoryIcon,
                onValueChange = { categoryIcon = it.trim().lowercase(Locale.US) },
                label = "Icon Name",
                singleLine = true,
            )
            LedgerTextField(
                value = categoryColor,
                onValueChange = { categoryColor = it.take(7).uppercase(Locale.US) },
                label = "Color Hex",
                singleLine = true,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = {
                        ledgerViewModel.saveCategory(
                            existing = editingCategory,
                            name = categoryName,
                            iconName = categoryIcon,
                            colorHex = categoryColor,
                            onSaved = {
                                categoryName = ""
                                categoryIcon = "category"
                                categoryColor = "#16A34A"
                                editingCategory = null
                                message = "Category saved offline."
                            },
                            onError = { message = it },
                        )
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White,
                    ),
                ) {
                    Text(text = if (editingCategory == null) "Add" else "Update")
                }
                OutlinedButton(
                    onClick = {
                        categoryName = ""
                        categoryIcon = "category"
                        categoryColor = "#16A34A"
                        editingCategory = null
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, LocalLedgerColors.current.outline),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = LocalLedgerColors.current.muted),
                ) {
                    Text(text = "Reset")
                }
            }
            categories.forEach { category ->
                CategoryManagementRow(
                    category = category,
                    onEdit = {
                        editingCategory = category
                        categoryName = category.name
                        categoryIcon = category.iconName
                        categoryColor = category.colorHex
                    },
                    onArchive = {
                        ledgerViewModel.archiveCategory(category.id)
                        if (editingCategory?.id == category.id) {
                            editingCategory = null
                            categoryName = ""
                            categoryIcon = "category"
                            categoryColor = "#16A34A"
                        }
                        message = "Category archived."
                    },
                )
            }
        }

        DashboardCard(title = "Data") {
            Text(
                text = "JSON import and export use an app-private local file. System document export can be added after this foundation.",
                color = LocalLedgerColors.current.muted,
                style = MaterialTheme.typography.bodySmall,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { ledgerViewModel.exportJson { message = it } },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White,
                    ),
                ) {
                    Text(text = "Export JSON")
                }
                OutlinedButton(
                    onClick = { ledgerViewModel.importJson(onDone = { message = it }, onError = { message = it }) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, LocalLedgerColors.current.outline),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                ) {
                    Text(text = "Import JSON")
                }
            }
            ComingSoonCard(title = "Export PDF")
        }

        DashboardCard(title = "Coming Soon") {
            comingSoonItems.forEach { item -> ComingSoonCard(title = item) }
        }

        DashboardCard(title = "Support") {
            Text(text = "Support This Project", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
            Text(text = "https://buymeacoffee.com/mkr_infinity", color = LocalLedgerColors.current.muted)
            Text(text = "Request Feature", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
            Text(text = "https://github.com/mkr-infinity/Solo-Ledger", color = LocalLedgerColors.current.muted)
            Text(text = "Report Bug", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
            Text(text = "https://github.com/mkr-infinity/Solo-Ledger", color = LocalLedgerColors.current.muted)
        }

        DashboardCard(title = "The Architect") {
            SelectionChip(
                text = if (architectExpanded) "Hide Details" else "Show Details",
                selected = architectExpanded,
                onClick = { architectExpanded = !architectExpanded },
                modifier = Modifier.fillMaxWidth(),
            )
            AnimatedVisibility(visible = architectExpanded) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Mohammad Kaif Raja", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                    Text(text = "GitHub: https://github.com/mkr-infinity", color = LocalLedgerColors.current.muted)
                    Text(text = "Website: https://mkr-infinity.github.io/", color = LocalLedgerColors.current.muted)
                    Text(text = "Instagram: https://instagram.com/mkr_infinity", color = LocalLedgerColors.current.muted)
                    Text(text = "Telegram: https://t.me/mkr_infinity", color = LocalLedgerColors.current.muted)
                }
            }
        }
    }
}

@Composable
private fun ComingSoonCard(title: String) {
    val ledgerColors = LocalLedgerColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(ledgerColors.surface)
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "COMING SOON",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun CategoryManagementRow(
    category: CategoryEntity,
    onEdit: () -> Unit,
    onArchive: () -> Unit,
) {
    val ledgerColors = LocalLedgerColors.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ledgerColors.surface),
        border = BorderStroke(1.dp, ledgerColors.outline),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = category.name,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "${category.iconName} - ${category.colorHex}",
                        color = ledgerColors.muted,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(parseColorOrDefault(category.colorHex, MaterialTheme.colorScheme.primary)),
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, ledgerColors.outline),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                ) {
                    Text(text = "Edit")
                }
                OutlinedButton(
                    onClick = onArchive,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, ledgerColors.outline),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ledgerColors.error),
                ) {
                    Text(text = "Archive")
                }
            }
        }
    }
}

@Composable
private fun CalendarScreen(ledgerViewModel: SoloLedgerViewModel) {
    val settings by ledgerViewModel.settings.collectAsState()
    val expenses by ledgerViewModel.activeExpenses.collectAsState()
    val categories by ledgerViewModel.categories.collectAsState()
    val activeSettings = settings ?: return
    val categoryNames = categories.associate { it.id to it.name }
    var visibleMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var rangeMode by remember { mutableStateOf(CalendarRange.Last30Days) }
    var customStart by remember { mutableStateOf(LocalDate.now().minusDays(30).toString()) }
    var customEnd by remember { mutableStateOf(LocalDate.now().toString()) }
    val spendingByDate = expenses.groupBy { it.dateEpochDay }.mapValues { entry -> entry.value.sumOf { it.amountMinor } }
    val selectedExpenses = expenses.filter { it.dateEpochDay == selectedDate.toEpochDay() }
    val range = rangeMode.resolve(customStart, customEnd)
    val rangeExpenses = expenses.filter { it.dateEpochDay in range.first.toEpochDay()..range.second.toEpochDay() }

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        DashboardCard(title = "Monthly Calendar") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(onClick = { visibleMonth = visibleMonth.minusMonths(1) }) {
                    Text(text = "Previous")
                }
                Text(
                    text = "${visibleMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${visibleMonth.year}",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                TextButton(onClick = { visibleMonth = visibleMonth.plusMonths(1) }) {
                    Text(text = "Next")
                }
            }
            CalendarGrid(
                month = visibleMonth,
                selectedDate = selectedDate,
                spendingByDate = spendingByDate,
                onSelected = { selectedDate = it },
            )
        }

        CalendarDateDetail(
            date = selectedDate,
            expenses = selectedExpenses,
            currencyCode = activeSettings.currencyCode,
            categoryNames = categoryNames,
        )

        DashboardCard(title = "Range Summary") {
            CalendarRangeSelector(selected = rangeMode, onSelected = { rangeMode = it })
            if (rangeMode == CalendarRange.Custom) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    LedgerTextField(
                        value = customStart,
                        onValueChange = { customStart = it },
                        label = "Start",
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                    )
                    LedgerTextField(
                        value = customEnd,
                        onValueChange = { customEnd = it },
                        label = "End",
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            MetricBlock(
                label = "Range Total",
                value = formatMoney(rangeExpenses.sumOf { it.amountMinor }, activeSettings.currencyCode),
            )
            CategoryBreakdownCard(
                settings = activeSettings,
                expenses = rangeExpenses,
                categories = categories,
            )
        }
    }
}

@Composable
private fun CalendarGrid(
    month: YearMonth,
    selectedDate: LocalDate,
    spendingByDate: Map<Long, Long>,
    onSelected: (LocalDate) -> Unit,
) {
    val firstDay = month.atDay(1)
    val leadingBlanks = firstDay.dayOfWeek.value % 7
    val days = buildList<LocalDate?> {
        repeat(leadingBlanks) { add(null) }
        repeat(month.lengthOfMonth()) { day -> add(month.atDay(day + 1)) }
        while (size % 7 != 0) add(null)
    }
    val dayLabels = listOf("S", "M", "T", "W", "T", "F", "S")

    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        dayLabels.forEach { label ->
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                color = LocalLedgerColors.current.muted,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
            )
        }
    }
    days.chunked(7).forEach { week ->
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            week.forEach { date ->
                CalendarDayCell(
                    date = date,
                    selected = date == selectedDate,
                    hasSpending = date?.let { spendingByDate.containsKey(it.toEpochDay()) } == true,
                    onSelected = onSelected,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    date: LocalDate?,
    selected: Boolean,
    hasSpending: Boolean,
    onSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val ledgerColors = LocalLedgerColors.current
    val background = when {
        selected -> MaterialTheme.colorScheme.primary
        hasSpending -> ledgerColors.navSelected
        else -> ledgerColors.surface
    }
    val textColor = when {
        selected -> Color.White
        hasSpending -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (date == null) Color.Transparent else background)
            .clickable(
                enabled = date != null,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { date?.let(onSelected) },
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (date != null) {
            Text(
                text = date.dayOfMonth.toString(),
                color = textColor,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun CalendarDateDetail(
    date: LocalDate,
    expenses: List<ExpenseEntity>,
    currencyCode: String,
    categoryNames: Map<String, String>,
) {
    DashboardCard(title = "Date Detail") {
        Text(
            text = date.toString(),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
        )
        MetricBlock(label = "Total", value = formatMoney(expenses.sumOf { it.amountMinor }, currencyCode))
        if (expenses.isEmpty()) {
            Text(
                text = "No expenses saved for this date.",
                color = LocalLedgerColors.current.muted,
                style = MaterialTheme.typography.bodyMedium,
            )
        } else {
            expenses.forEach { expense ->
                Text(
                    text = "${expense.title} - ${categoryNames[expense.categoryId] ?: "Other"} - ${formatMoney(expense.amountMinor, currencyCode)}",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun CalendarRangeSelector(
    selected: CalendarRange,
    onSelected: (CalendarRange) -> Unit,
) {
    CalendarRange.entries.chunked(2).forEach { rowItems ->
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            rowItems.forEach { range ->
                SelectionChip(
                    text = range.label,
                    selected = selected == range,
                    onClick = { onSelected(range) },
                    modifier = Modifier.weight(1f),
                )
            }
            repeat(2 - rowItems.size) { Box(modifier = Modifier.weight(1f)) }
        }
    }
}

@Composable
private fun HistoryScreen(ledgerViewModel: SoloLedgerViewModel) {
    val settings by ledgerViewModel.settings.collectAsState()
    val expenses by ledgerViewModel.activeExpenses.collectAsState()
    val deletedExpenses by ledgerViewModel.deletedExpenses.collectAsState()
    val categories by ledgerViewModel.categories.collectAsState()
    val activeSettings = settings ?: return
    val categoryNames = categories.associate { it.id to it.name }
    var query by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    var sortOrder by remember { mutableStateOf(HistorySort.NewestFirst) }
    var expandedId by remember { mutableStateOf<String?>(null) }
    var message by remember { mutableStateOf<String?>(null) }
    val filtered = expenses
        .filter { expense ->
            val matchesQuery = query.isBlank() ||
                expense.title.contains(query, ignoreCase = true) ||
                expense.notes.orEmpty().contains(query, ignoreCase = true) ||
                categoryNames[expense.categoryId].orEmpty().contains(query, ignoreCase = true)
            val matchesCategory = selectedCategoryId == null || expense.categoryId == selectedCategoryId
            matchesQuery && matchesCategory
        }
        .let { list ->
            when (sortOrder) {
                HistorySort.NewestFirst -> list.sortedWith(compareByDescending<ExpenseEntity> { it.dateEpochDay }.thenByDescending { it.timeMinuteOfDay })
                HistorySort.OldestFirst -> list.sortedWith(compareBy<ExpenseEntity> { it.dateEpochDay }.thenBy { it.timeMinuteOfDay })
                HistorySort.HighestAmount -> list.sortedByDescending { it.amountMinor }
                HistorySort.LowestAmount -> list.sortedBy { it.amountMinor }
            }
        }
    val grouped = filtered.groupBy { it.dateEpochDay }

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        DashboardCard(title = "Search And Filter") {
            LedgerTextField(
                value = query,
                onValueChange = { query = it },
                label = "Search Title, Note, Category",
                singleLine = true,
            )
            HistorySortSelector(selected = sortOrder, onSelected = { sortOrder = it })
            HistoryCategoryFilter(
                categories = categories,
                selectedCategoryId = selectedCategoryId,
                onSelected = { selectedCategoryId = it },
            )
        }

        message?.let { currentMessage ->
            DashboardCard(title = "History Update") {
                Text(
                    text = currentMessage,
                    color = LocalLedgerColors.current.success,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        if (filtered.isEmpty()) {
            DashboardCard(title = if (expenses.isEmpty()) "No Expenses" else "No Results") {
                Text(
                    text = if (expenses.isEmpty()) {
                        "Saved expenses appear here grouped by spending date."
                    } else {
                        "No transactions match the current search and filters."
                    },
                    color = LocalLedgerColors.current.muted,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        } else {
            grouped.forEach { (epochDay, dayExpenses) ->
                HistoryDateGroup(
                    epochDay = epochDay,
                    expenses = dayExpenses,
                    currencyCode = activeSettings.currencyCode,
                    categoryNames = categoryNames,
                    categories = categories,
                    expandedId = expandedId,
                    onExpanded = { expenseId -> expandedId = if (expandedId == expenseId) null else expenseId },
                    onSave = { expense, title, amount, categoryId, date, time, notes ->
                        ledgerViewModel.updateExpense(
                            expense = expense,
                            title = title,
                            amountText = amount,
                            categoryId = categoryId,
                            dateText = date,
                            timeText = time,
                            notes = notes,
                            onSaved = { message = "Transaction updated." },
                            onError = { message = it },
                        )
                    },
                    onDelete = { expenseId ->
                        ledgerViewModel.moveExpenseToBin(expenseId)
                        expandedId = null
                        message = "Transaction moved to Bin."
                    },
                )
            }
        }

        BinSection(
            deletedExpenses = deletedExpenses,
            currencyCode = activeSettings.currencyCode,
            categoryNames = categoryNames,
            onRestore = { expenseId ->
                ledgerViewModel.restoreExpense(expenseId)
                message = "Transaction restored."
            },
            onDeletePermanently = { expense ->
                ledgerViewModel.deleteExpensePermanently(expense)
                message = "Transaction deleted permanently."
            },
            onClearAll = {
                ledgerViewModel.clearBin()
                message = "Bin cleared."
            },
        )
    }
}

@Composable
private fun BinSection(
    deletedExpenses: List<ExpenseEntity>,
    currencyCode: String,
    categoryNames: Map<String, String>,
    onRestore: (String) -> Unit,
    onDeletePermanently: (ExpenseEntity) -> Unit,
    onClearAll: () -> Unit,
) {
    DashboardCard(title = "Bin") {
        if (deletedExpenses.isEmpty()) {
            Text(
                text = "Deleted transactions appear here before permanent removal.",
                color = LocalLedgerColors.current.muted,
                style = MaterialTheme.typography.bodyMedium,
            )
        } else {
            TextButton(
                onClick = onClearAll,
                colors = ButtonDefaults.textButtonColors(contentColor = LocalLedgerColors.current.error),
            ) {
                Text(text = "Clear All")
            }
            deletedExpenses.forEach { expense ->
                BinExpenseCard(
                    expense = expense,
                    currencyCode = currencyCode,
                    categoryName = categoryNames[expense.categoryId] ?: "Other",
                    onRestore = { onRestore(expense.id) },
                    onDeletePermanently = { onDeletePermanently(expense) },
                )
            }
        }
    }
}

@Composable
private fun BinExpenseCard(
    expense: ExpenseEntity,
    currencyCode: String,
    categoryName: String,
    onRestore: () -> Unit,
    onDeletePermanently: () -> Unit,
) {
    val ledgerColors = LocalLedgerColors.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ledgerColors.surface),
        border = BorderStroke(1.dp, ledgerColors.outline),
        shape = RoundedCornerShape(22.dp),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = expense.title,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "$categoryName - Deleted ${expense.deletedAtMillis?.let(::formatMillisDate) ?: "Recently"}",
                        color = ledgerColors.muted,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                Text(
                    text = formatMoney(expense.amountMinor, currencyCode),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = onRestore,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, ledgerColors.outline),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ledgerColors.success),
                ) {
                    Text(text = "Restore")
                }
                OutlinedButton(
                    onClick = onDeletePermanently,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, ledgerColors.outline),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ledgerColors.error),
                ) {
                    Text(text = "Delete Permanently")
                }
            }
        }
    }
}

@Composable
private fun HistorySortSelector(
    selected: HistorySort,
    onSelected: (HistorySort) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Sort",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
        )
        HistorySort.entries.chunked(2).forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowItems.forEach { sort ->
                    SelectionChip(
                        text = sort.label,
                        selected = selected == sort,
                        onClick = { onSelected(sort) },
                        modifier = Modifier.weight(1f),
                    )
                }
                repeat(2 - rowItems.size) { Box(modifier = Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun HistoryCategoryFilter(
    categories: List<CategoryEntity>,
    selectedCategoryId: String?,
    onSelected: (String?) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Filter",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
        )
        SelectionChip(
            text = "All Categories",
            selected = selectedCategoryId == null,
            onClick = { onSelected(null) },
            modifier = Modifier.fillMaxWidth(),
        )
        categories.chunked(3).forEach { rowCategories ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowCategories.forEach { category ->
                    SelectionChip(
                        text = category.name,
                        selected = selectedCategoryId == category.id,
                        onClick = { onSelected(category.id) },
                        modifier = Modifier.weight(1f),
                    )
                }
                repeat(3 - rowCategories.size) { Box(modifier = Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun HistoryDateGroup(
    epochDay: Long,
    expenses: List<ExpenseEntity>,
    currencyCode: String,
    categoryNames: Map<String, String>,
    categories: List<CategoryEntity>,
    expandedId: String?,
    onExpanded: (String) -> Unit,
    onSave: (ExpenseEntity, String, String, String, String, String, String) -> Unit,
    onDelete: (String) -> Unit,
) {
    DashboardCard(title = formatDate(epochDay)) {
        expenses.forEach { expense ->
            HistoryExpenseCard(
                expense = expense,
                currencyCode = currencyCode,
                categoryName = categoryNames[expense.categoryId] ?: "Other",
                categories = categories,
                expanded = expandedId == expense.id,
                onExpanded = { onExpanded(expense.id) },
                onSave = { title, amount, categoryId, date, time, notes ->
                    onSave(expense, title, amount, categoryId, date, time, notes)
                },
                onDelete = { onDelete(expense.id) },
            )
        }
    }
}

@Composable
private fun HistoryExpenseCard(
    expense: ExpenseEntity,
    currencyCode: String,
    categoryName: String,
    categories: List<CategoryEntity>,
    expanded: Boolean,
    onExpanded: () -> Unit,
    onSave: (String, String, String, String, String, String) -> Unit,
    onDelete: () -> Unit,
) {
    val ledgerColors = LocalLedgerColors.current
    var editing by remember(expense.id) { mutableStateOf(false) }
    var editTitle by remember(expense.id, expense.updatedAtMillis) { mutableStateOf(expense.title) }
    var editAmount by remember(expense.id, expense.updatedAtMillis) { mutableStateOf((expense.amountMinor / 100.0).toString()) }
    var editCategoryId by remember(expense.id, expense.updatedAtMillis) { mutableStateOf(expense.categoryId) }
    var editDate by remember(expense.id, expense.updatedAtMillis) { mutableStateOf(formatDate(expense.dateEpochDay)) }
    var editTime by remember(expense.id, expense.updatedAtMillis) { mutableStateOf(formatTime(expense.timeMinuteOfDay)) }
    var editNotes by remember(expense.id, expense.updatedAtMillis) { mutableStateOf(expense.notes.orEmpty()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onExpanded,
            ),
        colors = CardDefaults.cardColors(containerColor = ledgerColors.surface),
        border = BorderStroke(1.dp, ledgerColors.outline),
        shape = RoundedCornerShape(22.dp),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = expense.title,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "$categoryName - ${formatTime(expense.timeMinuteOfDay)}",
                        color = ledgerColors.muted,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                Text(
                    text = formatMoney(expense.amountMinor, currencyCode),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (editing) {
                        LedgerTextField(
                            value = editTitle,
                            onValueChange = { editTitle = it },
                            label = "Title",
                            singleLine = true,
                        )
                        LedgerTextField(
                            value = editAmount,
                            onValueChange = { editAmount = cleanAmountInput(it) },
                            label = "Amount $currencyCode",
                            singleLine = true,
                            keyboardType = KeyboardType.Decimal,
                        )
                        CategorySelector(
                            categories = categories,
                            selectedCategoryId = editCategoryId,
                            onSelected = { editCategoryId = it },
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            LedgerTextField(
                                value = editDate,
                                onValueChange = { editDate = it },
                                label = "Date",
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                            )
                            LedgerTextField(
                                value = editTime,
                                onValueChange = { editTime = it },
                                label = "Time",
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                            )
                        }
                        LedgerTextField(
                            value = editNotes,
                            onValueChange = { editNotes = it },
                            label = "Notes",
                            minLines = 2,
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = {
                                    onSave(editTitle, editAmount, editCategoryId, editDate, editTime, editNotes)
                                    editing = false
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = Color.White,
                                ),
                            ) {
                                Text(text = "Save")
                            }
                            OutlinedButton(
                                onClick = { editing = false },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, ledgerColors.outline),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = ledgerColors.muted),
                            ) {
                                Text(text = "Cancel")
                            }
                        }
                    } else {
                        DetailLine(label = "Amount", value = formatMoney(expense.amountMinor, currencyCode))
                        DetailLine(label = "Category", value = categoryName)
                        DetailLine(label = "Date", value = formatDate(expense.dateEpochDay))
                        DetailLine(label = "Time", value = formatTime(expense.timeMinuteOfDay))
                        DetailLine(label = "Notes", value = expense.notes ?: "No notes")
                        DetailLine(label = "Attachment", value = if (expense.attachmentPath == null) "No attachment" else "Saved locally")
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            TextButton(
                                onClick = { editing = true },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                            ) {
                                Text(text = "Edit")
                            }
                            TextButton(
                                onClick = onDelete,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.textButtonColors(contentColor = ledgerColors.error),
                            ) {
                                Text(text = "Move To Bin")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            color = LocalLedgerColors.current.muted,
            style = MaterialTheme.typography.bodySmall,
        )
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun SelectionChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ledgerColors = LocalLedgerColors.current

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) ledgerColors.navSelected else ledgerColors.surface,
        ),
        border = BorderStroke(1.dp, if (selected) MaterialTheme.colorScheme.primary else ledgerColors.outline),
        shape = RoundedCornerShape(16.dp),
    ) {
        Text(
            text = text,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun HomeDashboard(ledgerViewModel: SoloLedgerViewModel) {
    val settings by ledgerViewModel.settings.collectAsState()
    val expenses by ledgerViewModel.activeExpenses.collectAsState()
    val categories by ledgerViewModel.categories.collectAsState()
    val goals by ledgerViewModel.activeGoals.collectAsState()
    val activeSettings = settings ?: return
    val widgets = activeSettings.dashboardWidgets

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        if (widgets.isEmpty()) {
            DashboardCard(title = "Dashboard Hidden") {
                Text(
                    text = "All dashboard sections are hidden in settings.",
                    color = LocalLedgerColors.current.muted,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        if (DashboardWidget.MonthlyBudget in widgets) {
            MonthlyBudgetCard(settings = activeSettings, expenses = expenses)
        }
        if (DashboardWidget.DailySpending in widgets) {
            DailySpendingCard(settings = activeSettings, expenses = expenses)
        }
        if (DashboardWidget.SavingsGoalProgress in widgets) {
            SavingsGoalCard(
                settings = activeSettings,
                goals = goals,
                ledgerViewModel = ledgerViewModel,
            )
        }
        if (DashboardWidget.Insights in widgets) {
            InsightCard(settings = activeSettings, expenses = expenses)
        }
        if (DashboardWidget.RecentTransactions in widgets) {
            RecentTransactionsCard(
                settings = activeSettings,
                expenses = expenses,
                categories = categories,
            )
        }
        if (DashboardWidget.CategoryBreakdown in widgets) {
            CategoryBreakdownCard(
                settings = activeSettings,
                expenses = currentMonthExpenses(expenses),
                categories = categories,
            )
        }
        if (DashboardWidget.MonthlyGraph in widgets) {
            MonthlyGraphCard(settings = activeSettings, expenses = currentMonthExpenses(expenses))
        }
    }
}

@Composable
private fun MonthlyBudgetCard(settings: UserSettings, expenses: List<ExpenseEntity>) {
    val ledgerColors = LocalLedgerColors.current
    val used = currentMonthExpenses(expenses).sumOf { it.amountMinor }
    val budget = settings.monthlyBudgetMinor
    val remaining = (budget - used).coerceAtLeast(0L)
    val progress = if (budget > 0L) (used.toFloat() / budget.toFloat()).coerceIn(0f, 1f) else 0f

    DashboardCard(title = "Monthly Budget") {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricBlock(
                label = "Budget",
                value = formatMoney(budget, settings.currencyCode),
                modifier = Modifier.weight(1f),
            )
            MetricBlock(
                label = "Used",
                value = formatMoney(used, settings.currencyCode),
                modifier = Modifier.weight(1f),
            )
        }
        MetricBlock(label = "Remaining", value = formatMoney(remaining, settings.currencyCode))
        AmountBar(progress = progress, color = if (progress < 0.8f) ledgerColors.success else ledgerColors.warning)
    }
}

@Composable
private fun DailySpendingCard(settings: UserSettings, expenses: List<ExpenseEntity>) {
    val today = LocalDate.now().toEpochDay()
    val todaySpent = expenses.filter { it.dateEpochDay == today }.sumOf { it.amountMinor }

    DashboardCard(title = "Daily Spending") {
        MetricBlock(label = "Today", value = formatMoney(todaySpent, settings.currencyCode))
        Text(
            text = if (todaySpent == 0L) "No spending recorded today." else "Today has saved expense activity.",
            color = LocalLedgerColors.current.muted,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun SavingsGoalCard(
    settings: UserSettings,
    goals: List<SavingsGoalEntity>,
    ledgerViewModel: SoloLedgerViewModel,
) {
    var title by remember { mutableStateOf("") }
    var targetAmount by remember { mutableStateOf("") }
    var savedAmount by remember { mutableStateOf("") }
    var progressAmounts by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var message by remember { mutableStateOf<String?>(null) }

    DashboardCard(title = "Savings Goal Progress") {
        if (goals.isEmpty()) {
            Text(
                text = "Create a savings goal to track target, saved, and remaining amounts.",
                color = LocalLedgerColors.current.muted,
                style = MaterialTheme.typography.bodyMedium,
            )
            SavingsGoalForm(
                title = title,
                onTitleChange = { title = it },
                targetAmount = targetAmount,
                onTargetAmountChange = { targetAmount = cleanAmountInput(it) },
                savedAmount = savedAmount,
                onSavedAmountChange = { savedAmount = cleanAmountInput(it) },
                currencyCode = settings.currencyCode,
                onSave = {
                    ledgerViewModel.createSavingsGoal(
                        title = title,
                        targetAmountText = targetAmount,
                        savedAmountText = savedAmount,
                        currencyCode = settings.currencyCode,
                        onSaved = {
                            title = ""
                            targetAmount = ""
                            savedAmount = ""
                            message = "Savings goal created."
                        },
                        onError = { message = it },
                    )
                },
            )
        } else {
            goals.forEach { goal ->
                SavingsGoalRow(
                    settings = settings,
                    goal = goal,
                    progressAmount = progressAmounts[goal.id].orEmpty(),
                    onProgressAmountChange = { value ->
                        progressAmounts = progressAmounts + (goal.id to cleanAmountInput(value))
                    },
                    onAddProgress = {
                        ledgerViewModel.addSavingsProgress(
                            goal = goal,
                            amountText = progressAmounts[goal.id].orEmpty(),
                            onSaved = {
                                progressAmounts = progressAmounts - goal.id
                                message = "Savings progress updated."
                            },
                            onError = { message = it },
                        )
                    },
                    onArchive = {
                        ledgerViewModel.archiveSavingsGoal(goal.id)
                        message = "Savings goal archived."
                    },
                )
            }
        }
        message?.let { currentMessage ->
            Text(
                text = currentMessage,
                color = if (currentMessage.contains("Enter", ignoreCase = true)) LocalLedgerColors.current.error else LocalLedgerColors.current.success,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun SavingsGoalForm(
    title: String,
    onTitleChange: (String) -> Unit,
    targetAmount: String,
    onTargetAmountChange: (String) -> Unit,
    savedAmount: String,
    onSavedAmountChange: (String) -> Unit,
    currencyCode: String,
    onSave: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        LedgerTextField(
            value = title,
            onValueChange = onTitleChange,
            label = "Goal Title",
            singleLine = true,
        )
        LedgerTextField(
            value = targetAmount,
            onValueChange = onTargetAmountChange,
            label = "Target $currencyCode",
            singleLine = true,
            keyboardType = KeyboardType.Decimal,
        )
        LedgerTextField(
            value = savedAmount,
            onValueChange = onSavedAmountChange,
            label = "Saved $currencyCode",
            singleLine = true,
            keyboardType = KeyboardType.Decimal,
        )
        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
            ),
        ) {
            Text(text = "Create Goal")
        }
    }
}

@Composable
private fun SavingsGoalRow(
    settings: UserSettings,
    goal: SavingsGoalEntity,
    progressAmount: String,
    onProgressAmountChange: (String) -> Unit,
    onAddProgress: () -> Unit,
    onArchive: () -> Unit,
) {
    val progress = if (goal.targetAmountMinor > 0L) {
        (goal.savedAmountMinor.toFloat() / goal.targetAmountMinor.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = LocalLedgerColors.current.surface),
        border = BorderStroke(1.dp, LocalLedgerColors.current.outline),
        shape = RoundedCornerShape(22.dp),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = goal.title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            AmountBar(progress = progress, color = LocalLedgerColors.current.success)
            Text(
                text = "${formatMoney(goal.savedAmountMinor, settings.currencyCode)} saved of ${formatMoney(goal.targetAmountMinor, settings.currencyCode)}",
                color = LocalLedgerColors.current.muted,
                style = MaterialTheme.typography.bodyMedium,
            )
            LedgerTextField(
                value = progressAmount,
                onValueChange = onProgressAmountChange,
                label = "Add Saved ${settings.currencyCode}",
                singleLine = true,
                keyboardType = KeyboardType.Decimal,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onAddProgress,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White,
                    ),
                ) {
                    Text(text = "Add")
                }
                OutlinedButton(
                    onClick = onArchive,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, LocalLedgerColors.current.outline),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = LocalLedgerColors.current.muted),
                ) {
                    Text(text = "Archive")
                }
            }
        }
    }
}

@Composable
private fun InsightCard(settings: UserSettings, expenses: List<ExpenseEntity>) {
    val monthExpenses = currentMonthExpenses(expenses)
    val used = monthExpenses.sumOf { it.amountMinor }
    val budget = settings.monthlyBudgetMinor
    val message = when {
        monthExpenses.isEmpty() -> "Start with one saved expense to unlock spending insights."
        budget <= 0L -> "Set a monthly budget to compare usage against your spending."
        used <= budget -> "You are within your monthly budget."
        else -> "Monthly spending is above the saved budget."
    }

    DashboardCard(title = "Insights") {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun RecentTransactionsCard(
    settings: UserSettings,
    expenses: List<ExpenseEntity>,
    categories: List<CategoryEntity>,
) {
    val categoryNames = categories.associate { it.id to it.name }

    DashboardCard(title = "Recent Transactions") {
        if (expenses.isEmpty()) {
            Text(
                text = "No expenses saved yet.",
                color = LocalLedgerColors.current.muted,
                style = MaterialTheme.typography.bodyMedium,
            )
        } else {
            expenses.take(5).forEach { expense ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = expense.title,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = categoryNames[expense.categoryId] ?: "Other",
                            color = LocalLedgerColors.current.muted,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    Text(
                        text = formatMoney(expense.amountMinor, settings.currencyCode),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryBreakdownCard(
    settings: UserSettings,
    expenses: List<ExpenseEntity>,
    categories: List<CategoryEntity>,
) {
    val totals = expenses.groupBy { it.categoryId }.mapValues { entry -> entry.value.sumOf { it.amountMinor } }
    val max = totals.values.maxOrNull()?.coerceAtLeast(1L) ?: 1L
    val categoryNames = categories.associate { it.id to it.name }

    DashboardCard(title = "Category Breakdown") {
        if (expenses.isEmpty()) {
            Text(
                text = "Category spending appears after expenses are saved.",
                color = LocalLedgerColors.current.muted,
                style = MaterialTheme.typography.bodyMedium,
            )
        } else {
            totals.entries.sortedByDescending { it.value }.take(5).forEach { (categoryId, amount) ->
                Text(
                    text = "${categoryNames[categoryId] ?: "Other"} - ${formatMoney(amount, settings.currencyCode)}",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                AmountBar(
                    progress = (amount.toFloat() / max.toFloat()).coerceIn(0f, 1f),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun MonthlyGraphCard(settings: UserSettings, expenses: List<ExpenseEntity>) {
    val weeks = (1..5).map { week ->
        expenses.filter { expense -> LocalDate.ofEpochDay(expense.dateEpochDay).dayOfMonth in ((week - 1) * 7 + 1)..(week * 7) }
            .sumOf { it.amountMinor }
    }
    val max = weeks.maxOrNull()?.coerceAtLeast(1L) ?: 1L

    DashboardCard(title = "Monthly Graph") {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            weeks.forEachIndexed { index, amount ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height((16 + (64 * (amount.toFloat() / max.toFloat()))).dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.primary),
                    )
                    Text(
                        text = "W${index + 1}",
                        color = LocalLedgerColors.current.muted,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        }
        Text(
            text = "Monthly total ${formatMoney(expenses.sumOf { it.amountMinor }, settings.currencyCode)}",
            color = LocalLedgerColors.current.muted,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun DashboardCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    val ledgerColors = LocalLedgerColors.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ledgerColors.card),
        border = BorderStroke(1.dp, ledgerColors.outline),
        shape = RoundedCornerShape(28.dp),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            content()
        }
    }
}

@Composable
private fun MetricBlock(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    val ledgerColors = LocalLedgerColors.current

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(ledgerColors.surface)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(text = label, color = ledgerColors.muted, style = MaterialTheme.typography.labelMedium)
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun AmountBar(progress: Float, color: Color) {
    val ledgerColors = LocalLedgerColors.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(ledgerColors.surface),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .height(10.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(color),
        )
    }
}

private fun currentMonthExpenses(expenses: List<ExpenseEntity>): List<ExpenseEntity> {
    val now = LocalDate.now()
    val start = now.withDayOfMonth(1).toEpochDay()
    val end = now.withDayOfMonth(now.lengthOfMonth()).toEpochDay()
    return expenses.filter { it.dateEpochDay in start..end }
}

private fun formatDate(epochDay: Long): String = LocalDate.ofEpochDay(epochDay).toString()

private fun formatMillisDate(millis: Long): String = Instant.ofEpochMilli(millis)
    .atZone(ZoneId.systemDefault())
    .toLocalDate()
    .toString()

private fun formatTime(minuteOfDay: Int): String {
    val hour = minuteOfDay / 60
    val minute = minuteOfDay % 60
    return String.format(Locale.US, "%02d:%02d", hour, minute)
}

private fun formatMoney(minor: Long, currencyCode: String): String = runCatching {
    NumberFormat.getCurrencyInstance().apply {
        currency = Currency.getInstance(currencyCode)
    }.format(minor / 100.0)
}.getOrElse {
    "$currencyCode ${String.format(Locale.US, "%.2f", minor / 100.0)}"
}

private fun cleanAmountInput(value: String): String = value.filter { char -> char.isDigit() || char == '.' }

private fun parseColorOrDefault(value: String, fallback: Color): Color = runCatching {
    Color(android.graphics.Color.parseColor(value))
}.getOrElse { fallback }

private fun String.splitThemeName(): String = replace(Regex("(?<=[a-z])(?=[A-Z])"), " ")

private fun String.splitCamelName(): String = replace(Regex("(?<=[a-z])(?=[A-Z])"), " ")

private val comingSoonItems = listOf(
    "Login",
    "Cloud Sync",
    "Online Backup",
    "Multi Device Sync",
    "Shared Budgets",
    "Family Accounts",
    "AI Insights",
    "Bank Integration",
    "UPI Integration",
    "OCR Receipt Scanner",
)

private enum class HistorySort(val label: String) {
    NewestFirst("Newest First"),
    OldestFirst("Oldest First"),
    HighestAmount("Highest Amount"),
    LowestAmount("Lowest Amount"),
}

private enum class CalendarRange(val label: String) {
    Last7Days("7 Days"),
    Last30Days("30 Days"),
    Last90Days("90 Days"),
    Custom("Custom Range");

    fun resolve(customStart: String, customEnd: String): Pair<LocalDate, LocalDate> {
        val today = LocalDate.now()
        return when (this) {
            Last7Days -> today.minusDays(6) to today
            Last30Days -> today.minusDays(29) to today
            Last90Days -> today.minusDays(89) to today
            Custom -> {
                val start = runCatching { LocalDate.parse(customStart.trim()) }.getOrDefault(today.minusDays(29))
                val end = runCatching { LocalDate.parse(customEnd.trim()) }.getOrDefault(today)
                if (start <= end) start to end else end to start
            }
        }
    }
}

@Composable
private fun QuickAddScreen(
    ledgerViewModel: SoloLedgerViewModel,
    currencyCode: String,
) {
    val ledgerColors = LocalLedgerColors.current
    val categories by ledgerViewModel.categories.collectAsState()
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf(LocalDate.now().toString()) }
    var timeText by remember { mutableStateOf(LocalTime.now().withSecond(0).withNano(0).toString()) }
    var notes by remember { mutableStateOf("") }
    var attachmentUri by remember { mutableStateOf<Uri?>(null) }
    var formMessage by remember { mutableStateOf<String?>(null) }
    val attachmentPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        attachmentUri = uri
    }

    LaunchedEffect(categories) {
        if (selectedCategoryId.isBlank() && categories.isNotEmpty()) {
            selectedCategoryId = categories.first().id
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ledgerColors.card),
        border = BorderStroke(1.dp, ledgerColors.outline),
        shape = RoundedCornerShape(28.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            LedgerTextField(
                value = title,
                onValueChange = { title = it },
                label = "Title",
                singleLine = true,
            )
            LedgerTextField(
                value = amount,
                onValueChange = { amount = it.filter { char -> char.isDigit() || char == '.' } },
                label = "Amount $currencyCode",
                singleLine = true,
                keyboardType = KeyboardType.Decimal,
            )
            CategorySelector(
                categories = categories,
                selectedCategoryId = selectedCategoryId,
                onSelected = { selectedCategoryId = it },
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                LedgerTextField(
                    value = dateText,
                    onValueChange = { dateText = it },
                    label = "Date",
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
                LedgerTextField(
                    value = timeText,
                    onValueChange = { timeText = it },
                    label = "Time",
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
            }
            LedgerTextField(
                value = notes,
                onValueChange = { notes = it },
                label = "Notes",
                minLines = 3,
            )
            OutlinedButton(
                onClick = { attachmentPicker.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, ledgerColors.outline),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = ledgerColors.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
            ) {
                Text(text = if (attachmentUri == null) "Attach Receipt Or Bill" else "Attachment Selected")
            }
            Button(
                onClick = {
                    ledgerViewModel.addExpense(
                        title = title,
                        amountText = amount,
                        currencyCode = currencyCode,
                        categoryId = selectedCategoryId,
                        dateText = dateText,
                        timeText = timeText,
                        notes = notes,
                        attachmentUri = attachmentUri,
                        onSaved = {
                            title = ""
                            amount = ""
                            notes = ""
                            attachmentUri = null
                            formMessage = "Expense saved offline."
                        },
                        onError = { formMessage = it },
                    )
                },
                enabled = title.isNotBlank() && amount.isNotBlank() && selectedCategoryId.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    disabledContainerColor = ledgerColors.surface,
                    disabledContentColor = ledgerColors.muted,
                ),
            ) {
                Text(
                    text = "Save Expense",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            formMessage?.let { message ->
                Text(
                    text = message,
                    color = if (message.contains("saved", ignoreCase = true)) ledgerColors.success else ledgerColors.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun LedgerTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = false,
    minLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    val ledgerColors = LocalLedgerColors.current

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(text = label) },
        singleLine = singleLine,
        minLines = minLines,
        shape = RoundedCornerShape(18.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedContainerColor = ledgerColors.card,
            unfocusedContainerColor = ledgerColors.card,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = ledgerColors.outline,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = ledgerColors.muted,
            cursorColor = MaterialTheme.colorScheme.primary,
        ),
    )
}

@Composable
private fun CategorySelector(
    categories: List<CategoryEntity>,
    selectedCategoryId: String,
    onSelected: (String) -> Unit,
) {
    val ledgerColors = LocalLedgerColors.current

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Category",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
        )
        categories.chunked(3).forEach { rowCategories ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                rowCategories.forEach { category ->
                    CategoryChip(
                        category = category,
                        selected = selectedCategoryId == category.id,
                        onSelected = onSelected,
                        modifier = Modifier.weight(1f),
                    )
                }
                repeat(3 - rowCategories.size) {
                    SpacerWeight()
                }
            }
        }
        if (categories.isEmpty()) {
            Text(
                text = "Preparing default categories offline.",
                color = ledgerColors.muted,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun RowScope.SpacerWeight() {
    Box(modifier = Modifier.weight(1f))
}

@Composable
private fun CategoryChip(
    category: CategoryEntity,
    selected: Boolean,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val ledgerColors = LocalLedgerColors.current

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onSelected(category.id) },
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) ledgerColors.navSelected else ledgerColors.surface,
        ),
        border = BorderStroke(1.dp, if (selected) MaterialTheme.colorScheme.primary else ledgerColors.outline),
        shape = RoundedCornerShape(16.dp),
    ) {
        Text(
            text = category.name,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
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

private enum class AppRoute(val route: String) {
    Onboarding("onboarding"),
    Main("main"),
}

private data class OnboardingPage(
    val title: String,
    val body: String,
    val illustrationRes: Int,
)

private val onboardingPages = listOf(
    OnboardingPage(
        title = "Track Every Expense",
        body = "Monitor spending with clarity across daily purchases, bills, and subscriptions.",
        illustrationRes = R.drawable.onboarding_track,
    ),
    OnboardingPage(
        title = "Control Your Budget",
        body = "Stay within limits with a monthly budget, focused categories, and local records.",
        illustrationRes = R.drawable.onboarding_budget,
    ),
    OnboardingPage(
        title = "Build Better Habits",
        body = "Improve financial decisions with savings goals and spending awareness.",
        illustrationRes = R.drawable.onboarding_habits,
    ),
)

private enum class LedgerDestination(
    val title: String,
    val description: String,
    val emptyTitle: String,
    val emptyBody: String,
    val icon: ImageVector,
) {
    Home(
        title = "Home",
        description = "Monthly budget, spending awareness, savings goals, and insights.",
        emptyTitle = "No Expenses Yet",
        emptyBody = "Add your first expense from Quick Add to begin tracking your monthly budget offline.",
        icon = Icons.Outlined.Home,
    ),
    History(
        title = "History",
        description = "Searchable, grouped transactions ordered by spending date.",
        emptyTitle = "No Transaction History",
        emptyBody = "Saved expenses appear here with notes, categories, dates, and receipt attachments.",
        icon = Icons.Outlined.History,
    ),
    QuickAdd(
        title = "Quick Add",
        description = "Capture expenses quickly with the fields enabled in settings.",
        emptyTitle = "Ready For Expense Capture",
        emptyBody = "Title, amount, category, date, time, notes, and receipt attachment are stored offline.",
        icon = Icons.Outlined.Add,
    ),
    Calendar(
        title = "Calendar",
        description = "Spending days, date details, and range filters from local transaction data.",
        emptyTitle = "No Calendar Spending",
        emptyBody = "Dates with saved expenses are highlighted for quick spending review.",
        icon = Icons.Outlined.CalendarMonth,
    ),
    Settings(
        title = "Settings",
        description = "Appearance, dashboard controls, data export, accessibility, and support options.",
        emptyTitle = "Settings Foundation",
        emptyBody = "Theme selection, font scaling, radius, dashboard sections, and quick-add fields are stored locally.",
        icon = Icons.Outlined.Settings,
    ),
}

@Preview(showBackground = true)
@Composable
private fun SoloLedgerAppPreview() {
    SoloLedgerTheme(theme = LedgerTheme.LedgerDark) {
        MainLedgerShell()
    }
}
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
