package com.solo.ledger.ui.components.charts

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.shader.ShaderProvider
import com.patrykandpatrick.vico.core.common.shape.CorneredShape

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

    LaunchedEffect(trendData) {
        modelProducer.runTransaction {
            lineSeries {
                series(trendData.map { it.value })
            }
        }
    }

    val pointComponent = rememberShapeComponent(
        shape = CorneredShape.Pill,
        fill = fill(primaryColor)
    )

    val lineLayer = rememberLineCartesianLayer(
        lineProvider = LineCartesianLayer.LineProvider.series(
            rememberLine(
                fill = remember(primaryColor, transparent) {
                    LineCartesianLayer.LineFill.double(
                        topFill = fill(
                            ShaderProvider.verticalGradient(
                                primaryColor.copy(alpha = 0.35f).toArgb(),
                                transparent.toArgb()
                            )
                        ),
                        bottomFill = fill(transparent)
                    )
                },
                areaFill = remember(primaryColor, transparent) {
                    LineCartesianLayer.AreaFill.single(
                        fill(
                            ShaderProvider.verticalGradient(
                                primaryColor.copy(alpha = 0.25f).toArgb(),
                                transparent.toArgb()
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
            startAxis = VerticalAxis.rememberStart(
                label = labelTextComponent
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                label = labelTextComponent,
                valueFormatter = { _, value, _ ->
                    trendData.getOrNull(value.toInt())?.label ?: ""
                },
                itemPlacer = HorizontalAxis.ItemPlacer.aligned(spacing = 1)
            )
        ),
        modelProducer = modelProducer,
        modifier = modifier
    )
}
