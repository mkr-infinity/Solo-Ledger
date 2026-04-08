package com.mkr.soloLedger.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.mkr.soloLedger.R
import com.mkr.soloLedger.databinding.ActivityOnboardingBinding
import com.mkr.soloLedger.ui.MainActivity
import com.mkr.soloLedger.utils.Constants

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    private val pages = listOf(
        OnboardingPage(
            "Track Your Spending",
            "Easily record your daily expenses and stay on top of every rupee you spend.",
            R.drawable.ic_onboarding_1
        ),
        OnboardingPage(
            "Control Your Budget",
            "Set monthly budgets, stay within limits and manage your money smarter.",
            R.drawable.ic_onboarding_2
        ),
        OnboardingPage(
            "Smart Insights",
            "Understand your spending habits clearly with beautiful charts and insights.",
            R.drawable.ic_onboarding_3
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = OnboardingAdapter(pages)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabIndicator, binding.viewPager) { _, _ -> }.attach()

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == pages.size - 1) {
                    binding.btnNext.text = getString(R.string.get_started)
                } else {
                    binding.btnNext.text = getString(R.string.next)
                }
            }
        })

        binding.btnSkip.setOnClickListener { finishOnboarding() }

        binding.btnNext.setOnClickListener {
            val current = binding.viewPager.currentItem
            if (current < pages.size - 1) {
                binding.viewPager.currentItem = current + 1
            } else {
                finishOnboarding()
            }
        }
    }

    private fun finishOnboarding() {
        getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE)
            .edit()
            .putBoolean(Constants.PREF_ONBOARDING_DONE, true)
            .apply()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
