package com.mkr.soloLedger.ui.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mkr.soloLedger.BuildConfig
import com.mkr.soloLedger.databinding.FragmentAboutBinding

class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvVersion.text = "Version ${BuildConfig.VERSION_NAME}"

        binding.btnGithub.setOnClickListener {
            openUrl("https://github.com/mkr-infinity")
        }
        binding.btnInstagram.setOnClickListener {
            openUrl("https://instagram.com/mkr_infinity")
        }
        binding.btnWebsite.setOnClickListener {
            openUrl("https://mkr-infinity.github.io")
        }
        binding.btnSupport.setOnClickListener {
            openUrl("https://supportmkr.netlify.app")
        }
    }

    private fun openUrl(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
