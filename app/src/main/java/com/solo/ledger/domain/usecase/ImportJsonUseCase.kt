package com.solo.ledger.domain.usecase

import android.content.Context
import android.net.Uri
import com.solo.ledger.data.repository.BudgetRepository
import com.solo.ledger.data.repository.CategoryRepository
import com.solo.ledger.data.repository.TransactionRepository
import com.solo.ledger.util.JsonSerializer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ImportJsonUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val budgetRepository: BudgetRepository,
    @ApplicationContext private val context: Context
) {
    suspend fun import(uri: Uri): Result<Int> = runCatching {
        val jsonString = context.contentResolver.openInputStream(uri)?.use { inputStream ->
            inputStream.bufferedReader(Charsets.UTF_8).readText()
        } ?: throw IllegalStateException("Could not open input stream for URI: $uri")

        val exportData = JsonSerializer.fromJson(jsonString)

        if (exportData.version != 1) {
            throw IllegalArgumentException(
                "Unsupported export version: ${exportData.version}. Expected version 1."
            )
        }

        exportData.categories.forEach { category ->
            runCatching { categoryRepository.insert(category) }
        }

        exportData.budgets.forEach { budget ->
            runCatching { budgetRepository.insert(budget) }
        }

        var importedCount = 0
        exportData.transactions.forEach { transaction ->
            runCatching {
                transactionRepository.insert(transaction)
                importedCount++
            }
        }

        importedCount
    }
}
