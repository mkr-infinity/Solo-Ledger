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
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.solo.ledger.R
import com.solo.ledger.data.model.BudgetTemplate
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
                    MainLedgerShell()
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
private fun MainLedgerShell() {
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
