package com.solo.ledger.ui.screens.support

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val coffeeYellow = Color(0xFFFFDD00)
    val coffeeDark = Color(0xFF0D0C22)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Support",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Buy Me a Coffee - hero card
            item {
                Card(
                    onClick = {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://buymeacoffee.com/mkr_infinity")))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = coffeeYellow),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(coffeeDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.LocalCafe,
                                contentDescription = null,
                                tint = coffeeYellow,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Buy Me a Coffee",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = coffeeDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Your support keeps this project alive",
                            style = MaterialTheme.typography.bodySmall,
                            color = coffeeDark.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // GitHub Star
            item {
                SupportActionCard(
                    icon = Icons.Filled.Star,
                    title = "Star on GitHub",
                    subtitle = "Show your support with a star",
                    onClick = {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/mkr-infinity/Solo-Ledger")))
                    }
                )
            }

            // Report Bug
            item {
                SupportActionCard(
                    icon = Icons.Filled.BugReport,
                    title = "Report a Bug",
                    subtitle = "Help improve the app",
                    onClick = {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/mkr-infinity/Solo-Ledger/issues/new")))
                    }
                )
            }

            // Feature Request
            item {
                SupportActionCard(
                    icon = Icons.Filled.Lightbulb,
                    title = "Request Feature",
                    subtitle = "Suggest new ideas",
                    onClick = {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/mkr-infinity/Solo-Ledger/issues/new")))
                    }
                )
            }

            // Share
            item {
                SupportActionCard(
                    icon = Icons.Filled.Share,
                    title = "Share with Friends",
                    subtitle = "Spread the word",
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "Check out Solo Ledger - a premium offline budget tracker!\nhttps://github.com/mkr-infinity/Solo-Ledger")
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, null))
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SupportActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.Filled.OpenInNew,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
