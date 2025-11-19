package com.example.kasirlumpiasuper.ui.stats

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.kasirlumpiasuper.R
import com.example.kasirlumpiasuper.ui.components.DoubleBarChart
import com.example.kasirlumpiasuper.ui.components.GrowthLineChart
import com.example.kasirlumpiasuper.ui.components.SingleBarChart
import com.example.kasirlumpiasuper.ui.theme.Primary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@SuppressLint("DefaultLocale", "UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticScreen(
    navController: NavController,
    viewModel: StatisticViewModel = viewModel()
) {
    val context = LocalContext.current
    val isLoadingChart by viewModel.isLoadingChart.collectAsState()
    val format = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))

    var selectedMonth by remember { mutableStateOf(Calendar.getInstance().get(Calendar.MONTH)) }
    var selectedYear by remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    var incomeData by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }

    // label tanggal yang dipilih
    var selectedRangeLabel by remember {
        mutableStateOf(
            SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(Date())
        )
    }



    LaunchedEffect(Unit) {
        viewModel.loadRevenueRange(Date()) { i ->
            incomeData = i
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
                .padding(horizontal = 72.dp),
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
                        Text(
                            "Grafik untuk melihat pendapatan secara global",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.height(4.dp))
                        LaunchedEffect(Unit) {
                            val now = Date()
                            selectedRangeLabel = format.format(now)

                            viewModel.loadRevenueRange(now) { i ->
                                incomeData = i
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

                                        viewModel.loadRevenueRange(picked) { i ->
                                            incomeData = i
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
                            SingleBarChart(
                                income = incomeData.values.toList(),
                                labels = incomeData.keys.toList(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(260.dp)
                            )
                        }
                    }
                }
            }

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
                        Text(
                            "Grafik untuk melihat pertumbuhan pendapatan (%) secara global",
                            style = MaterialTheme.typography.bodyMedium
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
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded
                                    )
                                }
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
        }
    }
}