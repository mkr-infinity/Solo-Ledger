package com.solo.ledger.ui.screens.calendar

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.solo.ledger.data.model.Category
import com.solo.ledger.data.model.Transaction
import com.solo.ledger.data.model.TransactionType
import com.solo.ledger.ui.components.shared.ThemedCard
import com.solo.ledger.ui.components.shared.SectionHeader
import com.solo.ledger.util.CurrencyFormatter
import com.solo.ledger.util.DateUtils
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    navController: NavController,
    onQuickAddClick: () -> Unit = {},
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var previousMonth by remember { mutableStateOf(uiState.currentMonth) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calendar", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onQuickAddClick,
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add transaction", tint = Color.White)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 12.dp)
        ) {
            // Month navigation header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = {
                    previousMonth = uiState.currentMonth
                    viewModel.prevMonth()
                }) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Previous month")
                }

                AnimatedContent(
                    targetState = uiState.currentMonth,
                    transitionSpec = {
                        val slideIn = if (targetState > initialState) {
                            slideInHorizontally { it }
                        } else {
                            slideInHorizontally { -it }
                        }
                        val slideOut = if (targetState > initialState) {
                            slideOutHorizontally { -it }
                        } else {
                            slideOutHorizontally { it }
                        }
                        (slideIn + fadeIn()) togetherWith (slideOut + fadeOut())
                    },
                    label = "monthAnimation"
                ) { month ->
                    Text(
                        text = "${month.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${month.year}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                IconButton(onClick = {
                    previousMonth = uiState.currentMonth
                    viewModel.nextMonth()
                }) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Next month")
                }
            }

            // Day of week header
            DayOfWeekHeader()

            Spacer(modifier = Modifier.height(4.dp))

            // Calendar grid
            CalendarGrid(
                yearMonth = uiState.currentMonth,
                selectedDate = uiState.selectedDate,
                dailyExpense = uiState.dailyExpense,
                maxDailyExpense = uiState.maxDailyExpense,
                transactionsByDate = uiState.transactionsByDate,
                onDayClick = { date -> viewModel.selectDate(if (uiState.selectedDate == date) null else date) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Selected day transactions
            AnimatedVisibility(
                visible = uiState.selectedDate != null,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    uiState.selectedDate?.let { date ->
                        val totalExpenseForDay = uiState.dailyExpense[date] ?: 0.0
                        SectionHeader(
                            title = DateUtils.formatDateFull(date),
                            subtitle = if (totalExpenseForDay > 0) {
                                "Spent: ${CurrencyFormatter.format(totalExpenseForDay, "₹")}"
                            } else null
                        )
                        if (uiState.selectedDateTransactions.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No transactions on this day",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        } else {
                            LazyColumn(modifier = Modifier.height(260.dp)) {
                                items(uiState.selectedDateTransactions) { (transaction, category) ->
                                    CalendarTransactionCard(
                                        transaction = transaction,
                                        category = category
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayOfWeekHeader() {
    val days = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    Row(modifier = Modifier.fillMaxWidth()) {
        days.forEach { day ->
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CalendarGrid(
    yearMonth: YearMonth,
    selectedDate: LocalDate?,
    dailyExpense: Map<LocalDate, Double>,
    maxDailyExpense: Double,
    transactionsByDate: Map<LocalDate, List<Pair<Transaction, Category?>>>,
    onDayClick: (LocalDate) -> Unit
) {
    val firstDayOfMonth = yearMonth.atDay(1)
    val totalDaysInMonth = yearMonth.lengthOfMonth()
    // Sunday=0, Monday=1, ... Saturday=6
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7

    val totalCells = firstDayOfWeek + totalDaysInMonth
    val weeks = (totalCells + 6) / 7
    val today = LocalDate.now()

    Column(modifier = Modifier.fillMaxWidth()) {
        repeat(weeks) { weekIndex ->
            Row(modifier = Modifier.fillMaxWidth()) {
                repeat(7) { dayOfWeek ->
                    val cellIndex = weekIndex * 7 + dayOfWeek
                    val dayNumber = cellIndex - firstDayOfWeek + 1

                    if (dayNumber < 1 || dayNumber > totalDaysInMonth) {
                        Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                    } else {
                        val date = yearMonth.atDay(dayNumber)
                        val isToday = date == today
                        val isSelected = date == selectedDate
                        val expense = dailyExpense[date] ?: 0.0
                        val hasTransactions = transactionsByDate.containsKey(date)

                        DayCell(
                            dayNumber = dayNumber,
                            isToday = isToday,
                            isSelected = isSelected,
                            expense = expense,
                            maxExpense = maxDailyExpense,
                            hasTransactions = hasTransactions,
                            onClick = { onDayClick(date) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    dayNumber: Int,
    isToday: Boolean,
    isSelected: Boolean,
    expense: Double,
    maxExpense: Double,
    hasTransactions: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val errorColor = MaterialTheme.colorScheme.error

    val bgColor = when {
        isSelected -> primaryColor
        expense <= 0.0 || maxExpense <= 0.0 -> Color.Transparent
        expense > maxExpense * 0.9 -> errorColor.copy(alpha = 0.75f)
        expense > maxExpense * 0.7 -> primaryColor.copy(alpha = 0.8f)
        expense > maxExpense * 0.3 -> primaryColor.copy(alpha = 0.5f)
        else -> primaryColor.copy(alpha = 0.2f)
    }

    val textColor = when {
        isSelected -> Color.White
        expense > maxExpense * 0.9 && !isSelected -> Color.White
        expense > maxExpense * 0.7 && !isSelected -> Color.White
        else -> MaterialTheme.colorScheme.onBackground
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(RoundedCornerShape(8.dp))
            .then(
                if (isToday && !isSelected) {
                    Modifier.border(
                        width = 2.dp,
                        color = primaryColor,
                        shape = RoundedCornerShape(8.dp)
                    )
                } else Modifier
            )
            .background(bgColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = dayNumber.toString(),
                color = textColor,
                fontSize = 13.sp,
                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal
            )
            if (hasTransactions) {
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) Color.White
                            else primaryColor.copy(alpha = 0.7f)
                        )
                )
            }
        }
    }
}

@Composable
private fun CalendarTransactionCard(
    transaction: Transaction,
    category: Category?
) {
    ThemedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category color dot
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        category?.colorHex?.let { Color(android.graphics.Color.parseColor(it)) }
                            ?: MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category?.iconName?.take(1) ?: "?",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.title ?: category?.name ?: "Transaction",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (!transaction.notes.isNullOrBlank()) {
                    Text(
                        text = transaction.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                } else {
                    Text(
                        text = DateUtils.formatTime(transaction.time),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = "${if (transaction.type == TransactionType.EXPENSE) "-" else "+"}${CurrencyFormatter.format(transaction.amount, "₹")}",
                color = if (transaction.type == TransactionType.EXPENSE)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }
}
