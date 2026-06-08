package com.solo.ledger.ui.screens.comingsoon

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Backup
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material.icons.outlined.FamilyRestroom
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.solo.ledger.ui.components.cards.ComingSoonCard
import com.solo.ledger.ui.components.shared.SectionHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComingSoonScreen(navController: NavController) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Coming Soon",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(Modifier.height(4.dp))
                SectionHeader(
                    title = "Features in Development",
                    subtitle = "These features are actively being worked on"
                )
            }

            item {
                ComingSoonCard(
                    title = "Login System",
                    description = "Secure account creation and authentication",
                    icon = Icons.Outlined.Lock,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                ComingSoonCard(
                    title = "Cloud Sync",
                    description = "Sync your data across devices seamlessly",
                    icon = Icons.Outlined.CloudUpload,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                ComingSoonCard(
                    title = "Online Backup",
                    description = "Automatic cloud backup for peace of mind",
                    icon = Icons.Outlined.Backup,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                ComingSoonCard(
                    title = "Multi-Device Sync",
                    description = "Access your finances from any device",
                    icon = Icons.Outlined.Devices,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                ComingSoonCard(
                    title = "Shared Budgets",
                    description = "Collaborate on budgets with others",
                    icon = Icons.Outlined.Group,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                ComingSoonCard(
                    title = "Family Accounts",
                    description = "Manage finances for your entire family",
                    icon = Icons.Outlined.FamilyRestroom,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                ComingSoonCard(
                    title = "AI Spending Insights",
                    description = "Smart analysis of your spending patterns",
                    icon = Icons.Outlined.AutoAwesome,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                ComingSoonCard(
                    title = "Bank Account Integration",
                    description = "Connect and sync your bank accounts",
                    icon = Icons.Outlined.AccountBalance,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                ComingSoonCard(
                    title = "UPI Integration",
                    description = "Track UPI transactions automatically",
                    icon = Icons.Outlined.Payment,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                ComingSoonCard(
                    title = "Receipt OCR Scanner",
                    description = "Scan receipts to auto-add transactions",
                    icon = Icons.Outlined.DocumentScanner,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // ── Footer ────────────────────────────────────────────────────────
            item {
                Spacer(Modifier.height(8.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Have a feature request?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                    Button(
                        onClick = {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://github.com/mkr-infinity/Solo-Ledger/issues")
                                )
                            )
                        }
                    ) {
                        Text("Submit on GitHub")
                    }
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}
