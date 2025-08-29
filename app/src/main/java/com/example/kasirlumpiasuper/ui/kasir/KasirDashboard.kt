package com.example.kasirlumpiasuper.ui.kasir

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.kasirlumpiasuper.R
import com.example.kasirlumpiasuper.ui.theme.Primary
import com.google.firebase.Timestamp

@Composable
fun KasirDashboard(navController: NavHostController) {

    Scaffold(
        floatingActionButtonPosition = FabPosition.Center,
    ) { innerPadding ->

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp),
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 72.dp)
                .padding(bottom = 72.dp)
        ) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    shadowElevation = 4.dp,
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Ringkasan Hari Ini",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Timestamp.now()
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
                            Surface(
                                modifier = Modifier
                                    .weight(1f),
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

                            Surface(
                                modifier = Modifier
                                    .weight(1f),
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
            }

            item {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)

                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp, horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Ayo Atur Stok!",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Atur stok sebelum melakukan transaksi",
                                style = MaterialTheme.typography.bodyMedium
                            )

                        }

                        TextButton(onClick = {}) {
                            Icon(
                                modifier = Modifier.size(30.dp),
                                painter = painterResource(R.drawable.outline_management_stockout),
                                contentDescription = "Atur Stock",
                                tint = Primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Atur stok kamu disini",
                                style = MaterialTheme.typography.titleMedium,
                                color = Primary
                            )
                        }
                    }
                }
            }

            item {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)

                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp, horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Buat Pesanan Baru",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Ayo Buat Pesanan Sekarang!",
                                style = MaterialTheme.typography.bodyMedium
                            )

                        }

                        TextButton(onClick = {}) {
                            Icon(
                                modifier = Modifier.size(30.dp),
                                painter = painterResource(R.drawable.outline_add_24),
                                contentDescription = "Tambah Pesanan",
                                tint = Primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Klik disini untuk membuat pesanan baru",
                                style = MaterialTheme.typography.titleMedium,
                                color = Primary
                            )
                        }
                    }
                }
            }
        }
    }
}