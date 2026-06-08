package com.solo.ledger.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.TrendingDown
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.solo.ledger.data.datastore.AppSettings
import com.solo.ledger.data.model.Category
import com.solo.ledger.data.model.Transaction
import com.solo.ledger.ui.components.cards.BalanceCard
import com.solo.ledger.ui.components.cards.BudgetCard
import com.solo.ledger.ui.components.cards.TransactionCard
import com.solo.ledger.ui.components.empty.EmptyTransactions
import com.solo.ledger.ui.components.shared.AppLogo
import com.solo.ledger.ui.components.shared.SectionHeader
import com.solo.ledger.ui.components.shared.SwipeableTransactionItem
import com.solo.ledger.ui.components.shared.ThemedCard
import com.solo.ledger.ui.navigation.NavRoutes
import com.solo.ledger.util.CurrencyFormatter
import com.solo.ledger.util.DateUtils
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(
    navController: NavController,
    onQuickAddClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val scrollState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            LazyColumn(
                state = scrollState,
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                item {
                    HomeTopSection(settings = settings)
                }

                item {
                    val parallaxOffset = scrollState.firstVisibleItemScrollOffset * 0.3f
                    BalanceCard(
                        balance = uiState.balance,
                        income = uiState.incomeThisMonth,
                        expense = uiState.expenseThisMonth,
                        currencySymbol = settings.currencySymbol,
                        userName = settings.userName.ifBlank { "there" },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 8.dp)
                            .graphicsLayer { translationY = parallaxOffset },
                        onAnalyticsClick = { navController.navigate(NavRoutes.ANALYTICS) }
                    )
                }

                item {
                    MonthlyComparisonRow(
                        incomeThisMonth = uiState.incomeThisMonth,
                        expenseThisMonth = uiState.expenseThisMonth,
                        monthVsLastMonth = uiState.monthVsLastMonth,
                        currencySymbol = settings.currencySymbol
                    )
                    Spacer(Modifier.height(4.dp))
                }

                if (uiState.insights.isNotEmpty()) {
                    item {
                        InsightsSection(insights = uiState.insights)
                        Spacer(Modifier.height(4.dp))
                    }
                }

                if (uiState.budgetItems.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(8.dp))
                        SectionHeader(title = "Budget Overview")
                        Spacer(Modifier.height(8.dp))
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(uiState.budgetItems) { item ->
                                BudgetCard(
                                    category = item.category,
                                    spent = item.spent,
                                    limit = item.limit,
                                    currencySymbol = settings.currencySymbol
                                )
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                    }
                }

                item {
                    Spacer(Modifier.height(12.dp))
                    SectionHeader(
                        title = "Recent Transactions",
                        action = {
                            TextButton(onClick = { navController.navigate(NavRoutes.HISTORY) }) {
                                Text(
                                    text = "View All",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.width(2.dp))
                                Icon(
                                    imageVector = Icons.Outlined.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    )
                    Spacer(Modifier.height(8.dp))
                }

                if (uiState.recentTransactions.isEmpty()) {
                    item {
                        Spacer(Modifier.height(16.dp))
                        EmptyTransactions(onAddClick = onQuickAddClick)
                    }
                } else {
                    items(
                        items = uiState.recentTransactions,
                        key = { (txn, _) -> txn.id }
                    ) { (transaction, category) ->
                        HomeTransactionRow(
                            transaction = transaction,
                            category = category,
                            settings = settings,
                            onDelete = { viewModel.deleteTransaction(transaction.id) },
                            onEdit = {
                                navController.navigate(NavRoutes.editTransaction(transaction.id))
                            },
                            currencySymbol = settings.currencySymbol
                        )
                    }
                }

                item { Spacer(Modifier.height(100.dp)) }
            }
        }

        FloatingActionButton(
            onClick = onQuickAddClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 96.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = CircleShape
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "Quick Add")
        }
    }
}

