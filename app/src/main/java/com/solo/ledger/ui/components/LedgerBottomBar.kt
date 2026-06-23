package com.solo.ledger.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.solo.ledger.ui.navigation.Dest
import com.solo.ledger.ui.theme.LedgerTheme

/**
 * Customizable bottom navigation. The user picks a style in Settings; this renders
 * the matching variant. All styles share the same destinations + center Quick Add action.
 */
@Composable
fun LedgerBottomBar(
    style: String,
    current: String,
    onSelect: (Dest) -> Unit,
    onQuickAdd: () -> Unit
) {
    val c = LedgerTheme.colors
    val items = Dest.barItems

    @Composable
    fun NavCell(dest: Dest, modifier: Modifier = Modifier, pill: Boolean = false) {
        val selected = current == dest.route
        val tint by animateColorAsState(if (selected) c.primary else c.muted, label = "tint")
        val bg by animateColorAsState(
            if (selected && pill) c.primary.copy(alpha = 0.14f) else Color.Transparent, label = "bg"
        )
        Column(
            modifier = modifier
                .clip(RoundedCornerShape(24.dp))
                .background(bg)
                .clickable(MutableInteractionSource(), null) { onSelect(dest) }
                .padding(horizontal = if (pill) 16.dp else 8.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(dest.icon, dest.label, tint = tint, modifier = Modifier.size(24.dp))
            if (!pill || selected) {
                Spacer(Modifier.height(2.dp))
                Text(dest.label, style = MaterialTheme.typography.labelMedium, color = tint, maxLines = 1)
            }
        }
    }

    val container: @Composable (@Composable RowScope.() -> Unit) -> Unit = { rowContent ->
        when (style) {
            "capsule" -> Surface(
                color = c.card, shadowElevation = 8.dp,
                shape = RoundedCornerShape(32.dp),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp).fillMaxWidth()
            ) { Row(Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically,
                content = rowContent) }

            "floating" -> Surface(
                color = c.card, shadowElevation = 12.dp, shape = RoundedCornerShape(28.dp),
                modifier = Modifier.padding(20.dp).fillMaxWidth()
            ) { Row(Modifier.padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically, content = rowContent) }

            "minimal" -> Surface(color = c.background, modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(horizontal = 12.dp, vertical = 10.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically, content = rowContent)
            }

            "elevated" -> Surface(color = c.card, shadowElevation = 16.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(horizontal = 12.dp, vertical = 12.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically, content = rowContent)
            }

            "compact" -> Surface(color = c.card, shadowElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(horizontal = 8.dp, vertical = 6.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically, content = rowContent)
            }

            else -> Surface(color = c.card, shadowElevation = 3.dp, modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(horizontal = 8.dp, vertical = 8.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically, content = rowContent)
            }
        }
    }

    val pill = style == "capsule" || style == "floating"
    container {
        NavCell(items[0], Modifier.weight(1f), pill)
        NavCell(items[1], Modifier.weight(1f), pill)
        QuickAddButton(onQuickAdd)
        NavCell(items[2], Modifier.weight(1f), pill)
        NavCell(items[3], Modifier.weight(1f), pill)
    }
}

@Composable
private fun QuickAddButton(onClick: () -> Unit) {
    val c = LedgerTheme.colors
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val size by animateDpAsState(if (pressed) 50.dp else 56.dp, spring(), label = "fab")
    Box(Modifier.width(64.dp), contentAlignment = Alignment.Center) {
        Box(
            Modifier.size(size).shadow(8.dp, CircleShape).clip(CircleShape)
                .background(c.primary).clickable(interaction, null, onClick = onClick),
            contentAlignment = Alignment.Center
        ) { Icon(Icons.Rounded.Add, "Quick Add", tint = c.onPrimary, modifier = Modifier.size(28.dp)) }
    }
}
