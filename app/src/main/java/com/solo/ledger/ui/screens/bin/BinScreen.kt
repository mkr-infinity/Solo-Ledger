package com.solo.ledger.ui.screens.bin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.solo.ledger.core.Money
import com.solo.ledger.ui.components.EmptyState
import com.solo.ledger.ui.components.LedgerCard
import com.solo.ledger.ui.theme.LedgerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BinScreen(nav: NavController, vm: BinViewModel = viewModel()) {
    val c = LedgerTheme.colors
    val items by vm.items.collectAsState()
    var confirmClear by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = c.background,
        topBar = { TopAppBar(title = { Text("Bin") },
            navigationIcon = { IconButton(onClick = { nav.popBackStack() }) { Icon(Icons.Rounded.ArrowBack, "Back") } },
            actions = { if (items.isNotEmpty()) TextButton(onClick = { confirmClear = true }) { Text("Clear all", color = c.error) } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = c.background, titleContentColor = c.textPrimary, navigationIconContentColor = c.textPrimary)) }
    ) { pad ->
        if (items.isEmpty()) {
            Box(Modifier.padding(pad).fillMaxSize(), contentAlignment = Alignment.Center) {
                EmptyState(Icons.Outlined.DeleteSweep, "Bin is empty", "Deleted expenses appear here for recovery.")
            }
        } else {
            LazyColumn(Modifier.padding(pad).fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(items, key = { it.id }) { e ->
                    LedgerCard(Modifier.fillMaxWidth()) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(e.title, style = MaterialTheme.typography.titleMedium, color = c.textPrimary)
                                Text("${e.category} · ${Money.format(e.amount)}", style = MaterialTheme.typography.bodyMedium, color = c.textSecondary)
                            }
                            IconButton(onClick = { vm.restore(e.id) }) { Icon(Icons.Rounded.Restore, "Restore", tint = c.primary) }
                            IconButton(onClick = { vm.deleteForever(e.id) }) { Icon(Icons.Rounded.DeleteForever, "Delete forever", tint = c.error) }
                        }
                    }
                }
            }
        }
    }
    if (confirmClear) AlertDialog(onDismissRequest = { confirmClear = false },
        confirmButton = { TextButton(onClick = { vm.clearAll(); confirmClear = false }) { Text("Clear all", color = c.error) } },
        dismissButton = { TextButton(onClick = { confirmClear = false }) { Text("Cancel") } },
        title = { Text("Empty bin?") }, text = { Text("This permanently deletes all items in the bin.") })
}
