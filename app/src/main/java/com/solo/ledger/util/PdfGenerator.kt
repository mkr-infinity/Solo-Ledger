package com.solo.ledger.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.net.Uri
import android.print.PrintAttributes
import com.solo.ledger.data.model.Category
import com.solo.ledger.data.model.Transaction
import com.solo.ledger.data.model.TransactionType
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

object PdfGenerator {

    // ── Page dimensions (A4 at 72 dpi) ───────────────────────────────────────
    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val MARGIN = 40f
    private const val CONTENT_WIDTH = PAGE_WIDTH - MARGIN * 2

    // ── Colours ───────────────────────────────────────────────────────────────
    private val COL_BG_DARK   = Color.parseColor("#1A1A2E")
    private val COL_BG_CARD   = Color.parseColor("#16213E")
    private val COL_GOLD      = Color.parseColor("#D4AF37")
    private val COL_GOLD_DARK = Color.parseColor("#2C2410")
    private val COL_GREEN     = Color.parseColor("#4CAF50")
    private val COL_RED       = Color.parseColor("#FF4444")
    private val COL_WHITE     = Color.WHITE
    private val COL_GRAY      = Color.parseColor("#AAAAAA")
    private val COL_LIGHT_GRAY= Color.parseColor("#DDDDDD")
    private val COL_ROW_ALT   = Color.parseColor("#F8F8F8")
    private val COL_ROW_NORM  = Color.WHITE
    private val COL_HEADER_BG = Color.parseColor("#1A1A2E")
    private val COL_SECTION_BG= Color.parseColor("#EAF0FF")
    private val COL_INCOME_BG = Color.parseColor("#E8F5E9")
    private val COL_EXPENSE_BG= Color.parseColor("#FFEBEE")

    // ── Column layout for transaction table ───────────────────────────────────
    // Date | Day | Category | Title | Type | Amount
    private val COL_WIDTHS = floatArrayOf(60f, 52f, 80f, 140f, 56f, 80f)
    // X offsets computed from MARGIN
    private val COL_X: FloatArray by lazy {
        val xs = FloatArray(COL_WIDTHS.size)
        xs[0] = MARGIN
        for (i in 1 until COL_WIDTHS.size) xs[i] = xs[i - 1] + COL_WIDTHS[i - 1]
        xs
    }
    private const val ROW_HEIGHT = 18f
    private const val HEADER_ROW_HEIGHT = 22f
    private const val ROWS_PER_PAGE = 36

    // ─────────────────────────────────────────────────────────────────────────

    suspend fun generate(
        context: Context,
        uri: Uri,
        transactions: List<Transaction>,
        categoriesMap: Map<String, Category>,
        currencySymbol: String,
        userName: String
    ): Result<Unit> = runCatching {

        val doc = android.graphics.pdf.PdfDocument()

        // ── Page 1 — Cover ────────────────────────────────────────────────────
        val coverInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(
            PAGE_WIDTH, PAGE_HEIGHT, 1
        ).create()
        val coverPage = doc.startPage(coverInfo)
        drawCoverPage(coverPage.canvas, transactions, currencySymbol, userName)
        doc.finishPage(coverPage)

        // ── Transaction pages ─────────────────────────────────────────────────
        val sorted = transactions.sortedWith(compareByDescending<Transaction> { it.date }
            .thenByDescending { it.time })

        // Group by "YYYY-MM" for section headers
        val grouped: Map<String, List<Transaction>> = sorted
            .groupBy { "${it.date.year}-${it.date.monthValue.toString().padStart(2, '0')}" }
            .toSortedMap(reverseOrder())

        // Flatten into render items: section headers + transaction rows
        data class SectionHeader(val label: String)
        val items = mutableListOf<Any>() // SectionHeader or Transaction
        grouped.forEach { (key, txList) ->
            val (yr, mo) = key.split("-")
            val label = "${
                java.time.Month.of(mo.toInt()).getDisplayName(TextStyle.FULL, Locale.ENGLISH)
            } $yr"
            items.add(SectionHeader(label))
            items.addAll(txList)
        }

