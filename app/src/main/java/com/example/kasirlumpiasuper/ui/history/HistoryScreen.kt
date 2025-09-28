package com.example.kasirlumpiasuper.ui.history

import android.app.DatePickerDialog
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.kasirlumpiasuper.R
import com.example.kasirlumpiasuper.data.model.Order
import com.example.kasirlumpiasuper.ui.navigation.NavRoutes
import com.example.kasirlumpiasuper.ui.theme.OnSurfaceVariant
import com.example.kasirlumpiasuper.ui.theme.Primary
import com.example.kasirlumpiasuper.ui.utils.DateUtils
import com.example.kasirlumpiasuper.ui.utils.DateUtils.timeLabel
import com.example.kasirlumpiasuper.ui.utils.PrintHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun HistoryScreen(
    navController: NavHostController,
    viewModel: HistoryViewModel = viewModel()
) {

    val context = LocalContext.current
    val dateKey by viewModel.selectedDateKey.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.errorMessage.collectAsState()
    val receiptCount by viewModel.receiptCount.collectAsState()
    val grandTotal by viewModel.grandTotal.collectAsState()

    LaunchedEffect(dateKey) {
        viewModel.fetchOrders(dateKey)
    }

    LaunchedEffect(Unit) {
        viewModel.initLoadIfNeeded()
    }

    Scaffold { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 72.dp)
                .padding(top = 16.dp)
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = 4.dp,
                    onClick = {
                        showDatePicker(
                            context = context,
                            currentKey = dateKey,
                            onPick = { viewModel.setSelectedDateKey(it) }
                        )
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)

                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_date_range_24),
                            contentDescription = "date picker"
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = dateKey,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

            item {
                when {
                    isLoading -> {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }

                    error != null -> {
                        Text(
                            text = error ?: "Terjadi kesalahan",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    orders.isEmpty() -> {
                        Text(
                            "Belum ada transaksi untuk tanggal ini",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    else -> {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            shadowElevation = 4.dp,
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 24.dp, vertical = 16.dp)
                            ) {
                                Text(
                                    "Transaksi Hari Ini",
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Spacer(modifier = Modifier.height(4.dp))

                                // 🔹 Daftar transaksi
                                orders.forEach { order ->
                                    HistoryListItem(order) {
                                        try {
                                            PrintHelper.printReceipt(context, order)
                                        } catch (e: Exception) {
                                            Toast.makeText(
                                                context,
                                                "Gagal print: ${e.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            "Grand Total",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            "$receiptCount Total Struk",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                    Text(
                                        DateUtils.rupiah(grandTotal),
                                        style = MaterialTheme.typography.titleLarge,
                                        color = Primary
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        navController.navigate(NavRoutes.InputRecap.route)
                    },
                    enabled = orders.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Buat Rekapan")
                }
            }
        }
    }
}

@Composable
private fun HistoryListItem(
    order: Order,
    onPrint: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // Header: Struk #xxx + total
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    painter = painterResource(R.drawable.outline_receipt_24),
                    contentDescription = "Struk",
                    tint = OnSurfaceVariant,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        "Struk #${order.queueNumber}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Detail ringkas per cup
                        val desc = order.items.joinToString(", ") { item ->
                            "${item.name} ${item.qty} ${if (item.isFree) " (FREE)" else ""}"
                        }
                        Text(desc, style = MaterialTheme.typography.bodySmall)

                        // Waktu
                        Text(
                            text = " ${timeLabel(order.createdAt)}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Text(
                DateUtils.rupiah(order.total),
                style = MaterialTheme.typography.titleLarge,
                color = Primary
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Action bar
            IconButton(onClick = { onPrint }) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Primary
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_print_24),
                        contentDescription = "Print",
                        tint = Color.White,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
        }
        Divider(thickness = 1.dp)
    }
}

fun showDatePicker(
    context: Context,
    currentKey: String,
    onPick: (String) -> Unit
) {
    val cal = Calendar.getInstance()
    val fmt = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
    try {
        cal.time = fmt.parse(currentKey)!!
    } catch (_: Exception) {
    }

    DatePickerDialog(
        context,
        { _, year, month, day ->
            val pickedCal = Calendar.getInstance().apply {
                set(year, month, day)
            }
            onPick(fmt.format(pickedCal.time)) // ✅ format label
        },
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH),
        cal.get(Calendar.DAY_OF_MONTH)
    ).show()
}