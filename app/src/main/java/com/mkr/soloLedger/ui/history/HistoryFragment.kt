package com.mkr.soloLedger.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mkr.soloLedger.R
import com.mkr.soloLedger.adapters.ExpenseAdapter
import com.mkr.soloLedger.databinding.FragmentHistoryBinding
import com.mkr.soloLedger.viewmodel.CategoryViewModel
import com.mkr.soloLedger.viewmodel.ExpenseViewModel

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val expenseViewModel: ExpenseViewModel by viewModels()
    private val categoryViewModel: CategoryViewModel by viewModels()
    private lateinit var expenseAdapter: ExpenseAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        expenseAdapter = ExpenseAdapter(
            onItemClick = { expense ->
                val bundle = Bundle().apply { putLong("expenseId", expense.id) }
                findNavController().navigate(R.id.action_history_to_addExpense, bundle)
            },
            onItemLongClick = { expense ->
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Delete Expense")
                    .setMessage("Delete '${expense.title}'?")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Delete") { _, _ ->
                        expenseViewModel.delete(expense)
                    }
                    .show()
                true
            }
        )
        binding.rvExpenses.adapter = expenseAdapter

        observeData()

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    observeAllExpenses()
                } else {
                    expenseViewModel.searchExpenses(newText).observe(viewLifecycleOwner) { expenses ->
                        expenseAdapter.submitList(expenses)
                        binding.tvNoExpenses.visibility = if (expenses.isEmpty()) View.VISIBLE else View.GONE
                    }
                }
                return true
            }
        })
    }

    private fun observeData() {
        observeAllExpenses()
        categoryViewModel.allCategories.observe(viewLifecycleOwner) { categories ->
            expenseAdapter.setCategories(categories)
        }
    }

    private fun observeAllExpenses() {
        expenseViewModel.allExpenses.observe(viewLifecycleOwner) { expenses ->
            expenseAdapter.submitList(expenses)
            binding.tvNoExpenses.visibility = if (expenses.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
