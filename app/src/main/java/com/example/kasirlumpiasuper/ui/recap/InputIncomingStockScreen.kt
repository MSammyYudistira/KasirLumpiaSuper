package com.example.kasirlumpiasuper.ui.recap

import android.widget.Toast
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.kasirlumpiasuper.ui.components.CustomTopBarWithBackAction
import com.example.kasirlumpiasuper.ui.theme.Primary
import com.example.kasirlumpiasuper.ui.transaction.ProductListViewModel

@Composable
fun InputIncomingStockScreen(
    navController: NavHostController,
    recapViewModel: RecapViewModel,
) {
    val productViewModel: ProductListViewModel = viewModel()

    val context = LocalContext.current
    val products by productViewModel.productList.collectAsState()

    // state map productId -> string input
    val incomingMap = remember { mutableStateMapOf<String, String>() }

    Scaffold(
        topBar = {
            CustomTopBarWithBackAction(onBackClick = { navController.popBackStack() }, title = "Input Barang Masuk")
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding).padding(horizontal = 72.dp),
        ) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = 4.dp
                ) {
                    Column {
                        Text(
                            "Barang Masuk Tambahan",
                            style = MaterialTheme.typography.displaySmall,
                            modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 16.dp)
                        )

                        products.forEach { product ->
                            var value by remember { mutableStateOf("") }
                            // prefill with "" (user fills only products that incoming)
                            OutlinedTextField(
                                value = value,
                                onValueChange = {
                                    val filtered = it.filter(Char::isDigit)
                                    value = filtered
                                    incomingMap[product.id] = filtered
                                },
                                label = { Text(product.name) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                textStyle = MaterialTheme.typography.displaySmall.copy(textAlign = TextAlign.Center),
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            item {
                val hasAny = incomingMap.values.any { it.toIntOrNull() ?: 0 > 0 }
                Button(
                    onClick = {
                        if (!hasAny) {
                            Toast.makeText(context, "Isi minimal 1 produk dengan nilai > 0", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        // Build deltas map productId -> Pair(name, deltaInt)
                        val deltas = products.mapNotNull { p ->
                            val s = incomingMap[p.id].orEmpty()
                            val v = s.toIntOrNull() ?: 0
                            if (v > 0) p.id to (p.name to v) else null
                        }.toMap()

                        recapViewModel.addIncomingStock(
                            deltas = deltas,
                            onSuccess = {
                                Toast.makeText(context, "Barang masuk berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            },
                            onError = { err ->
                                Toast.makeText(context, "Error: $err", Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    enabled = hasAny
                ) {
                    Text("Simpan Barang Masuk", color = Color.White)
                }
            }
        }
    }
}
