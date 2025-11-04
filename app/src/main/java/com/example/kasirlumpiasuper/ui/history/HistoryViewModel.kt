package com.example.kasirlumpiasuper.ui.history

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kasirlumpiasuper.data.model.Order
import com.example.kasirlumpiasuper.data.model.OrderItem
import com.example.kasirlumpiasuper.data.repository.FirestoreRepository
import com.example.kasirlumpiasuper.ui.utils.BusinessDateManager
import com.example.kasirlumpiasuper.ui.utils.OrderCalculator
import com.example.kasirlumpiasuper.ui.utils.OrderMapper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HistoryViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val repository: FirestoreRepository = FirestoreRepository()

    /** 🔹 Tanggal aktif — pakai format label "29 September 2025" */
    private val _selectedDateKey = MutableStateFlow(BusinessDateManager.getBusinessDateLabel())
    val selectedDateKey: StateFlow<String> = _selectedDateKey

    /** 🔹 Daftar transaksi */
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders

    /** 🔹 Transaksi yang sedang dipilih (detail order) */
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


    // ============================================================
    // 🔹 Bagian untuk Order Detail (edit item)
    // ============================================================

    /** Daftar item dari order yang sedang dipilih */
    private val _orderItems = MutableStateFlow<List<OrderItem>>(emptyList())
    val orderItems: StateFlow<List<OrderItem>> = _orderItems

    /** Total harga dari order detail */
    private val _orderTotal = MutableStateFlow(0)
    val orderTotal: StateFlow<Int> = _orderTotal


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

            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch

            FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("orders")
                .document(dateKey)
                .collection("entries") // ubah ke .collection(dateKey) kalau kamu sudah hapus subcollection
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
            .collection("entries") // ubah ke .collection(dateKey) kalau tanpa subcollection
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

                    // ambil LIST `items` → konversi ke List<OrderItem>
                    val rawItems = snap.get("items")
                    val items = OrderMapper.mapListToItems(rawItems)
                    _orderItems.value = items

                    // hitung ulang total
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


//    // ===== Edit qty berbasis OBJEK item yang diklik (aman untuk item sejenis di cup berbeda) =====
//    fun incrementItem(target: OrderItem) {
//        _orderItems.value = _orderItems.value.map {
//            if (it.productId == target.productId &&
//                it.cupIndex  == target.cupIndex  &&
//                it.isFree    == target.isFree    &&
//                it.unitPrice == target.unitPrice
//            ) it.copy(qty = it.qty + 1) else it
//        }
//        recalc()
//    }
//
//    fun decrementItem(target: OrderItem) {
//        _orderItems.value = _orderItems.value
//            .map { item ->
//                if (item.productId == target.productId &&
//                    item.cupIndex  == target.cupIndex  &&
//                    item.isFree    == target.isFree    &&
//                    item.unitPrice == target.unitPrice
//                ) item.copy(qty = (item.qty - 1).coerceAtLeast(0))
//                else item
//            }
//            .filter { it.qty > 0 }   // ⬅️ qty 0 dihapus dari list
//        recalc()
//    }

//    private fun recalc() {
//        val sub  = OrderCalculator.subtotal(_orderItems.value)
//        val disc = _selectedOrder.value?.discount ?: 0
//        _orderTotal.value = OrderCalculator.total(sub, disc)
//    }

    // ===== Simpan: update FIELD `items`, `subtotal`, `total` (diskon tetap) =====
    fun saveUpdatedOrder(
        onDeleted: (() -> Unit)? = null,
        onSaved:   (() -> Unit)? = null
    ) {
        val order = _selectedOrder.value ?: return
        val dateKey = _selectedDateKey.value

        // Buang item qty 0
        val cleaned = _orderItems.value.filter { it.qty > 0 }

        val docRef = FirebaseFirestore.getInstance()
            .collection("orders")
            .document(dateKey)
            .collection("transactions")
            .document(order.queueNumber.toString())

        if (cleaned.isEmpty()) {
            // 🔴 Tidak ada item tersisa → hapus dokumen transaksi
            docRef.delete()
                .addOnSuccessListener {
                    // sinkron state lokal: kosongkan detail & juga keluarkan dari list history bila kamu memegangnya
                    _selectedOrder.value = null
                    _orderItems.value = emptyList()
                    _orderTotal.value = 0

                    // (opsional) kalau kamu menyimpan _orders untuk HistoryScreen, keluarkan transaksi ini dari list
                    // _orders.value = _orders.value.filterNot { it.queueNumber == order.queueNumber }
                    // _receiptCount.value = _orders.value.size
                    // _grandTotal.value = _orders.value.sumOf { it.total }

                    onDeleted?.invoke()
                }
                .addOnFailureListener { e ->
                    Log.e("HistoryVM", "Delete failed", e)
                }
            return
        }

        // ✅ Masih ada item → update dokumen (items/subtotal/total)
        val newSubtotal = OrderCalculator.subtotal(cleaned)
        val newTotal    = OrderCalculator.total(newSubtotal, order.discount ?: 0)

        val itemsMap = OrderMapper.itemsToMapList(cleaned)

        val updates = mapOf(
            "items" to itemsMap,
            "subtotal" to newSubtotal,
            "total" to newTotal
            // kalau perlu jaga konsistensi dengan skema lain:
            // "cupsRaw" to itemsMap
        )

        docRef.update(updates)
            .addOnSuccessListener {
                // sinkron lokal
                _selectedOrder.value = order.copy(
                    subtotal = newSubtotal,
                    total = newTotal
                )
                _orderItems.value = cleaned
                _orderTotal.value = newTotal

                onSaved?.invoke()
            }
            .addOnFailureListener { e ->
                Log.e("HistoryVM", "Update failed", e)
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

