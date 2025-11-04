package com.example.kasirlumpiasuper.ui.transaction

import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.navigation.NavHostController
import com.example.kasirlumpiasuper.R
import com.example.kasirlumpiasuper.data.model.OrderItem
import com.example.kasirlumpiasuper.ui.components.AddButtonTransaction
import com.example.kasirlumpiasuper.ui.components.queueLabel
import com.example.kasirlumpiasuper.ui.navigation.NavRoutes
import com.example.kasirlumpiasuper.ui.theme.Outline
import com.example.kasirlumpiasuper.ui.theme.Primary
import com.example.kasirlumpiasuper.ui.theme.PrimaryBold
import com.example.kasirlumpiasuper.ui.theme.Secondary
import com.example.kasirlumpiasuper.ui.theme.Success
import com.example.kasirlumpiasuper.ui.theme.Surface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    navController: NavHostController,
    transactionViewModel: TransactionViewModel,
    dateKey: String? = null,
    queueNumber: Int? = null
) {

    var showEmpty by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    val subtotal by transactionViewModel.subtotal.collectAsState()
    val total by transactionViewModel.total.collectAsState()
    val discountInput by transactionViewModel.discountInput.collectAsState()
    val currentCup by transactionViewModel.currentCupIndex.collectAsState()
    val queuePreview by transactionViewModel.queuePreview.collectAsState()
    val cups by transactionViewModel.cups.collectAsState()
    val currentItems = cups[currentCup] ?: emptyList()
    val notes by transactionViewModel.notes.collectAsState()
    val isLoading by transactionViewModel.isLoading.collectAsState()

    val allItems = cups.values.flatten()
    val isValid = allItems.isNotEmpty()
    val isEditMode = !dateKey.isNullOrBlank() && queueNumber != null

    val context = LocalContext.current

    LaunchedEffect(dateKey, queueNumber) {
        if (!dateKey.isNullOrBlank() && queueNumber != null) {
            transactionViewModel.loadOrderForEdit(dateKey, queueNumber)
        } else {
            transactionViewModel.fetchQueuePreview()
        }
    }

//    LaunchedEffect(Unit) {
//        transactionViewModel.fetchQueuePreview()
//    }


    Row(
        modifier = Modifier
            .fillMaxSize()
    ) {

        // =========================
        // KIRI - DAFTAR PRODUK
        // =========================

        Column(
            modifier = Modifier
                .weight(2f)
                .padding(end = 16.dp)
        ) {
            // TopBar kiri
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

            val products = listOf(
                Triple("Lumpia", 9000, R.drawable.lumpia),
                Triple("Tahu Lumpia", 9000, R.drawable.tahu_lumpia_3),
                Triple("Siomay", 10000, R.drawable.siomay_goreng),
                Triple("Siomay Basah", 10000, R.drawable.siomay_basah_2),
                Triple("Singkong Goreng", 20000, R.drawable.singkong_goreng),
                Triple("Mihun", 15000, R.drawable.mihun_2),
                Triple("Es Kacang Merah", 25000, R.drawable.es_kacang_merah),
                Triple("Air Mineral", 5000, R.drawable.air_mineral_2),
            )

            // Grid
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                products.chunked(3).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // render kartu yang ada
                        rowItems.forEach { (name, price, image) ->
                            ProductCard(
                                name = name,
                                price = price,
                                image = image,
                                modifier = Modifier.weight(1f),
                                onFreeClick = {
                                    transactionViewModel.addItemToCurrentCup(
                                        OrderItem(
                                            productId = name,
                                            name = name,
                                            unitPrice = price,
                                            isFree = true,
                                            imageRes = image
                                        )
                                    )
                                },
                                onItemClick = {
                                    transactionViewModel.addItemToCurrentCup(
                                        OrderItem(
                                            productId = name,
                                            name = name,
                                            unitPrice = price,
                                            isFree = false,
                                            imageRes = image
                                        )
                                    )
                                }
                            )
                        }
                        // isi kolom kosong agar baris terakhir tidak melebar
                        repeat(3 - rowItems.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // =========================
        // KANAN - KERANJANG (scrollable)
        // =========================


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
                // ==== HEADER: full-bleed, tidak kena padding ====
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
                                            Image(
                                                modifier = Modifier
                                                    .padding(16.dp),
                                                painter = painterResource(id = if (item.imageRes != 0) item.imageRes else R.drawable.lumper_logo),
                                                contentDescription = null
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(16.dp))

                                        Column(
                                            modifier = Modifier.weight(1f),
                                            horizontalAlignment = Alignment.Start
                                        ) {
                                            Text(text = item.name, fontWeight = FontWeight.SemiBold)
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
                                Text("Rp $subtotal", style = MaterialTheme.typography.labelLarge)
                            }

                            Spacer(Modifier.height(8.dp))

                            OutlinedTextField(
                                value = if (showEmpty && discountInput == 0) "()" else "(${discountInput})",
                                onValueChange = { input ->
                                    // filter hanya angka
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
                                            // Saat diklik, kosongkan tampilan kalau nilainya 0
                                            showEmpty = true
                                        } else {
                                            // Saat kehilangan fokus, kembalikan angka jika kosong
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
                                                Toast.makeText(context, "Perubahan disimpan", Toast.LENGTH_SHORT).show()
                                                navController.popBackStack() // kembali ke OrderDetailScreen
                                            },
                                            onError = { e ->
                                                Toast.makeText(context, "Gagal: ${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                    } else {
                                        // mode transaksi baru → lanjut ke pembayaran
                                        if (isValid) {
                                            navController.navigate(NavRoutes.Payment.route)
                                        } else {
                                            Toast.makeText(context, "Lengkapi pesanan & nama customer terlebih dahulu", Toast.LENGTH_SHORT).show()
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

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.6f)),
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
    image: Int,
    onFreeClick: () -> Unit,
    onItemClick: () -> Unit
) {
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
                Image(
                    modifier = modifier
                        .size(125.dp)
                        .padding(8.dp),
                    painter = painterResource(image),
                    contentDescription = null,
                    alignment = Alignment.Center
                )
            }
            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Free?",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Normal,
                        color = PrimaryBold,
                        modifier = Modifier
                            .clickable(
                                onClick = onFreeClick
                            )
                            .padding(4.dp),
                    )
                    Text(
                        text = "Rp. $price",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

        }
    }
}

//@Preview(showBackground = true)
//@Composable
//private fun CardPreview() {
//    KasirLumpiaSuperTheme {
//        ProductCard(
//            name = "Lumpia Super",
//            price = 8000,
//            modifier = Modifier.padding(8.dp),
//            image = painterResource(R.drawable.lumpia_super)
//        )
//    }
//}

//@Preview(showBackground = true, device = Devices.TABLET)
//@Composable
//fun TransactionPreview() {
//    KasirLumpiaSuperTheme {
//        TransactionScreen(
//            navController = rememberNavController()
//        )
//    }
//}
