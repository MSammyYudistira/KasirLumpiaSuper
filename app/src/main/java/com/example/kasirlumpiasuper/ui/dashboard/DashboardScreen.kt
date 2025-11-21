package com.example.kasirlumpiasuper.ui.dashboard

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.kasirlumpiasuper.R
import com.example.kasirlumpiasuper.data.datastore.PreferencesManager
import com.example.kasirlumpiasuper.data.firestore.FirestoreViewModel
import com.example.kasirlumpiasuper.ui.history.showDatePicker
import com.example.kasirlumpiasuper.ui.navigation.NavRoutes
import com.example.kasirlumpiasuper.ui.theme.Primary
import com.example.kasirlumpiasuper.helper.date.BusinessDateManager
import com.example.kasirlumpiasuper.helper.date.BusinessDateManager.getBusinessDateLabel
import com.example.kasirlumpiasuper.helper.date.DateUtils
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DashboardScreen(
    navController: NavHostController,
    viewModel: DashboardViewModel,
    prefs: PreferencesManager
) {

    val firestoreViewModel: FirestoreViewModel = viewModel()

    val user by firestoreViewModel.user.collectAsState()
    val role = user?.role ?: "kasir"

    val stockFilledToday by viewModel.stockFilledToday.collectAsState()
    val customerCountToday by viewModel.customerCountToday.collectAsState()
    val manualResetRequired by viewModel.manualResetRequired.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val businessDate by viewModel.businessDate.collectAsState()
    val totalRevenue by viewModel.grandTotalToday.collectAsState()
    val currentSystemDate = BusinessDateManager.getCurrentSystemDateLabel()
    val isDifferentDay = currentSystemDate != businessDate
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var showNewDayDialog by remember { mutableStateOf(true) }
    var selectedBusinessDate by remember { mutableStateOf(getBusinessDateLabel()) }

    LaunchedEffect(Unit) {
        viewModel.initializeBusinessDay(prefs)
        firestoreViewModel.loadUser()
        viewModel.observeBusinessDate(prefs)
    }

    LaunchedEffect(businessDate) {
        viewModel.fetchTodayRevenue()
        viewModel.isStockFilledToday()
        viewModel.checkCustomerCountToday()
        showNewDayDialog = true
    }

    if (isDifferentDay && showNewDayDialog) {
        AlertDialog(
            onDismissRequest = { showNewDayDialog = false },
            containerColor = Color.White,
            title = {
                Text(
                    "Hari Baru Dimulai",
                    style = MaterialTheme.typography.displaySmall
                )
            },
            text = {
                Text(
                    "Tanggal berganti. Apakah kamu ingin mulai hari baru sekarang?",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.resetDailyData(
                                prefs = prefs,
                                currentDate = currentSystemDate,
                                navController = navController
                            )
                            viewModel.markResetDone(prefs)
                            viewModel.updateBusinessDate(currentSystemDate, prefs)

                            showNewDayDialog = false
                            Toast.makeText(
                                context,
                                "Reset hari telah berhasil.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                ) {
                    Text("Ya, Reset")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.rejectAutoReset(prefs)

                            viewModel.updateBusinessDate(
                                getBusinessDateLabel(),
                                prefs
                            )

                            showNewDayDialog = false
                        }
                    }
                ) {
                    Text("Tidak Sekarang")
                }
            }
        )
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
                    .padding(horizontal = 72.dp)
                    .padding(top = 24.dp)
            ) {
                item {
                    // Ringkasan Hari Ini
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        shadowElevation = 4.dp,
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Text(
                                text = "Ringkasan Hari Ini",
                                style = MaterialTheme.typography.displaySmall
                            )

                            Spacer(Modifier.height(4.dp))

                            if (role == "kasir") {
                                Text(
                                    text = businessDate,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            } else {
                                Surface(
                                    onClick = {
                                        showDatePicker(
                                            context = context,
                                            currentKey = selectedBusinessDate,
                                            onPick = { pickedDate ->
                                                val todayLabel =
                                                    BusinessDateManager.getCurrentSystemDateLabel()
                                                val sdf = SimpleDateFormat(
                                                    "dd MMMM yyyy",
                                                    Locale("id", "ID")
                                                )

                                                try {
                                                    val picked = sdf.parse(pickedDate)
                                                    val today = sdf.parse(todayLabel)

                                                    if (picked != null && today != null) {
                                                        // Normalisasi ke tengah malam (jam 00:00)
                                                        val calPicked =
                                                            Calendar.getInstance().apply {
                                                                time = picked
                                                                set(Calendar.HOUR_OF_DAY, 0)
                                                                set(Calendar.MINUTE, 0)
                                                                set(Calendar.SECOND, 0)
                                                                set(Calendar.MILLISECOND, 0)
                                                            }
                                                        val calToday =
                                                            Calendar.getInstance().apply {
                                                                time = today
                                                                set(Calendar.HOUR_OF_DAY, 0)
                                                                set(Calendar.MINUTE, 0)
                                                                set(Calendar.SECOND, 0)
                                                                set(Calendar.MILLISECOND, 0)
                                                            }

                                                        when {
                                                            calPicked.after(calToday) -> {
                                                                Toast.makeText(
                                                                    context,
                                                                    "Tidak bisa memilih tanggal melebihi hari ini!",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }

                                                            else -> {
                                                                // ✅ boleh tanggal hari ini atau sebelumnya
                                                                viewModel.updateBusinessDate(
                                                                    pickedDate,
                                                                    prefs
                                                                )
                                                                Toast.makeText(
                                                                    context,
                                                                    "Tanggal bisnis diubah ke $pickedDate",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }
                                                        }
                                                    }
                                                } catch (e: Exception) {
                                                    Toast.makeText(
                                                        context,
                                                        "Format tanggal tidak valid",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                        )
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    shadowElevation = 4.dp
                                ) {
                                    Row(
                                        modifier = Modifier.padding(
                                            horizontal = 12.dp,
                                            vertical = 6.dp
                                        ),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.baseline_date_range_24),
                                            contentDescription = null,
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            text = businessDate,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .padding(24.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                // Card pelanggan
                                Surface(
                                    modifier = Modifier.weight(1f),
                                    color = Color(0xFFE1EEFE),
                                    shape = RoundedCornerShape(8.dp),
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(24.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.baseline_people_24),
                                            contentDescription = "Pelanggan Hari Ini",
                                            modifier = Modifier
                                                .size(40.dp)
                                                .background(
                                                    color = Primary,
                                                    shape = CircleShape
                                                )
                                                .padding(6.dp),
                                            tint = Color.White
                                        )
                                        Text(
                                            "Pelanggan Hari ini",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = customerCountToday.toString(),
                                            style = MaterialTheme.typography.displayMedium,
                                            color = Primary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(24.dp))

                                // Card pendapatan
                                Surface(
                                    modifier = Modifier.weight(1f),
                                    color = Color(0xFFE3FCEB),
                                    shape = RoundedCornerShape(8.dp),
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(24.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.baseline_money_bill_wave_24),
                                            contentDescription = "Total Pendapatan Hari Ini",
                                            modifier = Modifier
                                                .size(40.dp)
                                                .background(
                                                    color = Color(0xFF22C55E),
                                                    shape = CircleShape
                                                )
                                                .padding(6.dp),
                                            tint = Color.White
                                        )
                                        Text(
                                            "Total Pendapatan Hari Ini",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = DateUtils.rupiah(totalRevenue),
                                            style = MaterialTheme.typography.displayMedium,
                                            color = Color(0xFF22C55E)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    // Reset Hari
                    if (manualResetRequired) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            shadowElevation = 4.dp,
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFFFFD4D4)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp, horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Tanggal telah berganti ke ${getBusinessDateLabel()}",
                                        style = MaterialTheme.typography.displaySmall,
                                        color = Color.Red
                                    )
                                    Text(
                                        text = "Segera reset hari untuk memulai transaksi hari ini",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Red
                                    )
                                }
                                TextButton(onClick = {
                                    coroutineScope.launch {
                                        viewModel.resetDailyData(
                                            prefs = prefs,
                                            currentDate = currentSystemDate,
                                            navController = navController
                                        )
                                        viewModel.markResetDone(prefs)
                                        viewModel.updateBusinessDate(currentSystemDate, prefs)
                                    }
                                    Toast.makeText(
                                        context,
                                        "Reset hari berhasil.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.baseline_reset_tv_24),
                                        contentDescription = "Reset Hari",
                                        modifier = Modifier.size(30.dp),
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        "Reset Hari Baru",
                                        style = MaterialTheme.typography.displaySmall,
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(24.dp))
                    }
                }

                item {
                    // Buat pesanan baru
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        shadowElevation = 4.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp, horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "Buat Pesanan Baru",
                                    style = MaterialTheme.typography.displaySmall
                                )
                                Text(
                                    "Ayo Buat Pesanan Sekarang!",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            TextButton(onClick = {
                                if (!stockFilledToday) {
                                    Toast.makeText(
                                        context, "Silahkan atur stok terlebih dahulu.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    navController.navigate(NavRoutes.Transaction.route)
                                }
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.outline_add_24),
                                    contentDescription = "Tambah Pesanan",
                                    modifier = Modifier.size(30.dp),
                                    tint = Primary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Klik disini untuk membuat pesanan baru",
                                    style = MaterialTheme.typography.displaySmall,
                                    color = Primary
                                )
                            }
                        }
                    }
                }

                item {
                    if (showDialog) {
                        AlertDialog(
                            onDismissRequest = { showDialog = false },
                            containerColor = Color.White,
                            title = { Text("Stok sudah di isi", style = MaterialTheme.typography.displaySmall) },
                            text = { Text("Apakah kamu ingin mengatur ulang stok?", style = MaterialTheme.typography.bodyMedium) },
                            confirmButton = {
                                TextButton(onClick = {
                                    showDialog = false
                                    navController.navigate(NavRoutes.Stock.route)
                                }) {
                                    Text("Ya")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = {
                                    showDialog = false
                                }) {
                                    Text("Tidak")
                                }
                            }
                        )
                    }

                    // Atur stok
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        shadowElevation = 4.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        color = if (!stockFilledToday) Color(0xFFFFD4D4) else MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp, horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (!stockFilledToday) "Kamu Belum Atur Stok" else "Stok Hari Ini Sudah Diatur",
                                    style = MaterialTheme.typography.displaySmall,
                                    color = if (!stockFilledToday) Color.Red else Color.Black
                                )
                                Text(
                                    text = if (!stockFilledToday) "Segera atur stok sebelum melakukan transaksi" else "Kamu bisa mengubah stok kapan saja",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (!stockFilledToday) Color.Red else Color.Black
                                )
                            }
                            TextButton(
                                onClick = {
                                    if (stockFilledToday) {
                                        showDialog = true
                                    } else {
                                        navController.navigate(NavRoutes.Stock.route)
                                    }
                                }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.outline_management_stockout),
                                    contentDescription = "Atur Stock",
                                    modifier = Modifier.size(30.dp),
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Atur stok kamu disini",
                                    style = MaterialTheme.typography.displaySmall,
                                )
                            }
                        }
                    }
                }

                if (role == "admin") {
                    item {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            shadowElevation = 4.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 24.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp, horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        "Kelola Menu",
                                        style = MaterialTheme.typography.displaySmall
                                    )
                                    Text(
                                        "Kamu bisa tambah atau ubah daftar produk disini",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }

                                TextButton(onClick = {
                                    navController.navigate(NavRoutes.MenuManagement.route)
                                }) {
                                    Icon(
                                        painter = painterResource(R.drawable.outline_food_menu_24),
                                        contentDescription = "Kelola Menu",
                                        modifier = Modifier.size(30.dp),
                                        tint = Primary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        "Kelola Menu",
                                        style = MaterialTheme.typography.displaySmall,
                                        color = Primary
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}