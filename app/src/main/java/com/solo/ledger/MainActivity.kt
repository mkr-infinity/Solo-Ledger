package com.solo.ledger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.solo.ledger.ui.SoloLedgerMainApp
import com.solo.ledger.ui.theme.SoloLedgerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            SoloLedgerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SoloLedgerMainApp()
                }
            }
        }
    }
}
