package com.example.kasirlumpiasuper.ui.transaction

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.kasirlumpiasuper.R
import com.example.kasirlumpiasuper.domain.model.OrderItem
import com.example.kasirlumpiasuper.ui.components.AddButtonTransaction
import com.example.kasirlumpiasuper.ui.components.queueLabel
import com.example.kasirlumpiasuper.ui.navigation.NavRoutes
import com.example.kasirlumpiasuper.ui.theme.Danger
import com.example.kasirlumpiasuper.ui.theme.Outline
import com.example.kasirlumpiasuper.ui.theme.Primary
import com.example.kasirlumpiasuper.ui.theme.PrimaryBold
import com.example.kasirlumpiasuper.ui.theme.Secondary
import com.example.kasirlumpiasuper.ui.theme.Success
import com.example.kasirlumpiasuper.ui.theme.Surface
import com.example.kasirlumpiasuper.ui.theme.Warning

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    navController: NavHostController,
    transactionViewModel: TransactionViewModel,
    dateKey: String? = null,
    queueNumber: Int? = null
) {
    val productViewModel: ProductListViewModel = viewModel()
    val stockViewModel: TransactionStockViewModel = viewModel()

    var showEmpty by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    val products by productViewModel.productList.collectAsState()
    val remainingStock by stockViewModel.remainingStock.collectAsState()
    val subtotal by transactionViewModel.subtotal.collectAsState()
    val total by transactionViewModel.total.collectAsState()
    val discountInput by transactionViewModel.discountInput.collectAsState()
    val currentCup by transactionViewModel.currentCupIndex.collectAsState()
    val queuePreview by transactionViewModel.queuePreview.collectAsState()
    val cups by transactionViewModel.cups.collectAsState()
    val notes by transactionViewModel.notes.collectAsState()
    val isLoading by transactionViewModel.isLoading.collectAsState()
    val isLoadingStock by stockViewModel.isLoading.collectAsState()

    val currentItems = cups[currentCup] ?: emptyList()
    val allItems = cups.values.flatten()
    val isValid = allItems.isNotEmpty()
    val isEditMode = !dateKey.isNullOrBlank() && queueNumber != null
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        stockViewModel.loadTodayStock()
    }

    LaunchedEffect(dateKey, queueNumber) {
        if (!dateKey.isNullOrBlank() && queueNumber != null) {
            transactionViewModel.loadOrderForEdit(dateKey, queueNumber)
        } else {
            transactionViewModel.fetchQueuePreview()
        }
    }

    if (!isLoading && !isLoadingStock) {
        Row(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .weight(2f)
                    .padding(end = 16.dp)
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 2.dp,
                    shape = RoundedCornerShape(bottomEnd = 8.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                        Text(
                            text = "Detail Transaksi",
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(products, key = { it.id }) { product ->
                        ProductCard(
                            name = product.name,
                            price = product.price,
                            imageUrl = product.imageUrl,
                            remaining = remainingStock[product.id] ?: 0,
                            modifier = Modifier,
                            onFreeClick = {
                                transactionViewModel.addItemToCurrentCup(
                                    OrderItem(
                                        productId = product.id,
                                        name = product.name,
                                        unitPrice = product.price,
                                        isFree = true,
                                        imageUrl = product.imageUrl
                                    )
                                )
                            },
                            onItemClick = {
                                transactionViewModel.addItemToCurrentCup(
                                    OrderItem(
                                        productId = product.id,
                                        name = product.name,
                                        unitPrice = product.price,
                                        isFree = false,
                                        imageUrl = product.imageUrl
                                    )
                                )
                            }
                        )
                    }

                    if (products.isEmpty()) {
                        item(span = { GridItemSpan(3) }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Belum ada produk. Tambahkan dari Kelola Menu.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }

            Surface(
                modifier = Modifier
                    .weight(1.2f)
                    .fillMaxHeight()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Box(Modifier.fillMaxSize()) {
                    val headerHeight = 64.dp
                    Column {
                        Surface(
                            shape = RoundedCornerShape(
                                topStart = 16.dp, topEnd = 16.dp,
                                bottomStart = 0.dp, bottomEnd = 0.dp
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(headerHeight)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.padding(start = 16.dp)) {
                                    Surface(
                                        color = Secondary,
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier
                                            .size(40.dp)

                                    ) {
                                        Box(
                                            Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.outline_food_menu_24),
                                                contentDescription = null,
                                                tint = PrimaryBold,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    "Pesanan Nomor #${queueLabel(queuePreview)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f, fill = true),
                            shadowElevation = 4.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Button(
                                        onClick = { transactionViewModel.addCup() },
                                        shape = RoundedCornerShape(4.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(50.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(
                                                0xFFE0F2FF
                                            )
                                        )
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                painterResource(R.drawable.outline_add_24),
                                                contentDescription = "add Cup",
                                                tint = PrimaryBold
                                            )
                                            Text(
                                                text = "Tambah Cup",
                                                style = MaterialTheme.typography.titleMedium,
                                                color = PrimaryBold
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    if (cups.isNotEmpty()) {
                                        ExposedDropdownMenuBox(
                                            expanded = expanded,
                                            onExpandedChange = { expanded = !expanded },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            OutlinedTextField(
                                                readOnly = true,
                                                value = "Cup - $currentCup",
                                                onValueChange = {},
                                                label = { Text("Pilih Cup") },
                                                trailingIcon = {
                                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                                        expanded = expanded
                                                    )
                                                },
                                                modifier = Modifier
                                                    .menuAnchor(),
                                                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                                            )
                                            ExposedDropdownMenu(
                                                expanded = expanded,
                                                onDismissRequest = { expanded = false }
                                            ) {
                                                cups.keys.sorted().forEach { cup ->
                                                    DropdownMenuItem(
                                                        text = { Text("Cup - $cup") },
                                                        onClick = {
                                                            transactionViewModel.setCurrentCup(cup)
                                                            expanded = false
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(Modifier.height(12.dp))

                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(
                                        items = currentItems,
                                        key = { it.productId + "-" + it.isFree }
                                    ) { item ->

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(height = 90.dp, width = 110.dp)
                                                    .background(Outline, RoundedCornerShape(8.dp)),
                                                contentAlignment = Alignment.Center,
                                            ) {
                                                AsyncImage(
                                                    model = item.imageUrl.ifBlank { R.drawable.lumper_logo },
                                                    contentDescription = null,
                                                    modifier = Modifier
                                                        .padding(16.dp)
                                                        .size(70.dp),
                                                    placeholder = painterResource(R.drawable.lumper_logo)
                                                )

                                            }

                                            Spacer(modifier = Modifier.width(16.dp))

                                            Column(
                                                modifier = Modifier.weight(1f),
                                                horizontalAlignment = Alignment.Start
                                            ) {
                                                Text(
                                                    text = item.name,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                                Text(
                                                    text = if (item.isFree) "Rp 0" else "Rp ${item.unitPrice * item.qty}",
                                                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                                                    color = Color.Gray
                                                )
                                            }
                                            AddButtonTransaction(
                                                item = item,
                                                transactionViewModel = transactionViewModel
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth(),
                            shadowElevation = 4.dp,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Subtotal", style = MaterialTheme.typography.labelLarge)
                                    Text(
                                        "Rp $subtotal",
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }

                                Spacer(Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = if (showEmpty && discountInput == 0) "()" else "(${discountInput})",
                                    onValueChange = { input ->
                                        val onlyDigits = input.filter { it.isDigit() }
                                        transactionViewModel.setDiscount(onlyDigits)
                                    },
                                    label = { Text("Discount") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    textStyle = LocalTextStyle.current.copy(color = Success),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .onFocusChanged { focusState ->
                                            if (focusState.isFocused) {
                                                showEmpty = true
                                            } else {
                                                if (discountInput == 0) showEmpty = false
                                            }
                                        },
                                    prefix = { Text("Rp ", color = Success) },
                                )

                                OutlinedTextField(
                                    value = notes,
                                    onValueChange = transactionViewModel::setNotes,
                                    label = { Text("Catatan") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(Modifier.height(8.dp))
                                HorizontalDivider(thickness = 2.dp)
                                Spacer(Modifier.height(4.dp))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Total",
                                        style = MaterialTheme.typography.displaySmall,
                                        color = Color.Black
                                    )

                                    Text("Rp $total", style = MaterialTheme.typography.displaySmall)
                                }

                                Spacer(Modifier.height(8.dp))

                                Button(
                                    onClick = {
                                        if (isEditMode) {
                                            transactionViewModel.commitEditedOrder(
                                                dateKey!!,
                                                queueNumber!!,
                                                onSuccess = {
                                                    Toast.makeText(
                                                        context,
                                                        "Perubahan disimpan",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    navController.popBackStack()
                                                },
                                                onError = { e ->
                                                    Toast.makeText(
                                                        context,
                                                        "Gagal: ${e.message}",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            )
                                        } else {
                                            if (isValid) {
                                                navController.navigate(NavRoutes.Payment.route)
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Lengkapi pesanan & nama customer terlebih dahulu",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    },
                                    enabled = isValid,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(if (isEditMode) "Simpan Perubahan" else "Pilih Metode Pembayaran")
                                }
                            }
                        }
                    }
                }
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Primary)
        }
    }
}

@Composable
fun ProductCard(
    name: String,
    price: Int,
    modifier: Modifier = Modifier,
    imageUrl: String,
    remaining: Int,
    onFreeClick: () -> Unit,
    onItemClick: () -> Unit
) {

    val stockColor = when {
        remaining <= 0 -> Danger
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val stockText = when {
        remaining <= 0 -> "Stok kosong"
        remaining < 50 -> "Sisa stok: $remaining"
        else -> "Sisa stok: $remaining"
    }
    Card(
        modifier = modifier
            .heightIn(min = 140.dp)
            .padding(start = 16.dp)
            .clickable { onItemClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(Surface)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Outline, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = imageUrl.ifBlank { R.drawable.lumper_logo },
                    contentDescription = null,
                    modifier = modifier
                        .size(125.dp)
                        .padding(8.dp),
                    placeholder = painterResource(R.drawable.lumper_logo)
                )
            }
            Spacer(Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stockText,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Medium,
                    color = stockColor,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "Free?",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Normal,
                    color = PrimaryBold,
                    modifier = Modifier
                        .clickable(onClick = onFreeClick)
                        .padding(4.dp),
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "Rp. $price",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

//            Row(verticalAlignment = Alignment.Top) {
//                Text(
//                    text = name,
//                    fontSize = 14.sp,
//                    fontWeight = FontWeight.Medium
//                )
//                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
//                    Text(
//                        text = "Free?",
//                        fontSize = 8.sp,
//                        fontWeight = FontWeight.Normal,
//                        color = PrimaryBold,
//                        modifier = Modifier
//                            .clickable(
//                                onClick = onFreeClick
//                            )
//                            .padding(4.dp),
//                    )
//                    Text(
//                        text = "Rp. $price",
//                        fontSize = 12.sp,
//                        fontWeight = FontWeight.Medium
//                    )
//                }
//            }
        }
    }
}

