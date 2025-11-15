package com.example.kasirlumpiasuper.ui.menu

import android.R.attr.name
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.kasirlumpiasuper.R
import com.example.kasirlumpiasuper.ui.components.CustomTopBarWithBackAction
import com.example.kasirlumpiasuper.ui.navigation.NavRoutes
import com.example.kasirlumpiasuper.ui.theme.Outline
import com.example.kasirlumpiasuper.ui.theme.Primary
import com.example.kasirlumpiasuper.ui.theme.Surface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuDetailScreen(
    navController: NavController,
    productId: String,
    viewModel: MenuViewModel = viewModel()
) {
    val currentProduct by viewModel.currentProduct.collectAsState()

    LaunchedEffect(productId) {
        viewModel.loadProductDetail(productId)
    }

    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }


    LaunchedEffect(currentProduct) {
        name = currentProduct?.name ?: ""
        price = currentProduct?.price?.toString() ?: ""
    }

    Scaffold(
        topBar = {
            CustomTopBarWithBackAction(
                onBackClick = { navController.popBackStack() },
                title = "Edit Produk"
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 72.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // GAMBAR STATIS
                    Image(
                        painter = painterResource(R.drawable.lumper_logo),
                        contentDescription = null,
                        modifier = Modifier
                            .size(140.dp)
                            .background(Outline, RoundedCornerShape(8.dp))
                            .padding(16.dp)
                    )

                    Spacer(Modifier.height(24.dp))

                    // INPUT NAMA
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nama Produk") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(16.dp))

                    // INPUT HARGA
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it.filter { char -> char.isDigit() } },
                        label = { Text("Harga Produk") },
                        modifier = Modifier
                            .fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = MaterialTheme.typography.displaySmall.copy(textAlign = TextAlign.Center),
//                        colors = TextFieldDefaults.colors(unfocusedContainerColor = Surface),
                    )
                }
            }
            Spacer(Modifier.height(24.dp))

            Button(
                colors = ButtonDefaults.buttonColors(Primary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = {
                    if (productId == "new") {
                        viewModel.saveNewProduct(name, price.toIntOrNull() ?: 0)
                    } else {
                        viewModel.updateProduct(
                            productId,
                            name,
                            price.toIntOrNull() ?: 0
                        )
                    }
                    navController.popBackStack()
                }
            ) {
                Text("Simpan", style = MaterialTheme.typography.titleMedium)
            }

            if (productId != "new") {
                Spacer(Modifier.height(4.dp))

                TextButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Hapus Produk", color = Color.Red)
                }
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Hapus Produk") },
                text = { Text("Apakah kamu yakin ingin menghapus produk ini?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            viewModel.deleteProduct(productId) {
                                navController.popBackStack()   // kembali setelah delete
                            }
                        }
                    ) {
                        Text("Hapus", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Batal")
                    }
                }
            )
        }

    }
}
