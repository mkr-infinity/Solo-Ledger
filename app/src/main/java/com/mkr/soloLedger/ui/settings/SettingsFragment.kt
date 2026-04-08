package com.mkr.soloLedger.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mkr.soloLedger.R
import com.mkr.soloLedger.databinding.FragmentSettingsBinding
import com.mkr.soloLedger.utils.SecurityManager
import com.mkr.soloLedger.viewmodel.DashboardViewModel
import com.mkr.soloLedger.viewmodel.ExpenseViewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val dashboardViewModel: DashboardViewModel by viewModels()
    private val expenseViewModel: ExpenseViewModel by viewModels()
    private lateinit var securityManager: SecurityManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        securityManager = SecurityManager(requireContext())

        binding.switchAppLock.isChecked = securityManager.isPinSet()
        binding.switchFingerprint.isEnabled = securityManager.isBiometricAvailable()

        dashboardViewModel.userProfile.observe(viewLifecycleOwner) { profile ->
            profile?.let {
                binding.switchFingerprint.isChecked = it.isFingerprintEnabled
            }
        }

        binding.switchAppLock.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                findNavController().navigate(R.id.action_settings_to_security)
            } else {
                securityManager.clearPin()
            }
        }

        binding.btnSetupPin.setOnClickListener {
            findNavController().navigate(R.id.action_settings_to_security)
        }

        binding.btnImportExport.setOnClickListener {
            findNavController().navigate(R.id.action_settings_to_importExport)
        }

        binding.btnClearData.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Clear All Data")
                .setMessage("This will permanently delete all your expenses. This cannot be undone.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Clear") { _, _ ->
                    expenseViewModel.allExpenses.value?.forEach { expenseViewModel.delete(it) }
                    Toast.makeText(requireContext(), "All data cleared", Toast.LENGTH_SHORT).show()
                }
                .show()
        }

        binding.btnAbout.setOnClickListener {
            findNavController().navigate(R.id.action_settings_to_about)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
