package com.example.kasirlumpiasuper.ui.recap

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.kasirlumpiasuper.ui.components.CustomTopBarWithBackAction
import com.example.kasirlumpiasuper.ui.theme.Primary

@Composable
fun InputRecapScreen(
    navController: NavHostController,
    recapViewModel: RecapViewModel
) {
    val uangBesar = remember { mutableStateOf("") }
    val uangKecil = remember { mutableStateOf("") }
    val uangLebihan = remember { mutableStateOf("") }

    val lokasi = remember { mutableStateOf("") }
    val airMineral = remember { mutableStateOf("") }
    val pengeluaranLain = remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CustomTopBarWithBackAction(
                onBackClick = { navController.popBackStack() },
                title = "Isi Data Rekapan"
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 72.dp),
        ) {
            /** Bagian Pendapatan Kotor */
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, bottom = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            "Pendapatan Kotor",
                            style = MaterialTheme.typography.titleLarge,
                        )

                        OutlinedTextField(
                            value = uangBesar.value,
                            onValueChange = { uangBesar.value = it.filter { c -> c.isDigit() } },
                            label = { Text("Uang Besar") },
                            placeholder = { Text("Masukkan jumlah uang besar") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = uangKecil.value,
                            onValueChange = { uangKecil.value = it.filter { c -> c.isDigit() } },
                            label = { Text("Uang Kecil") },
                            placeholder = { Text("Masukkan jumlah uang kecil") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = uangLebihan.value,
                            onValueChange = { uangLebihan.value = it.filter { c -> c.isDigit() } },
                            label = { Text("Uang Lebihan (opsional)") },
                            placeholder = { Text("Masukkan jumlah uang lebihan jika ada") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            /** Bagian Pengeluaran Tambahan */
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            "Pengeluaran Tambahan",
                            style = MaterialTheme.typography.titleLarge
                        )

                        OutlinedTextField(
                            value = lokasi.value,
                            onValueChange = { lokasi.value = it },
                            label = { Text("Lokasi") },
                            placeholder = { Text("Masukkan lokasi tenant saat ini") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = airMineral.value,
                            onValueChange = { airMineral.value = it.filter { c -> c.isDigit() } },
                            label = { Text("Air Mineral") },
                            placeholder = { Text("Masukkan pengeluaran pada air mineral") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = pengeluaranLain.value,
                            onValueChange = {
                                pengeluaranLain.value = it.filter { c -> c.isDigit() }
                            },
                            label = { Text("Pengeluaran Lainnya (opsional)") },
                            placeholder = { Text("Masukkan pengeluaran lainnya jika ada") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            item {
                Button(
                    onClick = { /* TODO: Simpan & navigasi ke DetailRecapScreen */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
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
