package com.solo.ledger.ui.components.charts

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.shape.Shape

data class TrendPoint(
    val label: String,
    val value: Float
)

@Composable
fun BalanceTrendChart(
    trendData: List<TrendPoint>,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val labelColor = MaterialTheme.colorScheme.onSurface
    val transparent = Color.Transparent

    val modelProducer = remember { CartesianChartModelProducer() }

    remember(trendData) {
        modelProducer.runTransaction {
            lineSeries {
                series(trendData.map { it.value })
            }
        }
    }

    val pointComponent = rememberShapeComponent(
        shape = Shape.Pill,
        color = primaryColor
    )

    val lineLayer = rememberLineCartesianLayer(
        lineProvider = LineCartesianLayer.LineProvider.series(
            LineCartesianLayer.rememberLine(
                fill = remember(primaryColor, transparent) {
                    LineCartesianLayer.LineFill.double(
                        topFill = fill(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(primaryColor.copy(alpha = 0.35f), transparent)
                            )
                        ),
                        bottomFill = fill(transparent)
                    )
                },
                areaFill = remember(primaryColor, transparent) {
                    LineCartesianLayer.AreaFill.single(
                        fill(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(primaryColor.copy(alpha = 0.25f), transparent)
                            )
                        )
                    )
                },
                pointProvider = LineCartesianLayer.PointProvider.single(
                    LineCartesianLayer.Point(
                        component = pointComponent,
                        sizeDp = 8f
                    )
                )
            )
        )
    )

    val labelTextComponent = rememberTextComponent(
        color = labelColor.copy(alpha = 0.7f)
    )

    CartesianChartHost(
        chart = rememberCartesianChart(
            lineLayer,
            startAxis = rememberStartAxis(
                label = labelTextComponent
            ),
            bottomAxis = rememberBottomAxis(
                label = labelTextComponent,
                valueFormatter = { _, value, _ ->
                    trendData.getOrNull(value.toInt())?.label ?: ""
                },
                itemPlacer = AxisItemPlacer.Horizontal.default(spacing = 1)
            )
        ),
        modelProducer = modelProducer,
        modifier = modifier
    )
}
