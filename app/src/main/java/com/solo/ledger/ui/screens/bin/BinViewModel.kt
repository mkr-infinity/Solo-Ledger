package com.solo.ledger.ui.screens.bin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solo.ledger.core.ServiceLocator
import com.solo.ledger.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BinViewModel : ViewModel() {
    private val repo = ServiceLocator.ledgerRepository
    val items: StateFlow<List<ExpenseEntity>> =
        repo.deletedExpenses().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    fun restore(id: Long) = viewModelScope.launch { repo.restore(id) }
    fun deleteForever(id: Long) = viewModelScope.launch { repo.hardDelete(id) }
    fun clearAll() = viewModelScope.launch { repo.clearBin() }
}
