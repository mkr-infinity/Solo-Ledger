package com.solo.ledger.util

import android.util.Log
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class LogEntry(
    val timestamp: LocalDateTime,
    val level: LogLevel,
    val tag: String,
    val message: String,
    val throwable: Throwable? = null
)

enum class LogLevel { DEBUG, INFO, WARN, ERROR }

object AppLogger {
    private val _logs = mutableListOf<LogEntry>()
    val logs: List<LogEntry> get() = _logs.toList()
    var isEnabled: Boolean = false  // Default OFF

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

    fun d(tag: String, message: String) {
        if (!isEnabled) return
        log(LogLevel.DEBUG, tag, message)
    }

    fun i(tag: String, message: String) {
        if (!isEnabled) return
        log(LogLevel.INFO, tag, message)
    }

    fun w(tag: String, message: String, throwable: Throwable? = null) {
        if (!isEnabled) return
        log(LogLevel.WARN, tag, message, throwable)
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        // Always log errors even if disabled - for error reporting
        log(LogLevel.ERROR, tag, message, throwable)
        Log.e(tag, message, throwable)
    }

    private fun log(level: LogLevel, tag: String, message: String, throwable: Throwable? = null) {
        val entry = LogEntry(
            timestamp = LocalDateTime.now(),
            level = level,
            tag = tag,
            message = message,
            throwable = throwable
        )
        synchronized(_logs) {
            _logs.add(entry)
            if (_logs.size > 2000) _logs.removeAt(0) // Keep max 2000 entries
        }
    }

    fun clear() {
        synchronized(_logs) { _logs.clear() }
    }

    fun exportAsText(): String {
        return buildString {
            appendLine("=== Solo Ledger App Logs ===")
            appendLine("Exported: ${LocalDateTime.now().format(formatter)}")
            appendLine("Total entries: ${_logs.size}")
            appendLine("=".repeat(60))
            _logs.forEach { entry ->
                append("[${entry.timestamp.format(formatter)}]")
                append(" [${entry.level.name}]")
                append(" [${entry.tag}]")
                appendLine(" ${entry.message}")
                entry.throwable?.let { throwable ->
                    appendLine("  Exception: ${throwable.javaClass.simpleName}: ${throwable.message}")
                    throwable.stackTrace.take(5).forEach { frame ->
                        appendLine("    at $frame")
                    }
                }
            }
        }
    }

    // Build prefilled GitHub issue URL for error reporting
    fun buildGitHubIssueUrl(errorEntry: LogEntry): String {
        val title = "Bug Report: [${errorEntry.tag}] ${errorEntry.message.take(80)}"
        val body = buildString {
            appendLine("## Bug Report")
            appendLine()
            appendLine("**Error:** `${errorEntry.message}`")
            appendLine("**Tag:** `${errorEntry.tag}`")
            appendLine("**Time:** `${errorEntry.timestamp.format(formatter)}`")
            errorEntry.throwable?.let {
                appendLine("**Exception:** `${it.javaClass.simpleName}: ${it.message}`")
                appendLine()
                appendLine("**Stack Trace:**")
                appendLine("```")
                appendLine(it.stackTrace.take(10).joinToString("\n") { frame -> "  at $frame" })
                appendLine("```")
            }
            appendLine()
            appendLine("**Recent Logs (last 10 entries):**")
            appendLine("```")
            _logs.takeLast(10).forEach { log ->
                appendLine("[${log.level.name}] [${log.tag}] ${log.message}")
            }
            appendLine("```")
            appendLine()
            appendLine("**Steps to reproduce:**")
            appendLine("1. ")
            appendLine()
            appendLine("**Expected behavior:**")
            appendLine()
            appendLine("**Actual behavior:**")
        }
        val encodedTitle = java.net.URLEncoder.encode(title, "UTF-8")
        val encodedBody = java.net.URLEncoder.encode(body, "UTF-8")
        return "https://github.com/mkr-infinity/Solo-Ledger/issues/new?title=${encodedTitle}&body=${encodedBody}&labels=bug"
    }

    fun buildFeatureRequestUrl(): String {
        val title = "Feature Request: "
        val body = buildString {
            appendLine("## Feature Request")
            appendLine()
            appendLine("**Describe the feature:**")
            appendLine()
            appendLine("**Why is this useful?**")
            appendLine()
            appendLine("**Any alternative solutions?**")
        }
        val encodedTitle = java.net.URLEncoder.encode(title, "UTF-8")
        val encodedBody = java.net.URLEncoder.encode(body, "UTF-8")
        return "https://github.com/mkr-infinity/Solo-Ledger/issues/new?title=${encodedTitle}&body=${encodedBody}&labels=enhancement"
    }

    fun buildBugReportUrl(): String {
        val title = "Bug Report: "
        val body = buildString {
            appendLine("## Bug Report")
            appendLine()
            appendLine("**Describe the bug:**")
            appendLine()
            appendLine("**Steps to reproduce:**")
            appendLine("1. ")
            appendLine()
            appendLine("**Expected behavior:**")
            appendLine()
            appendLine("**Actual behavior:**")
            appendLine()
            appendLine("**Device info:**")
            appendLine("- Android version: ${android.os.Build.VERSION.RELEASE}")
            appendLine("- Device: ${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}")
        }
        val encodedTitle = java.net.URLEncoder.encode(title, "UTF-8")
        val encodedBody = java.net.URLEncoder.encode(body, "UTF-8")
        return "https://github.com/mkr-infinity/Solo-Ledger/issues/new?title=${encodedTitle}&body=${encodedBody}&labels=bug"
    }
}
