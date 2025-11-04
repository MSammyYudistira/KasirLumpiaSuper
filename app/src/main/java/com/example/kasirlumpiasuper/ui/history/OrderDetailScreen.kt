package com.example.kasirlumpiasuper.ui.history

import android.R.attr.onClick
import android.widget.Toast
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.kasirlumpiasuper.R
import com.example.kasirlumpiasuper.ui.components.CustomTopBarWithBackAction
import com.example.kasirlumpiasuper.ui.theme.Danger
import com.example.kasirlumpiasuper.ui.theme.Primary
import com.example.kasirlumpiasuper.ui.theme.Success
import com.example.kasirlumpiasuper.ui.utils.DateUtils
import com.example.kasirlumpiasuper.ui.utils.PrintHelper
import kotlinx.coroutines.launch


@Composable
fun OrderDetailScreen(
    navController: NavHostController,
    dateKey: String,
    queueNumber: Int,
    viewModel: HistoryViewModel,
    isAdmin: Boolean
) {
    val order by viewModel.selectedOrder.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val items by viewModel.orderItems.collectAsState()
    val total by viewModel.orderTotal.collectAsState()

    val openDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope() //

    LaunchedEffect(dateKey, queueNumber) {
        viewModel.loadOrderByQueue(dateKey, queueNumber)
    }

    Scaffold(
        topBar = {
            CustomTopBarWithBackAction(
                onBackClick = { navController.popBackStack() },
                title = "Detail Struk"
            )
        }
    ) { innerPadding ->
        when {
            isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            order == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Data struk tidak ditemukan")
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(horizontal = 72.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
//                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            shadowElevation = 4.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp)
                                .wrapContentHeight() // 🔹 penting agar tinggi mengikuti isi
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 32.dp, vertical = 24.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // 🔹 Header: Nomor Struk & Jumlah
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            "No. Struk",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Spacer(Modifier.height(8.dp))
                                        Text(
                                            "Tanggal Pemesanan",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Spacer(Modifier.height(8.dp))
                                        Text(
                                            "Metode Pembayaran",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            "#${order!!.queueNumber}",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Spacer(Modifier.height(8.dp))
                                        Text(
                                            "${DateUtils.dateLabel(order!!.createdAt)} | ${
                                                DateUtils.timeLabel(
                                                    order!!.createdAt
                                                )
                                            }",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(Modifier.height(8.dp))
                                        Text(
                                            text = order!!.paymentMethod.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Divider(thickness = 1.dp, color = Color.LightGray)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
//                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        // 🔹 Catatan
                                        if (order!!.notes.isNotBlank()) {
                                            Text(
                                                "Catatan",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                order!!.notes,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    }

                                    Surface(
                                        modifier = Modifier
                                            .size(40.dp) // ✅ Ukuran bebas kamu tentukan
                                            .clickable {
                                                if (order != null) {
                                                    scope.launch {
                                                        try {
                                                            PrintHelper.printReceipt(
                                                                context,
                                                                order!!
                                                            )
                                                        } catch (e: Exception) {
                                                            Toast.makeText(
                                                                context,
                                                                "Gagal print: ${e.message}",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    }
                                                }
                                            },
                                        shape = RoundedCornerShape(6.dp),
                                        color = Primary
                                    ) {
                                        Icon(
                                            modifier = Modifier.padding(6.dp),
                                            painter = painterResource(R.drawable.baseline_print_24),
                                            contentDescription = "Cetak Struk",
                                            tint = Color.White
                                        )
                                    }

                                    if (isAdmin == true) {
                                        Spacer(Modifier.width(16.dp))

                                        Surface(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clickable {
                                                    val currentDateKey = dateKey
                                                    val currentQueue = queueNumber

                                                    if (currentDateKey.isNotBlank() && currentQueue > 0) {
                                                        navController.navigate(
                                                            "transaction?dateKey=$currentDateKey&queueNumber=$currentQueue"
                                                        )
                                                    } else {
                                                        Toast.makeText(
                                                            context,
                                                            "Data transaksi tidak valid",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                },
                                            shape = RoundedCornerShape(6.dp),
                                            color = Primary
                                        ) {
                                            Icon(
                                                modifier = Modifier.padding(6.dp),
                                                painter = painterResource(R.drawable.outline_edit_square_24),
                                                contentDescription = "Edit Transaksi",
                                                tint = Color.White
                                            )
                                        }

                                        Spacer(Modifier.width(16.dp))

                                        Surface(
                                            modifier = Modifier
                                                .size(40.dp) // ✅ Ukuran bebas kamu tentukan
                                                .clickable {
                                                    openDialog.value = true
                                                },
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color.Red
                                        ) {
                                            Icon(
                                                modifier = Modifier.padding(6.dp),
                                                painter = painterResource(R.drawable.baseline_delete_24),
                                                contentDescription = "Hapus Transaksi",
                                                tint = Color.White
                                            )
                                        }
                                    }
                                }

                                Divider(thickness = 1.dp, color = Color.LightGray)

                                // 🔹 Jenis Makanan
                                Text(
                                    "Jenis Makanan",
                                    color = Primary,
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Spacer(Modifier.height(2.dp))

                                val groupedByCup = items.groupBy { it.cupIndex }

                                groupedByCup.forEach { (cupIndex, cupItems) ->
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        Text(
                                            "Cup $cupIndex",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Spacer(Modifier.height(8.dp))
                                        cupItems.forEach { item ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(Modifier.weight(1f)) {
                                                    Text(
                                                        if (item.isFree) "${item.name} (Free)" else item.name,
                                                        style = MaterialTheme.typography.titleMedium
                                                    )
                                                    Text(
                                                        "${item.qty}x ${DateUtils.rupiah(item.unitPrice)}",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = Color.Gray
                                                    )
                                                }

                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Text(
                                                        DateUtils.rupiah(item.unitPrice * item.qty),
                                                        style = MaterialTheme.typography.titleMedium
                                                    )
                                                }
                                            }
                                            Spacer(Modifier.height(4.dp))
                                        }
                                        Spacer(Modifier.height(8.dp))
                                    }
                                }
                                Divider(thickness = 1.dp, color = Color.LightGray)
                                if ((order?.discount ?: 0) > 0) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            "Hemat",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Success
                                        )
                                        Text(
                                            DateUtils.rupiah(order!!.discount),
                                            style = MaterialTheme.typography.titleMedium,
                                            color = Success
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Jumlah", style = MaterialTheme.typography.bodyLarge)
                                    Text(
                                        DateUtils.rupiah(total),
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Column {
//                            Row(
//                                modifier = Modifier.fillMaxWidth(),
//                                horizontalArrangement = Arrangement.SpaceBetween,
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                if (isAdmin) {
//                                    Button(
//                                        onClick = {
//                                            viewModel.saveUpdatedOrder(
//                                                onDeleted = {
//                                                    Toast.makeText(
//                                                        context,
//                                                        "Transaksi dihapus (semua item kosong)",
//                                                        Toast.LENGTH_SHORT
//                                                    ).show()
//                                                    navController.popBackStack() // kembali ke History setelah delete
//                                                },
//                                                onSaved = {
//                                                    Toast.makeText(
//                                                        context,
//                                                        "Perubahan disimpan",
//                                                        Toast.LENGTH_SHORT
//                                                    ).show()
//                                                }
//                                            )
//                                            Toast.makeText(
//                                                context,
//                                                "Perubahan disimpan",
//                                                Toast.LENGTH_SHORT
//                                            ).show()
//                                        },
//                                        modifier = Modifier
//                                            .weight(1f)
//                                            .padding(bottom = 24.dp),
//                                        shape = RoundedCornerShape(8.dp),
//                                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
//                                    ) {
//                                        Icon(
//                                            painter = painterResource(R.drawable.baseline_save_24),
//                                            contentDescription = "Simpan Perubahan",
//                                            tint = Color.White
//                                        )
//                                        Spacer(Modifier.width(8.dp))
//                                        Text(
//                                            "Simpan Perubahan",
//                                            color = Color.White,
//                                            style = MaterialTheme.typography.titleMedium
//                                        )
//                                    }
//                                }

                            Spacer(Modifier.width(16.dp))

//                            Button(
//                                onClick = {
//                                    if (order != null) {
//                                        scope.launch {
//                                            try {
//                                                PrintHelper.printReceipt(context, order!!)
//                                            } catch (e: Exception) {
//                                                Toast.makeText(
//                                                    context,
//                                                    "Gagal print: ${e.message}",
//                                                    Toast.LENGTH_SHORT
//                                                ).show()
//                                            }
//                                        }
//                                    }
//                                },
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(bottom = 24.dp),
//                                shape = RoundedCornerShape(8.dp),
//                                colors = ButtonDefaults.buttonColors(containerColor = Primary)
//                            ) {
//                                Icon(
//                                    painter = painterResource(R.drawable.baseline_print_24),
//                                    contentDescription = "Cetak Struk",
//                                    tint = Color.White
//                                )
//                                Spacer(Modifier.width(8.dp))
//                                Text(
//                                    "Cetak Struk",
//                                    color = Color.White,
//                                    style = MaterialTheme.typography.titleMedium
//                                )
//                            }


//                            Button(
//                                modifier = Modifier.fillMaxWidth(),
//                                shape = RoundedCornerShape(8.dp),
//                                colors = ButtonDefaults.buttonColors(containerColor = Primary),
//                                onClick = {
//                                    val currentDateKey = dateKey
//                                    val currentQueue = queueNumber
//
//                                    if (currentDateKey.isNotBlank() && currentQueue > 0) {
//                                        navController.navigate(
//                                            "transaction?dateKey=$currentDateKey&queueNumber=$currentQueue"
//                                        )
//                                    } else {
//                                        Toast.makeText(
//                                            context,
//                                            "Data transaksi tidak valid",
//                                            Toast.LENGTH_SHORT
//                                        ).show()
//                                    }
//                                }
//                            ) {
//                                Icon(
//                                    painter = painterResource(R.drawable.outline_edit_square_24),
//                                    contentDescription = "Edit Transaksi",
//                                    tint = Color.White
//                                )
//                                Spacer(Modifier.width(8.dp))
//                                Text(
//                                    "Edit Transaksi",
//                                    color = Color.White,
//                                    style = MaterialTheme.typography.titleMedium
//                                )
//                            }

                            if (openDialog.value) {
                                AlertDialog(
                                    onDismissRequest = { openDialog.value = false },
                                    title = { Text("Hapus Transaksi") },
                                    text = { Text("Apakah kamu yakin ingin menghapus transaksi ini? Tindakan ini tidak bisa dibatalkan.") },
                                    confirmButton = {
                                        TextButton(onClick = {
                                            openDialog.value = false
                                            viewModel.deleteTransaction(
                                                dateKey = dateKey,
                                                queueNumber = queueNumber,
                                                onSuccess = {
                                                    Toast.makeText(
                                                        context,
                                                        "Transaksi berhasil dihapus!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    navController.popBackStack() // kembali ke halaman sebelumnya
                                                },
                                                onError = { err ->
                                                    Toast.makeText(
                                                        context,
                                                        "Gagal menghapus: $err",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            )
                                        }) {
                                            Text("Hapus", color = Color.Red)
                                        }
                                    },
                                    dismissButton = {
                                        TextButton(onClick = { openDialog.value = false }) {
                                            Text("Batal")
                                        }
                                    }
                                )
                            }

//                            Button(
//                                modifier = Modifier.fillMaxWidth(),
//                                shape = RoundedCornerShape(8.dp),
//                                colors = ButtonDefaults.buttonColors(containerColor = Danger),
//                                onClick = {
//                                    openDialog.value = true
//                                }
//                            ) {
//                                Icon(
//                                    painter = painterResource(R.drawable.baseline_delete_24),
//                                    contentDescription = "Hapus Transaksi",
//                                    tint = Color.White
//                                )
//                                Spacer(Modifier.width(8.dp))
//                                Text(
//                                    "Hapus Transaksi",
//                                    color = Color.White,
//                                    style = MaterialTheme.typography.titleMedium
//                                )
//                            }
                        }
                    }
                }
            }
        }
    }
}

