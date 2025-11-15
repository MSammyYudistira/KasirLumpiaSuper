package com.example.kasirlumpiasuper.ui.stats

import android.R.attr.data
import android.R.attr.entries
import android.R.attr.onClick
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.kasirlumpiasuper.R
import com.example.kasirlumpiasuper.ui.theme.Success
import com.example.kasirlumpiasuper.ui.theme.Danger
import com.example.kasirlumpiasuper.ui.theme.Primary
import com.example.kasirlumpiasuper.ui.theme.PrimaryBold
import com.example.kasirlumpiasuper.ui.theme.Secondary
import com.example.kasirlumpiasuper.ui.utils.DateUtils
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import android.app.DatePickerDialog
import android.widget.Space
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.platform.LocalContext
import com.example.kasirlumpiasuper.data.model.ExpenseSummary
import com.example.kasirlumpiasuper.ui.components.DoubleBarChart
import com.example.kasirlumpiasuper.ui.components.GrowthLineChart
import com.example.kasirlumpiasuper.ui.history.showDatePicker
import okhttp3.internal.format
import java.text.SimpleDateFormat
import java.util.*

import java.util.*
import java.util.stream.Collectors.toList

@SuppressLint("DefaultLocale", "UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticScreen(
    navController: NavController,
    viewModel: StatisticViewModel = viewModel()
) {
    val context = LocalContext.current
    val dailyRevenue by viewModel.dailyRevenue.collectAsState()
    val totalWeekly by viewModel.totalWeekly.collectAsState()
    val averageDaily by viewModel.averageDaily.collectAsState()
    val growthPercent by viewModel.growthPercent.collectAsState()
    val selectedWeek by viewModel.selectedWeek.collectAsState()
    val weeksOfMonth by viewModel.weeksOfMonth.collectAsState()
    val isLoadingChart by viewModel.isLoadingChart.collectAsState()
    val format = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))

    var selectedDateLabel by remember { mutableStateOf("Pilih Tanggal") }
    var expanded by remember { mutableStateOf(false) }
    var selectedMonth by remember { mutableStateOf(Calendar.getInstance().get(Calendar.MONTH)) }
    var selectedYear by remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    var incomeData by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    var expenseData by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
