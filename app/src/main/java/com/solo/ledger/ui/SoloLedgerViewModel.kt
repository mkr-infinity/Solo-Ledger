package com.solo.ledger.ui

import android.app.Application
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.solo.ledger.SoloLedgerApplication
import com.solo.ledger.data.local.entity.CategoryEntity
import com.solo.ledger.data.local.entity.ExpenseEntity
import com.solo.ledger.data.local.entity.SavingsGoalEntity
import com.solo.ledger.data.model.BudgetTemplate
import com.solo.ledger.data.model.DashboardWidget
import com.solo.ledger.data.model.QuickAddField
import com.solo.ledger.data.model.UserSettings
import com.solo.ledger.ui.theme.LedgerTheme
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
import org.json.JSONArray
import org.json.JSONObject

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
            expense.attachmentPath?.let(::deleteLocalFile)
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

    fun updateTheme(theme: LedgerTheme) {
        viewModelScope.launch {
            container.settingsRepository.updateTheme(theme)
        }
    }

    fun updateProfile(
        name: String,
        monthlyBudgetText: String,
        currencyCode: String,
        avatarUri: Uri?,
        useSvgAvatar: Boolean,
        onSaved: () -> Unit,
        onError: (String) -> Unit,
    ) {
        val monthlyBudget = monthlyBudgetText.toMinorAmount()
        when {
            currencyCode.trim().length != 3 -> onError("Use a 3-letter currency code.")
            monthlyBudget == null || monthlyBudget < 0L -> onError("Enter a valid monthly budget.")
            else -> viewModelScope.launch {
                val avatarPath = when {
                    useSvgAvatar -> ledgerSvgAvatarPath
                    avatarUri != null -> copyLocalImage("avatar", avatarUri, onError)
                    else -> settings.value?.avatarPath
                }
                if (avatarUri != null && avatarPath == null) return@launch

                container.settingsRepository.updateProfile(
                    name = name,
                    avatarPath = avatarPath,
                    monthlyBudgetMinor = monthlyBudget,
                    currencyCode = currencyCode,
                )
                onSaved()
            }
        }
    }

    fun updateAppearance(
        fontScale: Float,
        animationsEnabled: Boolean,
        reducedMotion: Boolean,
        highContrast: Boolean,
        borderRadiusDp: Int,
    ) {
        viewModelScope.launch {
            container.settingsRepository.updateAppearance(
                fontScale = fontScale,
                animationsEnabled = animationsEnabled,
                reducedMotion = reducedMotion,
                highContrast = highContrast,
                borderRadiusDp = borderRadiusDp,
            )
        }
    }

    fun updateDashboardWidgets(widgets: List<DashboardWidget>) {
        viewModelScope.launch {
            container.settingsRepository.updateDashboardWidgets(widgets)
        }
    }

    fun updateQuickAddFields(fields: Set<QuickAddField>) {
        viewModelScope.launch {
            container.settingsRepository.updateQuickAddFields(fields)
        }
    }

    fun saveCategory(
        existing: CategoryEntity?,
        name: String,
        iconName: String,
        colorHex: String,
        onSaved: () -> Unit,
        onError: (String) -> Unit,
    ) {
        val cleanName = name.trim()
        val cleanIcon = iconName.trim().ifBlank { "category" }
        val cleanColor = colorHex.trim().uppercase()
        val validColor = Regex("^#[0-9A-F]{6}$").matches(cleanColor)
        val duplicateName = categories.value.firstOrNull {
            it.name.equals(cleanName, ignoreCase = true) && it.id != existing?.id
        }

        when {
            cleanName.isBlank() -> onError("Enter a category name.")
            !validColor -> onError("Use color format #RRGGBB.")
            duplicateName != null -> onError("Category name already exists.")
            else -> viewModelScope.launch {
                val now = System.currentTimeMillis()
                container.categoryRepository.upsert(
                    CategoryEntity(
                        id = existing?.id ?: UUID.randomUUID().toString(),
                        name = cleanName,
                        iconName = cleanIcon,
                        colorHex = cleanColor,
                        createdAtMillis = existing?.createdAtMillis ?: now,
                        updatedAtMillis = now,
                        isArchived = false,
                    ),
                )
                onSaved()
            }
        }
    }

    fun archiveCategory(categoryId: String) {
        viewModelScope.launch {
            container.categoryRepository.archive(categoryId, System.currentTimeMillis())
        }
    }

    fun deleteCategory(category: CategoryEntity, onDone: (String) -> Unit) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                runCatching { container.categoryRepository.delete(category) }
            }
            result.onSuccess {
                onDone("Category deleted permanently.")
            }.onFailure {
                onDone("Category is used by expenses, so it was archived instead.")
                container.categoryRepository.archive(category.id, System.currentTimeMillis())
            }
        }
    }

    fun exportJson(onDone: (String) -> Unit) {
        viewModelScope.launch {
            val path = withContext(Dispatchers.IO) {
                val app = getApplication<SoloLedgerApplication>()
                val exportDir = File(app.filesDir, "exports")
                exportDir.mkdirs()
                val exportFile = File(exportDir, "solo-ledger-export.json")
                val payload = JSONObject()
                    .put("version", 1)
                    .put("exportedAtMillis", System.currentTimeMillis())
                    .put("categories", categories.value.toCategoryJson())
                    .put("expenses", (activeExpenses.value + deletedExpenses.value).toExpenseJson())
                    .put("savingsGoals", activeGoals.value.toGoalJson())

                exportFile.writeText(payload.toString(2))
                exportFile.absolutePath
            }
            onDone("Exported JSON to $path")
        }
    }

    fun exportJsonToUri(uri: Uri, onDone: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                runCatching {
                    val app = getApplication<SoloLedgerApplication>()
                    val payload = JSONObject()
                        .put("version", 1)
                        .put("exportedAtMillis", System.currentTimeMillis())
                        .put("categories", categories.value.toCategoryJson())
                        .put("expenses", (activeExpenses.value + deletedExpenses.value).toExpenseJson())
                        .put("savingsGoals", activeGoals.value.toGoalJson())
                    app.contentResolver.openOutputStream(uri, "wt")?.use { output ->
                        output.write(payload.toString(2).toByteArray())
                    } ?: error("Unable to open export destination.")
                    resolveDisplayName(uri) ?: "solo-ledger-export.json"
                }
            }
            result.onSuccess { onDone("Exported JSON to $it") }.onFailure { onError(it.message ?: "JSON export failed.") }
        }
    }

    fun exportPdf(onDone: (String) -> Unit) {
        viewModelScope.launch {
            val path = withContext(Dispatchers.IO) {
                val app = getApplication<SoloLedgerApplication>()
                val exportDir = File(app.filesDir, "exports")
                exportDir.mkdirs()
                val exportFile = File(exportDir, "solo-ledger-report.pdf")
                val paint = Paint(Paint.ANTI_ALIAS_FLAG)
                val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    textSize = 22f
                    isFakeBoldText = true
                }
                val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 13f }
                val document = PdfDocument()
                val page = document.startPage(PdfDocument.PageInfo.Builder(595, 842, 1).create())
                val canvas = page.canvas
                var y = 56f
                val currentSettings = settings.value
                val active = activeExpenses.value
                val deleted = deletedExpenses.value
                val goals = activeGoals.value
                val categoryNames = categories.value.associate { it.id to it.name }

                canvas.drawText("Solo Ledger Offline Report", 40f, y, titlePaint)
                y += 34f
                paint.textSize = 15f
                canvas.drawText("Profile: ${currentSettings?.name?.ifBlank { "Local User" } ?: "Local User"}", 40f, y, paint)
                y += 24f
                canvas.drawText("Currency: ${currentSettings?.currencyCode ?: "INR"}", 40f, y, paint)
                y += 24f
                canvas.drawText("Active expenses: ${active.size}", 40f, y, paint)
                y += 24f
                canvas.drawText("Deleted expenses in bin: ${deleted.size}", 40f, y, paint)
                y += 30f
                canvas.drawText("Recent Transactions", 40f, y, titlePaint)
                y += 26f

                active.take(16).forEach { expense ->
                    val date = LocalDate.ofEpochDay(expense.dateEpochDay).toString()
                    val amount = formatMinorForExport(expense.amountMinor, expense.currencyCode)
                    val category = categoryNames[expense.categoryId] ?: "Other"
                    canvas.drawText("$date  $amount  $category  ${expense.title}".take(86), 40f, y, labelPaint)
                    y += 18f
                }

                y += 16f
                canvas.drawText("Savings Goals", 40f, y, titlePaint)
                y += 26f
                goals.take(8).forEach { goal ->
                    val saved = formatMinorForExport(goal.savedAmountMinor, goal.currencyCode)
                    val target = formatMinorForExport(goal.targetAmountMinor, goal.currencyCode)
                    canvas.drawText("${goal.title}: $saved saved of $target".take(86), 40f, y, labelPaint)
                    y += 18f
                }

                document.finishPage(page)
                exportFile.outputStream().use { output -> document.writeTo(output) }
                document.close()
                exportFile.absolutePath
            }
            onDone("Exported PDF to $path")
        }
    }

    fun exportPdfToUri(uri: Uri, onDone: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                runCatching {
                    val app = getApplication<SoloLedgerApplication>()
                    val currentSettings = settings.value
                    val active = activeExpenses.value
                    val deleted = deletedExpenses.value
                    val goals = activeGoals.value
                    val categoryNames = categories.value.associate { it.id to it.name }
                    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
                    val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        textSize = 22f
                        isFakeBoldText = true
                    }
                    val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 13f }
                    val document = PdfDocument()
                    val page = document.startPage(PdfDocument.PageInfo.Builder(595, 842, 1).create())
                    val canvas = page.canvas
                    var y = 56f

                    canvas.drawText("Solo Ledger Offline Report", 40f, y, titlePaint)
                    y += 34f
                    paint.textSize = 15f
                    canvas.drawText("Profile: ${currentSettings?.name?.ifBlank { "Local User" } ?: "Local User"}", 40f, y, paint)
                    y += 24f
                    canvas.drawText("Currency: ${currentSettings?.currencyCode ?: "INR"}", 40f, y, paint)
                    y += 24f
                    canvas.drawText("Active expenses: ${active.size}", 40f, y, paint)
                    y += 24f
                    canvas.drawText("Deleted expenses in bin: ${deleted.size}", 40f, y, paint)
                    y += 30f
                    canvas.drawText("Recent Transactions", 40f, y, titlePaint)
                    y += 26f

                    active.take(16).forEach { expense ->
                        val date = LocalDate.ofEpochDay(expense.dateEpochDay).toString()
                        val amount = formatMinorForExport(expense.amountMinor, expense.currencyCode)
                        val category = categoryNames[expense.categoryId] ?: "Other"
                        canvas.drawText("$date  $amount  $category  ${expense.title}".take(86), 40f, y, labelPaint)
                        y += 18f
                    }

                    y += 16f
                    canvas.drawText("Savings Goals", 40f, y, titlePaint)
                    y += 26f
                    goals.take(8).forEach { goal ->
                        val saved = formatMinorForExport(goal.savedAmountMinor, goal.currencyCode)
                        val target = formatMinorForExport(goal.targetAmountMinor, goal.currencyCode)
                        canvas.drawText("${goal.title}: $saved saved of $target".take(86), 40f, y, labelPaint)
                        y += 18f
                    }

                    document.finishPage(page)
                    app.contentResolver.openOutputStream(uri, "wt")?.use { output ->
                        document.writeTo(output)
                    } ?: error("Unable to open export destination.")
                    document.close()
                    resolveDisplayName(uri) ?: "solo-ledger-report.pdf"
                }
            }
            result.onSuccess { onDone("Exported PDF to $it") }.onFailure { onError(it.message ?: "PDF export failed.") }
        }
    }

    fun importJson(onDone: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                runCatching {
                    val app = getApplication<SoloLedgerApplication>()
                    val exportFile = File(File(app.filesDir, "exports"), "solo-ledger-export.json")
                    if (!exportFile.exists()) error("No local export file found.")
                    val payload = JSONObject(exportFile.readText())

                    payload.optJSONArray("categories")?.let { categoriesJson ->
                        for (index in 0 until categoriesJson.length()) {
                            container.categoryRepository.upsert(categoriesJson.getJSONObject(index).toCategoryEntity())
                        }
                    }
                    payload.optJSONArray("expenses")?.let { expensesJson ->
                        for (index in 0 until expensesJson.length()) {
                            container.expenseRepository.upsert(expensesJson.getJSONObject(index).toExpenseEntity())
                        }
                    }
                    payload.optJSONArray("savingsGoals")?.let { goalsJson ->
                        for (index in 0 until goalsJson.length()) {
                            container.goalRepository.upsert(goalsJson.getJSONObject(index).toSavingsGoalEntity())
                        }
                    }

                    "Imported JSON from ${exportFile.absolutePath}"
                }
            }

            result.onSuccess(onDone).onFailure { onError(it.message ?: "JSON import failed.") }
        }
    }

    fun importJsonFromUri(uri: Uri, onDone: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                runCatching {
                    val app = getApplication<SoloLedgerApplication>()
                    val payload = app.contentResolver.openInputStream(uri)?.use { input ->
                        JSONObject(input.bufferedReader().readText())
                    } ?: error("Unable to open selected JSON file.")

                    payload.optJSONArray("categories")?.let { categoriesJson ->
                        for (index in 0 until categoriesJson.length()) {
                            container.categoryRepository.upsert(categoriesJson.getJSONObject(index).toCategoryEntity())
                        }
                    }
                    payload.optJSONArray("expenses")?.let { expensesJson ->
                        for (index in 0 until expensesJson.length()) {
                            container.expenseRepository.upsert(expensesJson.getJSONObject(index).toExpenseEntity())
                        }
                    }
                    payload.optJSONArray("savingsGoals")?.let { goalsJson ->
                        for (index in 0 until goalsJson.length()) {
                            container.goalRepository.upsert(goalsJson.getJSONObject(index).toSavingsGoalEntity())
                        }
                    }

                    resolveDisplayName(uri) ?: "selected JSON file"
                }
            }
            result.onSuccess { onDone("Imported JSON from $it") }.onFailure { onError(it.message ?: "JSON import failed.") }
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
                val attachmentPath = attachmentUri?.let { uri -> copyLocalImage(id, uri, onError) }

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

    fun updateExpense(
        expense: ExpenseEntity,
        title: String,
        amountText: String,
        categoryId: String,
        dateText: String,
        timeText: String,
        notes: String,
        attachmentUri: Uri?,
        removeAttachment: Boolean,
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
                val previousAttachmentPath = expense.attachmentPath
                val nextAttachmentPath = when {
                    removeAttachment -> {
                        previousAttachmentPath?.let(::deleteLocalFile)
                        null
                    }
                    attachmentUri != null -> copyLocalImage(expense.id, attachmentUri, onError)
                    else -> expense.attachmentPath
                }

                if (attachmentUri != null && nextAttachmentPath == null) return@launch

                if (attachmentUri != null && previousAttachmentPath != null && previousAttachmentPath != nextAttachmentPath) {
                    deleteLocalFile(previousAttachmentPath)
                }

                container.expenseRepository.upsert(
                    expense.copy(
                        title = cleanTitle,
                        amountMinor = amountMinor,
                        categoryId = categoryId,
                        dateEpochDay = date.toEpochDay(),
                        timeMinuteOfDay = time.hour * 60 + time.minute,
                        notes = notes.trim().ifBlank { null },
                        attachmentPath = nextAttachmentPath,
                        updatedAtMillis = System.currentTimeMillis(),
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

    private suspend fun copyLocalImage(
        fileName: String,
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
                val imageDir = File(app.filesDir, "images")
                imageDir.mkdirs()
                val destination = File(imageDir, "$fileName.$extension")

                app.contentResolver.openInputStream(uri)?.use { input ->
                    destination.outputStream().use { output -> input.copyTo(output) }
                } ?: error("Attachment could not be opened.")

                destination.absolutePath
            }.getOrNull()
        }

        if (path == null) {
            onError("Image could not be saved locally.")
        }
        return path
    }

    private suspend fun deleteLocalFile(path: String) {
        withContext(Dispatchers.IO) {
            runCatching { File(path).delete() }
        }
    }

    private fun resolveDisplayName(uri: Uri): String? {
        val app = getApplication<SoloLedgerApplication>()
        return runCatching {
            app.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
                ?.use { cursor ->
                    val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (index >= 0 && cursor.moveToFirst()) cursor.getString(index) else null
                }
        }.getOrNull()
    }
}

