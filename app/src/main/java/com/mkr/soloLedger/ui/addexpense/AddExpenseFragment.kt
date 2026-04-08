package com.mkr.soloLedger.ui.addexpense

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mkr.soloLedger.data.entities.Category
import com.mkr.soloLedger.data.entities.Expense
import com.mkr.soloLedger.databinding.FragmentAddExpenseBinding
import com.mkr.soloLedger.utils.Constants
import com.mkr.soloLedger.viewmodel.CategoryViewModel
import com.mkr.soloLedger.viewmodel.ExpenseViewModel
import java.util.Calendar

class AddExpenseFragment : Fragment() {

    private var _binding: FragmentAddExpenseBinding? = null
    private val binding get() = _binding!!

    private val expenseViewModel: ExpenseViewModel by viewModels()
    private val categoryViewModel: CategoryViewModel by viewModels()

    private var categories: List<Category> = emptyList()
    private var selectedCategoryId: Long = 1L
    private var selectedDate: Long = System.currentTimeMillis()
    private var editingExpenseId: Long = -1L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editingExpenseId = arguments?.getLong("expenseId", -1L) ?: -1L

        setupDatePicker()
        setupAutoCategory()
        observeCategories()

        binding.btnSaveExpense.setOnClickListener { saveExpense() }
    }

    private fun setupDatePicker() {
        updateDateDisplay()
        binding.btnPickDate.setOnClickListener {
            val cal = Calendar.getInstance()
            cal.timeInMillis = selectedDate
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    cal.set(year, month, day)
                    selectedDate = cal.timeInMillis
                    updateDateDisplay()
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun updateDateDisplay() {
        val cal = Calendar.getInstance()
        cal.timeInMillis = selectedDate
        binding.tvSelectedDate.text = "%02d/%02d/%d".format(
            cal.get(Calendar.DAY_OF_MONTH),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.YEAR)
        )
    }

    private fun setupAutoCategory() {
        binding.etExpenseTitle.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrBlank() && categories.isNotEmpty()) {
                    val suggested = expenseViewModel.suggestCategory(s.toString())
                    val cat = categories.find { it.name == suggested }
                    cat?.let {
                        val pos = categories.indexOf(it)
                        binding.spinnerCategory.setSelection(pos)
                        selectedCategoryId = it.id
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun observeCategories() {
        categoryViewModel.allCategories.observe(viewLifecycleOwner) { cats ->
            categories = cats
            val names = cats.map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, names)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerCategory.adapter = adapter

            binding.spinnerCategory.onItemSelectedListener =
                object : android.widget.AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: android.widget.AdapterView<*>?,
                        view: View?, position: Int, id: Long
                    ) {
                        selectedCategoryId = cats[position].id
                    }
                    override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
                }
        }
    }

    private fun saveExpense() {
        val title = binding.etExpenseTitle.text.toString().trim()
        val amountStr = binding.etExpenseAmount.text.toString().trim()
        val notes = binding.etExpenseNotes.text.toString().trim()

        if (title.isEmpty()) {
            binding.tilTitle.error = "Please enter a title"
            return
        }
        binding.tilTitle.error = null

        val amount = amountStr.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            binding.tilAmount.error = "Please enter a valid amount"
            return
        }
        binding.tilAmount.error = null

        val recurringType = when (binding.rgRecurring.checkedRadioButtonId) {
            binding.rbWeekly.id -> Constants.RECURRING_WEEKLY
            binding.rbMonthly.id -> Constants.RECURRING_MONTHLY
            binding.rbYearly.id -> Constants.RECURRING_YEARLY
            else -> Constants.RECURRING_NONE
        }
        val isRecurring = recurringType != Constants.RECURRING_NONE

        val expense = Expense(
            id = if (editingExpenseId > 0) editingExpenseId else 0,
            title = title,
            amount = amount,
            categoryId = selectedCategoryId,
            date = selectedDate,
            notes = notes,
            isRecurring = isRecurring,
            recurringType = recurringType
        )

        if (editingExpenseId > 0) {
            expenseViewModel.update(expense)
            Toast.makeText(requireContext(), "Expense updated!", Toast.LENGTH_SHORT).show()
        } else {
            expenseViewModel.insert(expense)
            Toast.makeText(requireContext(), "Expense saved!", Toast.LENGTH_SHORT).show()
        }
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
