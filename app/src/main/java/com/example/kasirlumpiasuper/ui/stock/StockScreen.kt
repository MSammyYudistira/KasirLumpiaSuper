package com.example.kasirlumpiasuper.ui.stock

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.kasirlumpiasuper.domain.model.StockInputItem
import com.example.kasirlumpiasuper.ui.components.CustomActionButton
import com.example.kasirlumpiasuper.ui.components.CustomTopBarWithBackAction
import com.example.kasirlumpiasuper.ui.dashboard.DashboardViewModel
import com.example.kasirlumpiasuper.ui.recap.RecapViewModel
import com.example.kasirlumpiasuper.ui.theme.Surface
import com.example.kasirlumpiasuper.ui.transaction.ProductListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockScreen(
    navController: NavHostController,
    recapViewModel: RecapViewModel,
    dashboardViewModel: DashboardViewModel
) {
    val productViewModel: ProductListViewModel = viewModel()

    val context = LocalContext.current
    val products by productViewModel.productList.collectAsState()

    val initialStocks = remember { mutableStateMapOf<String, Int>() }
    val damagedStocks = remember { mutableStateMapOf<String, Int>() }

    var uangKas by remember { mutableStateOf("") }

    val hasAnyStockInput = remember {
        derivedStateOf {
            initialStocks.values.any { it > 0 } ||
                    damagedStocks.values.any { it > 0 }
        }
    }

    Scaffold(
        topBar = {
            CustomTopBarWithBackAction(
                onBackClick = { navController.popBackStack() },
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
                            style = MaterialTheme.typography.displaySmall,
                            modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 16.dp)
                        )

                        products.forEach { product ->
                            var value by remember { mutableStateOf("") }

                            OutlinedTextField(
                                value = value,
                                onValueChange = {
                                    value = it.filter(Char::isDigit)
                                    initialStocks[product.id] = value.toIntOrNull() ?: 0
                                },
                                label = { Text(product.name) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                textStyle = MaterialTheme.typography.displaySmall.copy(textAlign = TextAlign.Center),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 8.dp)
                            )
                        }
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
                            style = MaterialTheme.typography.displaySmall,
                            modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 16.dp)
                        )

                        products.forEach { product ->
                            var value by remember { mutableStateOf("") }

                            OutlinedTextField(
                                value = value,
                                onValueChange = {
                                    value = it.filter(Char::isDigit)
                                    damagedStocks[product.id] = value.toIntOrNull() ?: 0
                                },
                                label = { Text(product.name) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                textStyle = MaterialTheme.typography.displaySmall.copy(textAlign = TextAlign.Center),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 8.dp)
                            )
                        }
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
                            "Uang Bawaan (Kas)",
                            style = MaterialTheme.typography.displaySmall,
                            modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 16.dp)
                        )

                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            value = uangKas,
                            onValueChange = { uangKas = it.filter(Char::isDigit) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = MaterialTheme.typography.displaySmall.copy(textAlign = TextAlign.Center),
                            colors = TextFieldDefaults.colors(unfocusedContainerColor = Surface),
                        )
                    }
                }
            }
            item {
                val isButtonEnabled = hasAnyStockInput.value || uangKas.isNotBlank()

                CustomActionButton(
                    onClicked = {

                        if (uangKas.isBlank()) {
                            Toast.makeText(context, "Harap isi uang bawaan terlebih dahulu!", Toast.LENGTH_SHORT).show()
                            return@CustomActionButton
                        }

                        val items = products.map { product ->
                            StockInputItem(
                                productId = product.id,
                                name = product.name,
                                initialStock = initialStocks[product.id] ?: 0,
                                damagedStock = damagedStocks[product.id] ?: 0
                            )
                        }

                        recapViewModel.saveStockInput(
                            items = items,
                            cashOpening = uangKas.toIntOrNull() ?: 0,
                            onSuccess = {
                                Toast.makeText(context, "Stok berhasil disimpan!", Toast.LENGTH_SHORT).show()
                                dashboardViewModel.isStockFilledToday()
                                navController.popBackStack()
                            },
                            onError = { err ->
                                Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    text = "Simpan Perubahan",
                    enabled = isButtonEnabled
                )
            }
        }
    }
}
