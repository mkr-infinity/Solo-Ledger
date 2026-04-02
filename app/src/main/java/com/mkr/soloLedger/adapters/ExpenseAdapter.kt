package com.mkr.soloLedger.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mkr.soloLedger.data.entities.Category
import com.mkr.soloLedger.data.entities.Expense
import com.mkr.soloLedger.databinding.ItemExpenseBinding
import com.mkr.soloLedger.utils.formatCurrency
import com.mkr.soloLedger.utils.toFormattedDate

class ExpenseAdapter(
    private val currency: String = "USD",
    private val onItemClick: (Expense) -> Unit = {},
    private val onItemLongClick: (Expense) -> Boolean = { false }
) : ListAdapter<Expense, ExpenseAdapter.ViewHolder>(DiffCallback()) {

    private var categories: List<Category> = emptyList()

    fun setCategories(cats: List<Category>) {
        categories = cats
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemExpenseBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(expense: Expense) {
            binding.tvExpenseTitle.text = expense.title
            binding.tvExpenseAmount.text = expense.amount.formatCurrency(currency)
            binding.tvExpenseDate.text = expense.date.toFormattedDate()

            val category = categories.find { it.id == expense.categoryId }
            binding.tvExpenseCategory.text = category?.name ?: "Other"

            try {
                val color = android.graphics.Color.parseColor(category?.color ?: "#D3D3D3")
                binding.viewCategoryColor.setBackgroundColor(color)
            } catch (e: IllegalArgumentException) {
                binding.viewCategoryColor.setBackgroundColor(android.graphics.Color.LTGRAY)
            }

            binding.root.setOnClickListener { onItemClick(expense) }
            binding.root.setOnLongClickListener { onItemLongClick(expense) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<Expense>() {
        override fun areItemsTheSame(oldItem: Expense, newItem: Expense) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Expense, newItem: Expense) = oldItem == newItem
    }
}
