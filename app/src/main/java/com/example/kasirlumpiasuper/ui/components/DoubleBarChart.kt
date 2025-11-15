package com.example.kasirlumpiasuper.ui.components

import android.R.attr.text
import android.graphics.Color.DKGRAY
import android.graphics.Color.toArgb
import android.provider.SyncStateContract.Helpers.update
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.kasirlumpiasuper.ui.theme.Danger
import com.example.kasirlumpiasuper.ui.theme.Success
import com.example.kasirlumpiasuper.ui.theme.Warning
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
import androidx.core.graphics.toColorInt
import com.example.kasirlumpiasuper.ui.theme.PrimaryBold

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

                // 🔹 Konfigurasi Legend
                legend.apply {
                    isEnabled = true
                    textSize = 12f
                    xEntrySpace = 12f
                    yOffset = 10f // 🔹 turunkan posisi legend biar gak nempel chart
                    verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
//                    horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                    orientation = Legend.LegendOrientation.HORIZONTAL
                    setDrawInside(false) // 🔹 legend di luar area chart
                }

                // 🔹 Konfigurasi Sumbu X
                xAxis.apply {
                    valueFormatter = IndexAxisValueFormatter(
                        labels.map { DateUtils.shortDayLabelFromKey(it) }
                    )
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    granularity = 1f
                    yOffset = 6f // 🔹 geser label tanggal agak turun
                    setCenterAxisLabels(true) // 🔹 biar label rata tengah di antara dua bar
                    textColor = android.graphics.Color.DKGRAY
                    textSize = 12f
                }

                // 🔹 Konfigurasi Sumbu Y (kiri)
                axisLeft.apply {
                    textColor = android.graphics.Color.DKGRAY
                    setDrawGridLines(true)
                    gridColor = android.graphics.Color.LTGRAY
                    gridLineWidth = 0.5f // garis halus
                }

                // 🔹 Tambahkan sedikit padding di sisi kanan/kiri
                setExtraOffsets(8f, 0f, 8f, 0f)
            }
        },
        update = { chart ->
            // 🔹 Jangan update kalau belum ada data
            if (income.isEmpty() || cash.isEmpty() || labels.isEmpty()) return@AndroidView

            val barEntriesIncome =
                income.mapIndexed { i, v -> BarEntry(i.toFloat(), v.toFloat()) }
            val barEntriesCash =
                cash.mapIndexed { i, v -> BarEntry(i.toFloat(), v.toFloat()) }

            val dataSetIncome = BarDataSet(barEntriesIncome, "Pendapatan").apply {
                color = Color(0xFF2196F3).toArgb() // biru
                valueTextColor = android.graphics.Color.BLACK
                valueTextSize = 10f
            }

            val dataSetCash = BarDataSet(barEntriesCash, "Pengeluaran").apply {
                color = Danger.toArgb() // oranye
                valueTextColor = android.graphics.Color.BLACK
                valueTextSize = 10f
            }

            // 🔹 Setup BarData
            val barData = BarData(dataSetIncome, dataSetCash)
            val groupSpace = 0.26f // jarak antar grup bar (lebih kecil supaya muat semua)
            val barSpace = 0.04f   // jarak antar bar dalam satu grup
            val barWidth = 0.33f   // lebar batang

            barData.barWidth = barWidth
            chart.data = barData

            val groupCount = labels.size
            val groupWidth = barData.getGroupWidth(groupSpace, barSpace)

            // 🔹 Pastikan semua bar dan label terakhir kelihatan penuh
            chart.xAxis.axisMinimum = 0f
            chart.xAxis.axisMaximum = groupWidth * groupCount + 0.4f // padding kanan

            // 🔹 Terapkan pengelompokan bar
            chart.groupBars(0f, groupSpace, barSpace)

            // 🔹 Animasi biar smooth
            chart.animateY(800)
            chart.invalidate()
        }
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
                    textColor = android.graphics.Color.DKGRAY
                }

                axisLeft.apply {
                    textColor = android.graphics.Color.DKGRAY
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

                // ✅ Format label jadi "10%" atau "-5%"
                valueFormatter = object : ValueFormatter() {
                    override fun getPointLabel(entry: Entry?): String {
                        val y = entry?.y ?: 0f
                        val symbol = if (y > 0) "+" else "" // tambahkan + untuk nilai positif
                        return "$symbol${y.toInt()}%"
                    }
                }

                // ✅ Pewarnaan titik label per data
                val textColors = entries.map { e ->
                    when {
                        e.y < 0f  -> Danger.toArgb()       // merah
                        e.y == 0f -> Color.DarkGray.toArgb()  // netral
                        else      -> Success.toArgb()      // hijau
                    }
                }
                setValueTextColors(textColors)
            }

            val lineData = LineData(dataSet)
            chart.data = lineData

            // ✅ Sumbu Y dinamis biar tidak terpotong
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



