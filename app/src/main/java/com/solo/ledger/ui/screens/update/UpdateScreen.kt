package com.solo.ledger.ui.screens.update

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
    val htmlUrl: String,
    val publishedAt: String
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
    var checkResult by remember { mutableStateOf<String?>(null) }
    var autoCheckEnabled by remember { mutableStateOf(true) }
    var hasCheckedOnLoad by remember { mutableStateOf(false) }

    val currentVersion = "1.0.0"

    fun runCheck(showToast: Boolean) {
        scope.launch {
            isChecking = true
            try {
                val release = withContext(Dispatchers.IO) { fetchLatestRelease() }
                latestRelease = release
                checkResult = when {
                    release == null -> "up_to_date"
                    isNewerVersion(release.tagName, currentVersion) -> "update_available"
                    else -> "up_to_date"
                }
                if (showToast) {
                    viewModel.showToast(
                        if (checkResult == "update_available") "New update ${latestRelease?.tagName} available"
                        else "You are on the latest version",
                        if (checkResult == "update_available") com.solo.ledger.ui.components.ToastType.SUCCESS
                        else com.solo.ledger.ui.components.ToastType.INFO
                    )
                }
            } catch (e: Exception) {
                checkResult = "up_to_date"
                if (showToast) viewModel.showToast("Could not reach update server", com.solo.ledger.ui.components.ToastType.WARNING)
                viewModel.log(com.solo.ledger.data.model.LogType.ERROR, "Update check failed", "Error: ${e.message?.take(100) ?: "Unknown"}")
            }
            isChecking = false
        }
    }

    LaunchedEffect(Unit) {
        if (autoCheckEnabled && !hasCheckedOnLoad) {
            hasCheckedOnLoad = true
            runCheck(showToast = false)
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Animated app icon hero
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                    Color.Transparent
                                )
                            )
                        )
                )
                if (isChecking) {
                    val transition = rememberInfiniteTransition(label = "spin")
                    val angle by transition.animateFloat(
                        initialValue = 0f, targetValue = 360f,
                        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing)),
                        label = "angle"
                    )
                    CircularProgressIndicator(
                        modifier = Modifier.size(88.dp).rotate(angle),
                        strokeWidth = 3.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.AccountBalanceWallet,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
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
                checkResult == "up_to_date" -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier.size(52.dp).clip(CircleShape).background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(30.dp))
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("You are up to date", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "You are running the latest version of Solo Ledger. No updates available right now.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.75f),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
                checkResult == "update_available" -> {
                    latestRelease?.let { release ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Filled.NewReleases, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Update Available", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                        Text(release.tagName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                                    }
                                }
                                if (release.body.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(16.dp))
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
                                    onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(release.htmlUrl))) },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)
                                ) {
                                    Icon(Icons.Filled.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Download Latest Release", fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }

            // Check button
            Button(
                onClick = { runCheck(showToast = true) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                enabled = !isChecking
            ) {
                Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isChecking) "Checking..." else "Check for Updates", fontWeight = FontWeight.SemiBold)
            }

            // Auto check toggle
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
                        Text("Auto-check updates", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                        Text("Check GitHub on screen open", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(checked = autoCheckEnabled, onCheckedChange = { autoCheckEnabled = it })
                }
            }

            Text(
                "Updates are fetched from the official GitHub repository.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun MarkdownPreview(
    markdown: String,
    textColor: Color,
    accentColor: Color
) {
    val lines = markdown.trim().lines()
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        lines.forEach { raw ->
            val line = raw.trimEnd()
            when {
                line.isBlank() -> Spacer(modifier = Modifier.height(2.dp))
                line.startsWith("### ") -> Text(
                    parseInline(line.removePrefix("### ")),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
                line.startsWith("## ") -> Text(
                    parseInline(line.removePrefix("## ")),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
                line.startsWith("# ") -> Text(
                    parseInline(line.removePrefix("# ")),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
                line.trimStart().startsWith("- ") || line.trimStart().startsWith("* ") -> {
                    Row(verticalAlignment = Alignment.Top) {
                        Text("•  ", style = MaterialTheme.typography.bodySmall, color = accentColor, fontWeight = FontWeight.Bold)
                        Text(
                            parseInline(line.trimStart().removePrefix("- ").removePrefix("* ")),
                            style = MaterialTheme.typography.bodySmall,
                            color = textColor
                        )
                    }
                }
                else -> Text(
                    parseInline(line),
                    style = MaterialTheme.typography.bodySmall,
                    color = textColor
                )
            }
        }
    }
}

// Parse inline markdown (bold **text**, code `text`) into AnnotatedString
private fun parseInline(text: String): androidx.compose.ui.text.AnnotatedString {
    return androidx.compose.ui.text.buildAnnotatedString {
        var i = 0
        while (i < text.length) {
            when {
                text.startsWith("**", i) -> {
                    val end = text.indexOf("**", i + 2)
                    if (end != -1) {
                        pushStyle(androidx.compose.ui.text.SpanStyle(fontWeight = FontWeight.Bold))
                        append(text.substring(i + 2, end))
                        pop()
                        i = end + 2
                    } else { append(text[i]); i++ }
                }
                text.startsWith("`", i) -> {
                    val end = text.indexOf("`", i + 1)
                    if (end != -1) {
                        pushStyle(androidx.compose.ui.text.SpanStyle(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace))
                        append(text.substring(i + 1, end))
                        pop()
                        i = end + 1
                    } else { append(text[i]); i++ }
                }
                else -> { append(text[i]); i++ }
            }
        }
    }
}

private fun isNewerVersion(remote: String, current: String): Boolean {
    val remoteParts = remote.removePrefix("v").split(".").mapNotNull { it.toIntOrNull() }
    val currentParts = current.removePrefix("v").split(".").mapNotNull { it.toIntOrNull() }
    for (i in 0 until maxOf(remoteParts.size, currentParts.size)) {
        val r = remoteParts.getOrElse(i) { 0 }
        val c = currentParts.getOrElse(i) { 0 }
        if (r > c) return true
        if (r < c) return false
    }
    return false
}

private fun fetchLatestRelease(): GithubRelease? {
    return try {
        val url = URL("https://api.github.com/repos/mkr-infinity/Solo-Ledger/releases/latest")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("Accept", "application/vnd.github.v3+json")
        connection.setRequestProperty("User-Agent", "Solo-Ledger-App")
        connection.connectTimeout = 10000
        connection.readTimeout = 10000

        if (connection.responseCode == 200) {
            val json = connection.inputStream.bufferedReader().use { it.readText() }
            GithubRelease(
                tagName = extractJsonString(json, "tag_name"),
                name = extractJsonString(json, "name"),
                body = extractJsonString(json, "body"),
                htmlUrl = extractJsonString(json, "html_url"),
                publishedAt = extractJsonString(json, "published_at")
            )
        } else null
    } catch (e: Exception) {
        null
    }
}

private fun extractJsonString(json: String, key: String): String {
    val pattern = "\"$key\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"".toRegex()
    return pattern.find(json)?.groupValues?.get(1)
        ?.replace("\\n", "\n")
        ?.replace("\\\"", "\"")
        ?.replace("\\\\", "\\")
        ?: ""
}