@Composable
private fun HomeTopSection(settings: AppSettings) {
    val today = LocalDate.now()
    val monthYear = DateUtils.formatMonthYear(today)
    val dayOfMonth = today.dayOfMonth
    val dayOfWeek = today.dayOfWeek.getDisplayName(
        java.time.format.TextStyle.SHORT, Locale.ENGLISH
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = monthYear,
                style = MaterialTheme.typography.labelMedium.copy(
                    letterSpacing = 0.8.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
            )
            Text(
                text = "$dayOfWeek, $dayOfMonth",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        AppLogo(size = 36.dp, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun MonthlyComparisonRow(
    incomeThisMonth: Double,
    expenseThisMonth: Double,
    monthVsLastMonth: Pair<Double, Double>,
    currencySymbol: String
) {
    val (thisExpense, lastExpense) = monthVsLastMonth
    val expenseChange = if (lastExpense > 0.0) {
        ((thisExpense - lastExpense) / lastExpense * 100.0)
    } else 0.0
    val isExpenseUp = expenseChange > 0.0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        FilterChip(
            selected = false,
            onClick = {},
            label = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowUpward,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = Color(0xFF2E7D32)
                    )
                    Text(
                        text = "Income ${CurrencyFormatter.formatCompact(incomeThisMonth, currencySymbol)}",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            },
            colors = FilterChipDefaults.filterChipColors(
                containerColor = Color(0xFF2E7D32).copy(alpha = 0.10f),
                labelColor = Color(0xFF2E7D32)
            )
        )
        FilterChip(
            selected = false,
            onClick = {},
            label = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (isExpenseUp) Icons.Outlined.TrendingUp else Icons.Outlined.TrendingDown,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = if (isExpenseUp) Color(0xFFC62828) else Color(0xFF2E7D32)
                    )
                    val changeText = if (expenseChange != 0.0) {
                        val sign = if (isExpenseUp) "+" else ""
                        "$sign${String.format("%.1f", expenseChange)}% vs last month"
                    } else {
                        "Expense ${CurrencyFormatter.formatCompact(expenseThisMonth, currencySymbol)}"
                    }
                    Text(
                        text = changeText,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            },
            colors = FilterChipDefaults.filterChipColors(
                containerColor = if (isExpenseUp) Color(0xFFC62828).copy(alpha = 0.10f) else Color(0xFF2E7D32).copy(alpha = 0.10f),
                labelColor = if (isExpenseUp) Color(0xFFC62828) else Color(0xFF2E7D32)
            )
        )
    }
}

@Composable
private fun InsightsSection(insights: List<String>) {
    Column {
        SectionHeader(title = "Insights")
        Spacer(Modifier.height(8.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(insights) { insight ->
                InsightCard(text = insight)
            }
        }
    }
}

@Composable
private fun InsightCard(text: String) {
    ThemedCard(modifier = Modifier.width(220.dp)) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(18.dp)
                    .padding(top = 1.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun HomeTransactionRow(
    transaction: Transaction,
    category: Category?,
    settings: AppSettings,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    currencySymbol: String
) {
    val leftIsDelete = settings.swipeLeftAction == "delete"
    val rightIsDelete = settings.swipeRightAction == "delete"

    SwipeableTransactionItem(
        onSwipeLeft = if (leftIsDelete) onDelete else onEdit,
        onSwipeRight = if (rightIsDelete) onDelete else onEdit,
        leftActionIcon = if (leftIsDelete) Icons.Filled.Delete else Icons.Filled.Edit,
        rightActionIcon = if (rightIsDelete) Icons.Filled.Delete else Icons.Filled.Edit,
        leftActionColor = if (leftIsDelete) Color(0xFFFF4444) else Color(0xFF2196F3),
        rightActionColor = if (rightIsDelete) Color(0xFFFF4444) else Color(0xFF2196F3),
        leftActionLabel = if (leftIsDelete) "Delete" else "Edit",
        rightActionLabel = if (rightIsDelete) "Delete" else "Edit"
    ) {
        TransactionCard(
            transaction = transaction,
            category = category,
            currencySymbol = currencySymbol,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 3.dp),
            onClick = onEdit
        )
    }
}
