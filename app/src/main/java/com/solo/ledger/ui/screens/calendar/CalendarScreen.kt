package com.solo.ledger.ui.screens.calendar

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.solo.ledger.data.model.Expense
import com.solo.ledger.data.repository.PdfExporter
import com.solo.ledger.ui.screens.home.formatAmount
import com.solo.ledger.ui.screens.home.getCategoryIcon
import com.solo.ledger.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CalendarScreen(viewModel: MainViewModel) {
    val allExpenses by viewModel.allExpenses.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val currencySymbol by viewModel.currencySymbol.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var currentMonth by remember { mutableStateOf(Calendar.getInstance()) }
    var selectedDate by remember { mutableStateOf<Long?>(null) }

    val calendar = remember(currentMonth) {
        Calendar.getInstance().apply {
            timeInMillis = currentMonth.timeInMillis
            set(Calendar.DAY_OF_MONTH, 1)
        }
    }

    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1

    // Map of day -> total spending
    val dailySpendingMap = remember(allExpenses, currentMonth) {
        val cal = Calendar.getInstance()
        allExpenses.groupBy { expense ->
            cal.timeInMillis = expense.date
            if (cal.get(Calendar.YEAR) == currentMonth.get(Calendar.YEAR) &&
                cal.get(Calendar.MONTH) == currentMonth.get(Calendar.MONTH)
            ) {
                cal.get(Calendar.DAY_OF_MONTH)
            } else -1
        }.filterKeys { it > 0 }.mapValues { entry -> entry.value.sumOf { it.amount } }
    }

    // Expenses for selected date
    val selectedDateExpenses = remember(selectedDate, allExpenses) {
        if (selectedDate == null) emptyList()
        else {
            val cal = Calendar.getInstance()
            allExpenses.filter { expense ->
                cal.timeInMillis = expense.date
                val selectedCal = Calendar.getInstance().apply { timeInMillis = selectedDate!! }
                cal.get(Calendar.YEAR) == selectedCal.get(Calendar.YEAR) &&
                        cal.get(Calendar.MONTH) == selectedCal.get(Calendar.MONTH) &&
                        cal.get(Calendar.DAY_OF_MONTH) == selectedCal.get(Calendar.DAY_OF_MONTH)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Header with export
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Calendar",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            IconButton(onClick = {
                // Export current month as PDF statement
                val monthExpenses = allExpenses.filter { expense ->
                    val cal = Calendar.getInstance()
                    cal.timeInMillis = expense.date
                    cal.get(Calendar.YEAR) == currentMonth.get(Calendar.YEAR) &&
                            cal.get(Calendar.MONTH) == currentMonth.get(Calendar.MONTH)
                }
                val monthName = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(currentMonth.time)
                PdfExporter.exportTransactionsAsPdf(
                    context, monthExpenses, categories, currencySymbol,
                    "Solo Ledger Statement - $monthName"
                )
            }) {
                Icon(Icons.Filled.PictureAsPdf, contentDescription = "Export PDF")
            }
        }

        // Month navigation
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        currentMonth = Calendar.getInstance().apply {
                            timeInMillis = currentMonth.timeInMillis
                            add(Calendar.MONTH, -1)
                        }
                    }) {
                        Icon(Icons.Filled.ChevronLeft, contentDescription = "Previous Month")
                    }

                    Text(
                        text = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(currentMonth.time),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    IconButton(onClick = {
                        currentMonth = Calendar.getInstance().apply {
                            timeInMillis = currentMonth.timeInMillis
                            add(Calendar.MONTH, 1)
                        }
                    }) {
                        Icon(Icons.Filled.ChevronRight, contentDescription = "Next Month")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Day headers
                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                        Text(
                            text = day,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Calendar grid
                var dayCounter = 1
                val totalCells = firstDayOfWeek + daysInMonth
                val rows = (totalCells + 6) / 7

                repeat(rows) { row ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        repeat(7) { col ->
                            val cellIndex = row * 7 + col
                            if (cellIndex >= firstDayOfWeek && dayCounter <= daysInMonth) {
                                val day = dayCounter
                                val spending = dailySpendingMap[day] ?: 0.0
                                val hasSpending = spending > 0

                                val dayCal = Calendar.getInstance().apply {
                                    timeInMillis = currentMonth.timeInMillis
                                    set(Calendar.DAY_OF_MONTH, day)
                                    set(Calendar.HOUR_OF_DAY, 0)
                                    set(Calendar.MINUTE, 0)
                                    set(Calendar.SECOND, 0)
                                    set(Calendar.MILLISECOND, 0)
                                }
                                val isSelected = selectedDate != null &&
                                        dayCal.timeInMillis == selectedDate

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .padding(2.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .then(
                                            if (isSelected) Modifier.background(
                                                MaterialTheme.colorScheme.primaryContainer
                                            ) else Modifier
                                        )
                                        .clickable {
                                            selectedDate = dayCal.timeInMillis
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = day.toString(),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (isSelected)
                                                MaterialTheme.colorScheme.onPrimaryContainer
                                            else
                                                MaterialTheme.colorScheme.onSurface
                                        )
                                        if (hasSpending) {
                                            Box(
                                                modifier = Modifier
                                                    .size(4.dp)
                                                    .clip(CircleShape)
                                                    .background(MaterialTheme.colorScheme.tertiary)
                                            )
                                        }
                                    }
                                }
                                dayCounter++
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selected date details
        if (selectedDate != null) {
            val totalForDay = selectedDateExpenses.sumOf { it.amount }
            Text(
                text = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(Date(selectedDate!!)),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (totalForDay > 0) {
                Text(
                    text = "Total: $currencySymbol${formatAmount(totalForDay)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (selectedDateExpenses.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "No expenses on this day",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 90.dp)
                ) {
                    items(selectedDateExpenses, key = { it.id }) { expense ->
                        val category = categories.find { it.id == expense.categoryId }
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(category?.color ?: 0xFF90A4AE).copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = getCategoryIcon(category?.icon),
                                            contentDescription = null,
                                            tint = Color(category?.color ?: 0xFF90A4AE),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = expense.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = expense.time,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Text(
                                    text = "$currencySymbol${formatAmount(expense.amount)}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
