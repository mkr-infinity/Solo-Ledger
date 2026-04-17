package com.kaif.ledger.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kaif.ledger.data.database.entity.CategoryEntity
import com.kaif.ledger.data.database.entity.ExpenseEntity
import com.kaif.ledger.ui.components.BottomNavBar
import com.kaif.ledger.ui.components.BottomNavItem
import com.kaif.ledger.ui.components.BudgetVsSpentDonut
import com.kaif.ledger.ui.components.SpendingTrendChart
import com.kaif.ledger.ui.components.WeeklyComparisonChart
import com.kaif.ledger.ui.navigation.Screen
import com.kaif.ledger.ui.theme.ThemeType
import com.kaif.ledger.viewmodel.ExpenseViewModel
import com.kaif.ledger.viewmodel.SettingsViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max

private val categoryIconMap = mapOf(
    "Dining" to Icons.Default.Restaurant,
    "Retail" to Icons.Default.ShoppingCart,
    "Transit" to Icons.Default.DirectionsCar,
    "Wellness" to Icons.Default.FavoriteBorder,
    "Gadgets" to Icons.Default.Smartphone,
    "Dividends" to Icons.Default.AttachMoney,
    "Entertainment" to Icons.Default.Movie,
    "Utilities" to Icons.Default.Home,
    "Apparel" to Icons.Default.ShoppingBag,
    "Other" to Icons.Default.MoreHoriz
)

private val fallbackCategoryNames = listOf(
    "Dining",
    "Retail",
    "Transit",
    "Wellness",
    "Gadgets",
    "Dividends",
    "Entertainment",
    "Utilities",
    "Apparel",
    "Other"
)

private val dateTimeDisplayFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US)

private fun categoryNameFromId(categoryId: Long, categories: List<CategoryEntity> = emptyList()): String {
    categories.firstOrNull { it.id == categoryId }?.name?.let { return it }
    val fallbackIndex = (categoryId - 1L).toInt()
    return fallbackCategoryNames.getOrNull(fallbackIndex) ?: "Other"
}

private fun categoryIdFromName(name: String, categories: List<CategoryEntity>): Long {
    categories.firstOrNull { it.name.equals(name, ignoreCase = true) }?.id?.let { return it }
    val fallbackIndex = fallbackCategoryNames.indexOfFirst { it.equals(name, ignoreCase = true) }
    return if (fallbackIndex >= 0) fallbackIndex + 1L else 1L
}

private fun initialsFromName(name: String): String {
    val words = name.trim().split(" ").filter { it.isNotBlank() }
    return when {
        words.isEmpty() -> "U"
        words.size == 1 -> words.first().take(2).uppercase(Locale.getDefault())
        else -> (words[0].take(1) + words[1].take(1)).uppercase(Locale.getDefault())
    }
}

private fun formatMoney(value: Double): String = "$" + String.format(Locale.US, "%,.2f", value)

private fun formatSignedMoney(value: Double): String {
    return if (value < 0) {
        "-${formatMoney(abs(value))}"
    } else {
        "+${formatMoney(value)}"
    }
}

private fun formatQuickAddDate(dateTime: LocalDateTime): String {
    val now = LocalDate.now()
    val time = dateTime.format(dateTimeDisplayFormatter)
    return if (dateTime.toLocalDate() == now) {
        "TODAY, $time"
    } else {
        val day = dateTime.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US).uppercase(Locale.US)
        val monthDay = dateTime.format(DateTimeFormatter.ofPattern("MMM d", Locale.US))
        "$day, $monthDay, $time"
    }
}

