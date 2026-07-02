@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

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
import androidx.compose.ui.unit.sp
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
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "7 Days", "3 Months", "6 Months")

    val calendar = remember(currentMonth) {
        Calendar.getInstance().apply {
            timeInMillis = currentMonth.timeInMillis
            set(Calendar.DAY_OF_MONTH, 1)
        }
    }

    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1

    // Filtered expenses based on period
    val filteredExpenses = remember(allExpenses, selectedFilter) {
        val now = System.currentTimeMillis()
        when (selectedFilter) {
            "7 Days" -> allExpenses.filter { now - it.date <= 7L * 24 * 60 * 60 * 1000 }
            "3 Months" -> allExpenses.filter { now - it.date <= 90L * 24 * 60 * 60 * 1000 }
            "6 Months" -> allExpenses.filter { now - it.date <= 180L * 24 * 60 * 60 * 1000 }
            else -> allExpenses
        }
    }

    // Total outflow for period
    val totalOutflow = filteredExpenses.sumOf { it.amount }

    // Month expenses for calendar
    val monthExpenses = remember(allExpenses, currentMonth) {
        val cal = Calendar.getInstance()
        allExpenses.filter { expense ->
            cal.timeInMillis = expense.date
            cal.get(Calendar.YEAR) == currentMonth.get(Calendar.YEAR) &&
                    cal.get(Calendar.MONTH) == currentMonth.get(Calendar.MONTH)
        }
    }

    val totalMonthOutflow = monthExpenses.sumOf { it.amount }

    // Daily spending map
    val dailySpendingMap = remember(allExpenses, currentMonth) {
        val cal = Calendar.getInstance()
        allExpenses.groupBy { expense ->
            cal.timeInMillis = expense.date
            if (cal.get(Calendar.YEAR) == currentMonth.get(Calendar.YEAR) &&
                cal.get(Calendar.MONTH) == currentMonth.get(Calendar.MONTH)
            ) cal.get(Calendar.DAY_OF_MONTH) else -1
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

    // Weekly outflow data (last 5 weeks)
    val weeklyData = remember(allExpenses) {
        val now = System.currentTimeMillis()
        (1..5).map { week ->
            val weekEnd = now - (week - 1) * 7L * 24 * 60 * 60 * 1000
            val weekStart = weekEnd - 7L * 24 * 60 * 60 * 1000
            allExpenses.filter { it.date in weekStart..weekEnd }.sumOf { it.amount }
        }.reversed()
    }
    val avgWeekly = if (weeklyData.isNotEmpty()) weeklyData.average() else 0.0

    // Category breakdown for period
    val categoryBreakdown = remember(filteredExpenses, categories) {
        filteredExpenses.groupBy { it.categoryId }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
            .entries.sortedByDescending { it.value }
            .take(3)
            .map { entry ->
                val cat = categories.find { it.id == entry.key }
                Triple(cat?.name ?: "Other", entry.value, cat?.color ?: 0xFF90A4AE)
            }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "FINANCIAL PERIOD",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Statement Period",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // Filter chips
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                filters.forEach { filter ->
                    val isSelected = selectedFilter == filter
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter, fontSize = 13.sp) },
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }
        }

        // Total outflow card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "TOTAL OUTFLOW ${SimpleDateFormat("MMM", Locale.getDefault()).format(currentMonth.time).uppercase()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "$currencySymbol${formatAmount(totalMonthOutflow)}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 32.sp
                    )
                }
            }
        }

        // Calendar
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Month nav
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {
                            currentMonth = Calendar.getInstance().apply { timeInMillis = currentMonth.timeInMillis; add(Calendar.MONTH, -1) }
                        }) { Icon(Icons.Filled.ChevronLeft, contentDescription = "Previous") }
                        Text(SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(currentMonth.time), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        IconButton(onClick = {
                            currentMonth = Calendar.getInstance().apply { timeInMillis = currentMonth.timeInMillis; add(Calendar.MONTH, 1) }
                        }) { Icon(Icons.Filled.ChevronRight, contentDescription = "Next") }
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    // Day headers
                    Row(modifier = Modifier.fillMaxWidth()) {
                        listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                            Text(day, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    // Grid
                    var dayCounter = 1
                    val totalCells = firstDayOfWeek + daysInMonth
                    val rows = (totalCells + 6) / 7
                    val today = Calendar.getInstance()

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
                                        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                                    }
                                    val isSelected = selectedDate != null && dayCal.timeInMillis == selectedDate
                                    val isToday = today.get(Calendar.YEAR) == dayCal.get(Calendar.YEAR) &&
                                            today.get(Calendar.MONTH) == dayCal.get(Calendar.MONTH) &&
                                            today.get(Calendar.DAY_OF_MONTH) == day

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f)
                                            .padding(2.dp)
                                            .clip(CircleShape)
                                            .then(
                                                if (isSelected) Modifier.background(MaterialTheme.colorScheme.primary)
                                                else if (isToday) Modifier.background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                                                else Modifier
                                            )
                                            .clickable { selectedDate = dayCal.timeInMillis },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = day.toString(),
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                                else if (isToday) MaterialTheme.colorScheme.primary
                                                else MaterialTheme.colorScheme.onSurface
                                            )
                                            if (hasSpending) {
                                                Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(
                                                    if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                                                    else MaterialTheme.colorScheme.tertiary
                                                ))
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
        }

        // Selected date activity
        if (selectedDate != null) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(selectedDate!!))} Activity",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                        Text(
                            text = "${selectedDateExpenses.size} Transaction${if (selectedDateExpenses.size != 1) "s" else ""}",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (selectedDateExpenses.isEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), shape = RoundedCornerShape(12.dp)) {
                        Text("No expenses on this day", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(16.dp))
                    }
                }
            } else {
                items(selectedDateExpenses, key = { it.id }) { expense ->
                    val category = categories.find { it.id == expense.categoryId }
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(14.dp)) {
                        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Color(category?.color ?: 0xFF90A4AE).copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                                    Icon(getCategoryIcon(category?.icon), contentDescription = null, tint = Color(category?.color ?: 0xFF90A4AE), modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(expense.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                    Text("${expense.time} \u2022 ${category?.name ?: "Other"}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Text("-$currencySymbol${formatAmount(expense.amount)}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }

        // Insights section
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text("INSIGHTS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 1.5.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Financial Analytics", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        // Weekly outflow comparison
        item {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("WEEKLY OUTFLOW", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 1.sp)
                            Text("Comparison", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                        Text("Avg. $currencySymbol${formatAmount(avgWeekly)}/wk", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                    // Bar chart
                    val maxWeekly = weeklyData.maxOrNull() ?: 1.0
                    Row(modifier = Modifier.fillMaxWidth().height(80.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.Bottom) {
                        weeklyData.forEachIndexed { index, amount ->
                            val height = if (maxWeekly > 0) (amount / maxWeekly * 60).toFloat() else 0f
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .width(24.dp)
                                        .height(height.coerceAtLeast(4f).dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            if (index == 2) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                        )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("W${index + 1}", style = MaterialTheme.typography.labelSmall, fontWeight = if (index == 2) FontWeight.Bold else FontWeight.Normal, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        // Top category donut-style
        if (categoryBreakdown.isNotEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text("TOP CATEGORY", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        val topCat = categoryBreakdown.first()
                        Text(topCat.first, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                        // Category breakdown bars
                        categoryBreakdown.forEach { (name, amount, color) ->
                            val percent = if (totalOutflow > 0) (amount / totalOutflow * 100).toInt() else 0
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(color)))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(name, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                                Text("$percent%", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        // Download statement button
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = {
                    val monthName = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(currentMonth.time)
                    PdfExporter.exportTransactionsAsPdf(context, monthExpenses, categories, currencySymbol, "Solo Ledger Statement - $monthName")
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Filled.Download, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("Download Statement", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
