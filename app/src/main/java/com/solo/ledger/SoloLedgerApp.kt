package com.solo.ledger

import android.app.Application
import com.solo.ledger.core.ServiceLocator

class SoloLedgerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)
    }
}
