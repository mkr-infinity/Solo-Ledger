package com.solo.ledger.ui.screens.goals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.solo.ledger.core.Money
import com.solo.ledger.data.local.entity.GoalEntity
import com.solo.ledger.ui.components.EmptyState
import com.solo.ledger.ui.components.LedgerCard
import com.solo.ledger.ui.theme.LedgerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(nav: NavController, vm: GoalsViewModel = viewModel()) {
    val c = LedgerTheme.colors
    val goals by vm.goals.collectAsState()
    var showAdd by remember { mutableStateOf(false) }
    var contributeTo by remember { mutableStateOf<GoalEntity?>(null) }

    Scaffold(
        containerColor = c.background,
        topBar = { TopAppBar(title = { Text("Savings Goals") },
            navigationIcon = { IconButton(onClick = { nav.popBackStack() }) { Icon(Icons.Rounded.ArrowBack, "Back") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = c.background, titleContentColor = c.textPrimary, navigationIconContentColor = c.textPrimary)) },
        floatingActionButton = { FloatingActionButton(onClick = { showAdd = true }, containerColor = c.primary, contentColor = c.onPrimary) { Icon(Icons.Rounded.Add, "Add goal") } }
    ) { pad ->
        if (goals.isEmpty()) {
            Box(Modifier.padding(pad).fillMaxSize(), contentAlignment = Alignment.Center) {
                EmptyState(Icons.Outlined.Savings, "No goals yet", "Create a savings goal to start tracking progress.")
            }
        } else {
            LazyColumn(Modifier.padding(pad).fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                items(goals, key = { it.id }) { g ->
                    val pct = if (g.targetAmount > 0) (g.savedAmount / g.targetAmount).coerceIn(0.0, 1.0) else 0.0
                    LedgerCard(Modifier.fillMaxWidth()) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(g.title, style = MaterialTheme.typography.titleMedium, color = c.textPrimary, fontWeight = FontWeight.SemiBold)
                            IconButton(onClick = { vm.delete(g) }) { Icon(Icons.Rounded.Delete, "Delete", tint = c.muted) }
                        }
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(progress = { pct.toFloat() }, modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(6.dp)), color = c.primary, trackColor = c.outline)
                        Spacer(Modifier.height(10.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${Money.format(g.savedAmount)} saved", style = MaterialTheme.typography.bodyMedium, color = c.textSecondary)
                            Text("${Money.format(g.targetAmount - g.savedAmount)} left", style = MaterialTheme.typography.bodyMedium, color = c.textSecondary)
                        }
                        Spacer(Modifier.height(8.dp))
                        TextButton(onClick = { contributeTo = g }) { Text("Add money", color = c.primary) }
                    }
                }
            }
        }
    }

    if (showAdd) AddGoalDialog(onDismiss = { showAdd = false }, onConfirm = { t, a -> vm.add(t, a); showAdd = false })
    contributeTo?.let { g ->
        AmountDialog("Add to ${g.title}", onDismiss = { contributeTo = null }) { amt -> vm.contribute(g, amt); contributeTo = null }
    }
}

@Composable
private fun AddGoalDialog(onDismiss: () -> Unit, onConfirm: (String, Double) -> Unit) {
    var title by remember { mutableStateOf("") }
    var target by remember { mutableStateOf("") }
    AlertDialog(onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = { onConfirm(title.ifBlank { "Goal" }, target.toDoubleOrNull() ?: 0.0) }, enabled = (target.toDoubleOrNull() ?: 0.0) > 0) { Text("Create") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        title = { Text("New goal") },
        text = { Column {
            OutlinedTextField(title, { title = it }, label = { Text("Title") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(target, { target = it.filter { ch -> ch.isDigit() || ch == '.' } }, label = { Text("Target amount") }, prefix = { Text("₹ ") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        } })
}

@Composable
private fun AmountDialog(title: String, onDismiss: () -> Unit, onConfirm: (Double) -> Unit) {
    var amount by remember { mutableStateOf("") }
    AlertDialog(onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = { onConfirm(amount.toDoubleOrNull() ?: 0.0) }, enabled = (amount.toDoubleOrNull() ?: 0.0) > 0) { Text("Add") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        title = { Text(title) },
        text = { OutlinedTextField(amount, { amount = it.filter { ch -> ch.isDigit() || ch == '.' } }, label = { Text("Amount") }, prefix = { Text("₹ ") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth()) })
}
