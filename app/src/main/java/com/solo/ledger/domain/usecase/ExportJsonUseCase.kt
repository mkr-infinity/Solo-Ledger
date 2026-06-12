package com.solo.ledger.domain.usecase

import android.content.Context
import android.net.Uri
import com.solo.ledger.data.repository.BudgetRepository
import com.solo.ledger.data.repository.CategoryRepository
import com.solo.ledger.data.repository.TransactionRepository
import com.solo.ledger.data.model.Budget
import com.solo.ledger.util.ExportData
import com.solo.ledger.util.JsonSerializer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import javax.inject.Inject

class ExportJsonUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val budgetRepository: BudgetRepository,
    @ApplicationContext private val context: Context
) {
    suspend fun export(uri: Uri): Result<Unit> = runCatching {
        val transactions = transactionRepository.getAll().first()
        val categories = categoryRepository.getAll().first()
        val budgets = run {
            val today = java.time.LocalDate.now()
            val list = mutableListOf<Budget>()
            for (offset in 0L..23L) {
                val d = today.minusMonths(offset)
                val my = String.format("%04d-%02d", d.year, d.monthValue)
                list.addAll(budgetRepository.getByMonth(my).first())
            }
            list.distinctBy { it.id }
        }

        val exportData = ExportData(
            version = 1,
            exportedAt = LocalDateTime.now().toString(),
            transactions = transactions,
            categories = categories,
            budgets = budgets
        )

        val json = JsonSerializer.toJson(exportData)

        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            outputStream.write(json.toByteArray(Charsets.UTF_8))
        } ?: throw IllegalStateException("Could not open output stream for URI: $uri")
    }
}
