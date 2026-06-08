package com.solo.ledger.domain.usecase

import android.content.Context
import android.net.Uri
import com.solo.ledger.data.model.Category
import com.solo.ledger.data.repository.CategoryRepository
import com.solo.ledger.data.repository.TransactionRepository
import com.solo.ledger.util.PdfGenerator
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ExportPdfUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    @ApplicationContext private val context: Context
) {
    suspend fun export(
        uri: Uri,
        monthYear: String?,
        currencySymbol: String,
        userName: String
    ): Result<Unit> = runCatching {
        val transactions = if (monthYear != null) {
            transactionRepository.getByMonth(monthYear).first()
        } else {
            transactionRepository.getAll().first()
        }

        val categoriesMap: Map<String, Category> = categoryRepository.getAll()
            .first()
            .associateBy { it.id }

        PdfGenerator.generate(
            context = context,
            uri = uri,
            transactions = transactions,
            categoriesMap = categoriesMap,
            currencySymbol = currencySymbol,
            userName = userName
        ).getOrThrow()
    }
}
