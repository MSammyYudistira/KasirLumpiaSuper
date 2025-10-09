package com.example.kasirlumpiasuper.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kasirlumpiasuper.data.Result
import com.example.kasirlumpiasuper.data.model.Order
import com.example.kasirlumpiasuper.data.repository.FirestoreRepository
import com.example.kasirlumpiasuper.domain.error.DomainError
import com.example.kasirlumpiasuper.ui.utils.DateUtils
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HistoryViewModel() : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val repository: FirestoreRepository = FirestoreRepository()

    /** 🔹 Tanggal aktif — pakai format label "29 September 2025" */
    private val _selectedDateKey = MutableStateFlow(DateUtils.getBusinessDateLabel())
    val selectedDateKey: StateFlow<String> = _selectedDateKey

    /** 🔹 Daftar transaksi */
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders

    private val _selectedOrder = MutableStateFlow<Order?>(null)
    val selectedOrder: StateFlow<Order?> = _selectedOrder

    /** 🔹 Loading indicator */
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    /** 🔹 Pesan error */
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    /** 🔹 Jumlah struk */
    private val _receiptCount = MutableStateFlow(0)
    val receiptCount: StateFlow<Int> = _receiptCount

    /** 🔹 Grand total */
    private val _grandTotal = MutableStateFlow(0)
    val grandTotal: StateFlow<Int> = _grandTotal

    /**
     * ✅ Panggil saat pertama kali HistoryScreen dimuat
     */
    fun initLoadIfNeeded() {
        if (_orders.value.isEmpty() && !_isLoading.value) {
            fetchOrders(_selectedDateKey.value)
        }
    }

    /**
     * ✅ Ubah tanggal dan ambil ulang data
     */
    fun setSelectedDateKey(newKey: String) {
        if (newKey == _selectedDateKey.value) return
        _selectedDateKey.value = newKey
        fetchOrders(newKey)
    }

    /**
     * ✅ Ambil transaksi dari Firestore berdasarkan tanggal label
     * Struktur Firestore: orders / "29 September 2025" / transactions / ...
     */
    fun fetchOrders(dateKey: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            db.collection("orders")
                .document(dateKey)
                .collection("transactions")
                .orderBy("queueNumber")
                .get()
                .addOnSuccessListener { snapshot ->
                    val list = snapshot.documents.mapNotNull { it.toObject(Order::class.java) }
                    _orders.value = list
                    _receiptCount.value = list.size
                    _grandTotal.value = list.sumOf { it.total }
                    _isLoading.value = false
                }
                .addOnFailureListener { e ->
                    _orders.value = emptyList()
                    _receiptCount.value = 0
                    _grandTotal.value = 0
                    _errorMessage.value = e.message ?: "Gagal memuat data"
                    _isLoading.value = false
                }
        }
    }

    fun loadOrderByQueue(dateKey: String, queueNumber: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val order = repository.getOrderByQueue(dateKey, queueNumber)
                _selectedOrder.value = order
            } catch (e: Exception) {
                _selectedOrder.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }
}
