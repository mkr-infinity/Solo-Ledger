package com.solo.ledger.core

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.solo.ledger.data.repository.BackupManager
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/** Renders a simple, real PDF spending report using the platform PdfDocument API. */
object PdfExporter {

    suspend fun export(context: Context, backup: BackupManager): String? = runCatching {
        val repo = ServiceLocator.ledgerRepository
        val expenses = repo.allExpensesSnapshot().filter { !it.isDeleted }
            .sortedByDescending { it.dateEpochDay }
        val byCategory = expenses.groupBy { it.category }
            .mapValues { e -> e.value.sumOf { it.amount } }
            .toList().sortedByDescending { it.second }
        val total = expenses.sumOf { it.amount }

        val doc = PdfDocument()
        val titlePaint = Paint().apply { textSize = 22f; isFakeBoldText = true }
        val headerPaint = Paint().apply { textSize = 14f; isFakeBoldText = true }
        val bodyPaint = Paint().apply { textSize = 11f }
        val mutedPaint = Paint().apply { textSize = 11f; color = 0xFF666666.toInt() }

        val pageW = 595; val pageH = 842; val margin = 40f
        var pageNum = 1
        var page = doc.startPage(PdfDocument.PageInfo.Builder(pageW, pageH, pageNum).create())
        var canvas = page.canvas
        var y = margin + 10

        canvas.drawText("Solo Ledger — Spending Report", margin, y, titlePaint); y += 26
        canvas.drawText("Generated ${LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))}", margin, y, mutedPaint); y += 30
        canvas.drawText("Total spent: ${Money.format(total)}", margin, y, headerPaint); y += 20
        canvas.drawText("Transactions: ${expenses.size}", margin, y, bodyPaint); y += 28

        canvas.drawText("By category", margin, y, headerPaint); y += 18
        byCategory.forEach { (cat, amt) ->
            canvas.drawText(cat, margin, y, bodyPaint)
            canvas.drawText(Money.format(amt), pageW - margin - 120, y, bodyPaint); y += 16
        }
        y += 18
        canvas.drawText("Transactions", margin, y, headerPaint); y += 18

        val df = DateTimeFormatter.ofPattern("dd MMM yyyy")
        for (e in expenses) {
            if (y > pageH - margin) {
                doc.finishPage(page); pageNum++
                page = doc.startPage(PdfDocument.PageInfo.Builder(pageW, pageH, pageNum).create())
                canvas = page.canvas; y = margin
            }
            canvas.drawText(LocalDate.ofEpochDay(e.dateEpochDay).format(df), margin, y, mutedPaint)
            canvas.drawText(e.title.take(28), margin + 90, y, bodyPaint)
            canvas.drawText(e.category.take(14), margin + 300, y, mutedPaint)
            canvas.drawText(Money.format(e.amount), pageW - margin - 90, y, bodyPaint)
            y += 16
        }
        doc.finishPage(page)

        val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir
        val out = File(dir, "solo_ledger_report_${System.currentTimeMillis()}.pdf")
        out.outputStream().use { doc.writeTo(it) }
        doc.close()
        out.absolutePath
    }.getOrNull()
}
