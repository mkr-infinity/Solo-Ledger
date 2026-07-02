package com.solo.ledger.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun SupportPopup(
    onDismiss: () -> Unit,
    onMaybeLater: () -> Unit
) {
    val context = LocalContext.current

    val sheetBg = Color(0xFF161622)
    val cardBg = Color(0xFF1F1F2E)
    val accent = Color(0xFF9B7BE0)
    val coffeeGold = Color(0xFFFFC93C)
    val textPrimary = Color(0xFFF5F5FA)
    val textSecondary = Color(0xFFA0A0B8)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(sheetBg)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Decorative gradient header with floating coffee badge
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    accent.copy(alpha = 0.85f),
                                    Color(0xFF6C4BC4)
                                )
                            )
                        )
                ) {
                    // Decorative circles
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .offset(x = (-40).dp, y = (-60).dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f))
                    )
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .offset(x = 240.dp, y = 60.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f))
                    )
                    // Close button
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.TopEnd).padding(6.dp)
                    ) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Floating coffee cup badge overlapping header
                Box(
                    modifier = Modifier
                        .offset(y = (-38).dp)
                        .size(76.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(coffeeGold),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.LocalCafe,
                        contentDescription = null,
                        tint = Color(0xFF2A1A00),
                        modifier = Modifier.size(38.dp)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-24).dp)
                        .padding(horizontal = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Enjoying Solo Ledger?",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "This app is free and offline forever. If it helps you manage your money, consider fueling its development with a coffee.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Feature perks row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PerkChip(Modifier.weight(1f), Icons.Filled.FavoriteBorder, "Support", cardBg, accent, textSecondary)
                        PerkChip(Modifier.weight(1f), Icons.Filled.NewReleases, "New Features", cardBg, accent, textSecondary)
                        PerkChip(Modifier.weight(1f), Icons.Filled.BugReport, "Fewer Bugs", cardBg, accent, textSecondary)
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    // Buy Me a Coffee button
                    Button(
                        onClick = {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://buymeacoffee.com/mkr_infinity")))
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = coffeeGold, contentColor = Color(0xFF2A1A00))
                    ) {
                        Icon(Icons.Filled.LocalCafe, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Buy Me a Coffee", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Star on GitHub
                    Button(
                        onClick = {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/mkr-infinity/Solo-Ledger")))
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = cardBg, contentColor = textPrimary)
                    ) {
                        Icon(Icons.Filled.Star, contentDescription = null, modifier = Modifier.size(18.dp), tint = coffeeGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Star on GitHub", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(onClick = onMaybeLater) {
                        Text("Maybe later", style = MaterialTheme.typography.labelLarge, color = textSecondary.copy(alpha = 0.7f))
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun PerkChip(
    modifier: Modifier,
    icon: ImageVector,
    label: String,
    cardBg: Color,
    accent: Color,
    textSecondary: Color
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(cardBg)
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = textSecondary,
            textAlign = TextAlign.Center,
            fontSize = 10.sp,
            maxLines = 1
        )
    }
}
