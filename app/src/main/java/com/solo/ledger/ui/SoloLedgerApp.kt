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
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import java.time.LocalDate
import java.time.LocalTime
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
            destination == LedgerDestination.QuickAdd && ledgerViewModel != null -> QuickAddScreen(
                ledgerViewModel = ledgerViewModel,
                currencyCode = currencyCode,
            )
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
            SavingsGoalCard(settings = activeSettings, goals = goals)
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
private fun SavingsGoalCard(settings: UserSettings, goals: List<SavingsGoalEntity>) {
    DashboardCard(title = "Savings Goal Progress") {
        if (goals.isEmpty()) {
            Text(
                text = "Create a savings goal to track target, saved, and remaining amounts.",
                color = LocalLedgerColors.current.muted,
                style = MaterialTheme.typography.bodyMedium,
            )
        } else {
            val goal = goals.first()
            val progress = if (goal.targetAmountMinor > 0L) {
                (goal.savedAmountMinor.toFloat() / goal.targetAmountMinor.toFloat()).coerceIn(0f, 1f)
            } else {
                0f
            }
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

private fun formatMoney(minor: Long, currencyCode: String): String = runCatching {
    NumberFormat.getCurrencyInstance().apply {
        currency = Currency.getInstance(currencyCode)
    }.format(minor / 100.0)
}.getOrElse {
    "$currencyCode ${String.format(Locale.US, "%.2f", minor / 100.0)}"
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
