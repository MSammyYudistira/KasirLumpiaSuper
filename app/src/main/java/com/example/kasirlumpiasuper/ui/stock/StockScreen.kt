package com.example.kasirlumpiasuper.ui.stock

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.kasirlumpiasuper.ui.components.CustomActionButton
import com.example.kasirlumpiasuper.ui.components.CustomTopBarWithBackAction
import com.example.kasirlumpiasuper.ui.components.StockGridStatic
import com.example.kasirlumpiasuper.ui.navigation.NavRoutes
import com.example.kasirlumpiasuper.ui.theme.KasirLumpiaSuperTheme
import com.example.kasirlumpiasuper.ui.theme.Surface
import java.time.LocalDate
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockScreen(navController: NavHostController) {
    val stok = listOf("Lumpia", "Tahu", "Siomay", "Mihun", "Singkong", "Kacang Merah", "Aqua")
    var uangKas by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CustomTopBarWithBackAction(
                onBackClick = {
                    val popped = navController.popBackStack()
                    Log.d(
                        "Stock Screen",
                        "Back Pressed, Result: $popped, current route: ${NavRoutes.Stock.route}"
                    )
                },
                title = "Atur Stok"
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 72.dp),
        ) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = 4.dp
                ) {
                    Column {
                        Text(
                            "Stok Awal",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .padding(start = 24.dp, top = 24.dp, bottom = 16.dp)
                        )
                        StockGridStatic(items = stok)
                    }
                }
            }

            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = 4.dp
                ) {
                    Column {
                        Text(
                            "Stok Rusak / Retur",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .padding(start = 24.dp, top = 24.dp, bottom = 16.dp)
                        )
                        StockGridStatic(items = stok)
                    }
                }
            }

            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text("Uang Bawaan", style = MaterialTheme.typography.titleMedium)

                        Spacer(modifier = Modifier.height(4.dp))

                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Uang Kas") },
                            value = uangKas,
                            onValueChange = { uangKas = it },
                            placeholder = { Text("Masukkan jumlah uang kas yang dibawa saat ini") }
                        )
                    }

                }
            }

            item {
                CustomActionButton(
                    onClicked = {  },
                    text = "Simpan Perubahan"
                )
            }
        }
    }
}

@Preview(showBackground = true, device = Devices.TABLET)
@Composable
private fun StockScreenPreview() {
    KasirLumpiaSuperTheme {
        StockScreen(navController = rememberNavController())
    }
}