@Composable
fun HomeScreen(
    navController: NavController,
    expenseViewModel: ExpenseViewModel = hiltViewModel()
) {
    var currentTab by rememberSaveable { mutableStateOf(0) }
    var showQuickAddModal by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BottomNavBar(
                selectedTab = currentTab,
                onTabSelected = { currentTab = it },
                onFabClick = { showQuickAddModal = true },
                items = listOf(
                    BottomNavItem(Icons.Default.Home, "HOME"),
                    BottomNavItem(Icons.Default.TrendingUp, "TRENDS"),
                    BottomNavItem(Icons.Default.AccountBalanceWallet, "ASSETS"),
                    BottomNavItem(Icons.Default.Person, "ME")
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentTab) {
                0 -> HomeTabContent(
                    navController = navController,
                    expenseViewModel = expenseViewModel,
                    onOpenArchive = { currentTab = 1 }
                )
                1 -> HistoryTabContent(navController = navController, expenseViewModel = expenseViewModel)
                2 -> ReportTabContent(navController = navController, expenseViewModel = expenseViewModel)
                3 -> ProfileTabContent(navController = navController, expenseViewModel = expenseViewModel)
            }
        }
    }

    if (showQuickAddModal) {
        QuickAddModal(
            onDismiss = { showQuickAddModal = false },
            onConfirm = { expense ->
                expenseViewModel.addExpense(expense)
                showQuickAddModal = false
            },
            expenseViewModel = expenseViewModel
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HomeTabContent(
    navController: NavController,
    expenseViewModel: ExpenseViewModel = hiltViewModel(),
    onOpenArchive: () -> Unit = {}
) {
    val allExpenses by expenseViewModel.allExpenses.collectAsState()
    val allCategories by expenseViewModel.allCategories.collectAsState()
    val totalExpenses by expenseViewModel.totalExpenses.collectAsState()
    val dailyExpenses by expenseViewModel.dailyExpenses.collectAsState()
    val dailyBurnPercentage by expenseViewModel.dailyBurnPercentage.collectAsState()
    val remainingBudget by expenseViewModel.remainingBudget.collectAsState()
    val settings by expenseViewModel.settings.collectAsState()

    val userName = settings?.userName?.ifBlank { "User" } ?: "User"
    val usedAmount = abs(totalExpenses)
    val remainingAmount = max(0.0, remainingBudget)
    val portfolioAmount = usedAmount + remainingAmount
    val usedProgress = if (portfolioAmount <= 0.0) 0f else (usedAmount / portfolioAmount).toFloat().coerceIn(0f, 1f)
    val daySpend = abs(dailyExpenses)
    val dayLimit = settings?.dailyBudgetLimit ?: 250.0
    val recentExpenses = remember(allExpenses) { allExpenses.sortedByDescending { it.date }.take(3) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape)
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initialsFromName(userName),
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                    Column(modifier = Modifier.padding(start = 10.dp)) {
                        Text(
                            text = "CURATING FOR",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = userName,
                            fontSize = 28.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f), CircleShape)
                        .border(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f), CircleShape)
                        .clickable { onOpenArchive() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.28f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.surface,
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                                )
                            )
                        )
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AssistChip(
                        onClick = {},
                        enabled = false,
                        label = {
                            Text(
                                "PORTFOLIO NET WORTH",
                                fontSize = 10.sp,
                                letterSpacing = 1.2.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.35f),
                            disabledContainerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.35f)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.12f))
                    )

                    Text(
                        text = formatMoney(portfolioAmount),
                        fontSize = 56.sp,
                        lineHeight = 56.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "• USED",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
                            letterSpacing = 1.sp
                        )
                        Text(
                            "REMAINING •",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
                            letterSpacing = 1.sp
                        )
                    }

                    LinearProgressIndicator(
                        progress = usedProgress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.12f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formatMoney(usedAmount),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = formatMoney(remainingAmount),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.65f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "DAILY BURN",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
                            )
                        }
                        Text(
                            formatMoney(daySpend),
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "Limit: ${formatMoney(dayLimit)}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.72f)
                    )

                    LinearProgressIndicator(
                        progress = (dailyBurnPercentage / 100f).coerceIn(0f, 1f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.12f)
                    )

                    Text(
                        text = "${String.format(Locale.US, "%.0f", dailyBurnPercentage)}% used",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End
                    )
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "• INTELLIGENCE",
                        fontSize = 14.sp,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Subscription audit: You're paying for 3 unused streaming services. Cancellation could save you $42/mo.",
                        fontSize = 24.sp,
                        lineHeight = 32.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Button(
                        onClick = onOpenArchive,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text(
                            text = "REVIEW EXPENSES",
                            letterSpacing = 1.6.sp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Recent Ledger",
                        fontSize = 42.sp,
                        lineHeight = 42.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "REAL-TIME SETTLEMENT DATA",
                        fontSize = 11.sp,
                        letterSpacing = 1.2.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.58f)
                    )
                }
                Text(
                    text = "VIEW ARCHIVE",
                    fontSize = 12.sp,
                    letterSpacing = 1.5.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onOpenArchive() }
                )
            }
        }

        if (recentExpenses.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        text = "No entries yet. Tap + to add your first ledger record.",
                        modifier = Modifier.padding(20.dp),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            items(recentExpenses) { expense ->
                LedgerTransactionRow(
                    expense = expense,
                    categories = allCategories,
                    onClick = { navController.navigate(Screen.TransactionDetail.createRoute(expense.id)) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun HistoryTabContent(
    navController: NavController,
    expenseViewModel: ExpenseViewModel = hiltViewModel()
) {
    var searchText by rememberSaveable { mutableStateOf("") }

    val allExpenses by expenseViewModel.allExpenses.collectAsState()
    val allCategories by expenseViewModel.allCategories.collectAsState()

    val filtered = remember(searchText, allExpenses, allCategories) {
        val source = allExpenses.sortedByDescending { it.date }
        if (searchText.isBlank()) {
            source
        } else {
            source.filter { expense ->
                val categoryName = categoryNameFromId(expense.categoryId, allCategories)
                expense.title.contains(searchText, ignoreCase = true) ||
                    expense.notes.contains(searchText, ignoreCase = true) ||
                    categoryName.contains(searchText, ignoreCase = true)
            }
        }
    }

    val grouped = remember(filtered) {
        filtered.groupBy { it.date.toLocalDate() }
            .toList()
            .sortedByDescending { it.first }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("History", fontSize = 36.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "Your curated financial record",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.FilterList, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            }
        }

        item {
            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Search merchants or categories...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(14.dp)
            )
        }

        if (grouped.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        text = "No transactions found.",
                        modifier = Modifier.padding(20.dp),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            grouped.forEach { (date, expenses) ->
                item {
                    Text(
                        text = date.format(DateTimeFormatter.ofPattern("EEE, MMM d", Locale.US)).uppercase(Locale.US),
                        fontSize = 12.sp,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.58f),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                items(expenses) { expense ->
                    LedgerTransactionRow(
                        expense = expense,
                        categories = allCategories,
                        onClick = { navController.navigate(Screen.TransactionDetail.createRoute(expense.id)) }
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ReportTabContent(
    navController: NavController,
    expenseViewModel: ExpenseViewModel = hiltViewModel()
) {
    var selectedPeriod by rememberSaveable { mutableStateOf("All") }
    var selectedMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    val allExpenses by expenseViewModel.allExpenses.collectAsState()
    val allCategories by expenseViewModel.allCategories.collectAsState()
    val settings by expenseViewModel.settings.collectAsState()

    LaunchedEffect(selectedMonth) {
        if (selectedDate.year != selectedMonth.year || selectedDate.month != selectedMonth.month) {
            selectedDate = selectedMonth.atDay(1)
        }
    }

    val periodExpenses = remember(allExpenses, selectedPeriod) {
        val now = LocalDateTime.now()
        when (selectedPeriod) {
            "7 Days" -> allExpenses.filter { it.date >= now.minusDays(7) }
            "3 Months" -> allExpenses.filter { it.date >= now.minusMonths(3) }
            "6 Months" -> allExpenses.filter { it.date >= now.minusMonths(6) }
            else -> allExpenses
        }
    }

    val monthExpenses = remember(periodExpenses, selectedMonth) {
        periodExpenses.filter {
            it.date.year == selectedMonth.year && it.date.monthValue == selectedMonth.monthValue
        }
    }

    val totalOutflow = remember(monthExpenses) { monthExpenses.sumOf { abs(it.amount) } }
    val dateExpenses = remember(monthExpenses, selectedDate) {
        monthExpenses.filter { it.date.toLocalDate() == selectedDate }.sortedByDescending { it.date }
    }
    val activityExpenses = remember(dateExpenses, monthExpenses) {
        if (dateExpenses.isNotEmpty()) dateExpenses.take(3) else monthExpenses.sortedByDescending { it.date }.take(3)
    }

    val weeklyOutflow = remember(periodExpenses) {
        val today = LocalDate.now()
        (6 downTo 0).map { offset ->
            val day = today.minusDays(offset.toLong())
            periodExpenses
                .filter { it.date.toLocalDate() == day }
                .sumOf { abs(it.amount) }
                .toFloat()
        }
    }

    val trendData = remember(periodExpenses) {
        val today = LocalDate.now()
        (4 downTo 0).map { offset ->
            val weekEnd = today.minusWeeks(offset.toLong())
            val weekStart = weekEnd.minusDays(6)
            periodExpenses
                .filter {
                    val d = it.date.toLocalDate()
                    !d.isBefore(weekStart) && !d.isAfter(weekEnd)
                }
                .sumOf { abs(it.amount) }
                .toFloat()
        }
    }

    val categoryTotals = remember(monthExpenses, allCategories) {
        monthExpenses
            .groupBy { categoryNameFromId(it.categoryId, allCategories) }
            .mapValues { (_, values) -> values.sumOf { abs(it.amount) } }
            .toList()
            .sortedByDescending { it.second }
    }

    val spentValue = remember(monthExpenses) { monthExpenses.sumOf { abs(it.amount) } }
    val budgetValue = settings?.monthlyBudget ?: 5000.0
    val avgWeekly = if (weeklyOutflow.isNotEmpty()) weeklyOutflow.average() else 0.0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color(0xFFFF4B27), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Solo Ledger", fontWeight = FontWeight.Bold, fontSize = 26.sp)
            }
        }

        item {
            Text(
                "FINANCIAL PERIOD",
                fontSize = 11.sp,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.58f)
            )
            Text("Statement Period", fontSize = 36.sp, fontWeight = FontWeight.Bold)
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "7 Days", "3 Months", "6 Months").forEach { period ->
                    FilterChip(
                        selected = selectedPeriod == period,
                        onClick = { selectedPeriod = period },
                        label = { Text(period) },
                        shape = RoundedCornerShape(18.dp)
                    )
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "TOTAL OUTFLOW ${selectedMonth.month.getDisplayName(TextStyle.SHORT, Locale.US).uppercase(Locale.US)}",
                        fontSize = 11.sp,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                    Text(
                        text = formatMoney(totalOutflow),
                        fontSize = 52.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        item {
            CalendarGrid(
                selectedMonth = selectedMonth,
                selectedDate = selectedDate,
                expenses = monthExpenses,
                onDateClick = { date ->
                    selectedDate = date
                    navController.navigate(Screen.CalendarDetail.createRoute(date.toString()))
                }
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${selectedDate.month.getDisplayName(TextStyle.SHORT, Locale.US)} ${selectedDate.dayOfMonth} Activity",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "${activityExpenses.size} Transactions",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.72f)
                    )
                }
            }
        }

        if (activityExpenses.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        text = "No transactions in this period.",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            items(activityExpenses) { expense ->
                LedgerTransactionRow(
                    expense = expense,
                    categories = allCategories,
                    onClick = { navController.navigate(Screen.TransactionDetail.createRoute(expense.id)) }
                )
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    "INSIGHTS",
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.58f)
                )
                Text("Financial Analytics", fontSize = 34.sp, fontWeight = FontWeight.Bold)
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("WEEKLY OUTFLOW", fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Comparison", fontSize = 30.sp, fontWeight = FontWeight.SemiBold)
                        Text("Avg. ${formatMoney(avgWeekly)}/wk", fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f))
                    }
                    WeeklyComparisonChart(weeklyData = weeklyOutflow, modifier = Modifier.height(210.dp))
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("SPENDING PULSE", fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                    Text("Onyx Trends", fontSize = 32.sp, fontWeight = FontWeight.SemiBold)
                    SpendingTrendChart(trendData = trendData, modifier = Modifier.height(220.dp), label = "Forecast")
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val topCategory = categoryTotals.firstOrNull()?.first ?: "No Data"
                    Text(
                        text = "MAIN",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = topCategory,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    BudgetVsSpentDonut(
                        spent = spentValue.toFloat(),
                        budget = max(budgetValue, 1.0).toFloat(),
                        modifier = Modifier.height(220.dp)
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        val total = max(1.0, categoryTotals.sumOf { it.second })
                        val first = categoryTotals.getOrNull(0)
                        val second = categoryTotals.getOrNull(1)
                        Text(
                            text = first?.let { "${it.first.uppercase(Locale.US)} ${String.format(Locale.US, "%.0f", (it.second / total) * 100)}%" } ?: "NO DATA",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                        Text(
                            text = second?.let { "${it.first.uppercase(Locale.US)} ${String.format(Locale.US, "%.0f", (it.second / total) * 100)}%" } ?: "",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

@Composable
fun ProfileTabContent(
    navController: NavController,
    expenseViewModel: ExpenseViewModel = hiltViewModel()
) {
    val settings by expenseViewModel.settings.collectAsState()
    val remainingBudget by expenseViewModel.remainingBudget.collectAsState()

    val userName = settings?.userName?.ifBlank { "User" } ?: "User"
    val monthlyBudget = settings?.monthlyBudget ?: 5000.0
    val remaining = max(0.0, remainingBudget)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Solo Ledger",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 10.dp)
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f), CircleShape)
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            initialsFromName(userName),
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Text(
                        text = "PRIMARY ACCOUNT",
                        fontSize = 10.sp,
                        letterSpacing = 1.1.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.58f)
                    )
                    Text(userName, fontSize = 32.sp, fontWeight = FontWeight.Bold)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("MONTHLY BUDGET", fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                                Text(formatMoney(monthlyBudget), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("REMAINING", fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                                Text(formatMoney(remaining), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { navController.navigate(Screen.EditProfile.route) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Edit Profile")
                        }
                        OutlinedButton(
                            onClick = { navController.navigate(Screen.Settings.route) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Settings")
                        }
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.clickable { navController.navigate(Screen.Settings.route) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Workspace Preferences", fontWeight = FontWeight.SemiBold)
                        Text(
                            "Currency, themes, and quick add controls",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.62f)
                        )
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null)
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.clickable { navController.navigate(Screen.Trash.route) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Bin", fontWeight = FontWeight.SemiBold)
                    Icon(Icons.Default.Delete, contentDescription = null)
                }
            }
        }

        item {
            Text(
                text = "Built by an independent developer.",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
                fontSize = 12.sp
            )
        }
    }
}

private data class ThemeCardOption(
    val label: String,
    val preview: Color,
    val themeType: ThemeType?
)

@Composable
fun SettingsScreen(
    navController: NavController,
    expenseViewModel: ExpenseViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val currentTheme by settingsViewModel.currentTheme.collectAsState()
    val settings by expenseViewModel.settings.collectAsState()

    var trackBudgetCycles by rememberSaveable { mutableStateOf(true) }
    var monthlyBudgetInput by rememberSaveable { mutableStateOf("5000") }

    LaunchedEffect(settings?.monthlyBudget) {
        val current = settings?.monthlyBudget ?: 5000.0
        monthlyBudgetInput = String.format(Locale.US, "%.0f", current)
    }

    val currentThemeLabel = when (currentTheme) {
        ThemeType.MIDNIGHT -> "Dark 1"
        ThemeType.TWILIGHT -> "Dark 2"
        ThemeType.AURORA -> "Light 1"
        ThemeType.NOON -> "Light 2"
    }

    val themeOptions = listOf(
        ThemeCardOption("System", Color(0xFF3B3B3B), null),
        ThemeCardOption("Dark 1", Color(0xFF070A16), ThemeType.MIDNIGHT),
        ThemeCardOption("Dark 2", Color(0xFF13224F), ThemeType.TWILIGHT),
        ThemeCardOption("Light 1", Color(0xFFE8E8E8), ThemeType.AURORA),
        ThemeCardOption("Light 2", Color(0xFFF7F7F7), ThemeType.NOON)
    )

    val monthlyBudgetValue = settings?.monthlyBudget ?: monthlyBudgetInput.toDoubleOrNull() ?: 5000.0
    val weeklyTarget = monthlyBudgetValue / 4.0
    val dailyTarget = monthlyBudgetValue / 30.0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                Text(
                    "WORKSPACE PREFERENCES",
                    fontSize = 11.sp,
                    letterSpacing = 1.1.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.58f)
                )
                Text("Settings", fontSize = 40.sp, fontWeight = FontWeight.Bold)
            }
        }

        item {
            Text("Appearance", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(
                "SELECT THEME",
                fontSize = 11.sp,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.58f)
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                themeOptions.chunked(2).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        row.forEach { option ->
                            val selected = option.label == currentThemeLabel
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(102.dp)
                                    .clickable {
                                        val target = option.themeType
                                        if (target != null) {
                                            settingsViewModel.updateTheme(target)
                                        }
                                    },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = option.preview),
                                border = if (selected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    if (selected) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .size(20.dp)
                                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                                                .padding(1.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.Check,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onPrimary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                    Column(
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .padding(10.dp)
                                    ) {
                                        Text(
                                            option.label,
                                            color = if (option.label.startsWith("Light")) Color.Black else Color.White,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                        if (row.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.clickable { navController.navigate(Screen.Trash.route) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Bin", fontWeight = FontWeight.SemiBold)
                        Text(
                            "Manage deleted transactions",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.62f)
                        )
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null)
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Base Currency", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    OutlinedTextField(
                        value = "INR - Indian Rupee (₹)",
                        onValueChange = {},
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                        singleLine = true
                    )
                    Text(
                        "All your transactions will be converted to this currency for global reports.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.62f)
                    )
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Budget Customization", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text(
                        "Set your financial limits and track spending efficiency.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.62f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Track Budget Cycles", fontWeight = FontWeight.SemiBold)
                        Switch(checked = trackBudgetCycles, onCheckedChange = { trackBudgetCycles = it })
                    }

                    OutlinedTextField(
                        value = monthlyBudgetInput,
                        onValueChange = { monthlyBudgetInput = it },
                        prefix = { Text("₹") },
                        label = { Text("MONTHLY BUDGET LIMIT") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            monthlyBudgetInput.toDoubleOrNull()?.let { settingsViewModel.updateMonthlyBudget(it) }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Apply Budget")
                    }

                    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.55f))) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("MONTHLY TARGET", fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.58f))
                            Text("₹${String.format(Locale.US, "%,.0f", monthlyBudgetValue)}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text("WEEKLY TARGET", fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.58f))
                            Text("₹${String.format(Locale.US, "%,.0f", weeklyTarget)} / week", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                            Text("CALCULATED DAILY", fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.58f))
                            Text("₹${String.format(Locale.US, "%,.0f", dailyTarget)} / day", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Quick Add Customization", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text(
                        "Toggle which fields appear when creating a new record.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.62f)
                    )
                    SettingToggleRow(
                        label = "Title",
                        checked = settings?.showTitleField ?: true,
                        onCheckedChange = settingsViewModel::updateShowTitleField
                    )
                    Divider()
                    SettingToggleRow(
                        label = "Category",
                        checked = settings?.showCategoryField ?: true,
                        onCheckedChange = settingsViewModel::updateShowCategoryField
                    )
                    Divider()
                    SettingToggleRow(
                        label = "Notes",
                        checked = settings?.showNotesField ?: true,
                        onCheckedChange = settingsViewModel::updateShowNotesField
                    )
                    Divider()
                    SettingToggleRow(
                        label = "Date",
                        checked = settings?.showDateField ?: true,
                        onCheckedChange = settingsViewModel::updateShowDateField
                    )
                }
            }
        }

        item {
            Text("Data Management", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        item {
            SettingsActionCard(
                title = "Import JSON",
                subtitle = "Restore your data from a file"
            )
        }

        item {
            SettingsActionCard(
                title = "Export JSON",
                subtitle = "Backup your workspace data"
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {},
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Buy me a coffee")
                    }
                    Text(
                        "*Buy me a coffee to fuel future development.*",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.62f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        item {
            SettingsActionCard(
                title = "Request feature",
                subtitle = "Suggest the next big addition to your workspace"
            )
        }

        item {
            SettingsActionCard(
                title = "Report bug",
                subtitle = "Encountered an issue? Our team is on standby"
            )
        }

        item {
            Text("Developer Profile", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("The Architect", fontWeight = FontWeight.SemiBold)
                    Icon(Icons.Default.ChevronRight, contentDescription = null)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(10.dp)) }
    }
}

@Composable
private fun SettingToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontWeight = FontWeight.SemiBold)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SettingsActionCard(
    title: String,
    subtitle: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(title, fontWeight = FontWeight.SemiBold)
                Text(
                    subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.62f)
                )
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null)
        }
    }
}

@Composable
fun TrashScreen(
    navController: NavController,
    expenseViewModel: ExpenseViewModel = hiltViewModel()
) {
    val deletedExpenses by expenseViewModel.deletedExpenses.collectAsState()
    val allCategories by expenseViewModel.allCategories.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.clickable { navController.popBackStack() }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Trash", fontSize = 34.sp, fontWeight = FontWeight.Bold)
                }
                Text(
                    "CLEAR ALL",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { expenseViewModel.clearDeletedExpenses() }
                )
            }
        }

        item {
            Text("${deletedExpenses.size}", fontSize = 52.sp, fontWeight = FontWeight.Bold)
            Text(
                "Items pending permanent deletion",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
            )
        }

        if (deletedExpenses.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        "Trash is empty.",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            items(deletedExpenses) { expense ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            val categoryName = categoryNameFromId(expense.categoryId, allCategories)
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.14f), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    categoryIconMap[categoryName] ?: Icons.Default.MoreHoriz,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            Column(modifier = Modifier.padding(start = 10.dp)) {
                                Text(expense.title, maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.SemiBold)
                                Text(
                                    formatMoney(abs(expense.amount)),
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Restore",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.clickable { expenseViewModel.restoreExpense(expense.id) }
                            )
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.clickable { expenseViewModel.permanentlyDeleteExpense(expense.id) }
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

@Composable
fun EditProfileScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    expenseViewModel: ExpenseViewModel = hiltViewModel()
) {
    val settings by expenseViewModel.settings.collectAsState()

    var userName by rememberSaveable { mutableStateOf("") }
    var monthlyBudget by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(settings?.id) {
        userName = settings?.userName ?: ""
        monthlyBudget = String.format(Locale.US, "%.0f", settings?.monthlyBudget ?: 5000.0)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", modifier = Modifier.clickable { navController.popBackStack() })
                Text("Edit Profile", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(24.dp))
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Box(
                    modifier = Modifier
                        .size(112.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f), CircleShape)
                        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initialsFromName(userName.ifBlank { "User" }),
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        item {
            Text("Name", fontWeight = FontWeight.SemiBold)
            TextField(
                value = userName,
                onValueChange = { userName = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(14.dp)
            )
        }

        item {
            Text("Monthly Budget", fontWeight = FontWeight.SemiBold)
            TextField(
                value = monthlyBudget,
                onValueChange = { monthlyBudget = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                leadingIcon = { Text("₹", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)) },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(14.dp)
            )
        }

        item {
            Button(
                onClick = {
                    if (userName.isNotBlank()) {
                        settingsViewModel.updateUserName(userName.trim())
                    }
                    monthlyBudget.toDoubleOrNull()?.let { settingsViewModel.updateMonthlyBudget(it) }
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Save Changes")
            }
        }
    }
}

@Composable
fun TransactionDetailScreen(
    navController: NavController,
    expenseId: Long,
    expenseViewModel: ExpenseViewModel = hiltViewModel()
) {
    val allExpenses by expenseViewModel.allExpenses.collectAsState()
    val allCategories by expenseViewModel.allCategories.collectAsState()

    val expense = remember(allExpenses, expenseId) { allExpenses.find { it.id == expenseId } }
    if (expense == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Transaction not found")
        }
        return
    }

    val categoryName = categoryNameFromId(expense.categoryId, allCategories)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", modifier = Modifier.clickable { navController.popBackStack() })
                Text("Transaction Details", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.clickable {
                    navController.navigate(Screen.EditExpense.createRoute(expense.id))
                })
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(expense.title, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Text(
                        formatSignedMoney(expense.amount),
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (expense.amount < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                    Divider()
                    Text(
                        "CATEGORY: $categoryName",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.72f)
                    )
                    Text(
                        "DATE: ${expense.date.format(DateTimeFormatter.ofPattern("EEE, MMM d yyyy · hh:mm a", Locale.US))}",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.72f)
                    )
                    if (expense.notes.isNotBlank()) {
                        Divider()
                        Text("NOTES", fontWeight = FontWeight.SemiBold)
                        Text(expense.notes)
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { navController.navigate(Screen.EditExpense.createRoute(expense.id)) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text("Edit")
                }
                OutlinedButton(
                    onClick = {
                        expenseViewModel.deleteExpense(expense.id)
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun EditExpenseScreen(
    navController: NavController,
    expenseId: Long,
    expenseViewModel: ExpenseViewModel = hiltViewModel()
) {
    val allExpenses by expenseViewModel.allExpenses.collectAsState()
    val allCategories by expenseViewModel.allCategories.collectAsState()
    val expense = remember(allExpenses, expenseId) { allExpenses.find { it.id == expenseId } }

    if (expense == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Transaction not found")
        }
        return
    }

    var title by rememberSaveable(expenseId) { mutableStateOf(expense.title) }
    var amount by rememberSaveable(expenseId) { mutableStateOf(String.format(Locale.US, "%.2f", abs(expense.amount))) }
    var category by rememberSaveable(expenseId) { mutableStateOf(categoryNameFromId(expense.categoryId, allCategories)) }
    var notes by rememberSaveable(expenseId) { mutableStateOf(expense.notes) }

    val categoryNames = remember(allCategories) {
        if (allCategories.isNotEmpty()) {
            allCategories.map { it.name }
        } else {
            fallbackCategoryNames
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", modifier = Modifier.clickable { navController.popBackStack() })
                Text("Edit Expense", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(24.dp))
            }
        }

        item {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                prefix = { Text("$") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
        }

        item {
            Text("Category", fontWeight = FontWeight.SemiBold)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categoryNames.forEach { name ->
                    val selected = category.equals(name, ignoreCase = true)
                    FilterChip(
                        selected = selected,
                        onClick = { category = name },
                        label = { Text(name) }
                    )
                }
            }
        }

        item {
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp)
            )
        }

        item {
            Button(
                onClick = {
                    val parsedAmount = amount.toDoubleOrNull() ?: abs(expense.amount)
                    val signedAmount = if (expense.amount < 0) -abs(parsedAmount) else abs(parsedAmount)
                    expenseViewModel.updateExpense(
                        expense.copy(
                            title = title.ifBlank { category },
                            amount = signedAmount,
                            categoryId = categoryIdFromName(category, allCategories),
                            notes = notes,
                            updatedAt = LocalDateTime.now()
                        )
                    )
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Save Changes")
            }
        }
    }
}

@Composable
fun CalendarDetailScreen(
    navController: NavController,
    date: String,
    expenseViewModel: ExpenseViewModel = hiltViewModel()
) {
    val allExpenses by expenseViewModel.allExpenses.collectAsState()
    val allCategories by expenseViewModel.allCategories.collectAsState()

    val selectedDate = remember(date) {
        runCatching { LocalDate.parse(date) }.getOrElse { LocalDate.now() }
    }

    val dayExpenses = remember(allExpenses, selectedDate) {
        allExpenses.filter { it.date.toLocalDate() == selectedDate }.sortedByDescending { it.date }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", modifier = Modifier.clickable { navController.popBackStack() })
                Text(selectedDate.toString(), fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(24.dp))
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("TOTAL SPENDING", fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                    Text(
                        formatMoney(dayExpenses.sumOf { abs(it.amount) }),
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        item {
            Text("${dayExpenses.size} Transactions", fontWeight = FontWeight.SemiBold)
        }

        if (dayExpenses.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        "No transactions on this date.",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            items(dayExpenses) { expense ->
                LedgerTransactionRow(
                    expense = expense,
                    categories = allCategories,
                    onClick = { navController.navigate(Screen.TransactionDetail.createRoute(expense.id)) }
                )
            }
        }
    }
}

@Composable
fun QuickAddModal(
    onDismiss: () -> Unit,
    onConfirm: (ExpenseEntity) -> Unit,
    expenseViewModel: ExpenseViewModel = hiltViewModel()
) {
    val allCategories by expenseViewModel.allCategories.collectAsState()
    val settings by expenseViewModel.settings.collectAsState()

    val categoryNames = remember(allCategories) {
        if (allCategories.isNotEmpty()) {
            allCategories.map { it.name }
        } else {
            fallbackCategoryNames
        }
    }

    var entryTitle by rememberSaveable { mutableStateOf("") }
    var amount by rememberSaveable { mutableStateOf("0.00") }
    var selectedCategory by rememberSaveable { mutableStateOf(categoryNames.firstOrNull() ?: "Dining") }
    var notes by rememberSaveable { mutableStateOf("") }
    var selectedDateTime by remember { mutableStateOf(LocalDateTime.now()) }
    var showAllCategories by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(categoryNames) {
        if (selectedCategory !in categoryNames && categoryNames.isNotEmpty()) {
            selectedCategory = categoryNames.first()
        }
    }

    val visibleCategories = remember(categoryNames, showAllCategories) {
        if (showAllCategories) categoryNames else categoryNames.take(4)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            Box(
                                modifier = Modifier
                                    .height(4.dp)
                                    .width(64.dp)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            )
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("New Entry", fontSize = 52.sp, lineHeight = 52.sp, fontWeight = FontWeight.Bold)
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.8f), CircleShape)
                                    .clickable { onDismiss() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Close")
                            }
                        }
                    }

                    if (settings?.showTitleField != false) {
                        item {
                            TextField(
                                value = entryTitle,
                                onValueChange = { entryTitle = it },
                                placeholder = {
                                    Text(
                                        "Entry Title (Optional)",
                                        fontSize = 24.sp,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.45f)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent
                                )
                            )
                        }
                    }

                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "$",
                                fontSize = 52.sp,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                            )
                            TextField(
                                value = amount,
                                onValueChange = { amount = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 8.dp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                textStyle = MaterialTheme.typography.headlineLarge.copy(fontSize = 82.sp, fontWeight = FontWeight.Bold),
                                colors = TextFieldDefaults.colors(
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent
                                )
                            )
                        }
                    }

                    if (settings?.showDateField != false) {
                        item {
                            Card(
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.7f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.DateRange, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            formatQuickAddDate(selectedDateTime),
                                            fontSize = 18.sp,
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.84f)
                                        )
                                    }
                                    Icon(Icons.Default.CalendarMonth, contentDescription = null)
                                }
                            }
                        }
                    }

                    if (settings?.showCategoryField != false) {
                        item {
                            Text(
                                "SELECT CATEGORY",
                                fontSize = 12.sp,
                                letterSpacing = 1.1.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.58f)
                            )
                        }

                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                visibleCategories.chunked(2).forEach { row ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        row.forEach { category ->
                                            val selected = selectedCategory.equals(category, ignoreCase = true)
                                            Button(
                                                onClick = { selectedCategory = category },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(50.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = if (selected) {
                                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                                                    } else {
                                                        MaterialTheme.colorScheme.background.copy(alpha = 0.7f)
                                                    },
                                                    contentColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                                                ),
                                                shape = RoundedCornerShape(22.dp)
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                    Icon(
                                                        categoryIconMap[category] ?: Icons.Default.MoreHoriz,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Text(category, maxLines = 1)
                                                }
                                            }
                                        }
                                        if (row.size == 1) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }

                                if (categoryNames.size > 4) {
                                    OutlinedButton(
                                        onClick = { showAllCategories = !showAllCategories },
                                        shape = RoundedCornerShape(20.dp)
                                    ) {
                                        Text(if (showAllCategories) "Less" else "...")
                                    }
                                }
                            }
                        }
                    }

                    if (settings?.showNotesField == true) {
                        item {
                            OutlinedTextField(
                                value = notes,
                                onValueChange = { notes = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 100.dp),
                                placeholder = {
                                    Text(
                                        "What was this for?",
                                        fontSize = 18.sp,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.45f)
                                    )
                                },
                                trailingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                                shape = RoundedCornerShape(2.dp)
                            )
                        }
                    }

                    item {
                        Button(
                            onClick = {
                                val parsedAmount = amount.toDoubleOrNull() ?: 0.0
                                val created = ExpenseEntity(
                                    title = entryTitle.ifBlank { selectedCategory },
                                    amount = -abs(parsedAmount),
                                    categoryId = categoryIdFromName(selectedCategory, allCategories),
                                    date = selectedDateTime,
                                    notes = notes,
                                    isDeleted = false
                                )
                                onConfirm(created)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(58.dp),
                            shape = RoundedCornerShape(30.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text(
                                "Confirm Ledger Entry",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(8.dp)) }
                }
            }
        }
    }
}

