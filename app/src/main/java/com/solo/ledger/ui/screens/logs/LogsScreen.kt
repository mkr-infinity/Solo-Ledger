package com.solo.ledger.ui.screens.logs

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                        "Logs",
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
                        imageVector = Icons.Filled.Code,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No logs recorded",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            // Terminal-style log viewer
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0D1117))
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    item {
                        TerminalHeader(logCount = logs.size)
                    }

                    items(logs, key = { it.id }) { log ->
                        TerminalLogLine(log = log)
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$ _",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = Color(0xFF8B949E)
                        )
                    }
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
                    "1week" -> now - 7L * 24 * 60 * 60 * 1000
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
private fun TerminalHeader(logCount: Int) {
    Column {
        Text(
            text = "// Solo Ledger Activity Log",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            color = Color(0xFF8B949E)
        )
        Text(
            text = "// Total entries: $logCount",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            color = Color(0xFF8B949E)
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun TerminalLogLine(log: AppLog) {
    val timeStr = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date(log.timestamp))
    val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(log.timestamp))

    val typeColor = when (log.type) {
        LogType.EXPENSE_ADDED, LogType.GOAL_ADDED, LogType.CATEGORY_ADDED -> Color(0xFF7EE787)
        LogType.EXPENSE_EDITED, LogType.GOAL_UPDATED, LogType.SETTINGS_CHANGED,
        LogType.THEME_CHANGED, LogType.BUDGET_CHANGED -> Color(0xFFF0C14B)
        LogType.EXPENSE_DELETED, LogType.CATEGORY_DELETED, LogType.BIN_CLEARED -> Color(0xFFF85149)
        LogType.EXPENSE_RESTORED -> Color(0xFF79C0FF)
        LogType.DATA_EXPORTED, LogType.DATA_IMPORTED -> Color(0xFFD2A8FF)
        LogType.APP_OPENED -> Color(0xFF8B949E)
    }

    val typeTag = when (log.type) {
        LogType.EXPENSE_ADDED -> "ADD"
        LogType.EXPENSE_EDITED -> "EDT"
        LogType.EXPENSE_DELETED -> "DEL"
        LogType.EXPENSE_RESTORED -> "RST"
        LogType.CATEGORY_ADDED -> "CAT+"
        LogType.CATEGORY_DELETED -> "CAT-"
        LogType.GOAL_ADDED -> "GOL+"
        LogType.GOAL_UPDATED -> "GOL~"
        LogType.BUDGET_CHANGED -> "BDG"
        LogType.THEME_CHANGED -> "THM"
        LogType.DATA_EXPORTED -> "EXP"
        LogType.DATA_IMPORTED -> "IMP"
        LogType.SETTINGS_CHANGED -> "SET"
        LogType.APP_OPENED -> "SYS"
        LogType.BIN_CLEARED -> "CLR"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 1.dp)
    ) {
        Text(
            text = "[$dateStr $timeStr]",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            color = Color(0xFF8B949E)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = typeTag.padEnd(5),
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = typeColor
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = log.title,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            color = Color(0xFFE6EDF3)
        )
        if (log.details.isNotBlank()) {
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "| ${log.details}",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = Color(0xFF8B949E)
            )
        }
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
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Choose time range",
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
                        .padding(vertical = 3.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(10.dp)
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
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = label, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
