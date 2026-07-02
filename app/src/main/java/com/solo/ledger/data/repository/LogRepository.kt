package com.solo.ledger.data.repository

import com.solo.ledger.data.model.AppLog
import com.solo.ledger.data.model.LogType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LogRepository {

    private val _logs = MutableStateFlow<List<AppLog>>(emptyList())
    val logs: StateFlow<List<AppLog>> = _logs.asStateFlow()

    fun addLog(type: LogType, title: String, details: String = "") {
        val log = AppLog(
            timestamp = System.currentTimeMillis(),
            type = type,
            title = title,
            details = details
        )
        _logs.value = listOf(log) + _logs.value
    }

    fun clearLogs() {
        _logs.value = emptyList()
    }

    fun getLogsByTimeRange(startTime: Long, endTime: Long): List<AppLog> {
        return _logs.value.filter { it.timestamp in startTime..endTime }
    }

    fun exportLogsAsText(logs: List<AppLog>): String {
        val sb = StringBuilder()
        sb.appendLine("Solo Ledger - Activity Logs")
        sb.appendLine("Exported: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())}")
        sb.appendLine("Total entries: ${logs.size}")
        sb.appendLine("---")
        sb.appendLine()

        logs.forEach { log ->
            val time = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
                .format(java.util.Date(log.timestamp))
            sb.appendLine("[$time] [${log.type.name}] ${log.title}")
            if (log.details.isNotBlank()) {
                sb.appendLine("  Details: ${log.details}")
            }
            sb.appendLine()
        }
        return sb.toString()
    }
}
