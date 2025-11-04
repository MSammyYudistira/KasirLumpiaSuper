package com.example.kasirlumpiasuper.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kasirlumpiasuper.R
import com.example.kasirlumpiasuper.data.model.OrderItem
import com.example.kasirlumpiasuper.ui.theme.KasirLumpiaSuperTheme
import com.example.kasirlumpiasuper.ui.theme.Outline
import com.example.kasirlumpiasuper.ui.theme.Primary
import com.example.kasirlumpiasuper.ui.theme.PrimaryBold
import com.example.kasirlumpiasuper.ui.theme.Secondary
import com.example.kasirlumpiasuper.ui.theme.Surface
import com.example.kasirlumpiasuper.ui.transaction.TransactionViewModel

@Composable
fun AddButtonStock(
    title: String,
) {
    var count by remember { mutableStateOf(0) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(title, style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = Color.LightGray,
                    shape = RoundedCornerShape(8.dp)
                ),
            shape = RoundedCornerShape(8.dp),
            color = Color.Transparent
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Tombol minus
                Box(
                    modifier = Modifier
                        .background(Color(0xFFE0F2FF), shape = RoundedCornerShape(8.dp))
                ) {
                    IconButton(onClick = { if (count > 0) count-- }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_minus_24),
                            contentDescription = "Minus Button",
                            tint = Color(0xFF1565C0) // biru bold
                        )
                    }
                }
                // Jumlah
                BasicTextField(
                    value = count.toString(),
                    onValueChange = { newValue ->
                        // filter hanya angka
                        val filtered = newValue.filter { it.isDigit() }
                        count = filtered.toIntOrNull() ?: 0
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .width(300.dp) // biar ga kepanjangan
                )

                // Tombol plus
                Box(
                    modifier = Modifier
                        .background(Color(0xFFE0F2FF), shape = RoundedCornerShape(8.dp))
                ) {
                    IconButton(onClick = { count++ }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_add_24),
                            contentDescription = "Add Button",
                            tint = Color(0xFF1565C0) // biru bold
                        )
                    }
                }
            }
        }
    }
}

//@Composable
//fun PlusCircleButton(
//    onClick: () -> Unit,
//    enabled: Boolean = true,
//) {
//    Box(
//        modifier = Modifier
//            .clip(RoundedCornerShape(4.dp))
//            .background(Secondary)
//            .alpha(if (enabled) 1f else 0.5f)
//            .clickable(enabled = enabled, onClick = onClick),
//        contentAlignment = Alignment.Center
//    ) {
//        Icon(
//            imageVector = Icons.Default.Add,
//            contentDescription = "Tambah",
//            tint = PrimaryBold
//        )
//    }
//}
//
//@Composable
//fun MinusRoundedButton(
//    onClick: () -> Unit,
//    enabled: Boolean = true
//) {
//    Box(
//        modifier = Modifier
//            .clip(RoundedCornerShape(4.dp))
//            .background(Color.White)
//            .border(1.dp, Secondary, RoundedCornerShape(4.dp))
//            .alpha(if (enabled) 1f else 0.5f)
//            .clickable(enabled = enabled, onClick = onClick),
//        contentAlignment = Alignment.Center
//    ) {
//        // pakai ikon minus default, atau pakai Box garis custom kalau mau tipis
//        Icon(
//            painter = painterResource(R.drawable.outline_minus_24),
//            contentDescription = "Kurangi",
//            tint = Color.Black
//        )
//    }
//}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StockGridStatic(items: List<String>, modifier: Modifier = Modifier) {
    // Grid non-scroll: 3 item per baris, jarak rapi
    FlowRow(
        maxItemsInEachRow = 2,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        items.forEach { title ->
            // Biarkan komponen menentukan lebarnya; FlowRow yang membungkus jadi 3 kolom
            AddButtonStock(title = title)
        }
    }
}

@Composable
fun CustomActionButton(onClicked: () -> Unit, text: String, enabled: Boolean = true) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
//            .padding(start = 24.dp, end = 24.dp)
            .padding(bottom = 24.dp),
        shape = RoundedCornerShape(8.dp),
        onClick = onClicked,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled) Primary else Color.Gray.copy(alpha = 0.5f)
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )
    }
}

@Composable
fun AddButtonTransaction(
    item: OrderItem,
    transactionViewModel: TransactionViewModel = viewModel()
) {

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Outline
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(4.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(Surface, CircleShape)
                    .clickable { transactionViewModel.decQty(item) }
            ) {
                Icon(
                    painter = painterResource(R.drawable.outline_minus_24),
                    contentDescription = "Remove"
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Jumlah
            Text(
                text = item.qty.toString(),
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(Surface, CircleShape)
                    .clickable { transactionViewModel.incQty(item) }
            ) {
                Icon(
                    painter = painterResource(R.drawable.outline_add_24),
                    contentDescription = "Add"
                )
            }
        }
    }
}

fun queueLabel(n: Int?): String =
    if (n == null) "---" else n.toString().padStart(3, '0')

@Preview(showBackground = true)
@Composable
private fun AddButtonPreview() {
    KasirLumpiaSuperTheme {
        AddButtonStock(
            title = "Lumpia"
        )
    }
}