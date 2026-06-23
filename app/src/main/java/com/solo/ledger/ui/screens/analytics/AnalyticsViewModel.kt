package com.solo.ledger.ui.screens.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solo.ledger.core.ServiceLocator
import com.solo.ledger.domain.model.CategorySummary
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.YearMonth

enum class RangeMode(val days: Int, val label: String) {
    D7(7, "7 days"), D30(30, "30 days"), D90(90, "90 days")
}

data class AnalyticsState(
    val mode: RangeMode = RangeMode.D30,
    val total: Double = 0.0,
    val categories: List<CategorySummary> = emptyList(),
    val dailySeries: List<Float> = emptyList(),
    val monthlySeries: List<Pair<String, Float>> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
class AnalyticsViewModel : ViewModel() {
    private val repo = ServiceLocator.ledgerRepository
    private val mode = MutableStateFlow(RangeMode.D30)
    fun setMode(m: RangeMode) { mode.value = m }

    val state: StateFlow<AnalyticsState> = mode.flatMapLatest { m ->
        val end = LocalDate.now()
        val start = end.minusDays((m.days - 1).toLong())
        combine(
            repo.totalInRange(start.toEpochDay(), end.toEpochDay()),
            repo.categorySummary(start.toEpochDay(), end.toEpochDay()),
            repo.expensesInRange(start.toEpochDay(), end.toEpochDay())
        ) { total, cats, list ->
            val daily = (0 until m.days).map { i ->
                val day = start.plusDays(i.toLong()).toEpochDay()
                list.filter { it.dateEpochDay == day }.sumOf { it.amount }.toFloat()
            }
            val monthly = (0..5).map { i ->
                val ym = YearMonth.now().minusMonths((5 - i).toLong())
                val s = ym.atDay(1).toEpochDay(); val e = ym.atEndOfMonth().toEpochDay()
                ym.month.name.take(3) to list.filter { it.dateEpochDay in s..e }.sumOf { it.amount }.toFloat()
            }
            AnalyticsState(m, total, cats, daily, monthly)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AnalyticsState())
}
