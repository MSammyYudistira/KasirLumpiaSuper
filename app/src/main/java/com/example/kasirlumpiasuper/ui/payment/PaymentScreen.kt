package com.example.kasirlumpiasuper.ui.payment

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.kasirlumpiasuper.R
import com.example.kasirlumpiasuper.data.model.PaymentMethod
import com.example.kasirlumpiasuper.ui.components.CustomTopBarWithBackAction
import com.example.kasirlumpiasuper.ui.components.queueLabel
import com.example.kasirlumpiasuper.ui.navigation.NavRoutes
import com.example.kasirlumpiasuper.ui.theme.HintText
import com.example.kasirlumpiasuper.ui.theme.Outline
import com.example.kasirlumpiasuper.ui.theme.Primary
import com.example.kasirlumpiasuper.ui.theme.PrimaryBold
import com.example.kasirlumpiasuper.ui.theme.Secondary
import com.example.kasirlumpiasuper.ui.theme.Success
import com.example.kasirlumpiasuper.ui.theme.Surface
import com.example.kasirlumpiasuper.ui.transaction.TransactionViewModel
import com.example.kasirlumpiasuper.ui.utils.PrintHelper
import kotlinx.coroutines.launch

@Composable
fun PaymentScreen(
    navController: NavHostController,
    paymentViewModel: PaymentViewModel,
    transactionViewModel: TransactionViewModel,
    context: Context = LocalContext.current
) {
    val subtotal by transactionViewModel.subtotal.collectAsState()
    val total by transactionViewModel.total.collectAsState()
    val queuePreview by transactionViewModel.queuePreview.collectAsState()
    val cups by transactionViewModel.cups.collectAsState()
    val discount by transactionViewModel.discountInput.collectAsState()
    val notes by transactionViewModel.notes.collectAsState()

    val inputAmount by paymentViewModel.inputAmount.collectAsState()
    val selectedMethod by paymentViewModel.selectedPaymentMethod.collectAsState()
    val change by paymentViewModel.change.collectAsState()

    LaunchedEffect(total) {
        paymentViewModel.setTotalOrder(total)
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
                        Text("Detail Pesanan", style = MaterialTheme.typography.titleLarge)
                        Text("#${queueLabel(queuePreview)}")
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Divider(thickness = 2.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "Catatan", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = notes,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Column {
                        cups.toSortedMap().forEach { (cupIndex, items) ->
                            Spacer(Modifier.height(8.dp))

                            Text(
                                "Cup $cupIndex",
                                style = MaterialTheme.typography.titleMedium,
                                color = PrimaryBold
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

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subtotal", style = MaterialTheme.typography.titleLarge)
                        Text("Rp $subtotal", style = MaterialTheme.typography.titleLarge)
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
                    Text("Pilih Metode Pembayaran", style = MaterialTheme.typography.titleLarge)

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
                                Column {
                                    Text("Cash", style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        "Pembayaran dengan uang tunai",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
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

                        if (selectedMethod == PaymentMethod.CASHLESS) {
                            Column(
                                modifier = Modifier
                                    .padding(24.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.lumpia_super),
                                    contentDescription = "QRIS Image",
                                    modifier = Modifier.size(150.dp)
                                )

                                Text(
                                    "Scan QR Code untuk melakukan pembayaran",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text("Total: Rp 42.000", style = MaterialTheme.typography.bodySmall)
                            }
                        } else if (selectedMethod == PaymentMethod.CASH) {
                            Column(
                                modifier = Modifier
                                    .padding(24.dp)
                                    .fillMaxWidth(),
                            ) {

                                Text(
                                    "Jumlah Uang Diterima",
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Spacer(Modifier.height(24.dp))

                                OutlinedTextField(
                                    value = if (inputAmount == 0) "0" else inputAmount.toString(),
                                    onValueChange = { paymentViewModel.setInputAmount(it) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth(),
                                    textStyle = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center),
                                    colors = TextFieldDefaults.colors(
                                        unfocusedContainerColor = Surface,
                                    ),
                                )

                                Spacer(Modifier.height(16.dp))
                                // Rincian perhitungan
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Discount", style = MaterialTheme.typography.bodyMedium)
                                    Text(
                                        text = "Rp ($discount)",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Success
                                    )
                                }

                                Spacer(Modifier.height(8.dp))

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

                                Spacer(Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Total", style = MaterialTheme.typography.bodyMedium)
                                    Text("Rp $total", style = MaterialTheme.typography.titleMedium)
                                }

                                Spacer(Modifier.height(16.dp))
                                Divider(thickness = 1.dp)
                                Spacer(Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Kembalian", style = MaterialTheme.typography.bodyLarge)
                                    Text(
                                        text = "Rp $change",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = Primary
                                    )
                                }

                                Spacer(Modifier.height(24.dp))

                                Button(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    onClick = {
                                        val queueNumber =
                                            transactionViewModel.queuePreview.value ?: 1
                                        val order = transactionViewModel.buildOrderForCommit(
                                            cashierId = "kasir123",
                                            queueNumber = queueNumber,
                                            paymentMethod = paymentViewModel.selectedPaymentMethod.value
                                                ?: PaymentMethod.CASH,
                                            cashReceived = paymentViewModel.inputAmount.value,
                                            change = paymentViewModel.change.value,
                                            nonCashAmount = if (paymentViewModel.selectedPaymentMethod.value != PaymentMethod.CASH) paymentViewModel.inputAmount.value else null
                                        )

                                        transactionViewModel.saveOrder(order) { success, error ->
                                            if (success) {
                                                transactionViewModel.setLastOrder(order)

                                                transactionViewModel.viewModelScope.launch {
                                                    transactionViewModel.fetchQueuePreview()
                                                }

                                                PrintHelper.printReceipt(
                                                    context = context,
                                                    order = order
                                                )
                                                Toast.makeText(
                                                    context,
                                                    "Transaksi berhasil & struk dicetak",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Gagal menyimpan: $error",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }

                                        transactionViewModel.resetTransaction()
                                        paymentViewModel.reset()
                                        navController.navigate(NavRoutes.DashboardKasir.route)
                                    }
                                ) {
                                    Text("Cetak Struk")
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
                                    style = MaterialTheme.typography.titleLarge,
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

//@Preview(showBackground = true, device = Devices.TABLET)
//@Composable
//private fun PaymentPreview() {
//    KasirLumpiaSuperTheme {
//        PaymentScreen(navcontroller = rememberNavController())
//    }
//}