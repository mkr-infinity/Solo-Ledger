package com.solo.ledger.ui.screens.history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.solo.ledger.data.model.Category
import com.solo.ledger.data.model.Transaction
import com.solo.ledger.ui.components.empty.EmptySearch
import com.solo.ledger.ui.components.empty.EmptyTransactions
import com.solo.ledger.ui.components.cards.TransactionCard
import com.solo.ledger.ui.components.shared.SectionHeader
import com.solo.ledger.ui.components.shared.SwipeableTransactionItem
import com.solo.ledger.ui.navigation.NavRoutes
import com.solo.ledger.util.CurrencyFormatter
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HistoryScreen(
    navController: NavController,
    onQuickAddClick: () -> Unit = {},
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    var searchVisible by remember { mutableStateOf(false) }
    val searchFocusRequester = remember { FocusRequester() }

    LaunchedEffect(searchVisible) {
        if (searchVisible) searchFocusRequester.requestFocus()
    }

    val hasAnyTransactions = uiState.groupedTransactions.isNotEmpty() ||
        uiState.searchQuery.isNotBlank() ||
        uiState.filterType != FilterType.ALL ||
        uiState.selectedCategoryId != null

    val isFiltered = uiState.searchQuery.isNotBlank() ||
        uiState.filterType != FilterType.ALL ||
        uiState.selectedCategoryId != null

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = "History",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    },
                    actions = {
                        IconButton(onClick = {
                            searchVisible = !searchVisible
                            if (!searchVisible) {
                                viewModel.updateSearch("")
                                focusManager.clearFocus()
                            }
                        }) {
                            Icon(
                                imageVector = if (searchVisible) Icons.Filled.Close else Icons.Outlined.Search,
                                contentDescription = if (searchVisible) "Close search" else "Search"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )

                // Animated search bar
                AnimatedVisibility(
                    visible = searchVisible,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = viewModel::updateSearch,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .focusRequester(searchFocusRequester),
                        placeholder = {
                            Text(
                                text = "Search transactions, categories…",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                            )
                        },
                        singleLine = true,
                        trailingIcon = {
                            if (uiState.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.updateSearch("") }) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "Clear",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.None,
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = { focusManager.clearFocus() }
                        ),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        )
                    )
                }

                // Filter chips
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = uiState.filterType == FilterType.ALL,
                            onClick = { viewModel.updateFilter(FilterType.ALL) },
                            label = { Text("All", style = MaterialTheme.typography.labelMedium) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                    item {
                        FilterChip(
                            selected = uiState.filterType == FilterType.INCOME,
                            onClick = { viewModel.updateFilter(FilterType.INCOME) },
                            label = { Text("Income", style = MaterialTheme.typography.labelMedium) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF2E7D32).copy(alpha = 0.20f),
                                selectedLabelColor = Color(0xFF2E7D32)
                            )
                        )
                    }
                    item {
                        FilterChip(
                            selected = uiState.filterType == FilterType.EXPENSE,
                            onClick = { viewModel.updateFilter(FilterType.EXPENSE) },
                            label = { Text("Expense", style = MaterialTheme.typography.labelMedium) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFC62828).copy(alpha = 0.20f),
                                selectedLabelColor = Color(0xFFC62828)
                            )
                        )
                    }
                    items(uiState.allCategories) { category ->
                        FilterChip(
                            selected = uiState.selectedCategoryId == category.id,
                            onClick = {
                                viewModel.setCategory(
                                    if (uiState.selectedCategoryId == category.id) null else category.id
                                )
                            },
                            label = {
                                Text(
                                    text = category.name,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        )
                    }
                }

                // Summary strip
                if (!uiState.isLoading && (uiState.totalIncome > 0.0 || uiState.totalExpense > 0.0)) {
                    HistorySummaryStrip(
                        totalIncome = uiState.totalIncome,
                        totalExpense = uiState.totalExpense,
                        currencySymbol = settings.currencySymbol
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading -> {
                    androidx.compose.material3.CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                uiState.groupedTransactions.isEmpty() && isFiltered -> {
                    EmptySearch(
                        onClearFilters = {
                            viewModel.clearFilters()
                            searchVisible = false
                        },
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(top = 32.dp)
                    )
                }

                uiState.groupedTransactions.isEmpty() -> {
                    EmptyTransactions(
                        onAddClick = onQuickAddClick,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(top = 32.dp)
                    )
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        uiState.groupedTransactions.forEach { (dateLabel, txnPairs) ->
                            stickyHeader(key = "header_$dateLabel") {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.surface)
                                ) {
                                    SectionHeader(
                                        title = dateLabel,
                                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                                    )
                                }
                            }
                            items(
                                items = txnPairs,
                                key = { (txn, _) -> txn.id }
                            ) { (transaction, category) ->
                                HistoryTransactionRow(
                                    transaction = transaction,
                                    category = category,
                                    swipeLeftAction = settings.swipeLeftAction,
                                    swipeRightAction = settings.swipeRightAction,
                                    currencySymbol = settings.currencySymbol,
                                    onSoftDelete = { id ->
                                        viewModel.softDelete(id)
                                        coroutineScope.launch {
                                            val result = snackbarHostState.showSnackbar(
                                                message = "Transaction deleted",
                                                actionLabel = "Undo",
                                                duration = SnackbarDuration.Short
                                            )
                                            if (result == SnackbarResult.ActionPerformed) {
                                                viewModel.restore(id)
                                            }
                                        }
                                    },
                                    onEdit = { id ->
                                        navController.navigate(NavRoutes.editTransaction(id))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistorySummaryStrip(
    totalIncome: Double,
    totalExpense: Double,
    currencySymbol: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SummaryChip(
            label = "Income",
            amount = CurrencyFormatter.formatCompact(totalIncome, currencySymbol),
            color = Color(0xFF2E7D32),
            bgColor = Color(0xFF2E7D32).copy(alpha = 0.10f),
            modifier = Modifier.weight(1f)
        )
        SummaryChip(
            label = "Expense",
            amount = CurrencyFormatter.formatCompact(totalExpense, currencySymbol),
            color = Color(0xFFC62828),
            bgColor = Color(0xFFC62828).copy(alpha = 0.10f),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SummaryChip(
    label: String,
    amount: String,
    color: Color,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = color.copy(alpha = 0.75f)
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = amount,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = color
            )
        }
    }
}

@Composable
private fun HistoryTransactionRow(
    transaction: Transaction,
    category: Category?,
    swipeLeftAction: String,
    swipeRightAction: String,
    currencySymbol: String,
    onSoftDelete: (String) -> Unit,
    onEdit: (String) -> Unit
) {
    val leftIsDelete = swipeLeftAction == "delete"
    val rightIsDelete = swipeRightAction == "delete"

    SwipeableTransactionItem(
        onSwipeLeft = {
            if (leftIsDelete) onSoftDelete(transaction.id) else onEdit(transaction.id)
        },
        onSwipeRight = {
            if (rightIsDelete) onSoftDelete(transaction.id) else onEdit(transaction.id)
        },
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
            onClick = { onEdit(transaction.id) }
        )
    }
}
