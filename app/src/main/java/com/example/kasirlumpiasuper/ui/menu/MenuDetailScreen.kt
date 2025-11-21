package com.example.kasirlumpiasuper.ui.menu

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.kasirlumpiasuper.R
import com.example.kasirlumpiasuper.ui.components.CustomTopBarWithBackAction
import com.example.kasirlumpiasuper.ui.theme.Primary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuDetailScreen(
    navController: NavController,
    productId: String,
    viewModel: MenuViewModel = viewModel()
) {
    val currentProduct by viewModel.currentProduct.collectAsState()
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedImageUri = uri
    }

    LaunchedEffect(productId) {
        viewModel.loadProductDetail(productId)
    }

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

                    ProductImagePicker(
                        imageUrl = currentProduct?.imageUrl,
                        localImageUri = selectedImageUri,
                        onPickImage = { launcher.launch("image/*") }
                    )

                    Spacer(Modifier.height(24.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nama Produk") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it.filter { char -> char.isDigit() } },
                        label = { Text("Harga Produk") },
                        modifier = Modifier
                            .fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = MaterialTheme.typography.displaySmall.copy(textAlign = TextAlign.Center),
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
                    scope.launch {
                        if (productId == "new") {
                            viewModel.saveNewProduct(
                                name,
                                price.toIntOrNull() ?: 0,
                                selectedImageUri
                            )
                        } else {
                            viewModel.updateProduct(
                                productId,
                                name,
                                price.toIntOrNull() ?: 0,
                                selectedImageUri
                            )
                        }
                        navController.popBackStack()
                    }
                }
            ) {
                Text("Simpan Perubahan", style = MaterialTheme.typography.titleMedium)
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
                                navController.popBackStack()
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

@Composable
fun ProductImagePicker(
    imageUrl: String?,
    localImageUri: Uri?,
    onPickImage: () -> Unit
) {
    Box(
        modifier = Modifier.size(160.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        AsyncImage(
            model = localImageUri ?: imageUrl,
            contentDescription = "Product Image",
            placeholder = painterResource(R.drawable.lumper_logo),
            error = painterResource(R.drawable.lumper_logo),
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .border(BorderStroke(4.dp, Primary), CircleShape)
                .padding(4.dp)
        )
        IconButton(
            onClick = onPickImage,
            modifier = Modifier
                .size(36.dp)
                .background(Primary, CircleShape)
        ) {
            Icon(
                painterResource(R.drawable.baseline_photo_camera_24),
                contentDescription = null,
                tint = Color.White,
            )
        }
    }
}

