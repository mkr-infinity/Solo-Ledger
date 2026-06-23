package com.solo.ledger.ui.screens.quickadd

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solo.ledger.core.ServiceLocator
import com.solo.ledger.data.local.entity.CategoryEntity
import com.solo.ledger.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class QuickAddViewModel : ViewModel() {
    private val repo = ServiceLocator.ledgerRepository
    val categories: StateFlow<List<CategoryEntity>> =
        repo.categories().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val settings = ServiceLocator.settingsRepository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), com.solo.ledger.data.datastore.AppSettings())

    fun save(title: String, amount: Double, category: String, date: LocalDate, time: LocalTime,
             notes: String, attachment: String?, onDone: () -> Unit) = viewModelScope.launch {
        repo.addExpense(
            ExpenseEntity(
                title = title.ifBlank { category },
                amount = amount,
                category = category,
                dateEpochDay = date.toEpochDay(),
                timeMinutes = time.hour * 60 + time.minute,
                notes = notes,
                attachmentUri = attachment
            )
        )
        onDone()
    }
}
