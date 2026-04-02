package com.mkr.soloLedger.ui.importexport

import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.gson.Gson
import com.mkr.soloLedger.databinding.FragmentImportExportBinding
import com.mkr.soloLedger.utils.toFormattedDateTime
import com.mkr.soloLedger.viewmodel.ExpenseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter

class ImportExportFragment : Fragment() {

    private var _binding: FragmentImportExportBinding? = null
    private val binding get() = _binding!!

    private val expenseViewModel: ExpenseViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImportExportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnExportJson.setOnClickListener { exportJson() }
        binding.btnExportPdf.setOnClickListener { exportPdf() }
        binding.btnImportJson.setOnClickListener {
            Toast.makeText(requireContext(), "Select a JSON file to import", Toast.LENGTH_SHORT).show()
        }
    }

    private fun exportJson() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val expenses = expenseViewModel.allExpenses.value ?: emptyList()
                val json = Gson().toJson(expenses)
                val dir = requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                    ?: requireContext().filesDir
                val file = File(dir, "solo_ledger_backup_${System.currentTimeMillis()}.json")
                FileWriter(file).use { it.write(json) }
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Exported to ${file.absolutePath}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun exportPdf() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val expenses = expenseViewModel.allExpenses.value ?: emptyList()
                val pdfDocument = PdfDocument()
                val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
                val page = pdfDocument.startPage(pageInfo)
                val canvas = page.canvas
                val paint = Paint().apply { textSize = 12f }

                canvas.drawText("Solo Ledger - Expense Report", 40f, 40f, paint.apply { textSize = 18f; isFakeBoldText = true })
                canvas.drawText("Generated: ${System.currentTimeMillis().toFormattedDateTime()}", 40f, 65f, paint.apply { textSize = 10f; isFakeBoldText = false })

                var y = 100f
                paint.textSize = 11f
                expenses.take(50).forEach { expense ->
                    canvas.drawText("${expense.date.toFormattedDate()} | ${expense.title} | ${"%.2f".format(expense.amount)}", 40f, y, paint)
                    y += 20f
                    if (y > 800f) return@forEach
                }

                pdfDocument.finishPage(page)
                val dir = requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                    ?: requireContext().filesDir
                val file = File(dir, "solo_ledger_report_${System.currentTimeMillis()}.pdf")
                pdfDocument.writeTo(file.outputStream())
                pdfDocument.close()

                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "PDF exported to ${file.absolutePath}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "PDF export failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun Long.toFormattedDate(): String = this.toFormattedDateTime().split(",").firstOrNull() ?: ""

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
