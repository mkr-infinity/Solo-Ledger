package com.mkr.soloLedger.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.mkr.soloLedger.R
import com.mkr.soloLedger.adapters.CategoryAdapter
import com.mkr.soloLedger.data.entities.Category
import com.mkr.soloLedger.databinding.FragmentCategoriesBinding
import com.mkr.soloLedger.viewmodel.CategoryViewModel

class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    private val categoryViewModel: CategoryViewModel by viewModels()
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoryAdapter = CategoryAdapter(
            onEditClick = { category -> showEditDialog(category) },
            onDeleteClick = { category -> confirmDelete(category) }
        )
        binding.rvCategories.adapter = categoryAdapter

        categoryViewModel.allCategories.observe(viewLifecycleOwner) { categories ->
            categoryAdapter.submitList(categories)
        }

        binding.fabAddCategory.setOnClickListener { showAddDialog() }
    }

    private fun showAddDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_category, null)
        val etName = dialogView.findViewById<TextInputEditText>(R.id.etCategoryName)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add Category")
            .setView(dialogView)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Add") { _, _ ->
                val name = etName.text.toString().trim()
                if (name.isNotEmpty()) {
                    categoryViewModel.insert(Category(name = name, iconName = "ic_other", isDefault = false))
                    Toast.makeText(requireContext(), "Category added!", Toast.LENGTH_SHORT).show()
                }
            }
            .show()
    }

    private fun showEditDialog(category: Category) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_category, null)
        val etName = dialogView.findViewById<TextInputEditText>(R.id.etCategoryName)
        etName.setText(category.name)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Edit Category")
            .setView(dialogView)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Save") { _, _ ->
                val name = etName.text.toString().trim()
                if (name.isNotEmpty()) {
                    categoryViewModel.update(category.copy(name = name))
                    Toast.makeText(requireContext(), "Category updated!", Toast.LENGTH_SHORT).show()
                }
            }
            .show()
    }

    private fun confirmDelete(category: Category) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Category")
            .setMessage("Delete '${category.name}'? Expenses in this category won't be deleted.")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete") { _, _ ->
                categoryViewModel.delete(category)
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
