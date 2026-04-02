package com.mkr.soloLedger.ui.analytics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.mkr.soloLedger.databinding.FragmentAnalyticsBinding
import com.mkr.soloLedger.viewmodel.AnalyticsViewModel
import com.mkr.soloLedger.viewmodel.CategoryViewModel

class AnalyticsFragment : Fragment() {

    private var _binding: FragmentAnalyticsBinding? = null
    private val binding get() = _binding!!

    private val analyticsViewModel: AnalyticsViewModel by viewModels()
    private val categoryViewModel: CategoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalyticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        analyticsViewModel.loadCurrentMonthCategoryTotals()
        analyticsViewModel.loadLast6MonthsTotals()

        setupPieChart()
        setupBarChart()
        observeData()
    }

    private fun setupPieChart() {
        binding.pieChart.apply {
            description.isEnabled = false
            isDrawHoleEnabled = true
            holeRadius = 40f
            setHoleColor(Color.TRANSPARENT)
            legend.isEnabled = true
            setEntryLabelColor(Color.WHITE)
            setEntryLabelTextSize(12f)
        }
    }

    private fun setupBarChart() {
        binding.barChart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            xAxis.setDrawGridLines(false)
            axisLeft.setDrawGridLines(true)
            axisRight.isEnabled = false
        }
    }

    private fun observeData() {
        val categoryColors = mutableMapOf<Long, String>()
        categoryViewModel.allCategories.observe(viewLifecycleOwner) { categories ->
            categories.forEach { categoryColors[it.id] = it.color }
        }

        analyticsViewModel.categoryTotals.observe(viewLifecycleOwner) { totals ->
            if (totals.isEmpty()) {
                binding.pieChart.visibility = View.GONE
                binding.tvNoPieData.visibility = View.VISIBLE
                return@observe
            }
            binding.pieChart.visibility = View.VISIBLE
            binding.tvNoPieData.visibility = View.GONE

            val entries = totals.map { PieEntry(it.total.toFloat(), "Cat ${it.categoryId}") }
            val colors = totals.map { ct ->
                try { Color.parseColor(categoryColors[ct.categoryId] ?: "#6750A4") }
                catch (e: Exception) { Color.parseColor("#6750A4") }
            }
            val dataSet = PieDataSet(entries, "").apply {
                this.colors = colors
                valueTextSize = 12f
                valueTextColor = Color.WHITE
            }
            binding.pieChart.data = PieData(dataSet)
            binding.pieChart.invalidate()
        }

        analyticsViewModel.monthlyTotals.observe(viewLifecycleOwner) { monthly ->
            if (monthly.isEmpty()) return@observe

            val entries = monthly.mapIndexed { i, pair -> BarEntry(i.toFloat(), pair.second.toFloat()) }
            val labels = monthly.map { it.first }

            val dataSet = BarDataSet(entries, "Monthly Spending").apply {
                color = Color.parseColor("#6750A4")
                valueTextSize = 10f
            }
            binding.barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            binding.barChart.data = BarData(dataSet)
            binding.barChart.invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
