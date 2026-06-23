package com.solo.ledger.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solo.ledger.core.ServiceLocator
import com.solo.ledger.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.*

enum class SortMode(val label: String) { DATE_DESC("Newest"), DATE_ASC("Oldest"), AMOUNT_DESC("Highest") }

class HistoryViewModel : ViewModel() {
    private val repo = ServiceLocator.ledgerRepository
    private val query = MutableStateFlow("")
    private val sort = MutableStateFlow(SortMode.DATE_DESC)
    private val categoryFilter = MutableStateFlow<String?>(null)

    val search = query.asStateFlow()
    val sortMode = sort.asStateFlow()
    val activeCategory = categoryFilter.asStateFlow()

    fun setQuery(q: String) { query.value = q }
    fun setSort(s: SortMode) { sort.value = s }
    fun setCategory(c: String?) { categoryFilter.value = c }

    val categories: StateFlow<List<String>> =
        repo.categories().map { it.map { c -> c.name } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val grouped: StateFlow<Map<Long, List<ExpenseEntity>>> =
        combine(repo.activeExpenses(), query, sort, categoryFilter) { list, q, s, cat ->
            val filtered = list.filter { e ->
                (q.isBlank() || e.title.contains(q, true) || e.category.contains(q, true) || e.notes.contains(q, true)) &&
                    (cat == null || e.category == cat)
            }
            val sorted = when (s) {
                SortMode.DATE_DESC -> filtered.sortedWith(compareByDescending<ExpenseEntity> { it.dateEpochDay }.thenByDescending { it.timeMinutes })
                SortMode.DATE_ASC -> filtered.sortedWith(compareBy<ExpenseEntity> { it.dateEpochDay }.thenBy { it.timeMinutes })
                SortMode.AMOUNT_DESC -> filtered.sortedByDescending { it.amount }
            }
            sorted.groupBy { it.dateEpochDay }
                .let { m -> if (s == SortMode.DATE_ASC) m.toSortedMap() else m.toSortedMap(compareByDescending { it }) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())
}
