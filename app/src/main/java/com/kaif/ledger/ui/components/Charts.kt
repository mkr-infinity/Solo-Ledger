package com.kaif.ledger.ui.components

import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.time.DayOfWeek
import java.time.LocalDate

/**
 * Pie Chart showing category breakdown
 */
@Composable
fun CategoryPieChart(
    categoryData: Map<String, Float>,
    modifier: Modifier = Modifier
) {
    val colors = listOf(
        0xFF7C5AF4, // Purple - Primary
        0xFF9C6FD4, // Secondary
        0xFFF4A460, // Orange
        0xFF1976D2, // Blue
        0xFFFF6B6B, // Red
        0xFF4ECDC4, // Teal
        0xFF95E1D3, // Mint
        0xFFC7CEEA  // Light Purple
    ).map { it.toInt() }

    AndroidView(
        factory = { context ->
            PieChart(context).apply {
                val entries = categoryData.entries.mapIndexed { index, (category, value) ->
                    PieEntry(value, category)
                }
                val dataset = PieDataSet(entries, "Categories").apply {
                    this.colors = colors
                    valueTextSize = 12f
                    sliceSpace = 2f
                }
                data = PieData(dataset)
                description.isEnabled = false
                legend.apply {
                    isEnabled = true
                    orientation = Legend.LegendOrientation.VERTICAL
                    verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                    horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                    textSize = 10f
                }
                setEntryLabelColor(Color.WHITE)
                invalidate()
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
    )
}

/**
 * Bar Chart for weekly comparison
 */
@Composable
fun WeeklyComparisonChart(
    weeklyData: List<Float>,
    modifier: Modifier = Modifier
) {
    val dayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    
    AndroidView(
        factory = { context ->
            BarChart(context).apply {
                val entries = weeklyData.mapIndexed { index, value ->
                    BarEntry(index.toFloat(), value)
                }
                val dataset = BarDataSet(entries, "Weekly Outflow").apply {
                    color = 0xFF7C5AF4.toInt()
                    valueTextSize = 10f
                }
                data = BarData(dataset)
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    valueFormatter = IndexAxisValueFormatter(dayLabels)
                    setDrawGridLines(false)
                }
                description.isEnabled = false
                legend.isEnabled = false
                axisLeft.axisMinimum = 0f
                axisRight.isEnabled = false
                invalidate()
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
    )
}

/**
 * Line Chart for spending trends
 */
@Composable
fun SpendingTrendChart(
    trendData: List<Float>,
    modifier: Modifier = Modifier,
    label: String = "Spending Trend"
) {
    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                val entries = trendData.mapIndexed { index, value ->
                    Entry(index.toFloat(), value)
                }
                val dataset = LineDataSet(entries, label).apply {
                    color = 0xFF7C5AF4.toInt()
                    setCircleColor(0xFF7C5AF4.toInt())
                    lineWidth = 2f
                    circleRadius = 4f
                    setDrawValues(false)
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                    cubicIntensity = 0.2f
                }
                data = LineData(dataset)
                xAxis.apply {
                    setDrawGridLines(false)
                    position = XAxis.XAxisPosition.BOTTOM
                }
                description.isEnabled = false
                legend.isEnabled = false
                axisRight.isEnabled = false
                invalidate()
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
    )
}

/**
 * Simple Bar chart for daily spending
 */
@Composable
fun DailySpendingChart(
    dailyData: Map<String, Float>,
    modifier: Modifier = Modifier
) {
    val days = dailyData.keys.toList()
    val values = dailyData.values.toList()

    AndroidView(
        factory = { context ->
            BarChart(context).apply {
                val entries = values.mapIndexed { index, value ->
                    BarEntry(index.toFloat(), value)
                }
                val dataset = BarDataSet(entries, "Daily Spending").apply {
                    color = 0xFF7C5AF4.toInt()
                    valueTextSize = 10f
                }
                data = BarData(dataset)
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    valueFormatter = IndexAxisValueFormatter(days)
                    setDrawGridLines(false)
                    labelRotationAngle = 45f
                }
                description.isEnabled = false
                legend.isEnabled = false
                axisLeft.axisMinimum = 0f
                axisRight.isEnabled = false
                invalidate()
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
    )
}

/**
 * Donut Chart for budget vs spent
 */
@Composable
fun BudgetVsSpentDonut(
    spent: Float,
    budget: Float,
    modifier: Modifier = Modifier
) {
    val remaining = maxOf(0f, budget - spent)
    
    AndroidView(
        factory = { context ->
            PieChart(context).apply {
                val entries = listOf(
                    PieEntry(spent, "Spent"),
                    PieEntry(remaining, "Remaining")
                )
                val dataset = PieDataSet(entries, "Budget").apply {
                    colors = listOf(
                        Color.parseColor("#FF6B6B"),
                        Color.parseColor("#7C5AF4")
                    )
                    valueTextSize = 12f
                }
                data = PieData(dataset)
                description.isEnabled = false
                legend.apply {
                    isEnabled = true
                    orientation = Legend.LegendOrientation.HORIZONTAL
                    verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                    horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                }
                holeRadius = 45f
                transparentCircleRadius = 50f
                setEntryLabelColor(Color.WHITE)
                invalidate()
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
    )
}
