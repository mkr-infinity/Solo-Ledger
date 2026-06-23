package com.solo.ledger.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.solo.ledger.ui.theme.LedgerTheme

/** Card surface that respects the active theme's card color + outline. */
@Composable
fun LedgerCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val c = LedgerTheme.colors
    Surface(
        modifier = modifier,
        color = c.card,
        contentColor = c.textPrimary,
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 0.dp,
        shadowElevation = 1.dp
    ) {
        Column(Modifier.padding(16.dp), content = content)
    }
}

@Composable
fun SectionHeader(title: String, action: (@Composable () -> Unit)? = null) {
    val c = LedgerTheme.colors
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, color = c.textPrimary,
            fontWeight = FontWeight.SemiBold)
        action?.invoke()
    }
}

/** Empty-state block. In production these illustrations are SVG vector drawables in res/drawable. */
@Composable
fun EmptyState(icon: ImageVector, title: String, message: String, modifier: Modifier = Modifier) {
    val c = LedgerTheme.colors
    Column(
        modifier = modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier.size(96.dp).clip(RoundedCornerShape(28.dp)).background(c.surface),
            contentAlignment = Alignment.Center
        ) { Icon(icon, null, tint = c.muted, modifier = Modifier.size(44.dp)) }
        Spacer(Modifier.height(16.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, color = c.textPrimary)
        Spacer(Modifier.height(6.dp))
        Text(message, style = MaterialTheme.typography.bodyMedium, color = c.textSecondary,
            textAlign = TextAlign.Center)
    }
}

@Composable
fun StatChip(label: String, value: String, accent: Boolean = false) {
    val c = LedgerTheme.colors
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium, color = c.muted)
        Spacer(Modifier.height(2.dp))
        Text(value, style = MaterialTheme.typography.titleMedium,
            color = if (accent) c.primary else c.textPrimary, fontWeight = FontWeight.SemiBold)
    }
}
