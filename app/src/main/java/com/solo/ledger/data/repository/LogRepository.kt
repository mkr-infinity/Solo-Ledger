package com.solo.ledger.data.repository

import android.content.Context
import com.solo.ledger.data.model.AppLog
import com.solo.ledger.data.model.LogType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LogRepository(private val context: Context? = null) {

    private val _logs = MutableStateFlow<List<AppLog>>(emptyList())
    val logs: StateFlow<List<AppLog>> = _logs.asStateFlow()

    private val maxLogs = 2000
    private val logFile: File? get() = context?.let { File(it.filesDir, "solo_ledger_logs.txt") }
    private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        // Load persisted logs on startup (off main thread)
        ioScope.launch { loadPersistedLogs() }
    }

    fun addLog(type: LogType, title: String, details: String = "") {
        val log = AppLog(
            id = System.nanoTime(),
            timestamp = System.currentTimeMillis(),
            type = type,
            title = title,
            details = details
        )
        val updated = (listOf(log) + _logs.value).take(maxLogs)
        _logs.value = updated
        // Persist off the main thread
        ioScope.launch { persistLog(log) }
    }

    fun clearLogs() {
        _logs.value = emptyList()
        ioScope.launch {
            logFile?.let { file ->
                try { file.delete() } catch (_: Exception) {}
            }
        }
    }

    fun getLogsByTimeRange(startTime: Long, endTime: Long): List<AppLog> {
        return _logs.value.filter { it.timestamp in startTime..endTime }
    }

    fun exportLogsAsText(logs: List<AppLog>): String {
        val sb = StringBuilder()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
        sb.appendLine("=== Solo Ledger - Activity Logs ===")
        sb.appendLine("Exported: ${dateFormat.format(Date())}")
        sb.appendLine("Total entries: ${logs.size}")
        sb.appendLine("Device: ${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}")
        sb.appendLine("Android: ${android.os.Build.VERSION.RELEASE} (API ${android.os.Build.VERSION.SDK_INT})")
        sb.appendLine("App Version: 1.0.0")
        sb.appendLine("===================================")
        sb.appendLine()

        logs.forEach { log ->
            val time = dateFormat.format(Date(log.timestamp))
            sb.appendLine("[$time] [${log.type.name}] ${log.title}")
            if (log.details.isNotBlank()) {
                sb.appendLine("  > ${log.details}")
            }
        }
        return sb.toString()
    }

    @Synchronized
    private fun persistLog(log: AppLog) {
        logFile?.let { file ->
            try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
                val line = "${dateFormat.format(Date(log.timestamp))}|${log.type.name}|${log.title}|${log.details}\n"
                file.appendText(line)

                // Trim file if too large (> 500KB)
                if (file.length() > 500 * 1024) {
                    val lines = file.readLines()
                    val trimmed = lines.takeLast(1000)
                    file.writeText(trimmed.joinToString("\n") + "\n")
                }
            } catch (_: Exception) {
                // Silently fail - don't crash for logging
            }
        }
    }

    private fun loadPersistedLogs() {
        logFile?.let { file ->
            if (!file.exists()) return
            try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
                val lines = file.readLines().takeLast(maxLogs)
                val loaded = lines.mapNotNull { line ->
                    try {
                        val parts = line.split("|", limit = 4)
                        if (parts.size >= 3) {
                            val timestamp = try { dateFormat.parse(parts[0])?.time ?: 0L } catch (_: Exception) { 0L }
                            val type = try { LogType.valueOf(parts[1]) } catch (_: Exception) { LogType.APP_OPENED }
                            val title = parts[2]
                            val details = if (parts.size > 3) parts[3] else ""
                            AppLog(
                                id = timestamp + (Math.random() * 1000).toLong(),
                                timestamp = timestamp,
                                type = type,
                                title = title,
                                details = details
                            )
                        } else null
                    } catch (_: Exception) { null }
                }.reversed()
                _logs.value = loaded
            } catch (_: Exception) {
                // Silently fail
            }
        }
    }
}
