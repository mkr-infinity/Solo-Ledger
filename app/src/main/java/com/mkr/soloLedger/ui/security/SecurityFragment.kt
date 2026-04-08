package com.mkr.soloLedger.ui.security

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mkr.soloLedger.databinding.FragmentSecurityBinding
import com.mkr.soloLedger.utils.SecurityManager

class SecurityFragment : Fragment() {

    private var _binding: FragmentSecurityBinding? = null
    private val binding get() = _binding!!

    private lateinit var securityManager: SecurityManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecurityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        securityManager = SecurityManager(requireContext())

        binding.switchBiometric.isEnabled = securityManager.isBiometricAvailable()
        binding.switchBiometric.isChecked = false

        binding.btnSavePin.setOnClickListener {
            val pin = binding.etPin.text.toString()
            val confirm = binding.etConfirmPin.text.toString()

            when {
                pin.length < 4 -> binding.tilPin.error = "PIN must be at least 4 digits"
                pin != confirm -> binding.tilConfirmPin.error = "PINs do not match"
                else -> {
                    binding.tilPin.error = null
                    binding.tilConfirmPin.error = null
                    securityManager.savePin(pin)
                    Toast.makeText(requireContext(), "PIN saved successfully!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
