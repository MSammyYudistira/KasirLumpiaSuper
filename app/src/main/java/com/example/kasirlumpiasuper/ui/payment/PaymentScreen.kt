package com.example.kasirlumpiasuper.ui.payment

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.kasirlumpiasuper.R
import com.example.kasirlumpiasuper.data.Result
import com.example.kasirlumpiasuper.data.model.PaymentMethod
import com.example.kasirlumpiasuper.domain.error.DomainError
import com.example.kasirlumpiasuper.domain.validator.TransactionValidator
import com.example.kasirlumpiasuper.ui.components.CustomTopBarWithBackAction
import com.example.kasirlumpiasuper.ui.components.queueLabel
import com.example.kasirlumpiasuper.ui.navigation.NavRoutes
import com.example.kasirlumpiasuper.ui.theme.HintText
import com.example.kasirlumpiasuper.ui.theme.Outline
import com.example.kasirlumpiasuper.ui.theme.Primary
import com.example.kasirlumpiasuper.ui.theme.Secondary
import com.example.kasirlumpiasuper.ui.theme.Success
import com.example.kasirlumpiasuper.ui.theme.Surface
import com.example.kasirlumpiasuper.ui.transaction.TransactionViewModel
import com.example.kasirlumpiasuper.ui.utils.BusinessDateManager
import com.example.kasirlumpiasuper.ui.utils.PrintHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun PaymentScreen(
    navController: NavHostController,
    paymentViewModel: PaymentViewModel,
    transactionViewModel: TransactionViewModel,
    context: Context = LocalContext.current
) {

    val pgViewModel: PaymentGatewayViewModel = viewModel()
    val qrUrl by pgViewModel.qrUrl.collectAsState()

    val paymentStatus by pgViewModel.paymentStatus.collectAsState()
    var currentOrderId by remember { mutableStateOf<String?>(null) }

//    val subtotal by transactionViewModel.subtotal.collectAsState()
    val total by transactionViewModel.total.collectAsState()
    val queuePreview by transactionViewModel.queuePreview.collectAsState()
    val cups by transactionViewModel.cups.collectAsState()
    val discount by transactionViewModel.discountInput.collectAsState()
    val notes by transactionViewModel.notes.collectAsState()
    val state by transactionViewModel.saveOrderState.collectAsState()

    val inputAmount by paymentViewModel.inputAmount.collectAsState()
    val selectedMethod by paymentViewModel.selectedPaymentMethod.collectAsState()
    val change by paymentViewModel.change.collectAsState()

    var showEmpty by remember { mutableStateOf(false) }
    var isPrinterConnected by remember { mutableStateOf(false) }
    var showPrinterWarning by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(total) {
        paymentViewModel.setTotalOrder(total)
    }

    LaunchedEffect(selectedMethod) {
        if (selectedMethod == PaymentMethod.CASHLESS) {
            pgViewModel.stopPolling()
            pgViewModel.resetPayment()

            paymentViewModel.setInputAmount(total.toString())

            val dateKey = BusinessDateManager.getBusinessDateLabel()
            val q = transactionViewModel.queuePreview.value ?: 0
            val orderId = "LUMPER-${dateKey}-${q}-${System.currentTimeMillis()}"
            currentOrderId = orderId

            pgViewModel.createQris(orderId, total)
        } else {
            pgViewModel.stopPolling()
            pgViewModel.resetPayment()
        }
    }

    LaunchedEffect(qrUrl, currentOrderId, selectedMethod) {
        if (selectedMethod == PaymentMethod.CASHLESS &&
            !qrUrl.isNullOrBlank() &&
            currentOrderId != null) {

            // 2) Mulai polling setelah QR siap
            pgViewModel.startPollingStatus(currentOrderId!!)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            pgViewModel.stopPolling()
        }
    }

    LaunchedEffect(Unit) {
        isPrinterConnected = PrintHelper.initPrinter(context)
    }

    // Tangani status pembayaran
    LaunchedEffect(paymentStatus) {
        when (paymentStatus) {
            "settlement" -> {
//                commitTransactionAnyway(context, transactionViewModel, paymentViewModel)
                Toast.makeText(context, "Pembayaran berhasil!", Toast.LENGTH_SHORT).show()
//                pgViewModel.resetPayment()
            }

            "expire" -> {
                Toast.makeText(context, "Pembayaran kedaluwarsa", Toast.LENGTH_SHORT).show()
                pgViewModel.stopPolling()
            }

            "cancel" -> {
                Toast.makeText(context, "Pembayaran dibatalkan", Toast.LENGTH_SHORT).show()
                pgViewModel.stopPolling()
            }

            "error" -> {
                Toast.makeText(context, "Gagal memeriksa status", Toast.LENGTH_SHORT).show()
                pgViewModel.stopPolling()
            }
        }
    }


    if (showPrinterWarning) {
        AlertDialog(
            onDismissRequest = { showPrinterWarning = false },
            title = { Text("Printer Belum Tersambung") },
            text = { Text("Printer kamu belum tersambung. Yakin ingin lanjut transaksi tanpa mencetak struk?") },
            confirmButton = {
                TextButton(onClick = {
                    showPrinterWarning = false
                    commitTransactionAnyway(
                        context,
                        transactionViewModel,
                        paymentViewModel
                    )
                    Toast.makeText(context, "Transaksi berhasil.", Toast.LENGTH_SHORT).show()
                }) {
                    Text("Lanjutkan", color = Primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPrinterWarning = false }) {
                    Text("Batal")
                }
            }
        )
    }

    LaunchedEffect(state) {
        when (state) {
            is Result.Success -> {
                // 🔹 Ambil order terakhir
                val order = transactionViewModel.getLastOrder()
                if (order != null && isPrinterConnected) {
                    try {
                        PrintHelper.printReceipt(context, order)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Gagal mencetak struk", Toast.LENGTH_SHORT).show()
                    }
                }

                delay(1000L)

                // Reset form setelah sukses
                transactionViewModel.resetTransaction()
                paymentViewModel.reset()

                // Navigasi ke dashboard kasir
                navController.navigate(NavRoutes.Transaction.route)

                // Reset state supaya efek ini tidak ke-trigger ulang
                transactionViewModel.clearSaveOrderState()
            }

            is Result.Error -> {
                val error = (state as Result.Error).error
                val message = when (error) {
                    is DomainError.NetworkError -> "Koneksi bermasalah, coba lagi"
                    is DomainError.PermissionDenied -> "Akses ditolak, cek login"
                    is DomainError.PreconditionFailed -> "Index Firestore belum dibuat"
                    is DomainError.InvalidInput -> error.reason
                    else -> "Terjadi kesalahan tak dikenal"
                }
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()

                // Optional: reset state agar tidak berulang
                transactionViewModel.clearSaveOrderState()
            }

            else -> Unit
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            CustomTopBarWithBackAction(
                onBackClick = { navController.popBackStack() },
                title = "Detail Pembayaran"
            )
        }
        item {
            Surface(
                modifier = Modifier
                    .padding(horizontal = 48.dp),
                shadowElevation = 2.dp,
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Detail Pesanan", style = MaterialTheme.typography.displaySmall)
                        Text(
                            "#${queueLabel(queuePreview)}",
                            style = MaterialTheme.typography.displaySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Divider(thickness = 2.dp)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (notes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Catatan",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = notes,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Column {
                        cups.toSortedMap().forEach { (cupIndex, items) ->
                            Spacer(Modifier.height(8.dp))

                            Text(
                                "Cup ke - $cupIndex",
                                style = MaterialTheme.typography.titleLarge,
                                color = Primary
                            )

                            Spacer(Modifier.height(8.dp))
                            items.forEach { item ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Column {
                                        Text(
                                            item.name,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            text = if (item.isFree) "${item.qty}x Rp 0" else "${item.qty}x Rp ${item.unitPrice}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Spacer(Modifier.height(4.dp))
                                    }
                                    Text(
                                        text = if (item.isFree) "Rp 0" else "Rp ${item.unitPrice * item.qty}",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(thickness = 2.dp)
                    Spacer(modifier = Modifier.height(24.dp))

                    Column {

                        if (discount != 0) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Diskon",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Success
                                )
                                Text(
                                    text = "Rp ($discount)",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Success
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total", style = MaterialTheme.typography.displaySmall)
                            Text("Rp $total", style = MaterialTheme.typography.displaySmall)
                        }
                    }
                }
            }
        }

        item {
            Surface(
                modifier = Modifier
                    .padding(horizontal = 48.dp),
                shadowElevation = 2.dp,
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Text("Pilih Metode Pembayaran", style = MaterialTheme.typography.displaySmall)

                    Row {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { paymentViewModel.setPaymentMethod(PaymentMethod.CASH) },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(
                                1.dp,
                                if (selectedMethod == PaymentMethod.CASH) Secondary else HintText
                            ),
                            color = if (selectedMethod == PaymentMethod.CASH) Color(0xFFEFF6FF) else Color.Transparent,
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (selectedMethod == PaymentMethod.CASH) Primary else Outline,
                                            RoundedCornerShape(8.dp)
                                        ),
                                ) {
                                    Icon(
                                        modifier = Modifier
                                            .padding(8.dp),
                                        painter = painterResource(R.drawable.baseline_money_bill_wave_24),
                                        tint = if (selectedMethod == PaymentMethod.CASH) Surface else Color.Black,
                                        contentDescription = "Pembayaran Cash"
                                    )
                                }
                                Spacer(Modifier.width(8.dp))
                                Column(Modifier.weight(1f)) {
                                    Text("Cash", style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        "Pembayaran dengan uang tunai",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (selectedMethod == PaymentMethod.CASH) Primary else Color.Transparent,
                                            RoundedCornerShape(32.dp)
                                        ),
                                ) {
                                    Icon(
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .size(12.dp),
                                        painter = painterResource(R.drawable.outline_check_24),
                                        tint = if (selectedMethod == PaymentMethod.CASH) Color.White else Color.Transparent,
                                        contentDescription = "Checklist"
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { paymentViewModel.setPaymentMethod(PaymentMethod.CASHLESS) },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(
                                1.dp,
                                if (selectedMethod == PaymentMethod.CASHLESS) Secondary else HintText
                            ),
                            color = if (selectedMethod == PaymentMethod.CASHLESS) Color(
                                0xFFEFF6FF
                            ) else Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (selectedMethod == PaymentMethod.CASHLESS) Primary else Outline,
                                            RoundedCornerShape(8.dp)
                                        ),
                                ) {
                                    Icon(
                                        modifier = Modifier
                                            .padding(8.dp),
                                        painter = painterResource(R.drawable.baseline_qr_code_24),
                                        tint = if (selectedMethod == PaymentMethod.CASHLESS) Surface else Color.Black,
                                        contentDescription = "Pembayaran QRIS"
                                    )
                                }
                                Spacer(Modifier.width(8.dp))
                                Column(Modifier.weight(1f)) {
                                    Text("QRIS", style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        "Scan QR Code untuk bayar",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (selectedMethod == PaymentMethod.CASHLESS) Primary else Color.Transparent,
                                            RoundedCornerShape(32.dp)
                                        ),
                                ) {
                                    Icon(
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .size(12.dp),
                                        painter = painterResource(R.drawable.outline_check_24),
                                        tint = if (selectedMethod == PaymentMethod.CASHLESS) Color.White else Color.Transparent,
                                        contentDescription = "Checklist"
                                    )
                                }
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        shadowElevation = 2.dp,
                        color = Outline
                    ) {
                        // =========== CASHLESS ===================
                        if (selectedMethod == PaymentMethod.CASHLESS) {
                            val quickAmounts = listOf(
                                10_000,
                                20_000,
                                30_000,
                                50_000,
                                70_000,
                                80_000,
                                90_000,
                                100_000
                            )
                            val isExact = inputAmount == total

                            Column(
                                modifier = Modifier
                                    .padding(24.dp)
                                    .fillMaxWidth(),
                            ) {
                                Text(
                                    "Nominal Pembayaran (Non-Tunai)",
                                    style = MaterialTheme.typography.displaySmall
                                )
                                Spacer(Modifier.height(24.dp))

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    if (qrUrl == null) {
                                        CircularProgressIndicator()
                                        Text("Menyiapkan QRIS...")
                                    } else {

                                        // Gambar QR dari Midtrans
                                        AsyncImage(
                                            model = qrUrl,
                                            contentDescription = "QRIS",
                                            modifier = Modifier
                                                .fillMaxWidth(0.6f)
                                                .aspectRatio(1f)
                                        )

                                        Spacer(Modifier.height(8.dp))
                                        Text(
                                            "Silakan scan QR ini untuk membayar",
                                            style = MaterialTheme.typography.bodyMedium
                                        )

                                        Spacer(Modifier.height(8.dp))
                                        Divider()
                                        Spacer(Modifier.height(8.dp))

                                        // Status pembayaran
                                        Text(
                                            text = "Status: ${paymentStatus ?: "Menunggu..."}",
                                            style = MaterialTheme.typography.titleMedium
                                        )

                                        if (paymentStatus == "settlement") {
                                            Text(
                                                "✅ Pembayaran berhasil!",
                                                color = Color.Green
                                            )
                                        } else if (paymentStatus == "expire") {
                                            Text(
                                                "⚠️ Pembayaran kedaluwarsa",
                                                color = Color.Red
                                            )
                                        } else if (paymentStatus == "pending" || paymentStatus == null) {
                                            Text(
                                                "⌛ Menunggu pembayaran...",
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                }

                                Spacer(Modifier.height(16.dp))

                                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                                    Button(
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp),
                                        enabled = qrUrl != null,
                                        onClick = {
                                            val queueNumber =
                                                transactionViewModel.queuePreview.value
                                            if (queueNumber != null) {
                                                scope.launch {
                                                    PrintHelper.printQueueNumber(
                                                        context,
                                                        queueNumber
                                                    )
                                                }
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Nomor antrian tidak tersedia",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                painter = painterResource(R.drawable.baseline_receipt_24),
                                                contentDescription = "Cetak Nomor Antrian"
                                            )

                                            Spacer(Modifier.width(8.dp))
                                            Text(
                                                "Cetak Nomor Antrian",
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                        }
                                    }

                                    Spacer(Modifier.width(24.dp))

                                    Button(
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp),
                                        enabled = paymentStatus == "settlement",
                                        onClick = {
                                            if (!isPrinterConnected) {
                                                showPrinterWarning = true
                                            } else {
                                                commitTransactionAnyway(
                                                    context = context,
                                                    transactionViewModel = transactionViewModel,
                                                    paymentViewModel = paymentViewModel
                                                )
                                                Toast.makeText(context, "Transaksi berhasil disimpan.", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                painter = painterResource(R.drawable.outline_check_24),
                                                contentDescription = "Transaksi Selesai"
                                            )

                                            Spacer(Modifier.width(8.dp))
                                            Text(
                                                "Transaksi Selesai",
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                        }
                                    }
                                }
                            }

//                        // =========== CASHLESS ===================
//                        if (selectedMethod == PaymentMethod.CASHLESS) {
//                            val quickAmounts = listOf(
//                                10_000,
//                                20_000,
//                                30_000,
//                                50_000,
//                                70_000,
//                                80_000,
//                                90_000,
//                                100_000
//                            )
//                            val isExact = inputAmount == total
//
//                            Column(
//                                modifier = Modifier
//                                    .padding(24.dp)
//                                    .fillMaxWidth(),
//                            ) {
//                                Text(
//                                    "Nominal Pembayaran (Non-Tunai)",
//                                    style = MaterialTheme.typography.displaySmall
//                                )
//                                Spacer(Modifier.height(24.dp))
//
//                                OutlinedTextField(
//                                    value = if (inputAmount == 0) "0" else inputAmount.toString(),
//                                    onValueChange = { paymentViewModel.setInputAmount(it) },
//                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                                    modifier = Modifier.fillMaxWidth(),
//                                    textStyle = MaterialTheme.typography.displaySmall.copy(textAlign = TextAlign.Center),
//                                    colors = TextFieldDefaults.colors(
//                                        unfocusedContainerColor = Surface,
//                                    ),
//                                )
//
//                                Spacer(Modifier.height(16.dp))
//
//                                FlowRow {
//                                    Button(
//                                        onClick = { paymentViewModel.setInputAmount(total.toString()) },
//                                        shape = RoundedCornerShape(36.dp),
//                                        colors = ButtonDefaults.buttonColors(
//                                            containerColor = if (isExact) Primary else Color(
//                                                0xFFE1EEFE
//                                            )
//                                        )
//                                    ) {
//                                        Text(
//                                            "Uang Pas",
//                                            fontWeight = FontWeight.Bold,
//                                            color = if (isExact) Color.White else Primary
//                                        )
//                                    }
//
//                                    quickAmounts.forEach { amount ->
//                                        val isSelected = inputAmount == amount
//                                        Button(
//                                            onClick = { paymentViewModel.setInputAmount(amount.toString()) },
//                                            shape = RoundedCornerShape(36.dp),
//                                            colors = ButtonDefaults.buttonColors(
//                                                containerColor = if (isSelected) Primary else Color(
//                                                    0xFFE1EEFE
//                                                )
//                                            )
//                                        ) {
//                                            Text(
//                                                "Rp ${"%,d".format(amount)}",
//                                                color = if (isSelected) Color.White else Primary
//                                            )
//                                        }
//                                    }
//                                }
//
//                                Spacer(Modifier.height(16.dp))
//
//                                Row(
//                                    modifier = Modifier.fillMaxWidth(),
//                                    horizontalArrangement = Arrangement.SpaceBetween,
//                                    verticalAlignment = Alignment.CenterVertically
//                                ) {
//                                    Text(
//                                        "Uang Diterima",
//                                        style = MaterialTheme.typography.bodyMedium
//                                    )
//                                    Text(
//                                        "Rp $inputAmount",
//                                        style = MaterialTheme.typography.titleMedium
//                                    )
//                                }
//
//                                Spacer(Modifier.height(16.dp))
//                                Divider(thickness = 1.dp)
//                                Spacer(Modifier.height(16.dp))
//
//                                Row(
//                                    modifier = Modifier.fillMaxWidth(),
//                                    horizontalArrangement = Arrangement.SpaceBetween,
//                                    verticalAlignment = Alignment.CenterVertically
//                                ) {
//                                    Text("Kembalian", style = MaterialTheme.typography.displaySmall, color = Primary)
//                                    Text(
//                                        text = "Rp $change",
//                                        style = MaterialTheme.typography.displaySmall,
//                                        color = Primary
//                                    )
//                                }
//
//                                Spacer(Modifier.height(24.dp))
//
//                                Button(
//                                    modifier = Modifier.fillMaxWidth(),
//                                    shape = RoundedCornerShape(12.dp),
//                                    enabled = inputAmount > 0,
//                                    onClick = {
//
//                                        if (!isPrinterConnected) {
//                                            showPrinterWarning = true
//                                        } else {
//                                            commitTransactionAnyway(
//                                                context = context,
//                                                transactionViewModel = transactionViewModel,
//                                                paymentViewModel = paymentViewModel
//                                            )
//                                        }
//                                    }
//                                ) {
//                                    Text("Cetak Struk")
//                                }
//                            }

                            // =========== CASH ===================
                        } else if (selectedMethod == PaymentMethod.CASH) {
                            val quickAmounts = listOf(
                                10_000,
                                20_000,
                                30_000,
                                50_000,
                                70_000,
                                80_000,
                                90_000,
                                100_000
                            )
                            val isExact = inputAmount == total

                            Column(
                                modifier = Modifier
                                    .padding(24.dp)
                                    .fillMaxWidth(),
                            ) {
                                Text(
                                    "Jumlah Uang Diterima",
                                    style = MaterialTheme.typography.displaySmall
                                )
                                Spacer(Modifier.height(24.dp))

                                OutlinedTextField(
                                    value = if (showEmpty && inputAmount == 0) "" else inputAmount.toString(),
                                    onValueChange = { newValue ->
                                        val filtered = newValue.filter { it.isDigit() }
                                        paymentViewModel.setInputAmount(filtered)
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    textStyle = MaterialTheme.typography.displaySmall.copy(textAlign = TextAlign.Center),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .onFocusChanged { focusState ->
                                            if (focusState.isFocused) {
                                                // Saat diklik, kosongkan tampilan kalau nilainya 0
                                                showEmpty = true
                                            } else {
                                                // Saat kehilangan fokus, kembalikan angka jika kosong
                                                if (inputAmount == 0) showEmpty = false
                                            }
                                        },
                                    colors = TextFieldDefaults.colors(
                                        unfocusedContainerColor = Surface,
                                    ),
                                )

                                Spacer(Modifier.height(16.dp))

                                FlowRow {
                                    Button(
                                        onClick = { paymentViewModel.setInputAmount(total.toString()) },
                                        shape = RoundedCornerShape(36.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isExact) Primary else Color(
                                                0xFFE1EEFE
                                            )
                                        )
                                    ) {
                                        Text(
                                            "Uang Pas",
                                            fontWeight = FontWeight.Bold,
                                            color = if (isExact) Color.White else Primary
                                        )
                                    }

                                    quickAmounts.forEach { amount ->
                                        val isSelected = inputAmount == amount
                                        Button(
                                            onClick = { paymentViewModel.setInputAmount(amount.toString()) },
                                            shape = RoundedCornerShape(36.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (isSelected) Primary else Color(
                                                    0xFFE1EEFE
                                                )
                                            )
                                        ) {
                                            Text(
                                                "Rp ${"%,d".format(amount)}",
                                                color = if (isSelected) Color.White else Primary
                                            )
                                        }
                                    }
                                }

                                Spacer(Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Uang Diterima",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        "Rp $inputAmount",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }

                                Spacer(Modifier.height(16.dp))
                                Divider(thickness = 1.dp)
                                Spacer(Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Kembalian",
                                        style = MaterialTheme.typography.displaySmall,
                                        color = Primary
                                    )
                                    Text(
                                        text = "Rp $change",
                                        style = MaterialTheme.typography.displaySmall,
                                        color = Primary
                                    )
                                }

                                Spacer(Modifier.height(24.dp))

                                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                                    Button(
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp),
                                        enabled = isPrinterConnected,
                                        onClick = {
                                            val queueNumber =
                                                transactionViewModel.queuePreview.value
                                            if (queueNumber != null) {
                                                scope.launch {
                                                    PrintHelper.printQueueNumber(
                                                        context,
                                                        queueNumber
                                                    )
                                                }
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Nomor antrian tidak tersedia",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                painter = painterResource(R.drawable.baseline_receipt_24),
                                                contentDescription = "Cetak Nomor Antrian"
                                            )

                                            Spacer(Modifier.width(8.dp))
                                            Text(
                                                "Cetak Nomor Antrian",
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                        }
                                    }

                                    Spacer(Modifier.width(24.dp))

                                    Button(
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp),
                                        enabled = true,
                                        onClick = {

                                            if (!isPrinterConnected) {
                                                showPrinterWarning = true
                                            } else {
                                                commitTransactionAnyway(
                                                    context = context,
                                                    transactionViewModel = transactionViewModel,
                                                    paymentViewModel = paymentViewModel
                                                )
                                            }
                                        }
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                painter = painterResource(R.drawable.baseline_print_24),
                                                contentDescription = "Cetak Struk"
                                            )

                                            Spacer(Modifier.width(8.dp))
                                            Text(
                                                "Cetak Struk",
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            Column(
                                modifier = Modifier
                                    .padding(24.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Silahkan Pilih Metode Pembayaran",
                                    style = MaterialTheme.typography.displaySmall,
                                    color = HintText
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun commitTransactionAnyway(
    context: Context,
    transactionViewModel: TransactionViewModel,
    paymentViewModel: PaymentViewModel
) {
    val error = TransactionValidator.validateTransaction(
        items = transactionViewModel.cups.value,
        total = transactionViewModel.total.value,
        queueNumber = transactionViewModel.queuePreview.value,
        paymentMethod = paymentViewModel.selectedPaymentMethod.value ?: PaymentMethod.CASH,
        cashReceived = paymentViewModel.inputAmount.value,
        nonCashAmount = paymentViewModel.inputAmount.value
    )

    if (error != null) {
        Toast.makeText(
            context,
            (error as? DomainError.InvalidInput)?.reason ?: "Input tidak valid",
            Toast.LENGTH_LONG
        ).show()
        return
    }

    val queueNumber = transactionViewModel.queuePreview.value ?: 1
    val order = transactionViewModel.buildOrderForCommit(
//        cashierId = "kasir123",
        queueNumber = queueNumber,
        paymentMethod = paymentViewModel.selectedPaymentMethod.value ?: PaymentMethod.CASH,
        cashReceived = paymentViewModel.inputAmount.value,
        change = paymentViewModel.change.value,
        nonCashAmount = if (paymentViewModel.selectedPaymentMethod.value != PaymentMethod.CASH)
            paymentViewModel.inputAmount.value else null
    )

    transactionViewModel.setLastOrder(order)
    transactionViewModel.commitTransaction(order)
}