        var pageNum = 2
        var itemIdx = 0
        while (itemIdx < items.size) {
            val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(
                PAGE_WIDTH, PAGE_HEIGHT, pageNum
            ).create()
            val page = doc.startPage(pageInfo)
            val canvas = page.canvas

            // White background
            canvas.drawColor(Color.WHITE)

            // Page header
            drawPageHeader(canvas, pageNum, userName)

            // Column headers
            var y = 80f
            drawTableHeader(canvas, y)
            y += HEADER_ROW_HEIGHT

            var rowsOnPage = 0
            while (itemIdx < items.size && rowsOnPage < ROWS_PER_PAGE) {
                val item = items[itemIdx]
                when (item) {
                    is SectionHeader -> {
                        drawSectionHeader(canvas, item.label, y)
                        y += ROW_HEIGHT
                        rowsOnPage++
                    }
                    is Transaction -> {
                        val isAlt = rowsOnPage % 2 == 1
                        drawTransactionRow(canvas, item, categoriesMap, y, isAlt, currencySymbol)
                        y += ROW_HEIGHT
                        rowsOnPage++
                    }
                }
                itemIdx++
            }

            // Page footer
            drawPageFooter(canvas, pageNum)

            doc.finishPage(page)
            pageNum++
        }

        // ── Last page — Chart + Support ───────────────────────────────────────
        val chartPageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(
            PAGE_WIDTH, PAGE_HEIGHT, pageNum
        ).create()
        val chartPage = doc.startPage(chartPageInfo)
        drawChartPage(chartPage.canvas, transactions, currencySymbol, userName)
        doc.finishPage(chartPage)

        // ── Write to URI ──────────────────────────────────────────────────────
        context.contentResolver.openOutputStream(uri)?.use { out ->
            doc.writeTo(out)
        } ?: throw IllegalStateException("Cannot open output stream for URI: $uri")

