package com.solo.ledger

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.solo.ledger.data.datastore.AppSettings
import com.solo.ledger.data.datastore.SettingsDataStore
import com.solo.ledger.domain.usecase.DeleteTransactionUseCase
import com.solo.ledger.ui.navigation.AppNavigation
import com.solo.ledger.ui.theme.NavigationStyle
import com.solo.ledger.ui.screens.onboarding.OnboardingScreen
import com.solo.ledger.ui.screens.quickadd.QuickAddBottomSheet
import com.solo.ledger.ui.theme.SoloLedgerTheme
import com.solo.ledger.util.MarkdownParser
import com.solo.ledger.util.ReleaseInfo
import com.solo.ledger.util.UpdateChecker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsDataStore: SettingsDataStore

    @Inject
    lateinit var deleteTransactionUseCase: DeleteTransactionUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        lifecycleScope.launch {
            deleteTransactionUseCase.purgeExpired()
        }

        setContent {
            val settings by settingsDataStore.settings.collectAsStateWithLifecycle(
                initialValue = AppSettings()
            )

            SoloLedgerTheme(themeId = settings.activeThemeId) {

                val navController = rememberNavController()
                val navigationStyle = remember(settings.navigationStyle) {
                    try {
                        NavigationStyle.valueOf(settings.navigationStyle.uppercase())
                    } catch (e: IllegalArgumentException) {
                        NavigationStyle.MATERIAL3
                    }
                }

                var showQuickAdd by remember { mutableStateOf(false) }

                if (!settings.onboardingCompleted) {
                    OnboardingScreen(
                        onComplete = {
                            lifecycleScope.launch {
                                settingsDataStore.setOnboardingCompleted(true)
                            }
                        }
                    )
                } else {
                    AppNavigation(
                        navController = navController,
                        navigationStyle = navigationStyle,
                        onQuickAddClick = { showQuickAdd = true }
                    )

                    if (showQuickAdd) {
                        QuickAddBottomSheet(
                            onDismiss = { showQuickAdd = false }
                        )
                    }
                }

                SupportPopup(
                    settings = settings,
                    onDismiss = {
                        lifecycleScope.launch {
                            settingsDataStore.setLastSupportPopupDate(System.currentTimeMillis())
                        }
                    }
                )

                UpdateCheckerDialog(
                    settings = settings,
                    onDismiss = { version ->
                        lifecycleScope.launch {
                            settingsDataStore.setDismissedUpdateVersion(version)
                            settingsDataStore.setLastUpdateCheckDate(System.currentTimeMillis())
                        }
                    }
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// SupportPopup
// ---------------------------------------------------------------------------

private const val TWO_DAYS_MS = 2L * 24 * 60 * 60 * 1000
private const val THIRTY_DAYS_MS = 30L * 24 * 60 * 60 * 1000

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportPopup(
    settings: AppSettings,
    onDismiss: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    LaunchedEffect(settings.firstInstallDate, settings.lastSupportPopupDate) {
        val now = System.currentTimeMillis()
        val installDate = settings.firstInstallDate
        val lastPopup = settings.lastSupportPopupDate
        val hasBeenInstalledLongEnough = installDate > 0L && (now - installDate) >= TWO_DAYS_MS
        val notShownRecentlyEnough = lastPopup == 0L || (now - lastPopup) >= THIRTY_DAYS_MS
        visible = hasBeenInstalledLongEnough && notShownRecentlyEnough
    }

    if (visible) {
        ModalBottomSheet(
            onDismissRequest = {
                visible = false
                onDismiss()
            },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = {
                            visible = false
                            onDismiss()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "S",
                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Solo Ledger is free, forever.",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "This app is built and maintained by a solo developer. If it’s been useful, consider buying a coffee — it keeps the project alive and growing.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://buymeacoffee.com/mkr_infinity"))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Support the Project")
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        visible = false
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Maybe Later")
                }

                Spacer(modifier = Modifier.height(4.dp))

                TextButton(
                    onClick = {
                        visible = false
                        onDismiss()
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "Turn off reminders",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// ---------------------------------------------------------------------------
// UpdateCheckerDialog
// ---------------------------------------------------------------------------

@Composable
fun UpdateCheckerDialog(
    settings: AppSettings,
    onDismiss: (version: String) -> Unit
) {
    var releaseInfo by remember { mutableStateOf<ReleaseInfo?>(null) }
    var visible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(settings.autoCheckUpdates) {
        if (!settings.autoCheckUpdates) return@LaunchedEffect

        val result = UpdateChecker.checkForUpdate()
        val info = result.getOrNull() ?: return@LaunchedEffect

        if (info.tagName != settings.dismissedUpdateVersion) {
            releaseInfo = info
            visible = true
        }
    }

    val info = releaseInfo
    if (visible && info != null) {
        AlertDialog(
            onDismissRequest = {
                visible = false
                onDismiss(info.tagName)
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "S",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(32.dp)
                            .padding(end = 8.dp)
                    )
                    Text(
                        text = "Update Available",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SuggestionChip(
                            onClick = {},
                            label = { Text("v1.0.0") }
                        )
                        Text(
                            text = " → ",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                        SuggestionChip(
                            onClick = {},
                            label = { Text("v${info.tagName}") }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (info.body.isNotBlank()) {
                        Text(
                            text = MarkdownParser.parse(info.body),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(info.htmlUrl))
                        context.startActivity(intent)
                    }
                ) {
                    Text("Download Update")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        visible = false
                        onDismiss(info.tagName)
                    }
                ) {
                    Text("Dismiss")
                }
            }
        )
    }
}
