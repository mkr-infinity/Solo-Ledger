package com.solo.ledger.ui.screens.savings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.solo.ledger.data.model.SavingsGoal
import com.solo.ledger.ui.screens.home.formatAmount
import com.solo.ledger.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingsGoalsScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val savingsGoals by viewModel.savingsGoals.collectAsStateWithLifecycle()
    val currencySymbol by viewModel.currencySymbol.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingGoal by remember { mutableStateOf<SavingsGoal?>(null) }
    var addAmountGoal by remember { mutableStateOf<SavingsGoal?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Savings Goals",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Filled.Add, contentDescription = "Add Goal")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (savingsGoals.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.Savings,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No savings goals yet",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showAddDialog = true },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Create First Goal")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(savingsGoals, key = { it.id }) { goal ->
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
                                Text(
                                    text = goal.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Row {
                                    IconButton(
                                        onClick = { addAmountGoal = goal },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            Icons.Filled.Add,
                                            contentDescription = "Add savings",
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = { viewModel.deleteSavingsGoal(goal) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            Icons.Filled.Delete,
                                            contentDescription = "Delete",
                                            modifier = Modifier.size(18.dp),
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            LinearProgressIndicator(
                                progress = goal.progressPercent / 100f,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = MaterialTheme.colorScheme.secondary,
                                trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Saved",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "$currencySymbol${formatAmount(goal.savedAmount)}",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "Progress",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${goal.progressPercent.toInt()}%",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "Target",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "$currencySymbol${formatAmount(goal.targetAmount)}",
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

    // Add Goal Dialog
    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var target by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("New Savings Goal") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Goal Name") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = target,
                        onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) target = it },
                        label = { Text("Target Amount ($currencySymbol)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val targetAmount = target.toDoubleOrNull() ?: 0.0
                        if (name.isNotBlank() && targetAmount > 0) {
                            viewModel.addSavingsGoal(
                                SavingsGoal(name = name.trim(), targetAmount = targetAmount)
                            )
                            showAddDialog = false
                        }
                    },
                    enabled = name.isNotBlank() && (target.toDoubleOrNull() ?: 0.0) > 0
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add Amount Dialog
    if (addAmountGoal != null) {
        var addAmount by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { addAmountGoal = null },
            title = { Text("Add to ${addAmountGoal!!.name}") },
            text = {
                OutlinedTextField(
                    value = addAmount,
                    onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) addAmount = it },
                    label = { Text("Amount ($currencySymbol)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val amt = addAmount.toDoubleOrNull() ?: 0.0
                        if (amt > 0) {
                            viewModel.updateSavingsGoal(
                                addAmountGoal!!.copy(
                                    savedAmount = addAmountGoal!!.savedAmount + amt,
                                    updatedAt = System.currentTimeMillis()
                                )
                            )
                            addAmountGoal = null
                        }
                    },
                    enabled = (addAmount.toDoubleOrNull() ?: 0.0) > 0
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { addAmountGoal = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
