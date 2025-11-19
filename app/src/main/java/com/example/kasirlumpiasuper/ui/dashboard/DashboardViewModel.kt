package com.example.kasirlumpiasuper.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.kasirlumpiasuper.data.PreferencesManager
import com.example.kasirlumpiasuper.data.repository.FirestoreRepository
import com.example.kasirlumpiasuper.ui.navigation.NavRoutes
import com.example.kasirlumpiasuper.ui.utils.BusinessDateManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val repository: FirestoreRepository = FirestoreRepository()
) : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    // --- UI States ---
    private val _stockFilledToday = MutableStateFlow(false)
    val stockFilledToday: StateFlow<Boolean> = _stockFilledToday

    private val _customerCountToday = MutableStateFlow(0)
    val customerCountToday: StateFlow<Int> = _customerCountToday

    private val _cashFilledToday = MutableStateFlow(false)
    val cashFilledToday: StateFlow<Boolean> = _cashFilledToday

    private val _grandTotalToday = MutableStateFlow(0)
    val grandTotalToday: StateFlow<Int> = _grandTotalToday

    private val _isNewDay = MutableStateFlow(false)
    val isNewDay: StateFlow<Boolean> = _isNewDay

    private val _manualResetRequired = MutableStateFlow(false)
    val manualResetRequired: StateFlow<Boolean> = _manualResetRequired

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _businessDate = MutableStateFlow(BusinessDateManager.getBusinessDateLabel())
    val businessDate: StateFlow<String> = _businessDate

    fun fetchTodayRevenue() {
        val dateKey = BusinessDateManager.getBusinessDateLabel()

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch

                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(userId)
                    .collection("orders")
                    .document(dateKey)
                    .collection("entries")
                    .get()
                    .addOnSuccessListener { snapshot ->
                        val total = snapshot.documents.sumOf { doc ->
                            doc.getLong("total")?.toInt() ?: 0
                        }
                        _grandTotalToday.value = total
                    }
                    .addOnFailureListener {
                        _grandTotalToday.value = 0
                    }
                    .addOnCompleteListener {
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _grandTotalToday.value = 0
                _isLoading.value = false
            }
        }
    }


    fun isStockFilledToday() {
        viewModelScope.launch {
            _isLoading.value = true
            val date = businessDate.value   // ← BUKAN tanggal sistem
            val filled = repository.isStockFilled(date)
            _stockFilledToday.value = filled
            _isLoading.value = false
        }
    }


    fun observeBusinessDate(prefs: PreferencesManager) {
        viewModelScope.launch {
            prefs.lastBusinessDateFlow.collect { savedDate ->
                if (savedDate != null) {
                    _businessDate.value = savedDate
                }
            }
        }
    }

//    fun resetStock() {
//        _stockFilledToday.value = false
//    }
//
//    fun resetCash() {
//        _cashFilledToday.value = false
//    }


//    fun resetStock() {
//        viewModelScope.launch {
//            try {
//                val dateKey = BusinessDateManager.getBusinessDateLabel()
//                repository.resetStockForDate(dateKey)
//                _stockFilledToday.value = false
//                println("✅ Stok berhasil di-reset untuk $dateKey")
//            } catch (e: Exception) {
//                println("❌ Gagal reset stok: ${e.message}")
//            }
//        }
//    }
//
//    fun resetCash() {
//        viewModelScope.launch {
//            try {
//                val dateKey = BusinessDateManager.getBusinessDateLabel()
//                repository.resetCashForDate(dateKey)
//                _cashFilledToday.value = false
//                println("✅ Kas berhasil di-reset untuk $dateKey")
//            } catch (e: Exception) {
//                println("❌ Gagal reset kas: ${e.message}")
//            }
//        }
//    }

    fun checkCustomerCountToday(filterByCashierId: String? = null) {
        val dateKey = BusinessDateManager.getBusinessDateLabel()
        _isLoading.value = true

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        var query = FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .collection("orders")
            .document(dateKey)
            .collection("entries")

        if (!filterByCashierId.isNullOrBlank()) {
            query = query.whereEqualTo("cashierId", filterByCashierId) as CollectionReference
        }

        query.get()
            .addOnSuccessListener { snapshot ->
                _customerCountToday.value = snapshot.size()
                _isLoading.value = false
                println("✅ Jumlah pelanggan hari ini: ${snapshot.size()}")
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                _customerCountToday.value = 0
                _isLoading.value = false
                println("❌ Gagal ambil jumlah pelanggan: ${e.message}")
            }
    }

    suspend fun resetDailyData(
        prefs: PreferencesManager,
        currentDate: String,
        navController: NavHostController
    ) {
        _isLoading.value = true

        // RESET STATE HARI INI SAJA (JANGAN HAPUS DATA FIRESTORE)
        _stockFilledToday.value = false
//        _cashFilledToday.value = false

        delay(500)
        // UPDATE TANGGAL BISNIS
        prefs.saveLastBusinessDate(currentDate)
        BusinessDateManager.lockTo(currentDate)
        _businessDate.value = currentDate
        prefs.saveManualLock(true, currentDate)

        navController.navigate(NavRoutes.AuthCheck.route) {
            popUpTo(NavRoutes.Dashboard.route) { inclusive = false }
        }

        _isLoading.value = false
    }


    // --------------------------------------------------------------------
    // 🔹 CEK APAKAH HARI BERGANTI
    // --------------------------------------------------------------------
    fun initializeBusinessDay(prefs: PreferencesManager) {
        viewModelScope.launch {
            val savedDate = prefs.getLastBusinessDate()
            if (savedDate != null) {
                BusinessDateManager.lockTo(savedDate)   // WAJIB
                _businessDate.value = savedDate
            } else {
                val today = BusinessDateManager.getCurrentSystemDateLabel()
                BusinessDateManager.lockTo(today)       // WAJIB
                prefs.saveLastBusinessDate(today)
                _businessDate.value = today
            }
        }
    }


    fun updateBusinessDate(date: String, prefs: PreferencesManager) {
        viewModelScope.launch {
            _isLoading.value = true
            BusinessDateManager.lockTo(date)
            prefs.saveLastBusinessDate(date)     // WAJIB
            prefs.saveManualLock(true, date)
            _businessDate.value = date
            _isLoading.value = false
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
}
