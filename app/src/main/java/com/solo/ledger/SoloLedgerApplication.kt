package com.solo.ledger

import android.app.Application
import com.solo.ledger.data.AppContainer

class SoloLedgerApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