@Composable
fun CalendarGrid(
    selectedMonth: YearMonth,
    selectedDate: LocalDate,
    expenses: List<ExpenseEntity>,
    onDateClick: (LocalDate) -> Unit
) {
    val startDayOffset = selectedMonth.atDay(1).dayOfWeek.value % 7
    val daysInMonth = selectedMonth.lengthOfMonth()

    val expenseDays = remember(expenses, selectedMonth) {
        expenses
            .filter { it.date.year == selectedMonth.year && it.date.monthValue == selectedMonth.monthValue }
            .map { it.date.toLocalDate() }
            .toSet()
    }

    Card(
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                    Text(
                        day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }

            val totalCells = 42
            for (row in 0 until 6) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    for (column in 0 until 7) {
                        val cellIndex = row * 7 + column
                        val dayNumber = cellIndex - startDayOffset + 1
                        if (dayNumber in 1..daysInMonth) {
                            val date = selectedMonth.atDay(dayNumber)
                            val hasExpense = date in expenseDays
                            val isSelected = date == selectedDate

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(2.dp)
                                    .background(
                                        when {
                                            isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                                            hasExpense -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                            else -> Color.Transparent
                                        },
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { onDateClick(date) },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        dayNumber.toString(),
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                    if (hasExpense) {
                                        Box(
                                            modifier = Modifier
                                                .padding(top = 1.dp)
                                                .size(4.dp)
                                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                                        )
                                    }
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
            if (totalCells == 0) {
                Spacer(modifier = Modifier.height(1.dp))
            }
        }
    }
}

@Composable
private fun LedgerTransactionRow(
    expense: ExpenseEntity,
    categories: List<CategoryEntity>,
    onClick: () -> Unit
) {
    val categoryName = categoryNameFromId(expense.categoryId, categories)
    val amountText = formatSignedMoney(expense.amount)
    val amountColor = if (expense.amount < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f)),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.7f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = categoryIconMap[categoryName] ?: Icons.Default.MoreHoriz,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.78f)
                    )
                }
                Column(modifier = Modifier.padding(start = 10.dp)) {
                    Text(
                        text = expense.title,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "$categoryName · ${expense.date.format(dateTimeDisplayFormatter)}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.58f)
                    )
                }
            }

            Text(
                text = amountText,
                color = amountColor,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp
            )
        }
    }
}