        doc.close()
    }

    // ── Cover page ────────────────────────────────────────────────────────────

    private fun drawCoverPage(
        canvas: Canvas,
        transactions: List<Transaction>,
        currencySymbol: String,
        userName: String
    ) {
        // Background gradient simulation (dark top, slightly lighter bottom)
        val bgPaint = Paint().apply { color = COL_BG_DARK }
        canvas.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), PAGE_HEIGHT.toFloat(), bgPaint)

        val accentPaint = Paint().apply { color = COL_BG_CARD }
        canvas.drawRect(0f, PAGE_HEIGHT * 0.55f, PAGE_WIDTH.toFloat(), PAGE_HEIGHT.toFloat(), accentPaint)

        // ── Logo mark (hexagon-ish using path) ────────────────────────────────
        val lx = PAGE_WIDTH / 2f
        val ly = 130f
        val lr = 38f
        val logoPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = COL_GOLD
            style = Paint.Style.FILL
        }
        val logoPath = Path()
        for (i in 0 until 6) {
            val angle = Math.toRadians((i * 60 - 30).toDouble())
            val px = (lx + lr * Math.cos(angle)).toFloat()
            val py = (ly + lr * Math.sin(angle)).toFloat()
            if (i == 0) logoPath.moveTo(px, py) else logoPath.lineTo(px, py)
        }
        logoPath.close()
        canvas.drawPath(logoPath, logoPaint)

        // Inner S letter
        val sTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = COL_BG_DARK
            textSize = 28f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("SL", lx, ly + 10f, sTextPaint)

        // ── App name ──────────────────────────────────────────────────────────
        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = COL_WHITE
            textSize = 34f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("Solo Ledger", PAGE_WIDTH / 2f, 200f, titlePaint)

        val subtitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = COL_GOLD
            textSize = 14f
            typeface = Typeface.DEFAULT
            textAlign = Paint.Align.CENTER
            letterSpacing = 0.12f
        }
        canvas.drawText("FINANCIAL STATEMENT", PAGE_WIDTH / 2f, 222f, subtitlePaint)

        // Divider
        val divPaint = Paint().apply { color = COL_GOLD; strokeWidth = 1.5f }
        canvas.drawLine(MARGIN + 60, 235f, PAGE_WIDTH - MARGIN - 60, 235f, divPaint)

        // ── User & date ───────────────────────────────────────────────────────
        val infoLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = COL_GRAY
            textSize = 10f
            textAlign = Paint.Align.CENTER
        }
        val infoValuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = COL_WHITE
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }

        val today = LocalDate.now()
        val dateLabel = "${today.dayOfMonth} " +
            today.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH) +
            " ${today.year}"

        if (userName.isNotBlank()) {
            canvas.drawText("PREPARED FOR", PAGE_WIDTH / 2f, 258f, infoLabelPaint)
            canvas.drawText(userName, PAGE_WIDTH / 2f, 274f, infoValuePaint)
        }
        canvas.drawText("GENERATED ON", PAGE_WIDTH / 2f, 296f, infoLabelPaint)
        canvas.drawText(dateLabel, PAGE_WIDTH / 2f, 312f, infoValuePaint)

        // ── Summary card ──────────────────────────────────────────────────────
        val cardRect = RectF(MARGIN, 336f, PAGE_WIDTH - MARGIN.toFloat(), 520f)
        val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#0F1525") }
        canvas.drawRoundRect(cardRect, 12f, 12f, cardPaint)

        // Gold border
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = COL_GOLD
            style = Paint.Style.STROKE
            strokeWidth = 1f
        }
        canvas.drawRoundRect(cardRect, 12f, 12f, borderPaint)

        val cardTitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = COL_GOLD
            textSize = 11f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.LEFT
            letterSpacing = 0.1f
        }
        canvas.drawText("SUMMARY", MARGIN + 16f, 360f, cardTitlePaint)

        val totalIncome = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val totalExpense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        val netBalance = totalIncome - totalExpense
        val savingsRate = if (totalIncome > 0) (netBalance / totalIncome * 100) else 0.0

        val rows = listOf(
            Triple("Total Income", CurrencyFormatter.format(totalIncome, currencySymbol), COL_GREEN),
            Triple("Total Expense", CurrencyFormatter.format(totalExpense, currencySymbol), COL_RED),
            Triple("Net Balance", CurrencyFormatter.format(netBalance, currencySymbol),
                if (netBalance >= 0) COL_GREEN else COL_RED),
            Triple("Savings Rate", String.format("%.1f%%", savingsRate), COL_GOLD),
            Triple("Transactions", "${transactions.size}", COL_WHITE)
        )

        val rowLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = COL_GRAY; textSize = 11f; textAlign = Paint.Align.LEFT
        }
        val rowValuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.RIGHT
        }
        val rowDivPaint = Paint().apply { color = Color.parseColor("#2A2A3E"); strokeWidth = 0.5f }

        rows.forEachIndexed { i, (label, value, color) ->
            val ry = 380f + i * 26f
            canvas.drawText(label, MARGIN + 16f, ry, rowLabelPaint)
            rowValuePaint.color = color
            canvas.drawText(value, PAGE_WIDTH - MARGIN - 16f, ry, rowValuePaint)
            if (i < rows.size - 1) {
                canvas.drawLine(MARGIN + 16f, ry + 8f, PAGE_WIDTH - MARGIN - 16f, ry + 8f, rowDivPaint)
            }
        }

        // ── Decorative bottom strip ───────────────────────────────────────────
        val stripPaint = Paint().apply { color = COL_GOLD; strokeWidth = 3f }
        canvas.drawLine(MARGIN, PAGE_HEIGHT - 60f, PAGE_WIDTH - MARGIN.toFloat(), PAGE_HEIGHT - 60f, stripPaint)

        val footerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = COL_GRAY; textSize = 9f; textAlign = Paint.Align.CENTER
        }
        canvas.drawText(
            "Generated by Solo Ledger  •  Personal Finance Tracker",
            PAGE_WIDTH / 2f, PAGE_HEIGHT - 40f, footerPaint
        )
        canvas.drawText(
            "buymeacoffee.com/mkr_infinity",
            PAGE_WIDTH / 2f, PAGE_HEIGHT - 26f, footerPaint
        )
    }

    // ── Page header ───────────────────────────────────────────────────────────

    private fun drawPageHeader(canvas: Canvas, pageNum: Int, userName: String) {
        val bgPaint = Paint().apply { color = COL_BG_DARK }
        canvas.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), 56f, bgPaint)

        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = COL_GOLD
            textSize = 13f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.LEFT
        }
        canvas.drawText("Solo Ledger", MARGIN, 30f, titlePaint)

        val subPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = COL_GRAY; textSize = 9f; textAlign = Paint.Align.LEFT
        }
        canvas.drawText("Transaction Report", MARGIN, 44f, subPaint)

        val pageNumPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = COL_GRAY; textSize = 9f; textAlign = Paint.Align.RIGHT
        }
        canvas.drawText("Page $pageNum", PAGE_WIDTH - MARGIN, 36f, pageNumPaint)
    }

    // ── Table column headers ──────────────────────────────────────────────────

    private fun drawTableHeader(canvas: Canvas, y: Float) {
        val headerBgPaint = Paint().apply { color = COL_HEADER_BG }
        canvas.drawRect(MARGIN, y, PAGE_WIDTH - MARGIN.toFloat(), y + HEADER_ROW_HEIGHT, headerBgPaint)

        val headerTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = COL_GOLD
            textSize = 8.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.LEFT
        }
        val headers = listOf("DATE", "DAY", "CATEGORY", "TITLE", "TYPE", "AMOUNT")
        headers.forEachIndexed { i, header ->
            val x = COL_X[i] + 3f
            canvas.drawText(header, x, y + 15f, headerTextPaint)
        }
    }

    // ── Section month header ──────────────────────────────────────────────────

    private fun drawSectionHeader(canvas: Canvas, label: String, y: Float) {
        val bgPaint = Paint().apply { color = COL_SECTION_BG }
        canvas.drawRect(MARGIN, y, PAGE_WIDTH - MARGIN.toFloat(), y + ROW_HEIGHT, bgPaint)

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = COL_BG_DARK
            textSize = 8.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.LEFT
        }
        canvas.drawText("▶  $label", MARGIN + 4f, y + 13f, textPaint)
    }

    // ── Transaction row ───────────────────────────────────────────────────────

    private fun drawTransactionRow(
        canvas: Canvas,
        tx: Transaction,
        categoriesMap: Map<String, Category>,
        y: Float,
        isAlt: Boolean,
        currencySymbol: String
    ) {
        val bgColor = if (tx.type == TransactionType.INCOME) {
            if (isAlt) Color.parseColor("#F1FAF1") else COL_ROW_NORM
        } else {
            if (isAlt) Color.parseColor("#FFF5F5") else COL_ROW_NORM
        }

        val rowBgPaint = Paint().apply { color = bgColor }
        canvas.drawRect(MARGIN, y, PAGE_WIDTH - MARGIN.toFloat(), y + ROW_HEIGHT, rowBgPaint)

        val cellPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#333333")
            textSize = 8f
            textAlign = Paint.Align.LEFT
        }

        // Date
        val dateStr = "${tx.date.dayOfMonth} " +
            tx.date.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
        canvas.drawText(dateStr, COL_X[0] + 2f, y + 13f, cellPaint)

        // Day
        canvas.drawText(tx.dayOfWeek.take(3), COL_X[1] + 2f, y + 13f, cellPaint)

        // Category
        val catName = categoriesMap[tx.categoryId]?.name ?: "Other"
        canvas.drawText(catName.take(11), COL_X[2] + 2f, y + 13f, cellPaint)

        // Title
        val title = tx.title?.take(19) ?: "-"
        canvas.drawText(title, COL_X[3] + 2f, y + 13f, cellPaint)

        // Type
        val typePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = if (tx.type == TransactionType.INCOME) COL_GREEN else COL_RED
            textSize = 7.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.LEFT
        }
        canvas.drawText(tx.type.name.take(3), COL_X[4] + 2f, y + 13f, typePaint)

        // Amount
        val amtPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = if (tx.type == TransactionType.INCOME) COL_GREEN else COL_RED
            textSize = 8f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.RIGHT
        }
        val amtStr = CurrencyFormatter.format(tx.amount, currencySymbol)
        val amtX = COL_X[5] + COL_WIDTHS[5] - 4f
        canvas.drawText(amtStr, amtX, y + 13f, amtPaint)

        // Bottom border
        val borderPaint = Paint().apply { color = COL_LIGHT_GRAY; strokeWidth = 0.3f }
        canvas.drawLine(MARGIN, y + ROW_HEIGHT, PAGE_WIDTH - MARGIN.toFloat(), y + ROW_HEIGHT, borderPaint)
    }

    // ── Page footer ───────────────────────────────────────────────────────────

    private fun drawPageFooter(canvas: Canvas, pageNum: Int) {
        val divPaint = Paint().apply { color = COL_LIGHT_GRAY; strokeWidth = 0.5f }
        canvas.drawLine(MARGIN, PAGE_HEIGHT - 30f, PAGE_WIDTH - MARGIN.toFloat(), PAGE_HEIGHT - 30f, divPaint)

        val footerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = COL_GRAY; textSize = 8f; textAlign = Paint.Align.CENTER
        }
        canvas.drawText(
            "Solo Ledger  •  Personal Finance Tracker  •  Page $pageNum",
            PAGE_WIDTH / 2f, PAGE_HEIGHT - 16f, footerPaint
        )
    }

    // ── Chart page ────────────────────────────────────────────────────────────

    private fun drawChartPage(
        canvas: Canvas,
        transactions: List<Transaction>,
        currencySymbol: String,
        userName: String
    ) {
        canvas.drawColor(Color.WHITE)

        // Header band
        val headerBgPaint = Paint().apply { color = COL_BG_DARK }
        canvas.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), 56f, headerBgPaint)
        val headerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = COL_GOLD; textSize = 13f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.LEFT
        }
        canvas.drawText("Monthly Overview", MARGIN, 30f, headerPaint)
        val headerSubPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = COL_GRAY; textSize = 9f; textAlign = Paint.Align.LEFT
        }
        canvas.drawText("Income vs Expense by month", MARGIN, 44f, headerSubPaint)

        // ── Compute monthly totals ─────────────────────────────────────────────
        data class MonthData(val label: String, val income: Double, val expense: Double)

        val grouped = transactions.groupBy {
            val d = it.date
            "${d.year}-${d.monthValue.toString().padStart(2, '0')}"
        }.toSortedMap()

        val months = grouped.entries.takeLast(6).map { (key, txList) ->
            val (yr, mo) = key.split("-")
            val monthLabel = java.time.Month.of(mo.toInt())
                .getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + "\n'${yr.takeLast(2)}"
            MonthData(
                label = monthLabel,
                income = txList.filter { it.type == TransactionType.INCOME }.sumOf { it.amount },
                expense = txList.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
            )
        }

        if (months.isEmpty()) {
            val noDataPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = COL_GRAY; textSize = 14f; textAlign = Paint.Align.CENTER
            }
            canvas.drawText("No data available", PAGE_WIDTH / 2f, 420f, noDataPaint)
        } else {
            // ── Draw bar chart ────────────────────────────────────────────────
            val chartLeft = MARGIN + 30f
            val chartRight = PAGE_WIDTH - MARGIN.toFloat()
            val chartTop = 90f
            val chartBottom = 380f
            val chartH = chartBottom - chartTop

            val maxVal = months.maxOf { maxOf(it.income, it.expense) }.coerceAtLeast(1.0)

            // Axis lines
            val axisPaint = Paint().apply { color = COL_LIGHT_GRAY; strokeWidth = 1f }
            canvas.drawLine(chartLeft, chartTop, chartLeft, chartBottom, axisPaint)
            canvas.drawLine(chartLeft, chartBottom, chartRight, chartBottom, axisPaint)

            // Horizontal grid lines (5 lines)
            val gridPaint = Paint().apply {
                color = Color.parseColor("#EEEEEE"); strokeWidth = 0.5f
            }
            val gridLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = COL_GRAY; textSize = 7.5f; textAlign = Paint.Align.RIGHT
            }
            for (i in 1..4) {
                val gy = chartBottom - (i * chartH / 4f)
                canvas.drawLine(chartLeft, gy, chartRight, gy, gridPaint)
                val gridVal = maxVal / 4 * i
                canvas.drawText(
                    CurrencyFormatter.formatCompact(gridVal, ""),
                    chartLeft - 3f, gy + 3f, gridLabelPaint
                )
            }

            // Bars
            val groupCount = months.size
            val groupWidth = (chartRight - chartLeft) / groupCount
            val barWidth = groupWidth * 0.28f

            val incomePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = COL_GREEN; style = Paint.Style.FILL
            }
            val expensePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#FF7043"); style = Paint.Style.FILL
            }
            val barLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#555555"); textSize = 7f; textAlign = Paint.Align.CENTER
            }

            months.forEachIndexed { idx, data ->
                val groupX = chartLeft + idx * groupWidth + groupWidth * 0.1f

                // Income bar
                val incH = (data.income / maxVal * chartH).toFloat()
                val incRect = RectF(groupX, chartBottom - incH, groupX + barWidth, chartBottom)
                canvas.drawRoundRect(incRect, 3f, 3f, incomePaint)

                // Expense bar
                val expH = (data.expense / maxVal * chartH).toFloat()
                val expRect = RectF(
                    groupX + barWidth + 4f, chartBottom - expH,
                    groupX + barWidth * 2 + 4f, chartBottom
                )
                canvas.drawRoundRect(expRect, 3f, 3f, expensePaint)

                // Month label (two lines)
                val labelParts = data.label.split("\n")
                val labelX = groupX + barWidth + 2f
                canvas.drawText(labelParts[0], labelX, chartBottom + 12f, barLabelPaint)
                if (labelParts.size > 1) {
                    canvas.drawText(labelParts[1], labelX, chartBottom + 21f, barLabelPaint)
                }
            }

            // Legend
            val legendY = chartBottom + 42f
            val legendPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#333333"); textSize = 9f
            }
            val dotPaintIncome = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = COL_GREEN; style = Paint.Style.FILL
            }
            val dotPaintExpense = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#FF7043"); style = Paint.Style.FILL
            }
            val legendX = PAGE_WIDTH / 2f - 60f
            canvas.drawCircle(legendX, legendY - 3f, 5f, dotPaintIncome)
            canvas.drawText("Income", legendX + 10f, legendY, legendPaint)
            canvas.drawCircle(legendX + 70f, legendY - 3f, 5f, dotPaintExpense)
            canvas.drawText("Expense", legendX + 80f, legendY, legendPaint)
        }

        // ── Monthly summary table ─────────────────────────────────────────────
        val tableY = 450f
        val sumTitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = COL_BG_DARK; textSize = 11f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.LEFT
        }
        canvas.drawText("Monthly Breakdown", MARGIN, tableY, sumTitlePaint)

        val tHeaderBg = Paint().apply { color = COL_BG_DARK }
        canvas.drawRect(MARGIN, tableY + 8f, PAGE_WIDTH - MARGIN.toFloat(), tableY + 26f, tHeaderBg)

        val tHeaderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = COL_GOLD; textSize = 8.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("MONTH", MARGIN + 6f, tableY + 21f, tHeaderPaint)
        canvas.drawText("INCOME", MARGIN + 110f, tableY + 21f, tHeaderPaint)
        canvas.drawText("EXPENSE", MARGIN + 210f, tableY + 21f, tHeaderPaint)
        canvas.drawText("NET", MARGIN + 310f, tableY + 21f, tHeaderPaint)
        canvas.drawText("SAVINGS %", MARGIN + 380f, tableY + 21f, tHeaderPaint)

        val tRowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#333333"); textSize = 8.5f
        }
        val tIncPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = COL_GREEN; textSize = 8.5f
        }
        val tExpPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = COL_RED; textSize = 8.5f
        }
        val tRowDivPaint = Paint().apply { color = COL_LIGHT_GRAY; strokeWidth = 0.4f }

        val allGrouped = transactions.groupBy {
            val d = it.date
            "${d.year}-${d.monthValue.toString().padStart(2, '0')}"
        }.toSortedMap(reverseOrder())

        allGrouped.entries.take(8).forEachIndexed { i, (key, txList) ->
            val ry = tableY + 34f + i * 20f
            val (yr, mo) = key.split("-")
            val monthLabel = java.time.Month.of(mo.toInt())
                .getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " $yr"
            val inc = txList.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
            val exp = txList.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
            val net = inc - exp
            val savings = if (inc > 0) net / inc * 100 else 0.0

            val bg = if (i % 2 == 1) COL_ROW_ALT else COL_ROW_NORM
            val bgPaint = Paint().apply { color = bg }
            canvas.drawRect(MARGIN, ry - 13f, PAGE_WIDTH - MARGIN.toFloat(), ry + 6f, bgPaint)

            canvas.drawText(monthLabel, MARGIN + 6f, ry, tRowPaint)
            canvas.drawText(CurrencyFormatter.formatCompact(inc, ""), MARGIN + 110f, ry, tIncPaint)
            canvas.drawText(CurrencyFormatter.formatCompact(exp, ""), MARGIN + 210f, ry, tExpPaint)
            val netPaint = if (net >= 0) tIncPaint else tExpPaint
            canvas.drawText(CurrencyFormatter.formatCompact(net, ""), MARGIN + 310f, ry, netPaint)
            canvas.drawText(String.format("%.1f%%", savings), MARGIN + 380f, ry, tRowPaint)

            canvas.drawLine(MARGIN, ry + 6f, PAGE_WIDTH - MARGIN.toFloat(), ry + 6f, tRowDivPaint)
        }

        // ── Support & footer ──────────────────────────────────────────────────
        val supportY = PAGE_HEIGHT - 110f
        val supportBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#FFF8E1") }
        val supportRect = RectF(MARGIN, supportY, PAGE_WIDTH - MARGIN.toFloat(), supportY + 50f)
        canvas.drawRoundRect(supportRect, 8f, 8f, supportBgPaint)

        val supportBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#FFD700")
            style = Paint.Style.STROKE
            strokeWidth = 1f
        }
        canvas.drawRoundRect(supportRect, 8f, 8f, supportBorderPaint)

        val supportTitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#5D4037")
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("Support Solo Ledger", PAGE_WIDTH / 2f, supportY + 18f, supportTitlePaint)

        val supportLinkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#0288D1")
            textSize = 10f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(
            "buymeacoffee.com/mkr_infinity",
            PAGE_WIDTH / 2f, supportY + 36f, supportLinkPaint
        )

        // Footer divider
        val divPaint = Paint().apply { color = COL_LIGHT_GRAY; strokeWidth = 0.5f }
        canvas.drawLine(MARGIN, PAGE_HEIGHT - 42f, PAGE_WIDTH - MARGIN.toFloat(), PAGE_HEIGHT - 42f, divPaint)

        val footerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = COL_GRAY; textSize = 8f; textAlign = Paint.Align.CENTER
        }
        canvas.drawText(
            "Generated by Solo Ledger  •  Personal Finance Tracker",
            PAGE_WIDTH / 2f, PAGE_HEIGHT - 26f, footerPaint
        )
    }
}
