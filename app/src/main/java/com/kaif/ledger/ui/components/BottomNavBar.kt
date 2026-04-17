package com.kaif.ledger.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomNavBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onFabClick: () -> Unit,
    items: List<BottomNavItem>
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(94.dp)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(74.dp)
                .align(Alignment.BottomCenter)
                .background(
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                    RoundedCornerShape(28.dp)
                )
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f),
                    RoundedCornerShape(28.dp)
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side items
            items.take(2).forEachIndexed { index, item ->
                NavBarItem(
                    icon = item.icon,
                    label = item.label,
                    isSelected = selectedTab == index,
                    onClick = { onTabSelected(index) }
                )
            }

            // Center FAB placeholder
            Box(modifier = Modifier.weight(1f))

            // Right side items
            items.drop(2).forEachIndexed { index, item ->
                NavBarItem(
                    icon = item.icon,
                    label = item.label,
                    isSelected = selectedTab == (index + 2),
                    onClick = { onTabSelected(index + 2) }
                )
            }
        }

        // Floating Action Button
        Box(
            modifier = Modifier
                .size(56.dp)
                .align(Alignment.TopCenter)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.95f),
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.88f)
                        )
                    ),
                    shape = CircleShape
                )
                .border(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.25f), CircleShape)
        ) {
            IconButton(
                onClick = onFabClick,
                modifier = Modifier.align(Alignment.Center)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
fun NavBarItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = onClick) {
            Icon(
                icon,
                contentDescription = label,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
        }
        Text(
            text = label,
            fontSize = 9.sp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.58f),
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.6.sp
        )
        if (isSelected) {
            Box(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .width(4.dp)
                    .height(4.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            )
        }
    }
}

data class BottomNavItem(
    val icon: ImageVector,
    val label: String
)
