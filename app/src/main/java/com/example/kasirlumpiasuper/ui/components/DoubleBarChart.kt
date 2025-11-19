package com.example.kasirlumpiasuper.ui.components

import android.graphics.Color.DKGRAY
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.kasirlumpiasuper.ui.theme.Danger
import com.example.kasirlumpiasuper.ui.theme.Success
import com.example.kasirlumpiasuper.ui.utils.DateUtils
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun DoubleBarChart(
    income: List<Int>,
    cash: List<Int>,
    labels: List<String>
) {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
        factory = { context ->
            BarChart(context).apply {
                description.isEnabled = false
                axisRight.isEnabled = false
                legend.apply {
                    isEnabled = true
                    textSize = 12f
                    xEntrySpace = 12f
                    yOffset = 10f
                    verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                    orientation = Legend.LegendOrientation.HORIZONTAL
                    setDrawInside(false)
                }

                xAxis.apply {
                    valueFormatter = IndexAxisValueFormatter(
                        labels.map { DateUtils.shortDayLabelFromKey(it) }
                    )
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    granularity = 1f
                    yOffset = 6f
                    setCenterAxisLabels(true)
                    textColor = DKGRAY
                    textSize = 12f
                }

                axisLeft.apply {
                    textColor = DKGRAY
                    setDrawGridLines(true)
                    gridColor = android.graphics.Color.LTGRAY
                    gridLineWidth = 0.5f // garis halus
                }
                setExtraOffsets(8f, 0f, 8f, 0f)
            }
        },
        update = { chart ->
            if (income.isEmpty() || cash.isEmpty() || labels.isEmpty()) return@AndroidView

            val barEntriesIncome =
                income.mapIndexed { i, v -> BarEntry(i.toFloat(), v.toFloat()) }
            val barEntriesCash =
                cash.mapIndexed { i, v -> BarEntry(i.toFloat(), v.toFloat()) }

            val dataSetIncome = BarDataSet(barEntriesIncome, "Pendapatan").apply {
                color = Color(0xFF2196F3).toArgb()
                valueTextColor = android.graphics.Color.BLACK
                valueTextSize = 10f
            }

            val dataSetCash = BarDataSet(barEntriesCash, "Pengeluaran").apply {
                color = Danger.toArgb()
                valueTextColor = android.graphics.Color.BLACK
                valueTextSize = 10f
            }

            val barData = BarData(dataSetIncome, dataSetCash)
            val groupSpace = 0.26f
            val barSpace = 0.04f
            val barWidth = 0.33f

            barData.barWidth = barWidth
            chart.data = barData

            val groupCount = labels.size
            val groupWidth = barData.getGroupWidth(groupSpace, barSpace)

            chart.xAxis.axisMinimum = 0f
            chart.xAxis.axisMaximum = groupWidth * groupCount + 0.4f

            chart.groupBars(0f, groupSpace, barSpace)
            chart.animateY(800)
            chart.invalidate()
        }
    )
}

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



