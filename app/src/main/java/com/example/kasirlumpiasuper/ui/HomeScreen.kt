package com.example.kasirlumpiasuper.ui

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.kasirlumpiasuper.R
import com.example.kasirlumpiasuper.ui.theme.KasirLumpiaSuperTheme
import com.example.kasirlumpiasuper.ui.theme.OnSurfaceVariant
import com.example.kasirlumpiasuper.ui.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {

    Scaffold(
        topBar = {
            CustomTopBar(
                userName = "Sammy",
                onHomeClick = {},
                onHistoryClick = {},
                onStatsClick = {}
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Aksi ketika tombol diklik */ },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .padding(horizontal = 72.dp)
                    .fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Tambah Item",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tambahkan Item",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        },


        ) { innerPadding ->

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp),
            modifier = Modifier
                .padding(horizontal = 72.dp, vertical = 32.dp)
                .padding(innerPadding)
        ) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(),
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
                                text = "Ingin Lihat Riwayat Transaksi?",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Lihat semua transaksi yang dilakukan",
                                style = MaterialTheme.typography.bodyMedium
                            )

                        }

                        TextButton(onClick = {}) {
                            Icon(
                                modifier = Modifier.size(30.dp),
                                painter = painterResource(R.drawable.outline_history_24),
                                contentDescription = "Riwayat Transaksi",
                                tint = Primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Lihat Riwayat Transaksi disini",
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
                                text = "Ingin Lihat Statistik Penjualan?",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Analisis performa penjualan bulanan Anda",
                                style = MaterialTheme.typography.bodyMedium
                            )

                        }

                        TextButton(onClick = {}) {
                            Icon(
                                modifier = Modifier.size(30.dp),
                                painter = painterResource(R.drawable.outline_statistic_up),
                                contentDescription = "Statistik",
                                tint = Primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Lihat Statistik disini",
                                style = MaterialTheme.typography.titleMedium,
                                color = Primary
                            )
                        }
                    }
                }
            }

//            Button(onClick = {
//                Firebase.auth.signOut()
//                navController.navigate("login") {
//                    popUpTo("home") { inclusive = true }
//                }
//            }) { Text("Logout") }

        }
    }
}

@Composable
fun CustomTopBar(
    userName: String,
    onHomeClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onStatsClick: () -> Unit
) {
    Surface(
        tonalElevation = 4.dp,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 72.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Halaman Beranda",
                style = MaterialTheme.typography.titleMedium,
                color = Primary
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(64.dp)
            ) {
                TextButton(onClick = onHomeClick) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_home_24),
                        contentDescription = "Beranda"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Beranda", style = MaterialTheme.typography.titleSmall)
                }

                TextButton(onClick = onHistoryClick) {
                    Icon(
                        painter = painterResource(R.drawable.outline_history_24),
                        contentDescription = "Riwayat",
                        tint = OnSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Riwayat",
                        style = MaterialTheme.typography.titleSmall,
                        color = OnSurfaceVariant
                    )
                }

                TextButton(onClick = onStatsClick) {
                    Icon(
                        painter = painterResource(R.drawable.outline_statistic_up),
                        contentDescription = "Statistik",
                        tint = OnSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Statistik",
                        style = MaterialTheme.typography.titleSmall,
                        color = OnSurfaceVariant
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(id = R.drawable.lumper_logo),
                    contentDescription = "Foto Profil",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(8.dp))
                Text(text = userName, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Preview(showBackground = true, device = Devices.TABLET)
@Composable
private fun CustomTopBarPreview() {
    KasirLumpiaSuperTheme {
        CustomTopBar(
            userName = "Sammy",
            onHomeClick = {},
            onHistoryClick = {},
            onStatsClick = {}
        )
    }
}

@Preview(showBackground = true, device = Devices.TABLET)
@Composable
private fun HomeScreenPreview() {
    KasirLumpiaSuperTheme {
        HomeScreen(navController = rememberNavController())
    }
}