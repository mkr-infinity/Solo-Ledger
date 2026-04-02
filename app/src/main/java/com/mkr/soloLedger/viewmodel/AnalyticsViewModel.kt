package com.mkr.soloLedger.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.mkr.soloLedger.data.database.SoloLedgerDatabase
import com.mkr.soloLedger.data.dao.CategoryTotal
import com.mkr.soloLedger.data.repository.SoloLedgerRepository
import com.mkr.soloLedger.utils.getEndOfMonth
import com.mkr.soloLedger.utils.getStartOfMonth
import kotlinx.coroutines.launch
import java.util.Calendar

class AnalyticsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SoloLedgerRepository

    private val _categoryTotals = MutableLiveData<List<CategoryTotal>>()
    val categoryTotals: LiveData<List<CategoryTotal>> = _categoryTotals

    private val _monthlyTotals = MutableLiveData<List<Pair<String, Double>>>()
    val monthlyTotals: LiveData<List<Pair<String, Double>>> = _monthlyTotals

    init {
        val db = SoloLedgerDatabase.getDatabase(application)
        repository = SoloLedgerRepository(
            db.expenseDao(), db.categoryDao(), db.budgetDao(),
            db.userProfileDao(), db.recurringExpenseDao()
        )
    }

    fun loadCurrentMonthCategoryTotals() = viewModelScope.launch {
        val start = getStartOfMonth()
        val end = getEndOfMonth()
        _categoryTotals.value = repository.getCategoryTotals(start, end)
    }

    fun loadLast6MonthsTotals() = viewModelScope.launch {
        val cal = Calendar.getInstance()
        val results = mutableListOf<Pair<String, Double>>()

        for (i in 5 downTo 0) {
            val tempCal = cal.clone() as Calendar
            tempCal.add(Calendar.MONTH, -i)
            val year = tempCal.get(Calendar.YEAR)
            val month = tempCal.get(Calendar.MONTH)
            val start = getStartOfMonth(year, month)
            val end = getEndOfMonth(year, month)
            val total = repository.getTotalAmountByDateRange(start, end)
            val label = android.text.format.DateFormat.format("MMM", tempCal.time).toString()
            results.add(Pair(label, total))
        }
        _monthlyTotals.value = results
    }
}
