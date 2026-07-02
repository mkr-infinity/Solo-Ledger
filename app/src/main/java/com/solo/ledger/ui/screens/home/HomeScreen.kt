package com.solo.ledger.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.solo.ledger.data.model.Category
import com.solo.ledger.data.model.Expense
import com.solo.ledger.ui.components.DonutChart
import com.solo.ledger.ui.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToSavings: () -> Unit,
    onNavigateToProfile: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {}
) {
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val monthlyBudget by viewModel.monthlyBudget.collectAsStateWithLifecycle()
    val monthlySpending by viewModel.monthlySpending.collectAsStateWithLifecycle()
    val todaySpending by viewModel.todaySpending.collectAsStateWithLifecycle()
    val currencySymbol by viewModel.currencySymbol.collectAsStateWithLifecycle()
    val allExpenses by viewModel.allExpenses.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val categorySpending by viewModel.categorySpending.collectAsStateWithLifecycle()
    val savingsGoals by viewModel.savingsGoals.collectAsStateWithLifecycle()
    val borderRadius by viewModel.borderRadius.collectAsStateWithLifecycle()

    val remaining = monthlyBudget - monthlySpending
    val budgetUsagePercent = if (monthlyBudget > 0) (monthlySpending / monthlyBudget).coerceIn(0.0, 1.0) else 0.0
    val recentExpenses = allExpenses.take(5)
    val cardShape = RoundedCornerShape(borderRadius.dp)

    // Live clock
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = System.currentTimeMillis()
            delay(1000)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top bar: Profile image + Greeting + Live time
        item {
            HomeTopBar(
                userName = userName,
                currentTime = currentTime,
                borderRadius = borderRadius,
                onProfileClick = onNavigateToProfile
            )
        }

        // Flippable Budget Card
        item {
            FlippableBudgetCard(
                monthlyBudget = monthlyBudget,
                spent = monthlySpending,
                remaining = remaining,
                usagePercent = budgetUsagePercent.toFloat(),
                currencySymbol = currencySymbol,
                categorySpending = categorySpending,
                categories = categories,
                totalSpending = monthlySpending,
                cardShape = cardShape
            )
        }

        // Quick stats row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Today's spending
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = cardShape
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            "Today",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "$currencySymbol${formatAmount(todaySpending)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                // Total transactions
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = cardShape
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            "Transactions",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "${allExpenses.size}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                // Goals
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = cardShape
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            "Goals",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "${savingsGoals.size}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Recent Transactions header with "View All"
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Transactions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextButton(onClick = onNavigateToHistory) {
                    Text(
                        "View All",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        Icons.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Recent expenses
        if (recentExpenses.isNotEmpty()) {
            items(recentExpenses, key = { it.id }) { expense ->
                val category = categories.find { it.id == expense.categoryId }
                RecentTransactionItem(
                    expense = expense,
                    category = category,
                    currencySymbol = currencySymbol,
                    cardShape = cardShape
                )
            }
        } else {
            item {
                EmptyStateCard(cardShape)
            }
        }

        // Spending insight
        if (allExpenses.isNotEmpty()) {
            item {
                val insight = remember(monthlySpending, monthlyBudget, todaySpending) {
                    when {
                        budgetUsagePercent > 0.9 -> "You have used over 90% of your budget. Consider reducing spending."
                        budgetUsagePercent > 0.7 -> "Budget usage is high. Plan your remaining days carefully."
                        todaySpending > monthlyBudget / 30 * 2 -> "Today's spending is above your daily average."
                        monthlySpending == 0.0 -> "No spending recorded this month. Start tracking!"
                        else -> "You are on track. Keep maintaining your budget discipline."
                    }
                }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f)
                    ),
                    shape = cardShape
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Lightbulb,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = insight,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Savings Goals quick view
        if (savingsGoals.isNotEmpty()) {
            item {
                SavingsGoalPreview(
                    goals = savingsGoals,
                    currencySymbol = currencySymbol,
                    onViewAll = onNavigateToSavings,
                    cardShape = cardShape
                )
            }
        }
    }
}

@Composable
private fun HomeTopBar(
    userName: String,
    currentTime: Long,
    borderRadius: Float,
    onProfileClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onProfileClick() }
        ) {
            // Profile avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName.firstOrNull()?.uppercase() ?: "U",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Hello, ${userName.ifBlank { "there" }}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = dateFormat.format(Date(currentTime)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Live clock display
        Surface(
            shape = RoundedCornerShape(borderRadius.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 2.dp
        ) {
            Text(
                text = timeFormat.format(Date(currentTime)),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun FlippableBudgetCard(
    monthlyBudget: Double,
    spent: Double,
    remaining: Double,
    usagePercent: Float,
    currencySymbol: String,
    categorySpending: List<com.solo.ledger.data.dao.CategorySpending>,
    categories: List<Category>,
    totalSpending: Double,
    cardShape: RoundedCornerShape
) {
    var isFlipped by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "flip"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable { isFlipped = !isFlipped }
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
    ) {
        if (rotation <= 90f) {
            // Front: Budget info
            BudgetCardFront(
                monthlyBudget = monthlyBudget,
                spent = spent,
                remaining = remaining,
                usagePercent = usagePercent,
                currencySymbol = currencySymbol,
                cardShape = cardShape
            )
        } else {
            // Back: Spending graph
            Box(modifier = Modifier.graphicsLayer { rotationY = 180f }) {
                BudgetCardBack(
                    categorySpending = categorySpending,
                    categories = categories,
                    totalSpending = totalSpending,
                    currencySymbol = currencySymbol,
                    cardShape = cardShape
                )
            }
        }
    }
}

@Composable
private fun BudgetCardFront(
    monthlyBudget: Double,
    spent: Double,
    remaining: Double,
    usagePercent: Float,
    currencySymbol: String,
    cardShape: RoundedCornerShape
) {
    Card(
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = cardShape
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Subtle decorative circle
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .offset(x = 220.dp, y = (-20).dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Monthly Budget",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$currencySymbol${formatAmount(monthlyBudget)}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.TouchApp,
                            contentDescription = "Tap to flip",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Progress
                Column {
                    LinearProgressIndicator(
                        progress = usagePercent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (usagePercent > 0.8f) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.12f)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Spent",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "$currencySymbol${formatAmount(spent)}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Usage",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "${(usagePercent * 100).toInt()}%",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Remaining",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "$currencySymbol${formatAmount(remaining.coerceAtLeast(0.0))}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (remaining < 0) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BudgetCardBack(
    categorySpending: List<com.solo.ledger.data.dao.CategorySpending>,
    categories: List<Category>,
    totalSpending: Double,
    currencySymbol: String,
    cardShape: RoundedCornerShape
) {
    Card(
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = cardShape
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Spending Breakdown",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = Icons.Filled.TouchApp,
                    contentDescription = "Tap to flip",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (categorySpending.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val chartData = categorySpending.take(5).map { spending ->
                        val cat = categories.find { it.id == spending.categoryId }
                        DonutChart.Segment(
                            value = spending.total.toFloat(),
                            color = Color(cat?.color ?: 0xFF90A4AE),
                            label = cat?.name ?: "Other"
                        )
                    }

                    DonutChart(
                        segments = chartData,
                        modifier = Modifier.size(90.dp),
                        strokeWidth = 18f
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        categorySpending.take(5).forEach { spending ->
                            val cat = categories.find { it.id == spending.categoryId }
                            val percent = if (totalSpending > 0) (spending.total / totalSpending * 100).toInt() else 0
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(cat?.color ?: 0xFF90A4AE))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${cat?.name ?: "Other"}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "$percent%",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No spending data yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentTransactionItem(
    expense: Expense,
    category: Category?,
    currencySymbol: String,
    cardShape: RoundedCornerShape
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
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(category?.color ?: 0xFF90A4AE).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getCategoryIcon(category?.icon),
                        contentDescription = null,
                        tint = Color(category?.color ?: 0xFF90A4AE),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = expense.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${category?.name ?: "Other"} • ${expense.time}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "-$currencySymbol${formatAmount(expense.amount)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(expense.date)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SavingsGoalPreview(
    goals: List<com.solo.ledger.data.model.SavingsGoal>,
    currencySymbol: String,
    onViewAll: () -> Unit,
    cardShape: RoundedCornerShape
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = cardShape
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Savings Goals",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(onClick = onViewAll) {
                    Text("View All", style = MaterialTheme.typography.labelMedium)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            goals.take(2).forEach { goal ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = goal.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "$currencySymbol${formatAmount(goal.savedAmount)} / $currencySymbol${formatAmount(goal.targetAmount)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    CircularProgressIndicator(
                        progress = goal.progressPercent / 100f,
                        modifier = Modifier.size(36.dp),
                        strokeWidth = 4.dp,
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyStateCard(cardShape: RoundedCornerShape) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = cardShape
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.ReceiptLong,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No expenses yet",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Add your first expense to start tracking",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

fun getCategoryIcon(iconName: String?): androidx.compose.ui.graphics.vector.ImageVector {
    return when (iconName) {
        "restaurant" -> Icons.Filled.Restaurant
        "directions_car" -> Icons.Filled.DirectionsCar
        "shopping_bag" -> Icons.Filled.ShoppingBag
        "receipt_long" -> Icons.Filled.ReceiptLong
        "school" -> Icons.Filled.School
        "movie" -> Icons.Filled.Movie
        "local_grocery_store" -> Icons.Filled.LocalGroceryStore
        "subscriptions" -> Icons.Filled.Subscriptions
        "more_horiz" -> Icons.Filled.MoreHoriz
        "home" -> Icons.Filled.Home
        "health_and_safety" -> Icons.Filled.LocalHospital
        "fitness_center" -> Icons.Filled.FitnessCenter
        "coffee" -> Icons.Filled.LocalCafe
        "flight" -> Icons.Filled.Flight
        "pets" -> Icons.Filled.Pets
        else -> Icons.Filled.Category
    }
}

fun formatAmount(amount: Double): String {
    return if (amount == amount.toLong().toDouble()) {
        amount.toLong().toString()
    } else {
        String.format("%.2f", amount)
    }
}
