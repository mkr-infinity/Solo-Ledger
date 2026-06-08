package com.solo.ledger.ui.screens.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solo.ledger.data.model.Category
import com.solo.ledger.data.model.Transaction
import com.solo.ledger.data.model.TransactionType
import com.solo.ledger.data.repository.CategoryRepository
import com.solo.ledger.data.datastore.SettingsDataStore
import com.solo.ledger.domain.usecase.GetTransactionsUseCase
import com.solo.ledger.ui.components.charts.DonutChartEntry
import com.solo.ledger.ui.components.charts.MonthlyData
import com.solo.ledger.ui.components.charts.TrendPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

enum class AnalyticsPeriod(val label: String) {
    THIS_MONTH("This Month"),
    LAST_3_MONTHS("3 Months"),
    LAST_6_MONTHS("6 Months"),
    THIS_YEAR("Year"),
    ALL_TIME("All Time")
}

data class AnalyticsUiState(
    val isLoading: Boolean = true,
    val selectedPeriod: AnalyticsPeriod = AnalyticsPeriod.THIS_MONTH,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val savingsRate: Float = 0f,
    val monthlyData: List<MonthlyData> = emptyList(),
    val categoryBreakdown: List<DonutChartEntry> = emptyList(),
    val trendData: List<TrendPoint> = emptyList(),
    val topCategories: List<Pair<Category, Double>> = emptyList()
)

// A small set of chart colors used to assign distinct colors to category breakdown entries.
private val chartColors = listOf(
    0xFF6C63FF, 0xFFFF6584, 0xFF43CBFF, 0xFFFFBE0B, 0xFF3BBA9C, 0xFFFF6B6B
).map { it.toLong() }

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val categoryRepository: CategoryRepository,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        loadAnalytics(AnalyticsPeriod.THIS_MONTH)
    }

    fun setPeriod(period: AnalyticsPeriod) {
        _uiState.update { it.copy(selectedPeriod = period, isLoading = true) }
        loadAnalytics(period)
    }

    private fun loadAnalytics(period: AnalyticsPeriod) {
        viewModelScope.launch {
            val (startDate, endDate) = dateRangeForPeriod(period)

            combine(
                getTransactionsUseCase.getByDateRange(startDate, endDate),
                categoryRepository.getAll()
            ) { transactions, categories ->
                Pair(transactions, categories)
            }.collect { (transactions, categories) ->
                val active = transactions.filter { !it.isDeleted }
                val categoryMap = categories.associateBy { it.id }

                val totalIncome = active
                    .filter { it.type == TransactionType.INCOME }
                    .sumOf { it.amount }

                val totalExpense = active
                    .filter { it.type == TransactionType.EXPENSE }
                    .sumOf { it.amount }

                val savingsRate = if (totalIncome > 0.0) {
                    ((totalIncome - totalExpense) / totalIncome * 100.0).toFloat()
                        .coerceIn(-100f, 100f)
                } else 0f

                val monthlyData = buildMonthlyData(active, startDate, endDate)
                val categoryBreakdown = buildCategoryBreakdown(active, categoryMap)
                val trendData = buildTrendData(active, startDate, endDate)
                val topCategories = buildTopCategories(active, categoryMap)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        totalIncome = totalIncome,
                        totalExpense = totalExpense,
                        savingsRate = savingsRate,
                        monthlyData = monthlyData,
                        categoryBreakdown = categoryBreakdown,
                        trendData = trendData,
                        topCategories = topCategories
                    )
                }
            }
        }
    }

    private fun dateRangeForPeriod(period: AnalyticsPeriod): Pair<LocalDate, LocalDate> {
        val today = LocalDate.now()
        return when (period) {
            AnalyticsPeriod.THIS_MONTH -> {
                val ym = YearMonth.now()
                Pair(ym.atDay(1), ym.atEndOfMonth())
            }
            AnalyticsPeriod.LAST_3_MONTHS -> {
                val start = YearMonth.now().minusMonths(2).atDay(1)
                Pair(start, YearMonth.now().atEndOfMonth())
            }
            AnalyticsPeriod.LAST_6_MONTHS -> {
                val start = YearMonth.now().minusMonths(5).atDay(1)
                Pair(start, YearMonth.now().atEndOfMonth())
            }
            AnalyticsPeriod.THIS_YEAR -> {
                Pair(LocalDate.of(today.year, 1, 1), LocalDate.of(today.year, 12, 31))
            }
            AnalyticsPeriod.ALL_TIME -> {
                Pair(LocalDate.of(2000, 1, 1), today.plusYears(1))
            }
        }
    }

    private fun buildMonthlyData(
        transactions: List<Transaction>,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<MonthlyData> {
        val result = mutableListOf<MonthlyData>()
        var current = YearMonth.from(startDate)
        val end = YearMonth.from(endDate)

        while (!current.isAfter(end)) {
            val monthTx = transactions.filter { YearMonth.from(it.date) == current }
            val income = monthTx.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }.toFloat()
            val expense = monthTx.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }.toFloat()
            result.add(MonthlyData(month = current.month.name.take(3), income = income, expense = expense))
            current = current.plusMonths(1)
        }
        return result
    }

    private fun buildCategoryBreakdown(
        transactions: List<Transaction>,
        categoryMap: Map<String, Category>
    ): List<DonutChartEntry> {
        val expenseByCategory = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.categoryId }
            .mapValues { (_, txList) -> txList.sumOf { it.amount } }
            .entries
            .sortedByDescending { it.value }
            .take(6)

        return expenseByCategory.mapIndexed { index, (categoryId, amount) ->
            val category = categoryMap[categoryId]
            val colorLong = chartColors[index % chartColors.size]
            DonutChartEntry(
                label = category?.name ?: "Other",
                value = amount.toFloat(),
                color = androidx.compose.ui.graphics.Color(colorLong)
            )
        }
    }

    private fun buildTrendData(
        transactions: List<Transaction>,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<TrendPoint> {
        val result = mutableListOf<TrendPoint>()
        var current = YearMonth.from(startDate)
        val end = YearMonth.from(endDate)

        while (!current.isAfter(end)) {
            val monthTx = transactions.filter { YearMonth.from(it.date) == current }
            val income = monthTx.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
            val expense = monthTx.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
            result.add(TrendPoint(label = current.month.name.take(3), value = (income - expense).toFloat()))
            current = current.plusMonths(1)
        }
        return result
    }

    private fun buildTopCategories(
        transactions: List<Transaction>,
        categoryMap: Map<String, Category>
    ): List<Pair<Category, Double>> {
        return transactions
            .filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.categoryId }
            .mapValues { (_, txList) -> txList.sumOf { it.amount } }
            .entries
            .sortedByDescending { it.value }
            .take(5)
            .mapNotNull { (categoryId, amount) ->
                categoryMap[categoryId]?.let { Pair(it, amount) }
            }
    }
}