private fun formatMinorForExport(minor: Long, currencyCode: String): String =
    "$currencyCode ${minor / 100}.${(minor % 100).toString().padStart(2, '0')}"

private const val ledgerSvgAvatarPath = "svg:ledger-avatar"

private fun List<CategoryEntity>.toCategoryJson(): JSONArray = JSONArray().also { array ->
    forEach { category ->
        array.put(
            JSONObject()
                .put("id", category.id)
                .put("name", category.name)
                .put("iconName", category.iconName)
                .put("colorHex", category.colorHex)
                .put("createdAtMillis", category.createdAtMillis)
                .put("updatedAtMillis", category.updatedAtMillis)
                .put("isArchived", category.isArchived),
        )
    }
}

private fun List<ExpenseEntity>.toExpenseJson(): JSONArray = JSONArray().also { array ->
    forEach { expense ->
        array.put(
            JSONObject()
                .put("id", expense.id)
                .put("title", expense.title)
                .put("amountMinor", expense.amountMinor)
                .put("currencyCode", expense.currencyCode)
                .put("categoryId", expense.categoryId)
                .put("dateEpochDay", expense.dateEpochDay)
                .put("timeMinuteOfDay", expense.timeMinuteOfDay)
                .put("notes", expense.notes)
                .put("attachmentPath", expense.attachmentPath)
                .put("createdAtMillis", expense.createdAtMillis)
                .put("updatedAtMillis", expense.updatedAtMillis)
                .put("deletedAtMillis", expense.deletedAtMillis),
        )
    }
}

