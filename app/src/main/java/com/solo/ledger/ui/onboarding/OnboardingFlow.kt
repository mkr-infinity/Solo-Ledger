package com.solo.ledger.ui.onboarding

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import com.solo.ledger.ui.theme.LedgerTheme

@Composable
fun OnboardingFlow(onFinish: (String, Double) -> Unit) {
    val c = LedgerTheme.colors
    var step by remember { mutableIntStateOf(0) }
    var name by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    val permLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        onFinish(name.ifBlank { "User" }, budget.toDoubleOrNull() ?: 0.0)
    }

    Surface(color = c.background, modifier = Modifier.fillMaxSize()) {
        Column(
            Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).padding(24.dp)
        ) {
            // progress dots
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(3) { i ->
                    Box(Modifier.height(6.dp).weight(1f).clip(RoundedCornerShape(3.dp))
                        .background(if (i <= step) c.primary else c.outline))
                }
            }
            Spacer(Modifier.height(8.dp))
            AnimatedContent(
                targetState = step,
                transitionSpec = {
                    (slideInHorizontally { it } + fadeIn(tween(300))) togetherWith
                        (slideOutHorizontally { -it } + fadeOut(tween(200)))
                },
                label = "onboarding",
                modifier = Modifier.weight(1f)
            ) { s ->
                when (s) {
                    0 -> AboutPage()
                    1 -> SetupPage(name, budget, { name = it }, { budget = it })
                    else -> PermissionsPage()
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = { if (step > 0) step-- }, enabled = step > 0) { Text("Back") }
                Button(
                    onClick = {
                        if (step < 2) step++
                        else {
                            val perms = if (Build.VERSION.SDK_INT >= 33) arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES)
                            else arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                            permLauncher.launch(perms)
                        }
                    },
                    enabled = step != 1 || name.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = c.primary, contentColor = c.onPrimary)
                ) { Text(if (step < 2) "Continue" else "Get Started") }
            }
        }
    }
}

@Composable private fun HeroIcon(icon: androidx.compose.ui.graphics.vector.ImageVector) {
    val c = LedgerTheme.colors
    Box(Modifier.size(120.dp).clip(RoundedCornerShape(36.dp)).background(c.primary.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center) {
        Icon(icon, null, tint = c.primary, modifier = Modifier.size(56.dp))
    }
}

@Composable private fun AboutPage() {
    val c = LedgerTheme.colors
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        HeroIcon(Icons.Rounded.AccountBalanceWallet)
        Spacer(Modifier.height(28.dp))
        Text("Solo Ledger", style = MaterialTheme.typography.displaySmall, color = c.textPrimary)
        Spacer(Modifier.height(12.dp))
        Text("A premium, offline-first budgeting companion for students and young professionals.",
            style = MaterialTheme.typography.bodyLarge, color = c.textSecondary, textAlign = TextAlign.Center)
        Spacer(Modifier.height(24.dp))
        listOf(
            Icons.Rounded.TrendingUp to "Track every rupee with clarity",
            Icons.Rounded.Insights to "Understand where your money goes",
            Icons.Rounded.Savings to "Build savings goals that stick"
        ).forEach { (ic, t) ->
            Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(40.dp).clip(CircleShape).background(c.surface), contentAlignment = Alignment.Center) {
                    Icon(ic, null, tint = c.primary, modifier = Modifier.size(22.dp))
                }
                Spacer(Modifier.width(14.dp))
                Text(t, style = MaterialTheme.typography.bodyMedium, color = c.textPrimary)
            }
        }
    }
}

@Composable private fun SetupPage(name: String, budget: String, onName: (String)->Unit, onBudget:(String)->Unit) {
    val c = LedgerTheme.colors
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        Text("Let's set you up", style = MaterialTheme.typography.headlineMedium, color = c.textPrimary)
        Spacer(Modifier.height(8.dp))
        Text("Tell us a little about your budget.", style = MaterialTheme.typography.bodyMedium, color = c.textSecondary)
        Spacer(Modifier.height(28.dp))
        OutlinedTextField(value = name, onValueChange = onName, label = { Text("Your name") },
            singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(value = budget, onValueChange = { onBudget(it.filter { ch -> ch.isDigit() || ch == '.' }) },
            label = { Text("Monthly budget") }, singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            prefix = { Text("₹ ") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(20.dp))
        Text("Budget templates", style = MaterialTheme.typography.labelLarge, color = c.muted)
        Spacer(Modifier.height(8.dp))
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Student" to "8000", "Hostel" to "12000", "Saver" to "15000", "Minimal" to "5000").forEach { (name, value) ->
                FilterChip(selected = budget == value, onClick = { onBudget(value) }, label = { Text(name) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = c.primary, selectedLabelColor = c.onPrimary))
            }
        }
    }
}

@Composable private fun PermissionsPage() {
    val c = LedgerTheme.colors
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        HeroIcon(Icons.Rounded.Shield)
        Spacer(Modifier.height(24.dp))
        Text("Permissions", style = MaterialTheme.typography.headlineMedium, color = c.textPrimary)
        Spacer(Modifier.height(8.dp))
        Text("Solo Ledger stays fully offline. These permissions are only used on-device.",
            style = MaterialTheme.typography.bodyMedium, color = c.textSecondary)
        Spacer(Modifier.height(24.dp))
        listOf(
            Icons.Rounded.Folder to "Storage" to "Import & export your JSON backups",
            Icons.Rounded.Image to "Media access" to "Attach receipt & bill images"
        ).forEach { (head, sub) ->
            Surface(color = c.card, shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(head.first, null, tint = c.primary)
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(head.second, color = c.textPrimary, fontWeight = FontWeight.SemiBold)
                        Text(sub, style = MaterialTheme.typography.bodyMedium, color = c.textSecondary)
                    }
                }
            }
        }
    }
}
