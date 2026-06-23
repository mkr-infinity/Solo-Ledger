package com.solo.ledger.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.solo.ledger.core.Money
import com.solo.ledger.ui.AppViewModel
import com.solo.ledger.ui.components.*
import com.solo.ledger.ui.theme.LedgerTheme

@Composable
fun HomeScreen(nav: NavController, appVm: AppViewModel, vm: HomeViewModel = viewModel()) {
    val c = LedgerTheme.colors
    val state by vm.state.collectAsState()
    val settings by appVm.settings.collectAsState()
    val o = state.overview
    val pct = if (o.monthlyBudget > 0) (o.used / o.monthlyBudget).coerceIn(0.0, 1.0) else 0.0
    val hidden = settings.hiddenWidgets

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 12.dp, 16.dp, 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(Modifier.clickable { nav.navigate(com.solo.ledger.ui.navigation.Routes.PROFILE) }) {
                Text("Hello, ${settings.userName.ifBlank { "there" }}",
                    style = MaterialTheme.typography.headlineSmall, color = c.textPrimary, fontWeight = FontWeight.SemiBold)
                Text("Here is your month at a glance", style = MaterialTheme.typography.bodyMedium, color = c.textSecondary)
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickLink(Modifier.weight(1f), "Analytics") { nav.navigate(com.solo.ledger.ui.navigation.Routes.ANALYTICS) }
                QuickLink(Modifier.weight(1f), "Goals") { nav.navigate(com.solo.ledger.ui.navigation.Routes.GOALS) }
            }
        }
        item {
            LedgerCard(Modifier.fillMaxWidth()) {
                Text("Remaining this month", style = MaterialTheme.typography.labelLarge, color = c.muted)
                Spacer(Modifier.height(4.dp))
                Text(Money.format(o.remaining, settings.currency),
                    style = MaterialTheme.typography.displaySmall, color = c.textPrimary)
                Spacer(Modifier.height(16.dp))
                LinearProgressIndicator(
                    progress = { pct.toFloat() },
                    modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(6.dp)),
                    color = c.primary, trackColor = c.outline
                )
                Spacer(Modifier.height(14.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    StatChip("Budget", Money.format(o.monthlyBudget, settings.currency))
                    StatChip("Used", Money.format(o.used, settings.currency), accent = true)
                    StatChip("Daily avg", Money.format(o.dailyAverage, settings.currency))
                }
            }
        }
        if ("insights" !in hidden) item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                InsightTile(Modifier.weight(1f), Icons.Rounded.Savings, "Savings goal", "Set a goal")
                InsightTile(Modifier.weight(1f), Icons.Rounded.BarChart,
                    "Spent today", Money.format(state.monthlySeries.lastOrNull()?.toDouble() ?: 0.0, settings.currency))
            }
        }
        if ("graph" !in hidden) item {
            LedgerCard(Modifier.fillMaxWidth()) {
                SectionHeader("Monthly spending")
                Spacer(Modifier.height(12.dp))
                if (state.monthlySeries.any { it > 0f }) BarChart(state.monthlySeries.chunkedAvg(12))
                else EmptyState(Icons.Outlined.ReceiptLong, "No spending yet", "Your daily spending graph appears here.")
            }
        }
        if ("categories" !in hidden) item {
            LedgerCard(Modifier.fillMaxWidth()) {
                SectionHeader("Category breakdown")
                Spacer(Modifier.height(12.dp))
                if (state.categories.isEmpty()) {
                    EmptyState(Icons.Outlined.ReceiptLong, "No categories yet", "Add an expense to see the breakdown.")
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        DonutChart(
                            slices = state.categories.mapIndexed { i, cat ->
                                Slice(cat.total.toFloat(), c.chart[i % c.chart.size], cat.category)
                            },
                            modifier = Modifier.size(120.dp)
                        )
                        Spacer(Modifier.width(20.dp))
                        Column(Modifier.weight(1f)) {
                            state.categories.take(5).forEachIndexed { i, cat ->
                                Row(Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(Modifier.size(10.dp).clip(RoundedCornerShape(3.dp))
                                            .androidxBg(c.chart[i % c.chart.size]))
                                        Spacer(Modifier.width(8.dp))
                                        Text(cat.category, style = MaterialTheme.typography.bodyMedium, color = c.textSecondary)
                                    }
                                    Text(Money.format(cat.total, settings.currency),
                                        style = MaterialTheme.typography.bodyMedium, color = c.textPrimary)
                                }
                            }
                        }
                    }
                }
            }
        }
        if ("recent" !in hidden) item { SectionHeader("Recent transactions") }
        if ("recent" !in hidden && state.recent.isEmpty()) {
            item { EmptyState(Icons.Outlined.ReceiptLong, "No transactions", "Tap the + button to add your first expense.") }
        } else if ("recent" !in hidden) {
            items(state.recent, key = { it.id }) { e -> TransactionRow(e, settings.currency) { nav.navigate("edit/${e.id}") } }
        }
    }
}

@Composable
private fun InsightTile(modifier: Modifier, icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    val c = LedgerTheme.colors
    LedgerCard(modifier) {
        Icon(icon, null, tint = c.primary)
        Spacer(Modifier.height(10.dp))
        Text(label, style = MaterialTheme.typography.labelMedium, color = c.muted)
        Text(value, style = MaterialTheme.typography.titleMedium, color = c.textPrimary, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun QuickLink(modifier: Modifier, label: String, onClick: () -> Unit) {
    val c = LedgerTheme.colors
    Surface(color = c.primary.copy(alpha = 0.10f), shape = MaterialTheme.shapes.medium,
        modifier = modifier.clickable { onClick() }) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center) {
            Text(label, style = MaterialTheme.typography.titleMedium, color = c.primary, fontWeight = FontWeight.SemiBold)
        }
    }
}

private fun List<Float>.chunkedAvg(target: Int): List<Float> {
    if (isEmpty()) return this
    val size = (this.size / target).coerceAtLeast(1)
    return this.chunked(size).map { it.sum() }
}
