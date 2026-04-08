package com.mkr.soloLedger.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.mkr.soloLedger.R
import com.mkr.soloLedger.databinding.ActivitySplashBinding
import com.mkr.soloLedger.ui.MainActivity
import com.mkr.soloLedger.ui.onboarding.OnboardingActivity
import com.mkr.soloLedger.utils.Constants

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Animate logo
        val fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        binding.ivLogo.startAnimation(fadeIn)
        binding.tvAppName.startAnimation(fadeIn)
        binding.tvTagline.startAnimation(fadeIn)

        Handler(Looper.getMainLooper()).postDelayed({
            val prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE)
            val onboardingDone = prefs.getBoolean(Constants.PREF_ONBOARDING_DONE, false)
            val nextActivity = if (onboardingDone) {
                Intent(this, MainActivity::class.java)
            } else {
                Intent(this, OnboardingActivity::class.java)
            }
            startActivity(nextActivity)
            finish()
        }, 2000)
    }
}
