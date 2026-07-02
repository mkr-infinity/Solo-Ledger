package com.solo.ledger.ui.screens.logs

import android.content.Intent
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.solo.ledger.data.model.AppLog
import com.solo.ledger.data.model.LogType
import com.solo.ledger.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogsScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val logs by viewModel.appLogs.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showExportSheet by remember { mutableStateOf(false) }
    var showClearConfirm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Activity Logs",
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
                    IconButton(onClick = { showExportSheet = true }) {
                        Icon(Icons.Filled.FileDownload, contentDescription = "Export")
                    }
                    IconButton(onClick = { showClearConfirm = true }) {
                        Icon(Icons.Filled.DeleteSweep, contentDescription = "Clear")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (logs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.Article,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No logs recorded yet",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Activity will appear here as you use the app",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                item {
                    Text(
                        text = "${logs.size} log entries",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                items(logs, key = { it.id }) { log ->
                    LogItem(log = log)
                }
            }
        }
    }

    // Export bottom sheet
    if (showExportSheet) {
        ExportLogsSheet(
            onDismiss = { showExportSheet = false },
            onExport = { timeRange ->
                val now = System.currentTimeMillis()
                val startTime = when (timeRange) {
                    "10min" -> now - 10 * 60 * 1000
                    "1hour" -> now - 60 * 60 * 1000
                    "1day" -> now - 24 * 60 * 60 * 1000
                    "1week" -> now - 7 * 24 * 60 * 60 * 1000
                    "1month" -> now - 30L * 24 * 60 * 60 * 1000
                    else -> 0L
                }
                val filteredLogs = if (timeRange == "all") logs
                else logs.filter { it.timestamp >= startTime }

                val text = viewModel.exportLogs(filteredLogs)
                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, text)
                    putExtra(Intent.EXTRA_SUBJECT, "Solo Ledger Logs")
                }
                context.startActivity(Intent.createChooser(sendIntent, null))
                showExportSheet = false
            }
        )
    }

    // Clear confirmation
    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text("Clear All Logs") },
            text = { Text("This will permanently delete all ${logs.size} log entries.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearLogs()
                    showClearConfirm = false
                }) {
                    Text("Clear", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun LogItem(log: AppLog) {
    var expanded by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(getLogColor(log.type))
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = log.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${dateFormat.format(Date(log.timestamp))} at ${timeFormat.format(Date(log.timestamp))}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(18.dp)
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column {
                            Text(
                                text = "Type",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = log.type.name.replace("_", " "),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Column {
                            Text(
                                text = "Timestamp",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
                                    .format(Date(log.timestamp)),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    if (log.details.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Details",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = log.details,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun getLogColor(type: LogType): androidx.compose.ui.graphics.Color {
    return when (type) {
        LogType.EXPENSE_ADDED -> MaterialTheme.colorScheme.secondary
        LogType.EXPENSE_EDITED -> MaterialTheme.colorScheme.tertiary
        LogType.EXPENSE_DELETED, LogType.BIN_CLEARED -> MaterialTheme.colorScheme.error
        LogType.EXPENSE_RESTORED -> MaterialTheme.colorScheme.primary
        LogType.CATEGORY_ADDED, LogType.GOAL_ADDED -> MaterialTheme.colorScheme.secondary
        LogType.CATEGORY_DELETED -> MaterialTheme.colorScheme.error
        LogType.GOAL_UPDATED -> MaterialTheme.colorScheme.tertiary
        LogType.BUDGET_CHANGED, LogType.THEME_CHANGED, LogType.SETTINGS_CHANGED -> MaterialTheme.colorScheme.primary
        LogType.DATA_EXPORTED, LogType.DATA_IMPORTED -> MaterialTheme.colorScheme.secondary
        LogType.APP_OPENED -> MaterialTheme.colorScheme.onSurfaceVariant
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExportLogsSheet(
    onDismiss: () -> Unit,
    onExport: (String) -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Export Logs",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Choose a time range to export",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))

            listOf(
                "10min" to "Last 10 minutes",
                "1hour" to "Last 1 hour",
                "1day" to "Last 24 hours",
                "1week" to "Last 7 days",
                "1month" to "Last 30 days",
                "all" to "All logs"
            ).forEach { (key, label) ->
                Card(
                    onClick = { onExport(key) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
