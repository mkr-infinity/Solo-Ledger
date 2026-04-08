package com.mkr.soloLedger.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mkr.soloLedger.R
import com.mkr.soloLedger.adapters.ExpenseAdapter
import com.mkr.soloLedger.databinding.FragmentDashboardBinding
import com.mkr.soloLedger.utils.formatCurrency
import com.mkr.soloLedger.viewmodel.CategoryViewModel
import com.mkr.soloLedger.viewmodel.DashboardViewModel

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val dashboardViewModel: DashboardViewModel by viewModels()
    private val categoryViewModel: CategoryViewModel by viewModels()
    private lateinit var expenseAdapter: ExpenseAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        expenseAdapter = ExpenseAdapter(
            onItemClick = { expense ->
                val bundle = Bundle().apply { putLong("expenseId", expense.id) }
                findNavController().navigate(R.id.action_dashboard_to_addExpense, bundle)
            }
        )
        binding.rvRecentExpenses.adapter = expenseAdapter

        observeData()

        binding.fabAddExpense.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_addExpense)
        }
    }

    private fun observeData() {
        dashboardViewModel.userProfile.observe(viewLifecycleOwner) { profile ->
            profile?.let {
                binding.tvWelcome.text = "Hello, ${it.name}! 👋"
                val currency = it.currency
                expenseAdapter = ExpenseAdapter(currency = currency,
                    onItemClick = { expense ->
                        val bundle = Bundle().apply { putLong("expenseId", expense.id) }
                        findNavController().navigate(R.id.action_dashboard_to_addExpense, bundle)
                    }
                )
                binding.rvRecentExpenses.adapter = expenseAdapter

                dashboardViewModel.currentMonthExpenses.observe(viewLifecycleOwner) { expenses ->
                    val total = expenses.sumOf { e -> e.amount }
                    binding.tvTotalSpent.text = total.formatCurrency(currency)

                    val budget = it.monthlyBudget
                    if (budget > 0) {
                        binding.tvBudget.text = "of ${budget.formatCurrency(currency)}"
                        val percentage = ((total / budget) * 100).toInt().coerceIn(0, 100)
                        binding.progressBudget.progress = percentage
                        binding.tvBudgetPercentage.text = "$percentage%"
                    } else {
                        binding.tvBudget.text = "No budget set"
                        binding.progressBudget.progress = 0
                        binding.tvBudgetPercentage.text = "—"
                    }

                    binding.tvInsight.text = dashboardViewModel.generateInsight(total, budget)
                }
            }
        }

        dashboardViewModel.recentExpenses.observe(viewLifecycleOwner) { expenses ->
            expenseAdapter.submitList(expenses)
            binding.tvNoExpenses.visibility = if (expenses.isEmpty()) View.VISIBLE else View.GONE
        }

        categoryViewModel.allCategories.observe(viewLifecycleOwner) { categories ->
            expenseAdapter.setCategories(categories)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
