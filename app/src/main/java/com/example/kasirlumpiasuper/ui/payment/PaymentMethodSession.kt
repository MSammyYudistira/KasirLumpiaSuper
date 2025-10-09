package com.example.kasirlumpiasuper.ui.payment

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.kasirlumpiasuper.R
import com.example.kasirlumpiasuper.data.model.PaymentMethod
import com.example.kasirlumpiasuper.ui.theme.HintText
import com.example.kasirlumpiasuper.ui.theme.Outline
import com.example.kasirlumpiasuper.ui.theme.Primary
import com.example.kasirlumpiasuper.ui.theme.Secondary
import com.example.kasirlumpiasuper.ui.theme.Success
import com.example.kasirlumpiasuper.ui.theme.Surface

@Composable
fun PaymentMethodSection(
    selectedMethod: PaymentMethod?,
    inputAmount: Int,
    discount: Int,
    total: Int,
    change: Int,
    onSelectMethod: (PaymentMethod) -> Unit,
    onAmountChange: (String) -> Unit,
    onConfirm: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 48.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            "Pilih Metode Pembayaran",
            style = MaterialTheme.typography.displaySmall
        )

        // 🔘 PILIHAN METODE PEMBAYARAN
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // 💵 CASH
            PaymentMethodCard(
                title = "Cash",
                description = "Pembayaran dengan uang tunai",
                iconRes = R.drawable.baseline_money_bill_wave_24,
                isSelected = selectedMethod == PaymentMethod.CASH,
                onClick = { onSelectMethod(PaymentMethod.CASH) },
                modifier = Modifier.weight(1f)
            )

            // 📱 CASHLESS
            PaymentMethodCard(
                title = "QRIS",
                description = "Scan QR Code untuk bayar",
                iconRes = R.drawable.baseline_qr_code_24,
                isSelected = selectedMethod == PaymentMethod.CASHLESS,
                onClick = { onSelectMethod(PaymentMethod.CASHLESS) },
                modifier = Modifier.weight(1f)
            )
        }

        // 🧾 DETAIL INPUT BERDASARKAN METODE
        when (selectedMethod) {
            PaymentMethod.CASH -> {
                CashPaymentSection(
                    inputAmount = inputAmount,
                    discount = discount,
                    total = total,
                    change = change,
                    onAmountChange = onAmountChange,
                    onConfirm = onConfirm
                )
            }

            PaymentMethod.CASHLESS -> {
                CashlessPaymentSection(total = total, onConfirm = onConfirm)
            }

            else -> {
                Text(
                    text = "Silakan pilih metode pembayaran",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = HintText,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
fun PaymentMethodCard(
    title: String,
    description: String,
    iconRes: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = Modifier
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, if (isSelected) Secondary else HintText),
        color = if (isSelected) Color(0xFFEFF6FF) else Color.Transparent,
        shadowElevation = if (isSelected) 2.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(
                        if (isSelected) Primary else Outline,
                        RoundedCornerShape(8.dp)
                    ),
            ) {
                Icon(
                    modifier = Modifier.padding(8.dp),
                    painter = painterResource(iconRes),
                    tint = if (isSelected) Surface else Color.Black,
                    contentDescription = title
                )
            }

            Spacer(Modifier.width(8.dp))

            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(description, style = MaterialTheme.typography.bodySmall)
            }

            if (isSelected) {
                Icon(
                    painter = painterResource(R.drawable.outline_check_24),
                    contentDescription = "Selected",
                    tint = Primary
                )
            }
        }
    }
}

@Composable
fun CashPaymentSection(
    inputAmount: Int,
    discount: Int,
    total: Int,
    change: Int,
    onAmountChange: (String) -> Unit,
    onConfirm: () -> Unit
) {

    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth()
    ) {
        Text("Jumlah Uang Diterima", style = MaterialTheme.typography.displaySmall)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = if (inputAmount == 0) "" else inputAmount.toString(),
            onValueChange = onAmountChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.displaySmall.copy(textAlign = TextAlign.Center),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Surface
            ),
            placeholder = { Text("0", textAlign = TextAlign.Center) }
        )

        Spacer(Modifier.height(16.dp))
        Divider(thickness = 1.dp)
        Spacer(Modifier.height(16.dp))

        // 🔢 DETAIL PERHITUNGAN
        SummaryRow("Discount", "Rp ($discount)", Success)
        SummaryRow("Total", "Rp $total")
        SummaryRow("Uang Diterima", "Rp $inputAmount")
        SummaryRow("Kembalian", "Rp $change", Primary, bold = true)

        Spacer(Modifier.height(24.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            onClick = onConfirm
        ) {
            Text("Cetak Struk")
        }
    }
}

@Composable
fun CashlessPaymentSection(total: Int, onConfirm: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.lumpia_super),
            contentDescription = "QRIS",
            modifier = Modifier.size(150.dp)
        )

        Spacer(Modifier.height(8.dp))
        Text("Scan QR Code untuk membayar", style = MaterialTheme.typography.bodySmall)
        Text("Total: Rp $total", style = MaterialTheme.typography.titleMedium)

        Spacer(Modifier.height(16.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            onClick = onConfirm
        ) {
            Text("Konfirmasi & Cetak Struk")
        }
    }
}

@Composable
fun SummaryRow(
    label: String,
    value: String,
    color: Color = Color.Black,
    bold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = if (bold) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium
        )
        Text(
            value,
            style = if (bold) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            color = color
        )
    }
}



