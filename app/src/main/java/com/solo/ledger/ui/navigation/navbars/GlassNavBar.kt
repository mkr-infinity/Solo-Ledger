package com.solo.ledger.ui.navigation.navbars

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

@Composable
fun GlassNavBar(
    items: List<NavItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val quickAddIndex = items.indexOfFirst { it.isQuickAdd }
    val surfaceColor = MaterialTheme.colorScheme.surface
    val outlineColor = MaterialTheme.colorScheme.outline

    val glassModifier = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        modifier
            .fillMaxWidth()
            .height(72.dp)
            .graphicsLayer {
                renderEffect = android.graphics.RenderEffect
                    .createBlurEffect(20f, 20f, android.graphics.Shader.TileMode.CLAMP)
                    .asComposeRenderEffect()
            }
            .background(surfaceColor.copy(alpha = 0.7f))
    } else {
        modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(surfaceColor.copy(alpha = 0.85f))
    }

    Box(
        modifier = glassModifier
    ) {
        // Top border line (glass edge highlight)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .align(Alignment.TopCenter)
                .background(outlineColor.copy(alpha = 0.3f))
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = selectedIndex == index

                if (item.isQuickAdd) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .shadow(elevation = 6.dp, shape = CircleShape)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                            .clickable { onItemSelected(index) }
                            .zIndex(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.selectedIcon,
                            contentDescription = item.label,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                } else {
                    val chipBgColor by animateColorAsState(
                        targetValue = if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surface.copy(alpha = 0f),
                        animationSpec = spring(stiffness = Spring.StiffnessMedium),
                        label = "glassChipBg_$index"
                    )
                    val contentColor by animateColorAsState(
                        targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        animationSpec = spring(stiffness = Spring.StiffnessMedium),
                        label = "glassContent_$index"
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .background(chipBgColor, RoundedCornerShape(50.dp))
                            .clickable { onItemSelected(index) }
                            .padding(horizontal = if (isSelected) 12.dp else 8.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.icon,
                                contentDescription = item.label,
                                tint = contentColor,
                                modifier = Modifier.size(20.dp)
                            )
                            if (isSelected) {
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    color = contentColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
