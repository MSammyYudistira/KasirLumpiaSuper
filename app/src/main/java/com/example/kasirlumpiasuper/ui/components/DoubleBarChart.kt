package com.example.kasirlumpiasuper.ui.components

import android.graphics.Color.DKGRAY
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.example.kasirlumpiasuper.ui.theme.Danger
import com.example.kasirlumpiasuper.ui.theme.Success
import com.example.kasirlumpiasuper.helper.date.DateUtils
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter

@Composable
fun SingleBarChart(
    income: List<Int>,
    labels: List<String>,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { context ->
            BarChart(context).apply {

                description.isEnabled = false
                legend.isEnabled = false

                val entries = income.mapIndexed { index, value ->
                    BarEntry(index.toFloat(), value.toFloat())
                }

                val dataSet = BarDataSet(entries, null).apply {
                    valueTextSize = 12f
                    setDrawValues(true)
                }
                dataSet.color = Color(0xFF2196F3).toArgb()

                data = BarData(dataSet)

                xAxis.apply {
                    valueFormatter = IndexAxisValueFormatter(
                        labels.map { DateUtils.shortDayLabelFromKey(it) }
                    )
                    position = XAxis.XAxisPosition.BOTTOM
                    granularity = 1f
                    setDrawGridLines(false)
                    setDrawAxisLine(false)
                    textSize = 10f
                    textColor = DKGRAY
                }

                axisLeft.apply {
                    setDrawGridLines(true)
                    setDrawAxisLine(false)
                    textSize = 10f
                    textColor = DKGRAY
                }

                axisRight.isEnabled = false

                animateY(800)
            }
        },
        modifier = modifier
    )
}



@Composable
fun GrowthLineChart(
    growthData: Map<String, Float>,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                axisRight.isEnabled = false
                legend.isEnabled = false

                xAxis.apply {
                    valueFormatter = IndexAxisValueFormatter(
                        growthData.keys.map { DateUtils.shortDayLabelFromKey(it) }
                    )
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    granularity = 1f
                    yOffset = 6f
                    textColor = DKGRAY
                }

                axisLeft.apply {
                    textColor = DKGRAY
                    setDrawGridLines(true)
                    gridColor = android.graphics.Color.LTGRAY
                    gridLineWidth = 0.5f
                }
            }
        },
        update = { chart ->
            if (growthData.isEmpty()) return@AndroidView

            val entries = growthData.values.mapIndexed { i, value ->
                Entry(i.toFloat(), value)
            }

            val dataSet = LineDataSet(entries, "Growth %").apply {
                color = Color(0xFF2196F3).toArgb()
                lineWidth = 2f
                circleRadius = 4f
                setCircleColor(Color(0xFF2196F3).toArgb())
                setDrawCircleHole(false)
                valueTextSize = 10f
                mode = LineDataSet.Mode.CUBIC_BEZIER

                valueFormatter = object : ValueFormatter() {
                    override fun getPointLabel(entry: Entry?): String {
                        val y = entry?.y ?: 0f
                        val symbol = if (y > 0) "+" else ""
                        return "$symbol${y.toInt()}%"
                    }
                }

                val textColors = entries.map { e ->
                    when {
                        e.y < 0f  -> Danger.toArgb()
                        e.y == 0f -> Color.DarkGray.toArgb()
                        else      -> Success.toArgb()
                    }
                }
                setValueTextColors(textColors)
            }

            val lineData = LineData(dataSet)
            chart.data = lineData

            val minY = (growthData.values.minOrNull() ?: -100f) - 10
            val maxY = (growthData.values.maxOrNull() ?: 100f) + 10
            chart.axisLeft.axisMinimum = minY
            chart.axisLeft.axisMaximum = maxY
            chart.axisLeft.labelCount = 5

            chart.animateY(800)
            chart.invalidate()
        }
    )
}



