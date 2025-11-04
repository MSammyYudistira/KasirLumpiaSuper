package com.example.kasirlumpiasuper.ui.dashboard

import android.icu.util.Calendar
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.kasirlumpiasuper.data.PreferencesManager
import com.example.kasirlumpiasuper.data.Result
import com.example.kasirlumpiasuper.data.repository.FirestoreRepository
import com.example.kasirlumpiasuper.ui.navigation.NavRoutes
import com.example.kasirlumpiasuper.ui.utils.BusinessDateManager
import com.example.kasirlumpiasuper.ui.utils.DateUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class DashboardViewModel(
    private val repository: FirestoreRepository = FirestoreRepository()
) : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin = _isAdmin.asStateFlow()

    private val _userName = MutableStateFlow("")
    val userName = _userName.asStateFlow()

    // --- UI States ---
    private val _stockFilledToday = MutableStateFlow(false)
    val stockFilledToday: StateFlow<Boolean> = _stockFilledToday

    private val _customerCountToday = MutableStateFlow(0)
    val customerCountToday: StateFlow<Int> = _customerCountToday

    private val _cashFilledToday = MutableStateFlow(false)
    val cashFilledToday: StateFlow<Boolean> = _cashFilledToday

    private val _grandTotalToday = MutableStateFlow(0)
    val grandTotalToday: StateFlow<Int> = _grandTotalToday

    private val _queuePreview = MutableStateFlow<Int?>(null)
    val queuePreview: StateFlow<Int?> = _queuePreview

    private val _isNewDay = MutableStateFlow(false)
    val isNewDay: StateFlow<Boolean> = _isNewDay

    private val _manualResetRequired = MutableStateFlow(false)
    val manualResetRequired: StateFlow<Boolean> = _manualResetRequired

    private val _isLoadingCustomerCount = MutableStateFlow(false)
    val isLoadingCustomerCount: StateFlow<Boolean> = _isLoadingCustomerCount

    private val _businessDate = MutableStateFlow(BusinessDateManager.getBusinessDateLabel())
    val businessDate: StateFlow<String> = _businessDate

    private val _quote = MutableStateFlow<String?>(null)
    val quote: StateFlow<String?> = _quote


    fun setUserRole(role: String) {
        _isAdmin.value = role == "admin"
    }

    fun setUserName(name: String) {
        _userName.value = name
    }

    // --------------------------------------------------------------------
    // 🔹 FETCH TOTAL PENDAPATAN HARI INI
    // --------------------------------------------------------------------
    fun fetchTodayRevenue() {
        val dateKey = BusinessDateManager.getBusinessDateLabel()

        viewModelScope.launch {
            try {
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch

                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(userId)
                    .collection("orders")
                    .document(dateKey)
                    .collection("entries") // atau langsung .collection(dateKey) kalau tanpa subcollection
                    .get()
                    .addOnSuccessListener { snapshot ->
                        val total = snapshot.documents.sumOf { doc ->
                            doc.getLong("total")?.toInt() ?: 0
                        }
                        _grandTotalToday.value = total
                        println("✅ Total pendapatan hari ini: $total")
                    }
                    .addOnFailureListener { e ->
                        println("❌ Gagal ambil pendapatan: ${e.message}")
                        _grandTotalToday.value = 0
                    }
            } catch (e: Exception) {
                println("❌ Error fetchTodayRevenue: ${e.message}")
                _grandTotalToday.value = 0
            }
        }
    }

    // --------------------------------------------------------------------
    // 🔹 CEK APAKAH STOK SUDAH DIISI HARI INI
    // --------------------------------------------------------------------
    fun isStockFilledToday(isStockFilled: Boolean) {
        viewModelScope.launch {
            val today = BusinessDateManager.getBusinessDateLabel()
            val filled = repository.isStockFilled(today)
            _stockFilledToday.value = filled
        }
    }

    // --------------------------------------------------------------------
    // 🔹 RESET STOK & KAS HARIAN
    // --------------------------------------------------------------------
    fun resetStock() {
        viewModelScope.launch {
            try {
                val dateKey = BusinessDateManager.getBusinessDateLabel()
                repository.resetStockForDate(dateKey)
                _stockFilledToday.value = false
                println("✅ Stok berhasil di-reset untuk $dateKey")
            } catch (e: Exception) {
                println("❌ Gagal reset stok: ${e.message}")
            }
        }
    }

    fun resetCash() {
        viewModelScope.launch {
            try {
                val dateKey = BusinessDateManager.getBusinessDateLabel()
                repository.resetCashForDate(dateKey)
                _cashFilledToday.value = false
                println("✅ Kas berhasil di-reset untuk $dateKey")
            } catch (e: Exception) {
                println("❌ Gagal reset kas: ${e.message}")
            }
        }
    }

    // --------------------------------------------------------------------
    // 🔹 JUMLAH PELANGGAN HARI INI (DENGAN FILTER OPSIONAL)
    // --------------------------------------------------------------------
    fun checkCustomerCountToday(filterByCashierId: String? = null) {
        val dateKey = BusinessDateManager.getBusinessDateLabel()
        _isLoadingCustomerCount.value = true

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        var query = FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .collection("orders")
            .document(dateKey)
            .collection("entries") // atau langsung .collection(dateKey) jika tidak pakai subcollection

        if (!filterByCashierId.isNullOrBlank()) {
            query = query.whereEqualTo("cashierId", filterByCashierId) as CollectionReference
        }

        query.get()
            .addOnSuccessListener { snapshot ->
                _customerCountToday.value = snapshot.size()
                _isLoadingCustomerCount.value = false
                println("✅ Jumlah pelanggan hari ini: ${snapshot.size()}")
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                _customerCountToday.value = 0
                _isLoadingCustomerCount.value = false
                println("❌ Gagal ambil jumlah pelanggan: ${e.message}")
            }
    }

    // --------------------------------------------------------------------
    // 🔹 AMBIL NOMOR ANTRIAN TERBARU
    // --------------------------------------------------------------------
    fun fetchQueuePreview() {
        viewModelScope.launch {
            try {
                val dateKey = BusinessDateManager.getBusinessDateLabel()
                when (val result = repository.getNextQueueNumber(dateKey)) {
                    is Result.Success -> {
                        _queuePreview.value = result.data
                    }

                    is Result.Error -> {
                        _queuePreview.value = 1 // fallback jika gagal
                        println("❌ Gagal mendapatkan queue number: ${result.error}")
                    }
                }
            } catch (e: Exception) {
                _queuePreview.value = 1
                println("❌ Exception saat fetchQueuePreview: ${e.message}")
            }
        }
    }

    // --------------------------------------------------------------------
    // 🔹 RESET HARIAN (SETELAH HARI BERGANTI)
    // --------------------------------------------------------------------
    suspend fun resetDailyData(
        prefs: PreferencesManager,
        currentDate: String,
        viewModel: DashboardViewModel,
        navController: NavHostController
    ) {
        viewModel.resetStock()
        viewModel.resetCash()

        prefs.saveLastBusinessDate(currentDate)

        viewModel.fetchQueuePreview()

        navController.navigate(NavRoutes.AuthCheck.route) {
            popUpTo(NavRoutes.Dashboard.route) { inclusive = false }
        }
    }

    // --------------------------------------------------------------------
    // 🔹 CEK APAKAH HARI BERGANTI
    // --------------------------------------------------------------------
    fun initializeBusinessDay(prefs: PreferencesManager) {
        viewModelScope.launch {
            if (prefs.isManualLockActive()) {
                prefs.getLockedDate()?.let {
                    BusinessDateManager.lockTo(it)
                    _businessDate.value = it
                }
            } else {
                val today = BusinessDateManager.getCurrentSystemDateLabel()
                BusinessDateManager.releaseLock()
                _businessDate.value = today
            }
        }
    }

    fun updateBusinessDate(date: String, prefs: PreferencesManager) {
        viewModelScope.launch {
            BusinessDateManager.lockTo(date)
            prefs.saveManualLock(true, date)
            _businessDate.value = date
        }
    }

    fun setManualBusinessDate(prefs: PreferencesManager, newDate: String) {
        viewModelScope.launch {
            BusinessDateManager.lockTo(newDate)
            prefs.saveManualLock(true, newDate)
            println("📅 Manual lock diubah ke $newDate")
        }
    }

    fun rejectAutoReset(prefs: PreferencesManager) {
        viewModelScope.launch {
            val currentBusinessDate = BusinessDateManager.getBusinessDateLabel()

            BusinessDateManager.lockTo(currentBusinessDate)
            prefs.saveManualLock(true, currentBusinessDate)

            _isNewDay.value = false
            _manualResetRequired.value = true

            println("🔒 Hari tetap di $currentBusinessDate, manual reset diaktifkan")
        }
    }

    fun markResetDone(prefs: PreferencesManager) {
        viewModelScope.launch {
            val currentDate = BusinessDateManager.getBusinessDateLabel()
            prefs.saveLastBusinessDate(currentDate)
            prefs.clearManualLock()

            BusinessDateManager.releaseLock()

            _isNewDay.value = false
            _manualResetRequired.value = false
        }
    }

    // --------------------------------------------------------------------
    // 🔹 QUOTES HARI INI
    // --------------------------------------------------------------------
    fun loadQuote() {
        viewModelScope.launch {
            val quote = repository.getUserQuote()
            if (quote != null) {
                _quote.value = quote
            } else {
                Log.d("FirestoreVM", "Lewati loadQuote(): user belum login.")
            }
        }
    }

    //    fun restoreManualLock(prefs: PreferencesManager) {
//        viewModelScope.launch {
//            if (prefs.isManualLockActive()) {
//                prefs.getLockedDate()?.let { lockedDate ->
//                    BusinessDateManager.lockTo(lockedDate)
//                    println("🔁 Lock tanggal dipulihkan: $lockedDate")
//                }
//            } else {
//                BusinessDateManager.releaseLock()
//            }
//        }
//    }
//
//    fun checkIfNewDay(prefs: PreferencesManager) {
//        viewModelScope.launch {
//                val lastBusinessDate = prefs.getLastBusinessDate() ?: ""
//                val currentDate = BusinessDateManager.getBusinessDateLabel()
//
//            val isManualLock = prefs.isManualLockActive()
//
//            if (isManualLock) {
//                _isNewDay.value = false
//                println("ℹ️ Manual lock aktif, tidak tampilkan dialog hari baru.")
//                return@launch
//            }
//
//            // 🔹 Kalau tidak lock, baru cek apakah hari sudah ganti
//            _isNewDay.value = lastBusinessDate != currentDate
//            if (_isNewDay.value) {
//                println("🕒 Hari baru terdeteksi: dari $lastBusinessDate ke $currentDate")
//            }
//        }
//    }
}
