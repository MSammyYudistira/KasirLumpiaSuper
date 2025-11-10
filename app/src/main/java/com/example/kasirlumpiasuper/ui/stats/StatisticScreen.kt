package com.example.kasirlumpiasuper.ui.stats

import android.R.attr.entries
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.kasirlumpiasuper.ui.theme.Success
import com.example.kasirlumpiasuper.ui.theme.Danger
import com.example.kasirlumpiasuper.ui.theme.Primary
import com.example.kasirlumpiasuper.ui.utils.DateUtils
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModel
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries

import java.util.*

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticScreen(
    navController: NavController,
    viewModel: StatisticViewModel = viewModel()
) {
    val dailyRevenue by viewModel.dailyRevenue.collectAsState()
    val totalWeekly by viewModel.totalWeekly.collectAsState()
    val averageDaily by viewModel.averageDaily.collectAsState()
    val growthPercent by viewModel.growthPercent.collectAsState()
    val selectedWeek by viewModel.selectedWeek.collectAsState()
    val weeksOfMonth by viewModel.weeksOfMonth.collectAsState()

    var expanded by remember { mutableStateOf(false) }
    var selectedMonth by remember { mutableStateOf(Calendar.getInstance().get(Calendar.MONTH)) }
    var selectedYear by remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR)) }

    LaunchedEffect(Unit) {
        viewModel.getWeeksOfMonth(selectedYear, selectedMonth)
        viewModel.loadWeeklyRevenue(selectedYear, selectedMonth, 0)
    }

    Scaffold(
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            /** 🔹 Dropdown Pilih Minggu */
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    value = selectedWeek?.label ?: "Pilih Minggu",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Periode Mingguan") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    weeksOfMonth.forEachIndexed { index, week ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(week.label, fontWeight = FontWeight.Bold)
                                    Text("${week.startDate} – ${week.endDate}", style = MaterialTheme.typography.bodySmall)
                                }
                            },
                            onClick = {
                                viewModel.loadWeeklyRevenue(selectedYear, selectedMonth, index)
                                expanded = false
                            }
                        )
                    }
                }
            }

            /** 🔹 Grafik Pendapatan */
            Surface(
                shape = RoundedCornerShape(8.dp),
                shadowElevation = 3.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text("Grafik Pendapatan", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(16.dp))

                    val dailyRevenueList = dailyRevenue.values.toList()
                    val labels = dailyRevenue.keys.map { DateUtils.getDayName(it) }

                    val modelProducer = remember { CartesianChartModelProducer() }

                    LaunchedEffect(dailyRevenueList) {
                        modelProducer.runTransaction {
                            columnSeries {
                                series(dailyRevenueList)
                            }
                        }
                    }

                    if (dailyRevenueList.isNotEmpty()){
                        CartesianChartHost(
                            chart =
                                rememberCartesianChart(
                                    rememberColumnCartesianLayer(),
                                    startAxis = VerticalAxis.rememberStart(),
                                    bottomAxis = HorizontalAxis.rememberBottom(
                                        guideline = null,
                                        valueFormatter = { _, x, _ ->
                                            val index = x.toInt()
                                            labels.getOrNull(index) ?: ""
                                        }
                                    ),
                                ),
                            modelProducer = modelProducer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp)
                        )

                    } else {
                        Text("Belum ada data minggu ini.", color = Color.Gray)
                    }
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.SpaceEvenly,
//                        verticalAlignment = Alignment.Bottom
//                    ) {
//                        val max = (dailyRevenue.values.maxOrNull() ?: 1)
//                        dailyRevenue.forEach { (day, value) ->
//                            val heightRatio = value.toFloat() / max
//                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                                Text(
//                                    "Rp ${value / 1000}K",
//                                    style = MaterialTheme.typography.bodySmall,
//                                    fontWeight = FontWeight.Medium
//                                )
//                                Box(
//                                    modifier = Modifier
//                                        .height((120 * heightRatio).dp)
//                                        .width(20.dp)
//                                        .padding(vertical = 4.dp)
//                                        .background(Primary)
//                                )
//                                Text(
//                                    DateUtils.getDayName(day),
//                                    style = MaterialTheme.typography.bodySmall
//                                )
//                            }
//                        }
//                    }
                }
            }

            /** 🔹 Row Ringkasan */
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    SummaryCard(
                        title = "Total Pendapatan",
                        value = DateUtils.rupiah(totalWeekly)
                    )
                }
                item {
                    SummaryCard(
                        title = "Rata-rata Harian",
                        value = DateUtils.rupiah(averageDaily)
                    )
                }
                item {
                    SummaryCard(
                        title = "Pertumbuhan",
                        value = String.format(
                            "%.1f%%",
                            growthPercent ?: 0f
                        ),
                        valueColor = when {
                            (growthPercent ?: 0f) > 0 -> Success
                            (growthPercent ?: 0f) < 0 -> Danger
                            else -> Color.Gray
                        }
                    )
                }
            }
        }
    }
}

/** 🔹 Kartu Ringkasan */
@Composable
fun SummaryCard(
    title: String,
    value: String,
    valueColor: Color = Color.Black
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 2.dp,
        modifier = Modifier.width(220.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(title, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Spacer(Modifier.height(8.dp))
            Text(
                value,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = valueColor
            )
        }
    }
}