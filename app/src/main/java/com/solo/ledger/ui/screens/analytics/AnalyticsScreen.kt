package com.solo.ledger.ui.screens.analytics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material.icons.rounded.ArrowBack
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
import com.solo.ledger.ui.components.*
import com.solo.ledger.ui.theme.LedgerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(nav: NavController, vm: AnalyticsViewModel = viewModel()) {
    val c = LedgerTheme.colors
    val s by vm.state.collectAsState()
    Scaffold(
        containerColor = c.background,
        topBar = { TopAppBar(title = { Text("Analytics") },
            navigationIcon = { IconButton(onClick = { nav.popBackStack() }) { Icon(Icons.Rounded.ArrowBack, "Back") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = c.background, titleContentColor = c.textPrimary, navigationIconContentColor = c.textPrimary)) }
    ) { pad ->
        LazyColumn(Modifier.padding(pad).fillMaxSize(), contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    RangeMode.values().forEach { m ->
                        FilterChip(selected = s.mode == m, onClick = { vm.setMode(m) }, label = { Text(m.label) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = c.primary, selectedLabelColor = c.onPrimary))
                    }
                }
            }
            item {
                LedgerCard(Modifier.fillMaxWidth()) {
                    Text("Total spent · ${s.mode.label}", style = MaterialTheme.typography.labelLarge, color = c.muted)
                    Spacer(Modifier.height(4.dp))
                    Text(Money.format(s.total), style = MaterialTheme.typography.displaySmall, color = c.textPrimary)
                }
            }
            item {
                LedgerCard(Modifier.fillMaxWidth()) {
                    SectionHeader("Daily spending")
                    Spacer(Modifier.height(12.dp))
                    if (s.dailySeries.any { it > 0f }) BarChart(s.dailySeries)
                    else EmptyState(Icons.Outlined.QueryStats, "No data", "Spending in this range will appear here.")
                }
            }
            item {
                LedgerCard(Modifier.fillMaxWidth()) {
                    SectionHeader("Spending trend (6 months)")
                    Spacer(Modifier.height(12.dp))
                    LineChart(s.monthlySeries.map { it.second })
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        s.monthlySeries.forEach { Text(it.first, style = MaterialTheme.typography.labelMedium, color = c.muted) }
                    }
                }
            }
            item {
                LedgerCard(Modifier.fillMaxWidth()) {
                    SectionHeader("By category")
                    Spacer(Modifier.height(12.dp))
                    if (s.categories.isEmpty()) EmptyState(Icons.Outlined.QueryStats, "No categories", "Add expenses to compare categories.")
                    else Row(verticalAlignment = Alignment.CenterVertically) {
                        DonutChart(s.categories.mapIndexed { i, cat -> Slice(cat.total.toFloat(), c.chart[i % c.chart.size], cat.category) },
                            Modifier.size(130.dp))
                        Spacer(Modifier.width(20.dp))
                        Column(Modifier.weight(1f)) {
                            s.categories.take(6).forEachIndexed { i, cat ->
                                Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(Modifier.size(10.dp).clip(RoundedCornerShape(3.dp)).androidxBg(c.chart[i % c.chart.size]))
                                        Spacer(Modifier.width(8.dp))
                                        Text(cat.category, style = MaterialTheme.typography.bodyMedium, color = c.textSecondary)
                                    }
                                    Text(Money.format(cat.total), style = MaterialTheme.typography.bodyMedium, color = c.textPrimary, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
