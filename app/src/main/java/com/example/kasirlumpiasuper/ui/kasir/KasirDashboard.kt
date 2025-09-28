package com.example.kasirlumpiasuper.ui.kasir

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.kasirlumpiasuper.R
import com.example.kasirlumpiasuper.ui.navigation.NavRoutes
import com.example.kasirlumpiasuper.ui.theme.Primary
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.kasirlumpiasuper.data.PreferencesManager
import com.example.kasirlumpiasuper.ui.utils.DateUtils
import kotlinx.coroutines.launch

@Composable
fun KasirDashboard(
    navController: NavHostController,
    viewModel: KasirViewModel = viewModel()
    ) {

    val datekey = DateUtils.getBusinessDateLabel()
    val stockFilledToday by viewModel.stockFilledToday.collectAsState()
    val customerCountToday by viewModel.customerCountToday.collectAsState()
    val isNewDay by viewModel.isNewDay.collectAsState()
    val manualResetRequired by viewModel.manualResetRequired.collectAsState()
    val totalRevenue by viewModel.grandTotalToday.collectAsState()

    val context = LocalContext.current
    val prefs = remember { PreferencesManager(context) }
    val coroutineScope = rememberCoroutineScope()

    val currentBusinessDate = remember { DateUtils.getBusinessDateLabel() }

    LaunchedEffect(Unit) {
        viewModel.checkIfNewDay(prefs)
        viewModel.fetchTodayRevenue()
        viewModel.checkStockForToday()
        viewModel.checkCustomerCountToday()
    }

    if (isNewDay) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Hari Baru Dimulai")},
            text = {Text("Tanggal berganti. Apakah kamu ingin mulai hari baru sekarang?")},
            confirmButton = {
                TextButton(onClick = {
                    coroutineScope.launch {
                        viewModel.resetDailyData(
                            prefs = prefs,
                            currentDate = currentBusinessDate,
                            viewModel = viewModel,
                            navController = navController
                        )
                        viewModel.markResetDone(prefs)
                    }
                }) {
                    Text("Ya, Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.rejectAutoReset()
                }) {
                    Text("Tidak Sekarang")
                }
            }
        )
    }

    Scaffold { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 72.dp)
        ) {
            // Ringkasan Hari Ini
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                shadowElevation = 4.dp,
                shape = RoundedCornerShape(8.dp),
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Ringkasan Hari Ini",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = datekey.toString(),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Row(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Card pelanggan
                        Surface(
                            modifier = Modifier.weight(1f),
                            color = Color(0xFFE1EEFE),
                            shape = RoundedCornerShape(8.dp),
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(24.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_people_24),
                                    contentDescription = "Pelanggan Hari Ini",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            color = Primary,
                                            shape = CircleShape
                                        )
                                        .padding(6.dp),
                                    tint = Color.White
                                )
                                Text(
                                    "Pelanggan Hari ini",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = customerCountToday.toString(),
                                    style = MaterialTheme.typography.displayMedium,
                                    color = Primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(24.dp))

                        // Card pendapatan
                        Surface(
                            modifier = Modifier.weight(1f),
                            color = Color(0xFFE3FCEB),
                            shape = RoundedCornerShape(8.dp),
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(24.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_money_bill_wave_24),
                                    contentDescription = "Total Pendapatan Hari Ini",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            color = Color(0xFF22C55E),
                                            shape = CircleShape
                                        )
                                        .padding(6.dp),
                                    tint = Color.White
                                )
                                Text(
                                    "Total Pendapatan Hari Ini",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = DateUtils.rupiah(totalRevenue),
                                    style = MaterialTheme.typography.displayMedium,
                                    color = Color(0xFF22C55E)
                                )
                            }
                        }
                    }
                }
            }

            // Reset Hari
        if (manualResetRequired) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth(),
                    color =  Color(0xFFFFD4D4)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp, horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Tanggal telah berganti ke ${DateUtils.getBusinessDateLabel()}" ,
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.Red
                            )
                            Text(
                                text = "Segera reset hari untuk memulai transaksi hari ini",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Red
                            )
                        }
                        TextButton(onClick = {
                            coroutineScope.launch {
                                viewModel.resetDailyData(
                                    prefs = prefs,
                                    currentDate = DateUtils.getBusinessDateLabel(),
                                    viewModel = viewModel,
                                    navController = navController
                                )
                                viewModel.markResetDone(prefs)
                            }
                        }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_reset_tv_24),
                                contentDescription = "Reset Hari",
                                modifier = Modifier.size(30.dp),
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Reset Hari Baru",
                                style = MaterialTheme.typography.titleLarge,
                            )
                        }
                    }
                }
            }

            // Atur stok
            Surface(
                shape = RoundedCornerShape(8.dp),
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth(),
                color = if (!stockFilledToday) Color(0xFFFFD4D4) else Color.Transparent
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (!stockFilledToday)"Kamu Belum Atur Stok" else "Ayo Atur Stok!",
                            style = MaterialTheme.typography.titleLarge,
                            color = if (!stockFilledToday) Color.Red else Color.Black
                        )
                        Text(
                            text = if (!stockFilledToday)"Segera atur stok sebelum melakukan transaksi" else "Atur stok sebelum melakukan transaksi",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (!stockFilledToday) Color.Red else Color.Black
                        )
                    }
                    TextButton(onClick = {
                        navController.navigate(NavRoutes.Stock.route)
                    }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_management_stockout),
                            contentDescription = "Atur Stock",
                            modifier = Modifier.size(30.dp),
                            )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Atur stok kamu disini",
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }
                }
            }

            // Buat pesanan baru
            Surface(
                shape = RoundedCornerShape(8.dp),
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Buat Pesanan Baru", style = MaterialTheme.typography.titleLarge)
                        Text(
                            "Ayo Buat Pesanan Sekarang!",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    TextButton(onClick = { navController.navigate(NavRoutes.Transaction.route) }) {
                        Icon(
                            painter = painterResource(R.drawable.outline_add_24),
                            contentDescription = "Tambah Pesanan",
                            modifier = Modifier.size(30.dp),
                            tint = Primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Klik disini untuk membuat pesanan baru",
                            style = MaterialTheme.typography.titleLarge,
                            color = Primary
                        )
                    }
                }
            }
        }
    }
}