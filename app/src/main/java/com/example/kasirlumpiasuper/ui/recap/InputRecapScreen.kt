package com.example.kasirlumpiasuper.ui.recap

import android.util.Log
import android.widget.Toast
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.kasirlumpiasuper.data.model.RecapInput
import com.example.kasirlumpiasuper.ui.components.CustomTopBarWithBackAction
import com.example.kasirlumpiasuper.ui.theme.Primary

@Composable
fun InputRecapScreen(
    navController: NavHostController,
    recapViewModel: RecapViewModel,
    dateLabel: String
) {
    val context = LocalContext.current

    val uangBesar = remember { mutableStateOf("") }
    val uangKecil = remember { mutableStateOf("") }
    val uangLebihan = remember { mutableStateOf("") }

    val lokasi = remember { mutableStateOf("") }
    val airMineral = remember { mutableStateOf("") }
    val pengeluaranLain = remember { mutableStateOf("") }
    val notes = remember { mutableStateOf("") }

    val isAllEmpty = remember {
        derivedStateOf {
            uangBesar.value.isBlank() &&
                    uangKecil.value.isBlank() &&
                    uangLebihan.value.isBlank() &&
                    lokasi.value.isBlank() &&
                    airMineral.value.isBlank() &&
                    pengeluaranLain.value.isBlank() &&
                    notes.value.isBlank()
        }
    }

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
                            style = MaterialTheme.typography.displaySmall,
                        )
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = uangBesar.value,
                            onValueChange = {
                                uangBesar.value = it.filter(Char::isDigit)
                            },
                            label = { Text("Uang Besar") },
                            placeholder = { Text("Tulis pendapatan uang besar untuk hari ini...") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = MaterialTheme.typography.displaySmall.copy(textAlign = TextAlign.Center),
                        )

                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = uangKecil.value,
                            onValueChange = {
                                uangKecil.value = it.filter(Char::isDigit)
                            },
                            label = { Text("Uang Kecil") },
                            placeholder = { Text("Tulis pendapatan uang kecil untuk hari ini...") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = MaterialTheme.typography.displaySmall.copy(textAlign = TextAlign.Center),
                        )

                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = uangLebihan.value,
                            onValueChange = {
                                uangLebihan.value = it.filter(Char::isDigit)
                            },
                            label = { Text("Uang Lebihan") },
                            placeholder = { Text("Tulis pendapatan uang lebihan (jika ada)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = MaterialTheme.typography.displaySmall.copy(textAlign = TextAlign.Center),
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
                            "Pengeluaran",
                            style = MaterialTheme.typography.displaySmall
                        )
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = airMineral.value,
                            onValueChange = { airMineral.value = it.filter(Char::isDigit) },
                            label = { Text("Pengeluaran Air Mineral") },
                            placeholder = { Text("Apakah ada pengeluaran untuk membeli Air Mineral hari ini?") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = MaterialTheme.typography.displaySmall.copy(textAlign = TextAlign.Center),
                        )

                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = pengeluaranLain.value,
                            onValueChange = {
                                pengeluaranLain.value = it.filter(Char::isDigit)
                            },
                            label = { Text("Pengeluaran Lainnya") },
                            placeholder = { Text("Apakah ada pengeluaran lainnya?") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = MaterialTheme.typography.displaySmall.copy(textAlign = TextAlign.Center),
                        )
                    }
                }
            }
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
                            "Data Tambahan",
                            style = MaterialTheme.typography.displaySmall
                        )
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth(),
                            value = lokasi.value,
                            onValueChange = { lokasi.value = it },
                            label = { Text("Lokasi Acara") },
                            placeholder = { Text("Dimana letak lokasi acara hari ini?") },
                            textStyle = MaterialTheme.typography.displaySmall,
                        )

                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth(),
                            value = notes.value,
                            onValueChange = { notes.value = it },
                            label = { Text("Catatan") },
                            placeholder = { Text("Tulis catatan penting untuk hari ini...") },
                            textStyle = MaterialTheme.typography.bodyMedium,
                            maxLines = 4
                        )
                    }
                }
            }
            item {
                Button(
                    onClick = {
                        if (uangBesar.value.isBlank()) {
                            Toast.makeText(
                                context,
                                "Harap isi uang besar terlebih dahulu.",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        recapViewModel.saveRecapInput(
                            RecapInput(
                                bigCash = uangBesar.value.toIntOrNull() ?: 0,
                                smallCash = uangKecil.value.toIntOrNull() ?: 0,
                                extraCash = uangLebihan.value.toIntOrNull() ?: 0,
                                location = lokasi.value,
                                notes = notes.value,
                                mineralWaterExpense = airMineral.value.toIntOrNull() ?: 0,
                                otherExpense = pengeluaranLain.value.toIntOrNull() ?: 0
                            ),
                            dateLabel = dateLabel,
                            onSuccess = {
                                Toast.makeText(
                                    context,
                                    "Data rekapan disimpan!",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                navController.popBackStack()
                            },
                            onError = { err ->
                                Toast.makeText(context, "Error: $err", Toast.LENGTH_SHORT)
                                    .show()
                                Log.e("InputDebug", "Error: $err")
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    enabled = !isAllEmpty.value
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
