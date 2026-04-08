package com.mkr.soloLedger.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mkr.soloLedger.data.entities.Category
import com.mkr.soloLedger.databinding.ItemCategoryBinding

class CategoryAdapter(
    private val onItemClick: (Category) -> Unit = {},
    private val onEditClick: (Category) -> Unit = {},
    private val onDeleteClick: (Category) -> Unit = {}
) : ListAdapter<Category, CategoryAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {
            binding.tvCategoryName.text = category.name

            try {
                val color = android.graphics.Color.parseColor(category.color)
                binding.viewCategoryColor.setBackgroundColor(color)
            } catch (e: IllegalArgumentException) {
                binding.viewCategoryColor.setBackgroundColor(android.graphics.Color.LTGRAY)
            }

            binding.btnEditCategory.visibility =
                if (category.isDefault) android.view.View.GONE else android.view.View.VISIBLE
            binding.btnDeleteCategory.visibility =
                if (category.isDefault) android.view.View.GONE else android.view.View.VISIBLE

            binding.root.setOnClickListener { onItemClick(category) }
            binding.btnEditCategory.setOnClickListener { onEditClick(category) }
            binding.btnDeleteCategory.setOnClickListener { onDeleteClick(category) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Category, newItem: Category) = oldItem == newItem
    }
}
