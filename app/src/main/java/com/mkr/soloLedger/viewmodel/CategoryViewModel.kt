package com.mkr.soloLedger.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.mkr.soloLedger.data.database.SoloLedgerDatabase
import com.mkr.soloLedger.data.entities.Category
import com.mkr.soloLedger.data.repository.SoloLedgerRepository
import kotlinx.coroutines.launch

class CategoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SoloLedgerRepository
    val allCategories: LiveData<List<Category>>

    init {
        val db = SoloLedgerDatabase.getDatabase(application)
        repository = SoloLedgerRepository(
            db.expenseDao(), db.categoryDao(), db.budgetDao(),
            db.userProfileDao(), db.recurringExpenseDao()
        )
        allCategories = repository.allCategories
    }

    fun insert(category: Category) = viewModelScope.launch {
        repository.insertCategory(category)
    }

    fun update(category: Category) = viewModelScope.launch {
        repository.updateCategory(category)
    }

    fun delete(category: Category) = viewModelScope.launch {
        repository.deleteCategory(category)
    }
}
