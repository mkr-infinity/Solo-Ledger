package com.solo.ledger.ui.screens.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solo.ledger.core.ServiceLocator
import com.solo.ledger.data.local.entity.CategoryEntity
import com.solo.ledger.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class EditExpenseViewModel : ViewModel() {
    private val repo = ServiceLocator.ledgerRepository
    val categories: StateFlow<List<CategoryEntity>> =
        repo.categories().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _expense = MutableStateFlow<ExpenseEntity?>(null)
    val expense = _expense.asStateFlow()

    fun load(id: Long) = viewModelScope.launch { _expense.value = repo.getExpense(id) }
    fun update(e: ExpenseEntity, onDone: () -> Unit) = viewModelScope.launch { repo.updateExpense(e); onDone() }
    fun delete(id: Long, onDone: () -> Unit) = viewModelScope.launch { repo.softDelete(id); onDone() }
}
