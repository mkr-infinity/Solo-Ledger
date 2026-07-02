package com.solo.ledger.ui.screens.history

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.solo.ledger.data.model.Expense
import com.solo.ledger.ui.screens.home.formatAmount
import com.solo.ledger.ui.screens.home.getCategoryIcon
import com.solo.ledger.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: MainViewModel,
    onEditExpense: (Long) -> Unit,
    onNavigateToBin: () -> Unit
) {
    val allExpenses by viewModel.allExpenses.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val currencySymbol by viewModel.currencySymbol.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()

    var isSearchActive by remember { mutableStateOf(false) }
    var sortDescending by remember { mutableStateOf(true) }
    var selectedCategoryFilter by remember { mutableStateOf<Long?>(null) }
    var selectedExpense by remember { mutableStateOf<Expense?>(null) }
    var fullScreenImagePath by remember { mutableStateOf<String?>(null) }

    val displayedExpenses = if (isSearchActive && searchQuery.isNotBlank()) {
        searchResults
    } else {
        val filtered = if (selectedCategoryFilter != null) {
            allExpenses.filter { it.categoryId == selectedCategoryFilter }
        } else {
            allExpenses
        }
        if (sortDescending) filtered else filtered.reversed()
    }

    // Group by date
    val groupedExpenses = displayedExpenses.groupBy { expense ->
        SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date(expense.date))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "History",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Row {
                IconButton(onClick = { isSearchActive = !isSearchActive }) {
                    Icon(
                        if (isSearchActive) Icons.Filled.Close else Icons.Filled.Search,
                        contentDescription = "Search"
                    )
                }
                IconButton(onClick = { sortDescending = !sortDescending }) {
                    Icon(
                        if (sortDescending) Icons.Filled.ArrowDownward else Icons.Filled.ArrowUpward,
                        contentDescription = "Sort"
                    )
                }
                IconButton(onClick = onNavigateToBin) {
                    Icon(Icons.Filled.DeleteSweep, contentDescription = "Bin")
                }
            }
        }

        // Search bar
        AnimatedVisibility(visible = isSearchActive) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search expenses...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }

        // Category filter chips
        if (!isSearchActive) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedCategoryFilter == null,
                    onClick = { selectedCategoryFilter = null },
                    label = { Text("All") },
                    shape = RoundedCornerShape(20.dp)
                )
                val usedCategoryIds = allExpenses.map { it.categoryId }.distinct()
                categories.filter { it.id in usedCategoryIds }.take(4).forEach { cat ->
                    FilterChip(
                        selected = selectedCategoryFilter == cat.id,
                        onClick = {
                            selectedCategoryFilter = if (selectedCategoryFilter == cat.id) null else cat.id
                        },
                        label = { Text(cat.name) },
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }
        }

        // Expense list
        if (displayedExpenses.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.SearchOff,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (isSearchActive) "No results found" else "No expenses recorded",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                groupedExpenses.forEach { (dateLabel, expenses) ->
                    item {
                        Text(
                            text = dateLabel,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(expenses, key = { it.id }) { expense ->
                        val category = categories.find { it.id == expense.categoryId }
                        val dismissState = rememberDismissState(
                            confirmValueChange = { dismissValue ->
                                if (dismissValue == DismissValue.DismissedToStart) {
                                    viewModel.deleteExpense(expense.id)
                                    true
                                } else false
                            }
                        )

                        SwipeToDismiss(
                            state = dismissState,
                            directions = setOf(DismissDirection.EndToStart),
                            background = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.error.copy(alpha = 0.15f))
                                        .padding(end = 20.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(
                                        Icons.Filled.Delete,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                            dismissContent = {
                                HistoryExpenseItem(
                                    expense = expense,
                                    category = category,
                                    currencySymbol = currencySymbol,
                                    onClick = { selectedExpense = expense }
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    // Transaction detail popup
    if (selectedExpense != null) {
        val expense = selectedExpense!!
        val category = categories.find { it.id == expense.categoryId }

        AlertDialog(
            onDismissRequest = { selectedExpense = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (category != null) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(category.color).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                getCategoryIcon(category.icon),
                                contentDescription = null,
                                tint = Color(category.color),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                    Text(expense.title, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    DetailRow("Amount", "$currencySymbol${formatAmount(expense.amount)}")
                    DetailRow("Category", category?.name ?: "Other")
                    DetailRow("Date", SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(expense.date)))
                    DetailRow("Time", expense.time)
                    if (expense.notes.isNotBlank()) {
                        DetailRow("Notes", expense.notes)
                    }
                    if (!expense.attachmentPath.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        AsyncImage(
                            model = java.io.File(expense.attachmentPath!!),
                            contentDescription = "Receipt",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { fullScreenImagePath = expense.attachmentPath },
                            contentScale = ContentScale.Crop
                        )
                    }
                    DetailRow("Created", SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(Date(expense.createdAt)))
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    selectedExpense = null
                    onEditExpense(expense.id)
                }) {
                    Text("Edit")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.deleteExpense(expense.id)
                    selectedExpense = null
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            }
        )
    }

    // Full screen image viewer
    if (fullScreenImagePath != null) {
        com.solo.ledger.ui.components.FullScreenImageViewer(
            imagePath = fullScreenImagePath!!,
            onDismiss = { fullScreenImagePath = null }
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun HistoryExpenseItem(
    expense: Expense,
    category: com.solo.ledger.data.model.Category?,
    currencySymbol: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
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
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(category?.color ?: 0xFF90A4AE).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getCategoryIcon(category?.icon),
                        contentDescription = null,
                        tint = Color(category?.color ?: 0xFF90A4AE),
                        modifier = Modifier.size(22.dp)
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
