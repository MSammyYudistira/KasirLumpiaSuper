//package com.example.kasirlumpiasuper.ui.kasir
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import androidx.navigation.NavHostController
//import com.example.kasirlumpiasuper.data.PreferencesManager
//import com.example.kasirlumpiasuper.data.Result
//import com.example.kasirlumpiasuper.data.repository.FirestoreRepository
//import com.example.kasirlumpiasuper.ui.navigation.NavRoutes
//import com.example.kasirlumpiasuper.ui.utils.DateUtils
//import com.google.firebase.firestore.CollectionReference
//import com.google.firebase.firestore.FirebaseFirestore
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//
//class KasirViewModel(
//    private val repository: FirestoreRepository = FirestoreRepository(),
//    private val userRole: String
//) : ViewModel() {
//
//    private val db = FirebaseFirestore.getInstance()
//    val isAdmin = userRole == "admin"
//
//    // --- UI States ---
//    private val _stockFilledToday = MutableStateFlow(false)
//    val stockFilledToday: StateFlow<Boolean> = _stockFilledToday
//
//    private val _customerCountToday = MutableStateFlow(0)
//    val customerCountToday: StateFlow<Int> = _customerCountToday
//
//    private val _cashFilledToday = MutableStateFlow(false)
//    val cashFilledToday: StateFlow<Boolean> = _cashFilledToday
//
//    private val _grandTotalToday = MutableStateFlow(0)
//    val grandTotalToday: StateFlow<Int> = _grandTotalToday
//
//    private val _queuePreview = MutableStateFlow<Int?>(null)
//    val queuePreview: StateFlow<Int?> = _queuePreview
//
//    private val _lastBusinessDate = MutableStateFlow<String?>(null)
//    val lastBusinessDate: StateFlow<String?> = _lastBusinessDate
//
//    private val _isNewDay = MutableStateFlow(false)
//    val isNewDay: StateFlow<Boolean> = _isNewDay
//
//    private val _manualResetRequired = MutableStateFlow(false)
//    val manualResetRequired: StateFlow<Boolean> = _manualResetRequired
//
//    private val _isLoadingCustomerCount = MutableStateFlow(false)
//    val isLoadingCustomerCount: StateFlow<Boolean> = _isLoadingCustomerCount
//
//    private val _quote = MutableStateFlow<String?>(null)
//    val quote: StateFlow<String?> = _quote
//
//
//    fun fetchTodayRevenue() {
//        val dateKey = DateUtils.getBusinessDateLabel() // format: "29 September 2025"
//
//        viewModelScope.launch {
//            try {
//                FirebaseFirestore.getInstance()
//                    .collection("orders")
//                    .document(dateKey)
//                    .collection("transactions")
//                    .get()
//                    .addOnSuccessListener { snapshot ->
//                        val total = snapshot.documents.sumOf { doc ->
//                            doc.getLong("total")?.toInt() ?: 0
//                        }
//                        _grandTotalToday.value = total
//                        println("✅ Total pendapatan hari ini: $total")
//                    }
//                    .addOnFailureListener { e ->
//                        println("❌ Gagal ambil pendapatan: ${e.message}")
//                        _grandTotalToday.value = 0
//                    }
//            } catch (e: Exception) {
//                println("❌ Error fetchTodayRevenue: ${e.message}")
//                _grandTotalToday.value = 0
//            }
//        }
//    }
//
//    // ✅ Cek apakah stok sudah diisi hari ini
//    fun isStockFilledToday(isStockFilled: Boolean) {
//        viewModelScope.launch {
//            val today = DateUtils.getBusinessDateLabel()
//            val filled = repository.isStockFilled(today)
//            _stockFilledToday.value = filled
//        }
//    }
//
//    // ✅ Reset stok harian (hapus stok awal hari ini)
//    fun resetStock() {
//        viewModelScope.launch {
//            try {
//                val dateKey = DateUtils.getBusinessDateLabel()
//                repository.resetStockForDate(dateKey)
//                _stockFilledToday.value = false
//                println("✅ Stok berhasil di-reset untuk $dateKey")
//            } catch (e: Exception) {
//                println("❌ Gagal reset stok: ${e.message}")
//            }
//        }
//    }
//
//    // ✅ Reset kas harian (uang kas awal / uang masuk)
//    fun resetCash() {
//        viewModelScope.launch {
//            try {
//                val dateKey = DateUtils.getBusinessDateLabel()
//                repository.resetCashForDate(dateKey)
//                _cashFilledToday.value = false
//                println("✅ Kas berhasil di-reset untuk $dateKey")
//            } catch (e: Exception) {
//                println("❌ Gagal reset kas: ${e.message}")
//            }
//        }
//    }
//
//    // ✅ Ambil nomor antrian terbaru
//    fun fetchQueuePreview() {
//        viewModelScope.launch {
//            try {
//                val dateKey = DateUtils.getBusinessDateLabel()
//                when (val result = repository.getNextQueueNumber(dateKey)) {
//                    is Result.Success -> {
//                        _queuePreview.value = result.data
//                    }
//
//                    is Result.Error -> {
//                        _queuePreview.value = 1 // fallback jika gagal
//                        println("❌ Gagal mendapatkan queue number: ${result.error}")
//                    }
//                }
//            } catch (e: Exception) {
//                _queuePreview.value = 1
//                println("❌ Exception saat fetchQueuePreview: ${e.message}")
//            }
//        }
//    }
//
//    fun checkCustomerCountToday(filterByCashierId: String? = null) {
//        val dateKey = DateUtils.getBusinessDateLabel()
//        _isLoadingCustomerCount.value = true
//
//        var query = db.collection("orders")
//            .document(dateKey)
//            .collection("transactions")
//
//        if (!filterByCashierId.isNullOrBlank()) {
//            query = query.whereEqualTo("cashierId", filterByCashierId) as CollectionReference
//        }
//
//        query.get()
//            .addOnSuccessListener { snapshot ->
//                _customerCountToday.value = snapshot.size()
//                _isLoadingCustomerCount.value = false
//            }
//            .addOnFailureListener { e ->
//                e.printStackTrace()
//                _customerCountToday.value = 0
//                _isLoadingCustomerCount.value = false
//            }
//    }
//
//    fun observeCustomerCountToday(filterByCashierId: String? = null) {
//        val dateKey = DateUtils.getBusinessDateLabel()
//
//        var query = db.collection("orders")
//            .document(dateKey)
//            .collection("transactions")
//
//        if (!filterByCashierId.isNullOrBlank()) {
//            query = query.whereEqualTo("cashierId", filterByCashierId) as CollectionReference
//        }
//
//        query.addSnapshotListener { snapshot, error ->
//            if (error != null) {
//                _customerCountToday.value = 0
//                return@addSnapshotListener
//            }
//            _customerCountToday.value = snapshot?.size() ?: 0
//        }
//    }
//
//    suspend fun resetDailyData(
//        prefs: PreferencesManager,
//        currentDate: String,
//        viewModel: KasirViewModel,
//        navController: NavHostController
//    ) {
//        viewModel.resetStock()
//        viewModel.resetCash()
//
//        prefs.saveLastBusinessDate(currentDate)
//
//        viewModel.fetchQueuePreview()
//
//        navController.navigate(NavRoutes.Stock.route) {
//            popUpTo(NavRoutes.DashboardKasir.route) { inclusive = false }
//        }
//    }
//
//    fun checkBusinessDay(prefs: PreferencesManager) {
//        viewModelScope.launch {
//            val currentDate = DateUtils.getBusinessDateLabel()
//            val lastDate = prefs.getLastBusinessDate() // ambil dari SharedPreferences/DataStore
//
//            _isNewDay.value = lastDate == null || lastDate != currentDate
//        }
//    }
//
//    // ✅ Cek apakah hari berganti
//    fun checkIfNewDay(prefs: PreferencesManager) {
//        viewModelScope.launch {
//            val lastBusinessDate = prefs.getLastBusinessDate() ?: ""
//            val currentDate = DateUtils.getBusinessDateLabel()
//            _isNewDay.value = lastBusinessDate != currentDate
//        }
//    }
//
//    // ✅ Jika user pilih "Tidak" di dialog
//    fun rejectAutoReset() {
//        _manualResetRequired.value = true
//        _isNewDay.value = false
//    }
//
//    // ✅ Jika reset sudah dilakukan
//    fun markResetDone(prefs: PreferencesManager) {
//        viewModelScope.launch {
//            val currentDate = DateUtils.getBusinessDateLabel()
//            prefs.saveLastBusinessDate(currentDate)
//            _isNewDay.value = false
//            _manualResetRequired.value = false
//        }
//    }
//
//    fun loadQuote() {
//        viewModelScope.launch {
//            _quote.value = repository.getUserQuote().toString()
//        }
//    }
//}