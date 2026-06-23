package com.solo.ledger.ui.screens.history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.solo.ledger.core.Money
import com.solo.ledger.data.local.entity.ExpenseEntity
import com.solo.ledger.ui.components.EmptyState
import com.solo.ledger.ui.components.LedgerCard
import com.solo.ledger.ui.theme.LedgerTheme
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun HistoryScreen(nav: NavController, vm: HistoryViewModel = viewModel()) {
    val c = LedgerTheme.colors
    val grouped by vm.grouped.collectAsState()
    val query by vm.search.collectAsState()
    val sort by vm.sortMode.collectAsState()
    val activeCat by vm.activeCategory.collectAsState()
    val categories by vm.categories.collectAsState()

    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(12.dp))
        Text("History", style = MaterialTheme.typography.headlineSmall, color = c.textPrimary, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = query, onValueChange = vm::setQuery, modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search title, category or notes") }, singleLine = true,
            leadingIcon = { Icon(Icons.Rounded.Search, null) })
        Spacer(Modifier.height(10.dp))
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SortMode.values().forEach { m ->
                FilterChip(selected = sort == m, onClick = { vm.setSort(m) }, label = { Text(m.label) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = c.primary, selectedLabelColor = c.onPrimary))
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = activeCat == null, onClick = { vm.setCategory(null) }, label = { Text("All") },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = c.primary, selectedLabelColor = c.onPrimary))
            categories.forEach { cat ->
                FilterChip(selected = activeCat == cat, onClick = { vm.setCategory(cat) }, label = { Text(cat) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = c.primary, selectedLabelColor = c.onPrimary))
            }
        }
        Spacer(Modifier.height(12.dp))
        if (grouped.isEmpty()) {
            EmptyState(Icons.Outlined.SearchOff, "No results", "Nothing matches your filters yet.", Modifier.padding(top = 40.dp))
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 24.dp)) {
                grouped.forEach { (day, items) ->
                    item(key = day) {
                        DateGroupCard(day, items.sumOf { it.amount }, items) { id -> nav.navigate("edit/$id") }
                    }
                }
            }
        }
    }
}

@Composable
private fun DateGroupCard(epochDay: Long, total: Double, rows: List<ExpenseEntity>, onRowClick: (Long) -> Unit) {
    val c = LedgerTheme.colors
    var expanded by remember { mutableStateOf(false) }
    LedgerCard(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().clickable { expanded = !expanded },
            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(LocalDate.ofEpochDay(epochDay).format(DateTimeFormatter.ofPattern("EEEE, dd MMM")),
                    style = MaterialTheme.typography.titleMedium, color = c.textPrimary)
                Text("${rows.size} transactions", style = MaterialTheme.typography.bodyMedium, color = c.textSecondary)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(Money.format(total), style = MaterialTheme.typography.titleMedium, color = c.primary, fontWeight = FontWeight.SemiBold)
                Icon(Icons.Rounded.ExpandMore, null, tint = c.muted, modifier = Modifier.rotate(if (expanded) 180f else 0f))
            }
        }
        AnimatedVisibility(expanded) {
            Column {
                Spacer(Modifier.height(8.dp))
                rows.forEach { e ->
                    Column(Modifier.fillMaxWidth().clickable { onRowClick(e.id) }.padding(vertical = 8.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(Modifier.weight(1f)) {
                                Text(e.title, style = MaterialTheme.typography.bodyLarge, color = c.textPrimary)
                                Text("${e.category} · ${LocalTime.ofSecondOfDay(e.timeMinutes * 60L).format(DateTimeFormatter.ofPattern("hh:mm a"))}",
                                    style = MaterialTheme.typography.bodyMedium, color = c.textSecondary)
                                if (e.notes.isNotBlank()) Text(e.notes, style = MaterialTheme.typography.bodyMedium, color = c.muted, maxLines = 2)
                            }
                            Text("- ${Money.format(e.amount)}", style = MaterialTheme.typography.bodyLarge, color = c.textPrimary)
                        }
                        if (e.attachmentUri != null) {
                            Spacer(Modifier.height(6.dp))
                            AsyncImage(model = e.attachmentUri, contentDescription = "Attachment", contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(12.dp)))
                        }
                        HorizontalDivider(color = c.outline)
                    }
                }
            }
        }
    }
}
