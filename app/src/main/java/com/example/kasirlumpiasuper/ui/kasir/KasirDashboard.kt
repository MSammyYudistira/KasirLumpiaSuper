package com.example.kasirlumpiasuper.ui.kasir

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FabPosition
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
import com.example.kasirlumpiasuper.ui.stock.StockScreen
import com.example.kasirlumpiasuper.ui.theme.Primary
import com.google.firebase.Timestamp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun KasirDashboard(
    navController: NavHostController,
    viewModel: KasirViewModel = viewModel()
    ) {
    val stockFilledToday by viewModel.stockFilledToday.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.checkStockForToday()
    }

    Scaffold { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp),
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 72.dp)
        ) {
            // Ringkasan Hari Ini
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                shadowElevation = 4.dp,
                shape = RoundedCornerShape(8.dp),
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Ringkasan Hari Ini",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "15 Mei 2025",
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
                                    "25",
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
                                    painter = painterResource(R.drawable.round_money_24),
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
                                    "Rp 24.000",
                                    style = MaterialTheme.typography.displayMedium,
                                    color = Color(0xFF22C55E)
                                )
                            }
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
//                border = BorderStroke(
//                    width = 2.dp,
//                    color = if (!stockFilledToday) Color.Red else Color.Transparent
//                )
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
                            style = MaterialTheme.typography.titleMedium,
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
//                            tint = if (!stockFilledToday) Color.Black else Primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Atur stok kamu disini",
                            style = MaterialTheme.typography.titleMedium,
//                            color = if (!stockFilledToday) Color.Black else Primary
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
                        Text("Buat Pesanan Baru", style = MaterialTheme.typography.titleMedium)
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
                            style = MaterialTheme.typography.titleMedium,
                            color = Primary
                        )
                    }
                }
            }
        }
    }
}