package com.example.kasirlumpiasuper.ui.recap

import android.R.id.input
import android.net.Uri
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.kasirlumpiasuper.data.model.RecapInput
import com.example.kasirlumpiasuper.ui.components.CustomTopBarWithBackAction
import com.example.kasirlumpiasuper.ui.navigation.NavRoutes
import com.example.kasirlumpiasuper.ui.theme.Primary
import com.example.kasirlumpiasuper.ui.utils.DateUtils

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
                            value = uangBesar.value,
                            onValueChange = { uangBesar.value = it.filter(Char::isDigit) },
                            label = { Text("Uang Besar") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = uangKecil.value,
                            onValueChange = { uangKecil.value = it.filter(Char::isDigit) },
                            label = { Text("Uang Kecil") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = uangLebihan.value,
                            onValueChange = { uangLebihan.value = it.filter(Char::isDigit) },
                            label = { Text("Uang Lebihan (opsional)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
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
                            style = MaterialTheme.typography.displaySmall
                        )
                        OutlinedTextField(
                            value = lokasi.value,
                            onValueChange = { lokasi.value = it },
                            label = { Text("Lokasi") })
                        OutlinedTextField(
                            value = airMineral.value,
                            onValueChange = { airMineral.value = it.filter(Char::isDigit) },
                            label = { Text("Air Mineral") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = pengeluaranLain.value,
                            onValueChange = { pengeluaranLain.value = it.filter(Char::isDigit) },
                            label = { Text("Pengeluaran Lainnya (opsional)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                }
            }
            item {
                Button(
                    onClick = {
                        recapViewModel.saveRecapInput(
                            RecapInput(
                                bigCash = uangBesar.value.toIntOrNull() ?: 0,
                                smallCash = uangKecil.value.toIntOrNull() ?: 0,
                                extraCash = uangLebihan.value.toIntOrNull() ?: 0,
                                location = lokasi.value,
                                mineralWaterExpense = airMineral.value.toIntOrNull() ?: 0,
                                otherExpense = pengeluaranLain.value.toIntOrNull() ?: 0
                            ),
                            dateLabel = dateLabel,
                            onSuccess = {
                                Toast.makeText(context, "Data rekapan disimpan!", Toast.LENGTH_SHORT)
                                    .show()
                                navController.navigate(navController.popBackStack())
                            },
                            onError = { err ->
                                Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
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