private fun List<SavingsGoalEntity>.toGoalJson(): JSONArray = JSONArray().also { array ->
    forEach { goal ->
        array.put(
            JSONObject()
                .put("id", goal.id)
                .put("title", goal.title)
                .put("targetAmountMinor", goal.targetAmountMinor)
                .put("savedAmountMinor", goal.savedAmountMinor)
                .put("currencyCode", goal.currencyCode)
                .put("targetDateEpochDay", goal.targetDateEpochDay)
                .put("accentColorHex", goal.accentColorHex)
                .put("createdAtMillis", goal.createdAtMillis)
                .put("updatedAtMillis", goal.updatedAtMillis)
                .put("archivedAtMillis", goal.archivedAtMillis),
        )
    }
}

private fun JSONObject.toCategoryEntity(): CategoryEntity = CategoryEntity(
    id = getString("id"),
    name = getString("name"),
    iconName = optString("iconName", "category"),
    colorHex = optString("colorHex", "#16A34A"),
    createdAtMillis = optLong("createdAtMillis", System.currentTimeMillis()),
    updatedAtMillis = optLong("updatedAtMillis", System.currentTimeMillis()),
    isArchived = optBoolean("isArchived", false),
)

private fun JSONObject.toExpenseEntity(): ExpenseEntity = ExpenseEntity(
    id = getString("id"),
    title = getString("title"),
    amountMinor = getLong("amountMinor"),
    currencyCode = optString("currencyCode", "INR"),
    categoryId = getString("categoryId"),
    dateEpochDay = getLong("dateEpochDay"),
    timeMinuteOfDay = getInt("timeMinuteOfDay"),
    notes = nullableString("notes"),
    attachmentPath = nullableString("attachmentPath"),
    createdAtMillis = optLong("createdAtMillis", System.currentTimeMillis()),
    updatedAtMillis = optLong("updatedAtMillis", System.currentTimeMillis()),
    deletedAtMillis = nullableLong("deletedAtMillis"),
)

private fun JSONObject.toSavingsGoalEntity(): SavingsGoalEntity = SavingsGoalEntity(
    id = getString("id"),
    title = getString("title"),
    targetAmountMinor = getLong("targetAmountMinor"),
    savedAmountMinor = getLong("savedAmountMinor"),
    currencyCode = optString("currencyCode", "INR"),
    targetDateEpochDay = nullableLong("targetDateEpochDay"),
    accentColorHex = optString("accentColorHex", "#22C55E"),
    createdAtMillis = optLong("createdAtMillis", System.currentTimeMillis()),
    updatedAtMillis = optLong("updatedAtMillis", System.currentTimeMillis()),
    archivedAtMillis = nullableLong("archivedAtMillis"),
)

private fun JSONObject.nullableString(key: String): String? = if (isNull(key)) null else optString(key)

private fun JSONObject.nullableLong(key: String): Long? = if (isNull(key)) null else optLong(key)
