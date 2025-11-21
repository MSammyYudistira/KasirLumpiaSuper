package com.example.kasirlumpiasuper.ui.history

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kasirlumpiasuper.domain.model.Order
import com.example.kasirlumpiasuper.domain.model.OrderItem
import com.example.kasirlumpiasuper.data.firestore.FirestoreRepository
import com.example.kasirlumpiasuper.helper.date.BusinessDateManager
import com.example.kasirlumpiasuper.helper.order.OrderCalculator
import com.example.kasirlumpiasuper.helper.order.OrderMapper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HistoryViewModel : ViewModel() {

    private val repository: FirestoreRepository = FirestoreRepository()

    private val _selectedDateKey = MutableStateFlow(BusinessDateManager.getBusinessDateLabel())
    val selectedDateKey: StateFlow<String> = _selectedDateKey

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders

    private val _selectedOrder = MutableStateFlow<Order?>(null)
    val selectedOrder: StateFlow<Order?> = _selectedOrder

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _receiptCount = MutableStateFlow(0)
    val receiptCount: StateFlow<Int> = _receiptCount

    private val _grandTotal = MutableStateFlow(0)
    val grandTotal: StateFlow<Int> = _grandTotal

    private val _orderItems = MutableStateFlow<List<OrderItem>>(emptyList())
    val orderItems: StateFlow<List<OrderItem>> = _orderItems

    private val _orderTotal = MutableStateFlow(0)
    val orderTotal: StateFlow<Int> = _orderTotal

    fun initLoadIfNeeded() {
        if (_orders.value.isEmpty() && !_isLoading.value) {
            fetchOrders(_selectedDateKey.value)
        }
    }

    fun setSelectedDateKey(newKey: String) {
        if (newKey == _selectedDateKey.value) return
        _selectedDateKey.value = newKey
        fetchOrders(newKey)
    }

    fun fetchOrders(dateKey: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch

            FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("orders")
                .document(dateKey)
                .collection("entries")
                .orderBy("queueNumber")
                .get()
                .addOnSuccessListener { snapshot ->
                    val list = snapshot.documents.mapNotNull { it.toObject(Order::class.java) }
                    _orders.value = list
                    _receiptCount.value = list.size
                    _grandTotal.value = list.sumOf { it.total }
                    _isLoading.value = false
                    println("✅ History berhasil dimuat: ${list.size} transaksi")
                }
                .addOnFailureListener { e ->
                    _orders.value = emptyList()
                    _receiptCount.value = 0
                    _grandTotal.value = 0
                    _errorMessage.value = e.message ?: "Gagal memuat data"
                    _isLoading.value = false
                    println("❌ Gagal memuat history: ${e.message}")
                }
        }
    }

    fun loadOrderByQueue(dateKey: String, queueNumber: Int) {
        _isLoading.value = true

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .collection("orders")
            .document(dateKey)
            .collection("entries")
            .document(queueNumber.toString())
            .get()
            .addOnSuccessListener { snap ->
                if (!snap.exists()) {
                    _selectedOrder.value = null
                    _orderItems.value = emptyList()
                    _orderTotal.value = 0
                } else {
                    val order = snap.toObject(Order::class.java)
                    _selectedOrder.value = order

                    val rawItems = snap.get("items")
                    val items = OrderMapper.mapListToItems(rawItems)
                    _orderItems.value = items

                    val sub = OrderCalculator.subtotal(items)
                    val disc = order?.discount ?: 0
                    _orderTotal.value = OrderCalculator.total(sub, disc)
                }
                _isLoading.value = false
            }
            .addOnFailureListener { e ->
                _selectedOrder.value = null
                _orderItems.value = emptyList()
                _orderTotal.value = 0
                _errorMessage.value = e.message
                _isLoading.value = false
                println("❌ Gagal memuat order detail: ${e.message}")
            }
    }

    fun deleteTransaction(
        dateKey: String,
        queueNumber: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val success = repository.deleteOrder(dateKey, queueNumber)
                _isLoading.value = false
                if (success) {
                    Log.d("TransactionVM", "✅ Transaksi #$queueNumber berhasil dihapus.")
                    onSuccess()
                } else {
                    onError("Gagal menghapus transaksi.")
                }
            } catch (e: Exception) {
                _isLoading.value = false
                onError(e.message ?: "Terjadi kesalahan saat menghapus.")
            }
        }
    }
}

