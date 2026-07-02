package com.solo.ledger.ui.screens.update

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solo.ledger.ui.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

data class GithubRelease(
    val tagName: String,
    val name: String,
    val body: String,
    val htmlUrl: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isChecking by remember { mutableStateOf(false) }
    var latestRelease by remember { mutableStateOf<GithubRelease?>(null) }
    var checkResult by remember { mutableStateOf("") }
    var autoCheckEnabled by remember { mutableStateOf(true) }
    var hasChecked by remember { mutableStateOf(false) }

    val currentVersion = "1.0.0"

    fun doCheck(withToast: Boolean) {
        scope.launch {
            isChecking = true
            checkResult = ""
            val release = try {
                withContext(Dispatchers.IO) { fetchLatestRelease() }
            } catch (e: Exception) {
                viewModel.log(com.solo.ledger.data.model.LogType.ERROR, "Update check failed", e.message ?: "unknown")
                null
            }
            isChecking = false
            latestRelease = release
            checkResult = when {
                release != null && isNewerVersion(release.tagName, currentVersion) -> "available"
                else -> "uptodate"
            }
            if (withToast) {
                if (checkResult == "available") {
                    viewModel.showToast("Update ${release?.tagName} available", com.solo.ledger.ui.components.ToastType.SUCCESS)
                } else {
                    viewModel.showToast("You are using the latest version", com.solo.ledger.ui.components.ToastType.INFO)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (autoCheckEnabled && !hasChecked) {
            hasChecked = true
            doCheck(withToast = false)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Updates", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // App icon hero
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                if (isChecking) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(40.dp),
                        strokeWidth = 3.dp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                } else {
                    Icon(
                        Icons.Filled.AccountBalanceWallet,
                        contentDescription = null,
                        modifier = Modifier.size(44.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Text("Solo Ledger", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                Text(
                    "Version $currentVersion",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Result state
            when {
                isChecking -> {
                    Text("Checking for updates...", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                checkResult == "uptodate" -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(22.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("You are up to date", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "You are running the latest version available.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.75f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                checkResult == "available" && latestRelease != null -> {
                    val release = latestRelease!!
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.NewReleases, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(28.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Update Available", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                    Text(release.tagName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                                }
                            }
                            if (release.body.isNotBlank()) {
                                Spacer(modifier = Modifier.height(14.dp))
                                Divider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("What's New", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                Spacer(modifier = Modifier.height(8.dp))
                                MarkdownPreview(
                                    markdown = release.body,
                                    textColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                                    accentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    try { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(release.htmlUrl))) } catch (_: Exception) {}
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)
                            ) {
                                Icon(Icons.Filled.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Download Update", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            // Check button
            Button(
                onClick = { doCheck(withToast = true) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                enabled = !isChecking
            ) {
                Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isChecking) "Checking..." else "Check for Updates", fontWeight = FontWeight.SemiBold)
            }

            // Auto-check toggle
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Update, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Auto-check for updates", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                        Text("Check GitHub when this screen opens", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(checked = autoCheckEnabled, onCheckedChange = { autoCheckEnabled = it })
                }
            }

            Text(
                "Updates are fetched from the official GitHub repository.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun MarkdownPreview(markdown: String, textColor: Color, accentColor: Color) {
    val lines = markdown.trim().split("\n")
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        for (raw in lines) {
            val line = raw.trim()
            when {
                line.isEmpty() -> Spacer(modifier = Modifier.height(2.dp))
                line.startsWith("###") -> Text(line.removePrefix("###").trim(), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = accentColor)
                line.startsWith("##") -> Text(line.removePrefix("##").trim(), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = accentColor)
                line.startsWith("#") -> Text(line.removePrefix("#").trim(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = accentColor)
                line.startsWith("-") || line.startsWith("*") -> {
                    Row(verticalAlignment = Alignment.Top) {
                        Text("\u2022  ", style = MaterialTheme.typography.bodySmall, color = accentColor, fontWeight = FontWeight.Bold)
                        Text(cleanInline(line.removePrefix("-").removePrefix("*").trim()), style = MaterialTheme.typography.bodySmall, color = textColor)
                    }
                }
                else -> Text(cleanInline(line), style = MaterialTheme.typography.bodySmall, color = textColor)
            }
        }
    }
}

private fun cleanInline(text: String): String {
    return text
        .replace("**", "")
        .replace("`", "")
        .replace("__", "")
}

private fun isNewerVersion(remote: String, current: String): Boolean {
    val r = remote.removePrefix("v").split(".").mapNotNull { it.toIntOrNull() }
    val c = current.removePrefix("v").split(".").mapNotNull { it.toIntOrNull() }
    for (i in 0 until maxOf(r.size, c.size)) {
        val rv = r.getOrElse(i) { 0 }
        val cv = c.getOrElse(i) { 0 }
        if (rv > cv) return true
        if (rv < cv) return false
    }
    return false
}

private fun fetchLatestRelease(): GithubRelease? {
    var connection: HttpURLConnection? = null
    return try {
        val url = URL("https://api.github.com/repos/mkr-infinity/Solo-Ledger/releases/latest")
        connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("Accept", "application/vnd.github.v3+json")
        connection.setRequestProperty("User-Agent", "Solo-Ledger-App")
        connection.connectTimeout = 10000
        connection.readTimeout = 10000
        val code = connection.responseCode
        if (code == 200) {
            val json = connection.inputStream.bufferedReader().readText()
            GithubRelease(
                tagName = extractJson(json, "tag_name"),
                name = extractJson(json, "name"),
                body = extractJson(json, "body"),
                htmlUrl = extractJson(json, "html_url")
            )
        } else {
            null
        }
    } catch (e: Exception) {
        null
    } finally {
        try { connection?.disconnect() } catch (_: Exception) {}
    }
}

private fun extractJson(json: String, key: String): String {
    val marker = "\"" + key + "\""
    val keyIndex = json.indexOf(marker)
    if (keyIndex == -1) return ""
    val colonIndex = json.indexOf(":", keyIndex + marker.length)
    if (colonIndex == -1) return ""
    var i = colonIndex + 1
    while (i < json.length && json[i] != '"') {
        if (json[i] == ',' || json[i] == '}') return ""
        i++
    }
    if (i >= json.length) return ""
    val start = i + 1
    val sb = StringBuilder()
    var j = start
    while (j < json.length) {
        val ch = json[j]
        if (ch == '\\' && j + 1 < json.length) {
            val next = json[j + 1]
            when (next) {
                'n' -> sb.append('\n')
                't' -> sb.append('\t')
                'r' -> {}
                '"' -> sb.append('"')
                '\\' -> sb.append('\\')
                '/' -> sb.append('/')
                else -> sb.append(next)
            }
            j += 2
        } else if (ch == '"') {
            break
        } else {
            sb.append(ch)
            j++
        }
    }
    return sb.toString()
}
