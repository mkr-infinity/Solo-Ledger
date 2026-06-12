package com.solo.ledger.ui.components.charts

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import com.solo.ledger.ui.theme.extendedColors

data class MonthlyData(
    val month: String,
    val income: Float,
    val expense: Float
)

@Composable
fun IncomeExpenseBarChart(
    monthlyData: List<MonthlyData>,
    modifier: Modifier = Modifier
) {
    val extColors = extendedColors
    val incomeColor  = extColors.income
    val expenseColor = extColors.expense
    val labelColor   = MaterialTheme.colorScheme.onSurface

    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(monthlyData) {
        modelProducer.runTransaction {
            columnSeries {
                series(monthlyData.map { it.income })
                series(monthlyData.map { it.expense })
            }
        }
    }

    val incomeColumn = rememberLineComponent(
        fill = fill(incomeColor),
        thickness = 16.dp,
        shape = CorneredShape.rounded(topLeftPercent = 30, topRightPercent = 30)
    )
    val expenseColumn = rememberLineComponent(
        fill = fill(expenseColor),
        thickness = 16.dp,
        shape = CorneredShape.rounded(topLeftPercent = 30, topRightPercent = 30)
    )

    val columnLayer = rememberColumnCartesianLayer(
        columnProvider = ColumnCartesianLayer.ColumnProvider.series(
            incomeColumn,
            expenseColumn
        ),
        mergeMode = { ColumnCartesianLayer.MergeMode.Grouped() }
    )

    val labelTextComponent = rememberTextComponent(
        color = labelColor.copy(alpha = 0.7f)
    )

    CartesianChartHost(
        chart = rememberCartesianChart(
            columnLayer,
            startAxis = VerticalAxis.rememberStart(
                label = labelTextComponent
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                label = labelTextComponent,
                valueFormatter = { _, value, _ ->
                    monthlyData.getOrNull(value.toInt())?.month ?: ""
                },
                itemPlacer = HorizontalAxis.ItemPlacer.aligned(spacing = 1)
            )
        ),
        modelProducer = modelProducer,
        modifier = modifier
    )
}
