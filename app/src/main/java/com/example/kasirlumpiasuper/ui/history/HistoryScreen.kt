package com.example.kasirlumpiasuper.ui.history

import android.R.attr.top
import android.graphics.drawable.Icon
import android.widget.Space
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.kasirlumpiasuper.R
import com.example.kasirlumpiasuper.ui.theme.OnSurfaceVariant
import com.example.kasirlumpiasuper.ui.theme.Primary

@Composable
fun HistoryScreen(navController: NavHostController) {

    Scaffold { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 72.dp)
                .padding(top = 16.dp)
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Surface(
//                    modifier = Modifier.padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = 4.dp,
                    onClick = {}
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_date_range_24),
                            contentDescription = "date picker"
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "15 Mei 2025",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

            item {
                val transaksiList = listOf(
                    Triple(
                        "Struk #1",
                        "Lumpia 2, Lumpia 1 (FREE), Siomay 2, Burger 3 - 18:24 WITA",
                        70000
                    ),
                    Triple(
                        "Struk #2",
                        "Lumpia 2, Lumpia 1 (FREE), Siomay 2, Burger 3 - 18:24 WITA",
                        60000
                    ),
                    Triple("Struk #3", "Lumpia 2, Siomay 2, Burger 3 - 18:24 WITA", 30000)
                )
                val grandTotal = transaksiList.sumOf { it.third }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = 4.dp,
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                    ) {
                        Text("Transaksi Hari Ini", style = MaterialTheme.typography.titleLarge)

                        Spacer(modifier = Modifier.height(4.dp))

                        // Daftar transaksi
                        transaksiList.forEach { (title, desc, harga) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.outline_receipt_24),
                                        contentDescription = "Struk",
                                        tint = OnSurfaceVariant,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(title, style = MaterialTheme.typography.titleMedium)
                                        Text(
                                            desc,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = OnSurfaceVariant
                                        )
                                    }
                                }

                                // Harga
                                Text(
                                    "Rp ${"%,.0f".format(harga.toDouble())}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Primary
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                // Tombol print
                                IconButton(onClick = { /* cetak struk */ }) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Primary
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.baseline_print_24),
                                            contentDescription = "Print",
                                            tint = Color.White,
                                            modifier = Modifier.padding(4.dp)
                                        )
                                    }

                                }
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        // Total
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Grand Total", style = MaterialTheme.typography.titleMedium)
                                Text("${transaksiList.size} Total Struk", style = MaterialTheme.typography.bodySmall)
                            }
                            Text(
                                "Rp ${"%,.0f".format(grandTotal.toDouble())}",
                                style = MaterialTheme.typography.titleLarge,
                                color = Primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tombol Buat Rekapan
                Button(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text(
                        "Buat Rekapan",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }

}