package com.example.kasirlumpiasuper.ui.components

import android.R.attr.fontWeight
import android.R.attr.maxLines
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
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kasirlumpiasuper.R
import com.example.kasirlumpiasuper.data.model.OrderItem
import com.example.kasirlumpiasuper.ui.theme.KasirLumpiaSuperTheme
import com.example.kasirlumpiasuper.ui.theme.Outline
import com.example.kasirlumpiasuper.ui.theme.Surface
import com.example.kasirlumpiasuper.ui.transaction.TransactionViewModel
import com.google.firebase.firestore.AggregateField.count

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

//                Spacer(modifier = Modifier.width(96.dp))

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
                        .width(232.dp) // biar ga kepanjangan
                )

//                Spacer(modifier = Modifier.width(96.dp))

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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StockGridStatic(items: List<String>, modifier: Modifier = Modifier) {
    // Grid non-scroll: 3 item per baris, jarak rapi
    FlowRow(
        maxItemsInEachRow = 3,
        horizontalArrangement = Arrangement.spacedBy(46.dp, Alignment.CenterHorizontally),
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
fun CustomActionButton(onClicked: () -> Unit, text: String) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp)
            .padding(bottom = 24.dp),
        shape = RoundedCornerShape(8.dp),
        onClick = onClicked
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

@Composable
fun CustomDropdown(
    options: List<String>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    placeholder: String? = null,
    containerColor: Color = Color(0xFFF2F4F6), // abu terang seperti contoh
    contentColor: Color = Color(0xFF1D2433),   // teks gelap
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Surface(
            color = if (enabled) containerColor else containerColor.copy(alpha = 0.6f),
            shape = RoundedCornerShape(128.dp),
            shadowElevation = 0.dp,
            tonalElevation = 0.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .defaultMinSize(minWidth = 110.dp)
                .clickable(
                    enabled = enabled,
                    role = Role.Button,
                    onClick = { expanded = true }
                )
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 14.dp)
                    .fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = selectedIndex.takeIf { it in options.indices }
                        ?.let { options[it] }
                        ?: (placeholder ?: ""),
                    color = contentColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
//                    modifier = Modifier.weight(1f),
                    maxLines = 1
                )

                Box(
                    modifier = Modifier
                        .background(Surface, CircleShape)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.outline_arrow_drop_down_24),
                        contentDescription = null,
                        tint = contentColor
                    )
                }
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            options.forEachIndexed { i, label ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        onSelected(i)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CupDropdown(
    current: Int,
    onChange: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val options = (1..10).toList()

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            readOnly = true,
            value = "Cup-$current",
            onValueChange = {},
            label = { Text("Cup") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { v ->
                DropdownMenuItem(text = { Text("Cup-$v") }, onClick = {
                    onChange(v); expanded = false
                })
            }
        }
    }
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ServingDropdown(
//    current: Serving,
//    onChange: (Serving) -> Unit
//) {
//    var expanded by remember { mutableStateOf(false) }
//    val options = Serving.values()
//
//    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
//        OutlinedTextField(
//            readOnly = true,
//            value = current.name,
//            onValueChange = {},
//            label = { Text("Penyajian") },
//            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
//            modifier = Modifier.menuAnchor()
//        )
//        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
//            options.forEach { p ->
//                DropdownMenuItem(text = { Text(p.name) }, onClick = {
//                    onChange(p); expanded = false
//                })
//            }
//        }
//    }
//}

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

//@Preview(showBackground = true)
//@Composable
//private fun AddButtonTransactionPreview() {
//    KasirLumpiaSuperTheme {
//        AddButtonTransaction(
//        )
//    }
//}