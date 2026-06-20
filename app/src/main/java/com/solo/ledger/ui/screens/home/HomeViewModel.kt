package com.solo.ledger.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solo.ledger.data.datastore.AppSettings
import com.solo.ledger.data.datastore.SettingsDataStore
import com.solo.ledger.data.model.Category
import com.solo.ledger.data.model.CategoryType
import com.solo.ledger.data.model.Transaction
import com.solo.ledger.data.model.TransactionType
import com.solo.ledger.data.repository.BudgetRepository
import com.solo.ledger.data.repository.CategoryRepository
import com.solo.ledger.domain.usecase.DeleteTransactionUseCase
import com.solo.ledger.domain.usecase.GetTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

data class BudgetItem(
    val category: Category,
    val spent: Double,
    val limit: Double
)

data class HomeUiState(
    val isLoading: Boolean = true,
    val balance: Double = 0.0,
    val incomeThisMonth: Double = 0.0,
    val expenseThisMonth: Double = 0.0,
    val recentTransactions: List<Pair<Transaction, Category?>> = emptyList(),
    val budgetItems: List<BudgetItem> = emptyList(),
    val insights: List<String> = emptyList(),
    val dailyAverage: Double = 0.0,
    val monthVsLastMonth: Pair<Double, Double> = 0.0 to 0.0
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val categoryRepository: CategoryRepository,
    private val budgetRepository: BudgetRepository,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    val settings: StateFlow<AppSettings> = settingsDataStore.settings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppSettings()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<HomeUiState> = combine(
        getTransactionsUseCase.getAll(),
        categoryRepository.getAll(),
        budgetRepository.getByMonth(currentMonthYear()),
        settingsDataStore.settings
    ) { allTransactions, categories, budgets, settings ->
        val active = allTransactions.filter { !it.isDeleted }
        val categoryMap = categories.associateBy { it.id }

        val today = LocalDate.now()
        val currentYM = YearMonth.now()
        val lastYM = currentYM.minusMonths(1)

        val thisMonthTxns = active.filter {
            it.date.year == currentYM.year && it.date.monthValue == currentYM.monthValue
        }
        val lastMonthTxns = active.filter {
            it.date.year == lastYM.year && it.date.monthValue == lastYM.monthValue
        }

        val incomeThisMonth = thisMonthTxns.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val expenseThisMonth = thisMonthTxns.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        val incomeLastMonth = lastMonthTxns.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val expenseLastMonth = lastMonthTxns.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

        val totalIncome = active.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val totalExpense = active.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        val balance = totalIncome - totalExpense

        val recentTransactions = active
            .sortedByDescending { it.date.atTime(it.time) }
            .take(5)
            .map { txn -> txn to categoryMap[txn.categoryId] }

        val daysElapsed = today.dayOfMonth.coerceAtLeast(1)
        val dailyAverage = expenseThisMonth / daysElapsed

        val insights = buildInsights(
            thisMonthTxns = thisMonthTxns,
            lastMonthTxns = lastMonthTxns,
            categoryMap = categoryMap,
            dailyAverage = dailyAverage,
            currencySymbol = settings.currencySymbol
        )

        val categoryBudgetItems = budgets
            .filter { it.categoryId != null }
            .mapNotNull { budget ->
                val cat = categoryMap[budget.categoryId] ?: return@mapNotNull null
                val spent = thisMonthTxns
                    .filter { it.categoryId == budget.categoryId && it.type == TransactionType.EXPENSE }
                    .sumOf { it.amount }
                BudgetItem(category = cat, spent = spent, limit = budget.limitAmount)
            }
            .sortedByDescending { it.spent / it.limit.coerceAtLeast(0.01) }

        val globalBudgetItem = settings.monthlyBudgetLimit
            .takeIf { it > 0.0 }
            ?.let { limit ->
                BudgetItem(
                    category = Category(
                        id = "monthly_budget",
                        name = "Monthly",
                        iconName = "Wallet",
                        colorHex = "#43A047",
                        type = CategoryType.EXPENSE,
                        isDefault = true,
                        sortOrder = -1
                    ),
                    spent = expenseThisMonth,
                    limit = limit
                )
            }

        val budgetItems = listOfNotNull(globalBudgetItem) + categoryBudgetItems

        HomeUiState(
            isLoading = false,
            balance = balance,
            incomeThisMonth = incomeThisMonth,
            expenseThisMonth = expenseThisMonth,
            recentTransactions = recentTransactions,
            budgetItems = budgetItems,
            insights = insights,
            dailyAverage = dailyAverage,
            monthVsLastMonth = expenseThisMonth to expenseLastMonth
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )

    fun deleteTransaction(id: String) {
        viewModelScope.launch {
            deleteTransactionUseCase.softDelete(id)
        }
    }

    private fun currentMonthYear(): String {
        val now = YearMonth.now()
        return String.format("%04d-%02d", now.year, now.monthValue)
    }

    private fun buildInsights(
        thisMonthTxns: List<Transaction>,
        lastMonthTxns: List<Transaction>,
        categoryMap: Map<String, Category>,
        dailyAverage: Double,
        currencySymbol: String
    ): List<String> {
        val insights = mutableListOf<String>()

        val fmt = java.text.DecimalFormat("#,##,##0.00", java.text.DecimalFormatSymbols(Locale("en", "IN")))

        if (dailyAverage > 0.0) {
            insights.add("$currencySymbol ${fmt.format(dailyAverage)} / day average this month")
        }

        val thisExpByCategory = thisMonthTxns
            .filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.categoryId }
            .mapValues { (_, txns) -> txns.sumOf { it.amount } }

        val lastExpByCategory = lastMonthTxns
            .filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.categoryId }
            .mapValues { (_, txns) -> txns.sumOf { it.amount } }

        thisExpByCategory.entries
            .sortedByDescending { it.value }
            .take(3)
            .forEach { (catId, thisAmount) ->
                val lastAmount = lastExpByCategory[catId] ?: 0.0
                val catName = categoryMap[catId]?.name ?: return@forEach
                if (lastAmount > 0.0) {
                    val pctChange = ((thisAmount - lastAmount) / lastAmount) * 100.0
                    when {
                        pctChange > 10.0 -> insights.add(
                            "You spent ${fmt.format(pctChange)}% more on $catName this month"
                        )
                        pctChange < -10.0 -> insights.add(
                            "You spent ${fmt.format(-pctChange)}% less on $catName this month"
                        )
                    }
                }
                if (thisAmount > 0.0) {
                    insights.add(
                        "Top spend: $catName — $currencySymbol ${fmt.format(thisAmount)} this month"
                    )
                }
            }

        val thisIncome = thisMonthTxns.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val thisExpense = thisMonthTxns.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        if (thisIncome > 0.0 && thisExpense > 0.0) {
            val savingsRate = ((thisIncome - thisExpense) / thisIncome) * 100.0
            if (savingsRate > 0.0) {
                insights.add("Savings rate this month: ${fmt.format(savingsRate)}%")
            }
        }

        return insights.distinct().take(5)
    }
}
