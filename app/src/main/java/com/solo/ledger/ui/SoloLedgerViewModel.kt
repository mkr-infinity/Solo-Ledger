package com.solo.ledger.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.solo.ledger.SoloLedgerApplication
import com.solo.ledger.data.local.entity.CategoryEntity
import com.solo.ledger.data.local.entity.ExpenseEntity
import com.solo.ledger.data.local.entity.SavingsGoalEntity
import com.solo.ledger.data.model.BudgetTemplate
import com.solo.ledger.data.model.UserSettings
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SoloLedgerViewModel(application: Application) : AndroidViewModel(application) {
    private val container = (application as SoloLedgerApplication).container

    val settings: StateFlow<UserSettings?> = container.settingsRepository.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    val categories: StateFlow<List<CategoryEntity>> = container.categoryRepository.observeActiveCategories().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    val activeExpenses: StateFlow<List<ExpenseEntity>> = container.expenseRepository.observeActiveExpenses().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    val deletedExpenses: StateFlow<List<ExpenseEntity>> = container.expenseRepository.observeDeletedExpenses().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    val activeGoals: StateFlow<List<SavingsGoalEntity>> = container.goalRepository.observeActiveGoals().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    init {
        viewModelScope.launch {
            container.categoryRepository.ensureDefaultCategories(System.currentTimeMillis())
        }
    }

    fun completeOnboarding(template: BudgetTemplate?) {
        viewModelScope.launch {
            container.settingsRepository.completeOnboarding(template)
        }
    }

    fun moveExpenseToBin(expenseId: String) {
        viewModelScope.launch {
            container.expenseRepository.moveToBin(expenseId, System.currentTimeMillis())
        }
    }

    fun restoreExpense(expenseId: String) {
        viewModelScope.launch {
            container.expenseRepository.restore(expenseId, System.currentTimeMillis())
        }
    }

    fun deleteExpensePermanently(expense: ExpenseEntity) {
        viewModelScope.launch {
            container.expenseRepository.deletePermanently(expense)
        }
    }

    fun clearBin() {
        viewModelScope.launch {
            container.expenseRepository.clearBin()
        }
    }

    fun createSavingsGoal(
        title: String,
        targetAmountText: String,
        savedAmountText: String,
        currencyCode: String,
        onSaved: () -> Unit,
        onError: (String) -> Unit,
    ) {
        val cleanTitle = title.trim()
        val targetAmount = targetAmountText.toMinorAmount()
        val savedAmount = savedAmountText.ifBlank { "0" }.toMinorAmount()

        when {
            cleanTitle.isBlank() -> onError("Enter a goal title.")
            targetAmount == null || targetAmount <= 0L -> onError("Enter a valid target amount.")
            savedAmount == null || savedAmount < 0L -> onError("Enter a valid saved amount.")
            else -> viewModelScope.launch {
                val now = System.currentTimeMillis()
                container.goalRepository.upsert(
                    SavingsGoalEntity(
                        id = UUID.randomUUID().toString(),
                        title = cleanTitle,
                        targetAmountMinor = targetAmount,
                        savedAmountMinor = savedAmount.coerceAtMost(targetAmount),
                        currencyCode = currencyCode.ifBlank { "INR" },
                        targetDateEpochDay = null,
                        accentColorHex = "#22C55E",
                        createdAtMillis = now,
                        updatedAtMillis = now,
                        archivedAtMillis = null,
                    ),
                )
                onSaved()
            }
        }
    }

    fun addSavingsProgress(
        goal: SavingsGoalEntity,
        amountText: String,
        onSaved: () -> Unit,
        onError: (String) -> Unit,
    ) {
        val amount = amountText.toMinorAmount()
        if (amount == null || amount <= 0L) {
            onError("Enter a valid progress amount.")
            return
        }

        viewModelScope.launch {
            container.goalRepository.upsert(
                goal.copy(
                    savedAmountMinor = (goal.savedAmountMinor + amount).coerceAtMost(goal.targetAmountMinor),
                    updatedAtMillis = System.currentTimeMillis(),
                ),
            )
            onSaved()
        }
    }

    fun archiveSavingsGoal(goalId: String) {
        viewModelScope.launch {
            container.goalRepository.archive(goalId, System.currentTimeMillis())
        }
    }

    fun addExpense(
        title: String,
        amountText: String,
        currencyCode: String,
        categoryId: String,
        dateText: String,
        timeText: String,
        notes: String,
        attachmentUri: Uri?,
        onSaved: () -> Unit,
        onError: (String) -> Unit,
    ) {
        val cleanTitle = title.trim()
        val amountMinor = amountText.toMinorAmount()
        val date = runCatching { LocalDate.parse(dateText.trim()) }.getOrNull()
        val time = runCatching { LocalTime.parse(timeText.trim()) }.getOrNull()

        when {
            cleanTitle.isBlank() -> onError("Enter an expense title.")
            amountMinor == null || amountMinor <= 0L -> onError("Enter a valid amount.")
            categoryId.isBlank() -> onError("Choose a category.")
            date == null -> onError("Use date format YYYY-MM-DD.")
            time == null -> onError("Use time format HH:MM.")
            else -> viewModelScope.launch {
                val id = UUID.randomUUID().toString()
                val now = System.currentTimeMillis()
                val attachmentPath = attachmentUri?.let { uri -> copyAttachment(id, uri, onError) }

                if (attachmentUri != null && attachmentPath == null) return@launch

                container.expenseRepository.upsert(
                    ExpenseEntity(
                        id = id,
                        title = cleanTitle,
                        amountMinor = amountMinor,
                        currencyCode = currencyCode.ifBlank { "INR" },
                        categoryId = categoryId,
                        dateEpochDay = date.toEpochDay(),
                        timeMinuteOfDay = time.hour * 60 + time.minute,
                        notes = notes.trim().ifBlank { null },
                        attachmentPath = attachmentPath,
                        createdAtMillis = now,
                        updatedAtMillis = now,
                        deletedAtMillis = null,
                    ),
                )
                onSaved()
            }
        }
    }

    private fun String.toMinorAmount(): Long? = runCatching {
        BigDecimal(trim())
            .movePointRight(2)
            .setScale(0, RoundingMode.HALF_UP)
            .longValueExact()
    }.getOrNull()

    private suspend fun copyAttachment(
        expenseId: String,
        uri: Uri,
        onError: (String) -> Unit,
    ): String? {
        val path = withContext(Dispatchers.IO) {
            runCatching {
                val app = getApplication<SoloLedgerApplication>()
                val mimeType = app.contentResolver.getType(uri).orEmpty()
                val extension = when {
                    mimeType.contains("png", ignoreCase = true) -> "png"
                    mimeType.contains("jpeg", ignoreCase = true) -> "jpg"
                    mimeType.contains("jpg", ignoreCase = true) -> "jpg"
                    else -> "bin"
                }
                val attachmentDir = File(app.filesDir, "attachments")
                attachmentDir.mkdirs()
                val destination = File(attachmentDir, "$expenseId.$extension")

                app.contentResolver.openInputStream(uri)?.use { input ->
                    destination.outputStream().use { output -> input.copyTo(output) }
                } ?: error("Attachment could not be opened.")

                destination.absolutePath
            }.getOrNull()
        }

        if (path == null) {
            onError("Attachment could not be saved locally.")
        }
        return path
    }
}
