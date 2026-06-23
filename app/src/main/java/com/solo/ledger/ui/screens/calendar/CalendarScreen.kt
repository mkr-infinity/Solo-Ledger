package com.solo.ledger.ui.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EventBusy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.solo.ledger.core.Money
import com.solo.ledger.core.ServiceLocator
import com.solo.ledger.data.local.entity.ExpenseEntity
import com.solo.ledger.ui.components.DonutChart
import com.solo.ledger.ui.components.EmptyState
import com.solo.ledger.ui.components.LedgerCard
import com.solo.ledger.ui.components.Slice
import com.solo.ledger.ui.theme.LedgerTheme
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.YearMonth

class CalendarViewModel : ViewModel() {
    private val repo = ServiceLocator.ledgerRepository
    private val month = YearMonth.now()
    val byDay: StateFlow<Map<Int, List<ExpenseEntity>>> =
        repo.expensesInRange(month.atDay(1).toEpochDay(), month.atEndOfMonth().toEpochDay())
            .map { list -> list.groupBy { LocalDate.ofEpochDay(it.dateEpochDay).dayOfMonth } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())
}

@Composable
fun CalendarScreen(nav: NavController, vm: CalendarViewModel = viewModel()) {
    val c = LedgerTheme.colors
    val byDay by vm.byDay.collectAsState()
    val month = YearMonth.now()
    val firstDow = month.atDay(1).dayOfWeek.value % 7
    val days = month.lengthOfMonth()
    val maxSpend = (byDay.values.maxOfOrNull { l -> l.sumOf { it.amount } } ?: 1.0).coerceAtLeast(1.0)
    var selectedDay by remember { mutableStateOf(LocalDate.now().dayOfMonth.coerceAtMost(days)) }

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Text(month.month.name.lowercase().replaceFirstChar { it.uppercase() } + " ${month.year}",
                style = MaterialTheme.typography.headlineSmall, color = c.textPrimary, fontWeight = FontWeight.SemiBold)
        }
        item {
            Column {
                Row(Modifier.fillMaxWidth()) {
                    listOf("S","M","T","W","T","F","S").forEach {
                        Text(it, Modifier.weight(1f), color = c.muted, textAlign = TextAlign.Center, style = MaterialTheme.typography.labelMedium)
                    }
                }
                Spacer(Modifier.height(8.dp))
                val rows = (firstDow + days + 6) / 7
                var day = 1
                for (r in 0 until rows) {
                    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        for (col in 0 until 7) {
                            val idx = r * 7 + col
                            if (idx < firstDow || day > days) {
                                Box(Modifier.weight(1f).aspectRatio(1f))
                            } else {
                                val d = day
                                val spend = byDay[d]?.sumOf { it.amount } ?: 0.0
                                val intensity = (spend / maxSpend).toFloat()
                                val isSel = d == selectedDay
                                Box(Modifier.weight(1f).aspectRatio(1f).padding(3.dp).clip(CircleShape)
                                    .background(if (spend > 0) c.primary.copy(alpha = 0.18f + 0.5f * intensity) else Color.Transparent)
                                    .border(if (isSel) 2.dp else 0.dp, c.primary, CircleShape)
                                    .clickable { selectedDay = d },
                                    contentAlignment = Alignment.Center) {
                                    Text("$d", color = if (spend > 0 || isSel) c.textPrimary else c.textSecondary, style = MaterialTheme.typography.bodyMedium)
                                }
                                day++
                            }
                        }
                    }
                }
            }
        }
        // Date detail
        val dayItems = byDay[selectedDay].orEmpty()
        item {
            LedgerCard(Modifier.fillMaxWidth()) {
                Text(month.atDay(selectedDay).format(java.time.format.DateTimeFormatter.ofPattern("EEEE, dd MMM")),
                    style = MaterialTheme.typography.titleMedium, color = c.textPrimary)
                Spacer(Modifier.height(4.dp))
                Text("Spent ${Money.format(dayItems.sumOf { it.amount })}", style = MaterialTheme.typography.headlineMedium, color = c.primary)
                if (dayItems.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    val cats = dayItems.groupBy { it.category }.map { it.key to it.value.sumOf { e -> e.amount } }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        DonutChart(cats.mapIndexed { i, p -> Slice(p.second.toFloat(), c.chart[i % c.chart.size], p.first) }, Modifier.size(96.dp))
                        Spacer(Modifier.width(16.dp))
                        Column(Modifier.weight(1f)) {
                            cats.forEach { (cat, amt) ->
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(cat, style = MaterialTheme.typography.bodyMedium, color = c.textSecondary)
                                    Text(Money.format(amt), style = MaterialTheme.typography.bodyMedium, color = c.textPrimary)
                                }
                            }
                        }
                    }
                }
            }
        }
        if (dayItems.isEmpty()) {
            item { EmptyState(Icons.Outlined.EventBusy, "No spending", "Nothing recorded on this day.") }
        } else {
            items(dayItems, key = { it.id }) { e ->
                Surface(color = c.card, shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth().clickable { nav.navigate("edit/${e.id}") }) {
                    Row(Modifier.padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column { Text(e.title, color = c.textPrimary, style = MaterialTheme.typography.titleMedium); Text(e.category, color = c.textSecondary, style = MaterialTheme.typography.bodyMedium) }
                        Text("- ${Money.format(e.amount)}", color = c.textPrimary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
