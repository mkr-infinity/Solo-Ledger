package com.mkr.soloLedger.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mkr.soloLedger.R
import com.mkr.soloLedger.data.entities.UserProfile
import com.mkr.soloLedger.databinding.FragmentProfileBinding
import com.mkr.soloLedger.utils.Constants
import com.mkr.soloLedger.viewmodel.DashboardViewModel

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val dashboardViewModel: DashboardViewModel by viewModels()
    private var currentProfile: UserProfile? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCurrencySpinner()

        dashboardViewModel.userProfile.observe(viewLifecycleOwner) { profile ->
            profile?.let {
                currentProfile = it
                binding.etProfileName.setText(it.name)
                binding.etMonthlyBudget.setText(it.monthlyBudget.toString())
                binding.switchDarkTheme.isChecked = it.themeMode == 1

                val currencyPos = Constants.CURRENCIES.indexOf(it.currency)
                if (currencyPos >= 0) binding.spinnerCurrency.setSelection(currencyPos)
            }
        }

        binding.switchDarkTheme.setOnCheckedChangeListener { _, isChecked ->
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        binding.btnSaveProfile.setOnClickListener { saveProfile() }

        binding.btnGoToSettings.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_settings)
        }
        binding.btnGoToCategories.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_categories)
        }
    }

    private fun setupCurrencySpinner() {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, Constants.CURRENCIES)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCurrency.adapter = adapter
    }

    private fun saveProfile() {
        val name = binding.etProfileName.text.toString().trim()
        val budgetStr = binding.etMonthlyBudget.text.toString().trim()
        val budget = budgetStr.toDoubleOrNull() ?: 0.0
        val currency = Constants.CURRENCIES[binding.spinnerCurrency.selectedItemPosition]
        val themeMode = if (binding.switchDarkTheme.isChecked) 1 else 0

        val profile = currentProfile?.copy(
            name = name.ifEmpty { "User" },
            currency = currency,
            monthlyBudget = budget,
            themeMode = themeMode
        ) ?: UserProfile(
            name = name.ifEmpty { "User" },
            currency = currency,
            monthlyBudget = budget,
            themeMode = themeMode
        )

        dashboardViewModel.updateUserProfile(profile)
        Toast.makeText(requireContext(), "Profile saved!", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
