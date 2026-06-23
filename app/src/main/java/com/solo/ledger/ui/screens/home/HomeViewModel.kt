package com.solo.ledger.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solo.ledger.core.ServiceLocator
import com.solo.ledger.data.local.entity.ExpenseEntity
import com.solo.ledger.domain.model.BudgetOverview
import com.solo.ledger.domain.model.CategorySummary
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.YearMonth

data class HomeState(
    val overview: BudgetOverview = BudgetOverview(0.0, 0.0, 0.0, 0.0),
    val recent: List<ExpenseEntity> = emptyList(),
    val categories: List<CategorySummary> = emptyList(),
    val monthlySeries: List<Float> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel : ViewModel() {
    private val repo = ServiceLocator.ledgerRepository
    private val settings = ServiceLocator.settingsRepository.settings

    private val month = YearMonth.now()
    private val start = month.atDay(1).toEpochDay()
    private val end = month.atEndOfMonth().toEpochDay()

    val state: StateFlow<HomeState> = combine(
        settings,
        repo.totalInRange(start, end),
        repo.activeExpenses(),
        repo.categorySummary(start, end)
    ) { s, used, all, cats ->
        val daysElapsed = (LocalDate.now().dayOfMonth).coerceAtLeast(1)
        val series = (1..month.lengthOfMonth()).map { day ->
            all.filter { it.dateEpochDay == month.atDay(day).toEpochDay() }
                .sumOf { it.amount }.toFloat()
        }
        HomeState(
            overview = BudgetOverview(
                monthlyBudget = s.monthlyBudget,
                used = used,
                remaining = (s.monthlyBudget - used),
                dailyAverage = used / daysElapsed
            ),
            recent = all.take(5),
            categories = cats,
            monthlySeries = series
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeState())
}
