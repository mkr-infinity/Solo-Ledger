package com.mkr.soloLedger.utils

import com.mkr.soloLedger.data.entities.Expense
import com.mkr.soloLedger.data.entities.RecurringExpense
import com.mkr.soloLedger.data.repository.SoloLedgerRepository
import java.util.Calendar

class RecurringExpenseManager(private val repository: SoloLedgerRepository) {

    suspend fun checkAndAddDueExpenses() {
        val now = System.currentTimeMillis()
        val dueExpenses = repository.getDueRecurringExpenses(now)

        dueExpenses.forEach { recurring ->
            // Add as a regular expense
            val expense = Expense(
                title = recurring.title,
                amount = recurring.amount,
                categoryId = recurring.categoryId,
                date = System.currentTimeMillis(),
                notes = "Auto-added recurring expense",
                isRecurring = true,
                recurringType = recurring.recurringType
            )
            repository.insertExpense(expense)

            // Update next due date
            val nextDate = calculateNextDueDate(recurring.nextDueDate, recurring.recurringType)
            repository.updateRecurringExpense(
                recurring.copy(nextDueDate = nextDate, lastAddedDate = now)
            )
        }
    }

    private fun calculateNextDueDate(currentDueDate: Long, recurringType: String): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = currentDueDate
        return when (recurringType) {
            Constants.RECURRING_WEEKLY -> {
                cal.add(Calendar.WEEK_OF_YEAR, 1)
                cal.timeInMillis
            }
            Constants.RECURRING_MONTHLY -> {
                cal.add(Calendar.MONTH, 1)
                cal.timeInMillis
            }
            Constants.RECURRING_YEARLY -> {
                cal.add(Calendar.YEAR, 1)
                cal.timeInMillis
            }
            else -> currentDueDate
        }
    }
}
