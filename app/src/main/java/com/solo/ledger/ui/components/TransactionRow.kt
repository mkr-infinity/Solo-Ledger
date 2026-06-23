package com.solo.ledger.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.solo.ledger.core.Money
import com.solo.ledger.data.local.entity.ExpenseEntity
import com.solo.ledger.ui.theme.LedgerTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun TransactionRow(e: ExpenseEntity, currency: String, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
    val c = LedgerTheme.colors
    Surface(color = c.card, shape = MaterialTheme.shapes.medium,
        modifier = modifier.fillMaxWidth().then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(42.dp).clip(CircleShape).background(c.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center) {
                Icon(Icons.Rounded.ShoppingCart, null, tint = c.primary, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(e.title, style = MaterialTheme.typography.titleMedium, color = c.textPrimary, maxLines = 1)
                Text("${e.category} · ${LocalDate.ofEpochDay(e.dateEpochDay).format(DateTimeFormatter.ofPattern("dd MMM"))}",
                    style = MaterialTheme.typography.bodyMedium, color = c.textSecondary, maxLines = 1)
            }
            Text("- ${Money.format(e.amount, currency)}", style = MaterialTheme.typography.titleMedium,
                color = c.textPrimary, fontWeight = FontWeight.SemiBold)
        }
    }
}
