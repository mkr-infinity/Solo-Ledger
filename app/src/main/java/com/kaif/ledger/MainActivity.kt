package com.kaif.ledger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.kaif.ledger.ui.navigation.NavGraph
import com.kaif.ledger.ui.theme.SoloLedgerTheme
import com.kaif.ledger.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val currentTheme by settingsViewModel.currentTheme.collectAsState()

            SoloLedgerTheme(themeType = currentTheme) {
                NavGraph()
            }
        }
    }
}
