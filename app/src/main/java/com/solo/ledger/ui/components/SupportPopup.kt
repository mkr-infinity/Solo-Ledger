package com.solo.ledger.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun SupportPopup(
    onDismiss: () -> Unit,
    onMaybeLater: () -> Unit
) {
    val context = LocalContext.current
    val coffeeYellow = Color(0xFFFFDD00)
    val coffeeDark = Color(0xFF0D0C22)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = coffeeYellow
            )
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                // Close button top right
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = coffeeDark.copy(alpha = 0.6f)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Coffee cup icon
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(coffeeDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.LocalCafe,
                            contentDescription = null,
                            tint = coffeeYellow,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Enjoying Solo Ledger?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = coffeeDark,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Your support keeps this project alive and helps build new features. Consider buying the developer a coffee.",
                        style = MaterialTheme.typography.bodySmall,
                        color = coffeeDark.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Buy Me a Coffee button
                    Button(
                        onClick = {
                            context.startActivity(
                                Intent(Intent.ACTION_VIEW, Uri.parse("https://buymeacoffee.com/mkr_infinity"))
                            )
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = coffeeDark,
                            contentColor = coffeeYellow
                        )
                    ) {
                        Icon(
                            Icons.Filled.LocalCafe,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Buy Me a Coffee",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Maybe later
                    TextButton(onClick = onMaybeLater) {
                        Text(
                            "Maybe Later",
                            style = MaterialTheme.typography.labelLarge,
                            color = coffeeDark.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}
