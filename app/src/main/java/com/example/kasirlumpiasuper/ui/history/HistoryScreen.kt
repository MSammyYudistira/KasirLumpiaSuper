package com.example.kasirlumpiasuper.ui.history

import android.R.attr.onClick
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.view.ContextThemeWrapper
import android.widget.Toast
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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.kasirlumpiasuper.ui.recap.RecapViewModel
import com.example.kasirlumpiasuper.ui.theme.OnSurfaceVariant
import com.example.kasirlumpiasuper.ui.theme.Primary
import com.example.kasirlumpiasuper.ui.theme.PrimaryBold
import com.example.kasirlumpiasuper.ui.utils.DateUtils
import com.example.kasirlumpiasuper.ui.utils.DateUtils.timeLabel
import com.example.kasirlumpiasuper.ui.utils.PrintHelper
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HistoryScreen(
    navController: NavHostController,
    viewModel: HistoryViewModel = viewModel()
) {
    val recapViewModel: RecapViewModel = viewModel()
    var hasInputRecap by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val context = LocalContext.current
    val dateKey by viewModel.selectedDateKey.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.errorMessage.collectAsState()
    val receiptCount by viewModel.receiptCount.collectAsState()
    val grandTotal by viewModel.grandTotal.collectAsState()

    val scope = rememberCoroutineScope()

    LaunchedEffect(dateKey) {
        val result = recapViewModel.hasRecapInput(dateKey)
        hasInputRecap = result
    }

    LaunchedEffect(dateKey) {
        viewModel.fetchOrders(dateKey)
    }

    LaunchedEffect(Unit) {
        viewModel.initLoadIfNeeded()
    }

    Scaffold {
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Primary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .padding(horizontal = 72.dp),
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
                                contentDescription = "date picker",
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = dateKey,
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                    }
                }

                item {
                    val filterOrders = remember(searchQuery, orders) {
                        if (searchQuery.isBlank()) {
                            orders
                        } else {
                            val q = searchQuery.trim().lowercase()

                            orders.filter { order ->
                                val queueMatch = q.startsWith("#") && order.queueNumber.toString() == q.removePrefix("#")
                                val itemMatch = order.items.any { item ->
                                    val nameMatch = item.name.lowercase().contains(q)
                                    val qtyMatch = "${item.name.lowercase()} ${item.qty}".contains(q)
                                    nameMatch || qtyMatch
                                }
                                queueMatch || itemMatch
                            }
                        }
                    }

                    when {
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

//                        filterOrders.isEmpty() && searchQuery.isNotBlank() -> {
//                            Text(
//                                "Tidak ada hasil untuk \"$searchQuery\"",
//                                style = MaterialTheme.typography.bodyMedium,
//                                color = Color.Gray,
//                                modifier = Modifier.padding(vertical = 16.dp)
//                            )
//                        }
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
                                        style = MaterialTheme.typography.displaySmall
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))

                                    if (filterOrders.isEmpty() && searchQuery.isNotBlank()) {
                                        Text(
                                            text = "Tidak ada hasil untuk \"$searchQuery\"",
                                            color = Color.Red.copy(alpha = 0.8f),
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        )
                                    }

                                    OutlinedTextField(
                                        value = searchQuery,
                                        onValueChange = { searchQuery = it },
                                        label = { Text("Cari Struk atau Item...") },
                                        trailingIcon = {
                                            if (searchQuery.isNotEmpty()) {
                                                IconButton(onClick =  { searchQuery = "" }) {
                                                    Icon(
                                                        painter = painterResource(R.drawable.baseline_close_24),
                                                        contentDescription = "Hapus pencarian"
                                                    )
                                                }
                                            }
                                        },
                                        placeholder = { Text("contoh: #5 atau Lumpia 2") },
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp),
                                        singleLine = true,
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    // 🔹 Daftar transaksi
                                    filterOrders
                                        .sortedByDescending { it.queueNumber }
                                        .forEach { order ->
                                        HistoryListItem(
                                            order,
                                            onPrint = {
                                                scope.launch {
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
                                            },
                                            onClick = {
                                                navController.navigate("${NavRoutes.OrderDetail.route}/${dateKey}/${order.queueNumber}")
                                            }
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                "Grand Total",
                                                style = MaterialTheme.typography.titleLarge
                                            )
                                            Text(
                                                "$receiptCount Total Struk",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                        Text(
                                            DateUtils.rupiah(grandTotal),
                                            style = MaterialTheme.typography.displaySmall,
                                            color = Primary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        if (!hasInputRecap) {
                            Text(
                                "Kamu belum isi data rekapan hari ini!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Red
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Surface(
                            onClick = {
                                scope.launch {
                                    navController.navigate(
                                        "${NavRoutes.InputRecap.route}/${
                                            Uri.encode(
                                                dateKey
                                            )
                                        }"
                                    )
                                }
                            },
                            enabled = orders.isNotEmpty(),
                            shape = RoundedCornerShape(8.dp),
                            color = Color.Transparent
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.outline_edit_square_24),
                                    contentDescription = "Isi Data Rekapan",
                                    tint = Primary
                                )

                                Spacer(Modifier.width(6.dp))

                                Text(
                                    "Isi Data Rekapan",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Primary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                navController.navigate(
                                    "${NavRoutes.DetailRecap.route}/${
                                        Uri.encode(
                                            dateKey
                                        )
                                    }"
                                )
                            }

                        },
                        enabled = hasInputRecap,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Lihat Rekapan", style = MaterialTheme.typography.titleMedium)
                    }

                }
            }
        }
    }
}

@Composable
private fun HistoryListItem(
    order: Order,
    onPrint: () -> Unit,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clickable{ onClick() }
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
                        fontWeight = FontWeight.Bold,
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Detail ringkas per cup + metode pembayaran
                        val desc = buildString {
                            append(
                                order.items.joinToString(", ") { item ->
                                    "${item.name} ${item.qty}${if (item.isFree) " (Free)" else ""}"
                                }
                            )
                            append(" • ${order.paymentMethod.name}") // tambahkan metode pembayaran di akhir
                        }

                        Text(
                            desc,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Text(
                DateUtils.rupiah(order.total),
                style = MaterialTheme.typography.displaySmall,
                color = Primary
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Action bar
            IconButton(onClick = { onPrint() }) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Primary,
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

    // 🔹 Bungkus context dengan tema biru AppCompat
    val themedContext = ContextThemeWrapper(context, R.style.BlueDatePickerTheme)

    DatePickerDialog(
        themedContext,
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