//    var selectedRangeLabel by remember { mutableStateOf(format.format(Date())) }
    var cashData by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    // label tanggal yang dipilih
    var selectedRangeLabel by remember {
        mutableStateOf(
            SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(Date())
        )
    }



    LaunchedEffect(Unit) {
        viewModel.loadRevenueAndExpenseRange(Date()) { i, e ->
            incomeData = i
            expenseData = e
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getWeeksOfMonth(selectedYear, selectedMonth)
        viewModel.loadWeeklyRevenue(selectedYear, selectedMonth, 0)
    }

    Scaffold {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 72.dp,),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            /** 🔹 Grafik Pendapatan */
            item {
                Spacer(Modifier.height(24.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = 3.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text("Grafik Pendapatan", style = MaterialTheme.typography.displaySmall)
                        Spacer(Modifier.height(4.dp))
                        LaunchedEffect(Unit) {
                            val now = Date()
                            selectedRangeLabel = format.format(now)

                            viewModel.loadRevenueAndExpenseRange(now) { i, e ->
                                incomeData = i
                                expenseData = e
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            shadowElevation = 4.dp,
                            onClick = {
                                val cal = Calendar.getInstance()
                                DatePickerDialog(
                                    context,
                                    { _, year, month, day ->
                                        val picked =
                                            Calendar.getInstance()
                                                .apply { set(year, month, day) }.time
                                        val format =
                                            SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
                                        selectedRangeLabel = format.format(picked)

                                        viewModel.loadRevenueAndExpenseRange(picked) { i, e ->
                                            incomeData = i
                                            expenseData = e
                                        }
                                    },
                                    cal.get(Calendar.YEAR),
                                    cal.get(Calendar.MONTH),
                                    cal.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)

                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_date_range_24),
                                    contentDescription = "date picker",
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = selectedRangeLabel,
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        if (isLoadingChart) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(240.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = Primary,
                                    strokeWidth = 4.dp
                                )
                            }
                        } else {
                            DoubleBarChart(
                                income = incomeData.values.toList(),
                                cash = expenseData.values.toList(),
                                labels = incomeData.keys.toList()
                            )
                        }
                    }
                }
            }

            /** 🔹 Grafik Pertumbuhan Pendapatan */
            item {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = 3.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            "Pertumbuhan Pendapatan",
                            style = MaterialTheme.typography.displaySmall
                        )
                        Spacer(Modifier.height(8.dp))

                        var selectedPeriod by remember { mutableStateOf("Minggu Ini") }
                        var growthData by remember { mutableStateOf<Map<String, Float>>(emptyMap()) }
                        val isLoadingGrowth by viewModel.isLoadingGrowth.collectAsState()

                        LaunchedEffect(Unit) {
                            viewModel.loadGrowthData(7) { data ->
                                growthData = data
                            }
                        }

                        // Dropdown filter
                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = selectedPeriod,
                                onValueChange = {},
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                readOnly = true,
                                label = { Text("Periode") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }) {
                                listOf("Minggu Ini", "Bulan Ini").forEach { label ->
                                    DropdownMenuItem(
                                        text = { Text(label) },
                                        onClick = {
                                            selectedPeriod = label
                                            expanded = false
                                            val days = if (label == "Minggu Ini") 7 else 30

                                            viewModel.loadGrowthData(days) { data ->
                                                growthData = data
                                            }
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        if (isLoadingGrowth) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(240.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Primary, strokeWidth = 4.dp)
                            }
                        } else {
                            GrowthLineChart(
                                growthData = growthData,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(260.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }


//            /** 🔹 Row Ringkasan */
//            item {
//                Row(
//                    horizontalArrangement = Arrangement.spacedBy(32.dp),
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    SummaryCard(
//                        title = "Total Pendapatan",
//                        value = DateUtils.rupiah(totalWeekly),
//                        modifier = Modifier.weight(1f),
//                        icon = painterResource(R.drawable.outline_edit_square_24),
//                    )
//                    SummaryCard(
//                        title = "Rata-rata Harian",
//                        value = DateUtils.rupiah(averageDaily),
//                        modifier = Modifier.weight(1f),
//                        icon = painterResource(R.drawable.outline_edit_square_24),
//                    )
//                    SummaryCard(
//                        title = "Pertumbuhan",
//                        value = String.format(
//                            "%.1f%%",
//                            growthPercent ?: 0f
//                        ),
//                        valueColor = when {
//                            (growthPercent ?: 0f) > 0 -> Success
//                            (growthPercent ?: 0f) < 0 -> Danger
//                            else -> Color.Black
//                        },
//                        modifier = Modifier.weight(1f),
//                        icon = painterResource(R.drawable.outline_edit_square_24),
//                    )
//                }
//            }
        }
    }
}

@Composable
fun WeeklyRevenueBarChart(
    data: Map<String, Int>, // dari dailyRevenue
    modifier: Modifier = Modifier
) {
    val entries = data.entries.mapIndexed { index, (label, value) ->
        BarEntry(index.toFloat(), value.toFloat())
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            BarChart(context).apply {
                setFitBars(true)
                description.isEnabled = false
                axisRight.isEnabled = false
                legend.isEnabled = false

                // sumbu X (hari)
                xAxis.apply {
                    valueFormatter = IndexAxisValueFormatter(data.keys.toList())
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    granularity = 1f
                    textColor = android.graphics.Color.DKGRAY
                }

                // sumbu Y kiri
                axisLeft.apply {
                    textColor = android.graphics.Color.DKGRAY
                    setDrawGridLines(true)
                    gridColor = android.graphics.Color.LTGRAY
                }
            }
        },
        update = { chart ->
            val dataSet = BarDataSet(entries, "Pendapatan Harian").apply {
                color = Primary.hashCode() // ubah sesuai warna tema
                valueTextColor = android.graphics.Color.BLACK
                valueTextSize = 12f
            }

            chart.data = BarData(dataSet)
            chart.invalidate() // refresh
        }
    )
}

/** 🔹 Kartu Ringkasan */
@Composable
fun SummaryCard(
    title: String,
    value: String,
    modifier: Modifier,
    icon: Painter,
    valueColor: Color = Color.Black
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 2.dp,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Secondary,
                modifier = Modifier.padding(end = 12.dp)
            ) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = PrimaryBold,
                    modifier = Modifier.padding(10.dp)
                )
            }

            Column(
            ) {
                Text(title, style = MaterialTheme.typography.bodyMedium)
                Text(
                    value,
                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                    color = valueColor
                )
            }
        }
    }
}