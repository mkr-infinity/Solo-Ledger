package com.mkr.soloLedger.utils

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class SecurityManager(private val context: Context) {

    private val prefs = context.getSharedPreferences("security_prefs", Context.MODE_PRIVATE)
    private val KEY_ALIAS = "solo_ledger_pin_key"

    fun savePin(pin: String) {
        prefs.edit().putString("pin", hashPin(pin)).apply()
    }

    fun verifyPin(pin: String): Boolean {
        val stored = prefs.getString("pin", null) ?: return false
        return stored == hashPin(pin)
    }

    fun clearPin() {
        prefs.edit().remove("pin").apply()
    }

    fun isPinSet(): Boolean = prefs.getString("pin", null) != null

    private fun hashPin(pin: String): String {
        val bytes = pin.toByteArray()
        val digest = java.security.MessageDigest.getInstance("SHA-256")
        return Base64.encodeToString(digest.digest(bytes), Base64.DEFAULT)
    }

    fun isBiometricAvailable(): Boolean {
        val biometricManager = BiometricManager.from(context)
        return biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK) ==
                BiometricManager.BIOMETRIC_SUCCESS
    }

    fun showBiometricPrompt(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(context)
        val biometricPrompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError(errString.toString())
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onError("Authentication failed")
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Authenticate")
            .setSubtitle("Use your fingerprint to access Solo Ledger")
            .setNegativeButtonText("Use PIN")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}
