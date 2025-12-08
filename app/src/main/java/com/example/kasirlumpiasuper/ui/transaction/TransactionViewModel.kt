package com.example.kasirlumpiasuper.ui.transaction

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kasirlumpiasuper.data.Result
import com.example.kasirlumpiasuper.domain.model.Order
import com.example.kasirlumpiasuper.domain.model.OrderItem
import com.example.kasirlumpiasuper.domain.model.PaymentMethod
import com.example.kasirlumpiasuper.data.firestore.FirestoreRepository
import com.example.kasirlumpiasuper.domain.error.DomainError
import com.example.kasirlumpiasuper.helper.date.BusinessDateManager.getBusinessDateLabel
import com.example.kasirlumpiasuper.helper.order.OrderCalculator
import com.example.kasirlumpiasuper.helper.order.OrderMapper
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TransactionViewModel(
    private val repository: FirestoreRepository = FirestoreRepository()
) : ViewModel() {
    private val _cups = MutableStateFlow<Map<Int, List<OrderItem>>>(mapOf(1 to emptyList()))
    val cups: StateFlow<Map<Int, List<OrderItem>>> = _cups

    private val _currentCupIndex = MutableStateFlow(1)
    val currentCupIndex: StateFlow<Int> = _currentCupIndex

    private val _customerName = MutableStateFlow("")

    private val _notes = MutableStateFlow("")
    val notes: StateFlow<String> = _notes

    private val _discountInput = MutableStateFlow(0)
    val discountInput: StateFlow<Int> = _discountInput

    private val _queuePreview = MutableStateFlow<Int?>(null)
    val queuePreview: StateFlow<Int?> = _queuePreview

    private val _lastOrder = MutableStateFlow<Order?>(null)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isSuccess = MutableStateFlow<Boolean?>(null)

    private val _errorMessage = MutableStateFlow<String?>(null)

    private val _saveOrderState = MutableStateFlow<Result<Unit>?>(null)
    val saveOrderState: StateFlow<Result<Unit>?> = _saveOrderState

    val subtotal: StateFlow<Int> = cups.map { allCups ->
        allCups.values.flatten()
            .sumOf { if (it.isFree) 0 else it.unitPrice * it.qty }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val total: StateFlow<Int> = combine(subtotal, discountInput) { sub, disc ->
        (sub - disc).coerceAtLeast(0)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    fun addCup() {
        val newIndex = (_cups.value.keys.maxOrNull() ?: 0) + 1
        _cups.value = _cups.value + (newIndex to emptyList())
        _currentCupIndex.value = newIndex
    }

    fun setCurrentCup(index: Int) {
        if (_cups.value.containsKey(index)) {
            _currentCupIndex.value = index
        }
    }

    fun addItemToCurrentCup(item: OrderItem) {
        val cup = _currentCupIndex.value
        val itemWithCup = item.copy(cupIndex = cup)
        val all = _cups.value.toMutableMap()
        val list = all[cup]?.toMutableList() ?: mutableListOf()

        val idx = list.indexOfFirst {
            it.productId == item.productId && it.isFree == item.isFree
        }
        if (idx >= 0) {
            list[idx] = list[idx].copy(qty = list[idx].qty + 1)
        } else {
            list.add(itemWithCup)
        }

        all[cup] = list
        _cups.value = all
    }

    fun incQty(item: OrderItem) {
        val cupIndex = _currentCupIndex.value
        val current = _cups.value.toMutableMap()
        current[cupIndex] = current[cupIndex]!!.map {
            if (it == item) it.copy(qty = it.qty + 1) else it
        }
        _cups.value = current
    }

    fun decQty(item: OrderItem) {
        val cupIndex = _currentCupIndex.value
        val current = _cups.value.toMutableMap()
        current[cupIndex] = current[cupIndex]!!.mapNotNull {
            if (it == item) if (it.qty > 1) it.copy(qty = it.qty - 1) else null else it
        }
        _cups.value = current
    }

    fun setNotes(note: String) {
        _notes.value = note
    }

    fun setDiscount(input: String) {
        _discountInput.value = input.toIntOrNull() ?: 0
    }

    fun setLastOrder(order: Order) {
        _lastOrder.value = order
    }

//    fun commitTransaction(order: Order) {
//        viewModelScope.launch(SupervisorJob()) {
//            val result = tryCommitWithRetry(order)
//            _saveOrderState.value = result
//        }
//    }

    fun submitOrder(
        paymentMethod: PaymentMethod,
        cashReceived: Int? = null,
        change: Int? = null,
        nonCashAmount: Int? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            val dateKey = getBusinessDateLabel()

            val localQueue = repository.getNextLocalQueueNumber(uid, dateKey)

            val globalQueue = repository.getNextGlobalQueueNumber(dateKey)

            val order = buildOrderForCommit(
                queueNumber = localQueue,
                paymentMethod = paymentMethod,
                cashReceived = cashReceived,
                change = change,
                nonCashAmount = nonCashAmount
            ).copy(
                queueNumber = localQueue,
                globalQueueNumber = globalQueue
            )

            val result = repository.saveOrder(
                uid = uid,
                date = dateKey,
                globalQueue = globalQueue,
                localQueue = localQueue,
                order = order
            )

            if (result is Result.Success) {
                _lastOrder.value = order
            }

            _saveOrderState.value = result
            _isLoading.value = false
        }
    }



    private suspend fun tryCommitWithRetry(
        uid: String,
        date: String,
        globalQueue: Int,
        localQueue: Int,
        order: Order
    ): Result<Unit> {
        val maxAttempts = 3
        val delays = listOf(200L, 500L, 1000L)

        repeat(maxAttempts) { attempt ->
            when (
                val result = repository.saveOrder(
                    uid = uid,
                    date = date,
                    globalQueue = globalQueue,
                    localQueue = localQueue,
                    order = order
                )
            ) {
                is Result.Success -> return result
                is Result.Error -> {
                    if (result.error is DomainError.NetworkError && attempt < maxAttempts - 1) {
                        delay(delays[attempt])
                    } else {
                        return result
                    }
                }
            }
        }

        return Result.Error(DomainError.UnknownError)
    }


    fun getLastOrder(): Order? = _lastOrder.value

    fun clearSaveOrderState() {
        _saveOrderState.value = null
    }

    suspend fun fetchQueuePreview() {
        val date = getBusinessDateLabel()
        when (val result = repository.getNextQueueNumber(date)) {
            is Result.Success -> {
                _queuePreview.value = result.data // ✅ Ambil data Int di dalam Result
            }

            is Result.Error -> {
                _queuePreview.value = 1
                _errorMessage.value = when (result.error) {
                    DomainError.NetworkError -> "Gagal ambil nomor antrian (koneksi buruk)"
                    DomainError.PreconditionFailed -> "Index Firestore belum dibuat"
                    else -> "Terjadi kesalahan mengambil nomor antrian"
                }
            }
        }
    }

    fun resetTransaction() {
        _cups.value = mapOf(1 to emptyList())
        _currentCupIndex.value = 1
        _customerName.value = ""
        _notes.value = ""
        _discountInput.value = 0
    }

    fun buildOrderForCommit(
        queueNumber: Int,
        paymentMethod: PaymentMethod,
        cashReceived: Int? = null,
        change: Int? = null,
        nonCashAmount: Int? = null
    ): Order {
        val businessDate = getBusinessDateLabel()

        val itemsFlat = cups.value.flatMap { (cupIndex, items) ->
            items.map { item ->
                if (item.isFree) {
                    item.copy(
                        originalUnitPrice = item.unitPrice,
                        unitPrice = 0,
                        isFree = true,
                        cupIndex = cupIndex
                    )
                } else {
                    item.copy(
                        originalUnitPrice = item.unitPrice,
                        isFree = false,
                        cupIndex = cupIndex
                    )
                }
            }
        }

        val sub = subtotal.value
        val disc = discountInput.value
        val tot = total.value

        val cashierId = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown"
        val safeId = when (queueNumber) {
            is Int -> queueNumber
            is String -> queueNumber.toIntOrNull() ?: 0
            else -> 0
        }

        return Order(
            id = safeId,
            queueNumber = queueNumber,
            createdAt = System.currentTimeMillis(),
            businessDate = businessDate,
            cashierId = cashierId,
            items = itemsFlat,
            subtotal = sub,
            discount = disc,
            total = tot,
            cashReceived = cashReceived,
            change = change,
            nonCashAmount = nonCashAmount,
            paymentMethod = paymentMethod,
            notes = notes.value
        )
    }

    fun loadOrderForEdit(dateKey: String, queueNumber: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val order = repository.getOrderByQueue(dateKey, queueNumber)
                if (order == null) {
                    _errorMessage.value = "Transaksi tidak ditemukan"
                    _isLoading.value = false
                    return@launch
                }

                val items = repository.getOrderItems(dateKey, queueNumber)
                val groupedByCup = items.groupBy { it.cupIndex }.toSortedMap()

                _cups.value = groupedByCup
                _notes.value = order.notes ?: ""
                _discountInput.value = order.discount ?: 0
                _queuePreview.value = order.queueNumber
                _lastOrder.value = order
                _isLoading.value = false

                Log.d("TransactionVM", "✅ Berhasil load transaksi #${order.queueNumber}")
            } catch (e: Exception) {
                Log.e("TransactionVM", "Gagal load transaksi: ${e.message}", e)
                _errorMessage.value = e.message
                _isLoading.value = false
            }
        }
    }

    fun commitEditedOrder(
        dateKey: String,
        queueNumber: Int,
        onSuccess: (() -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val cupsData = _cups.value
                val items = cupsData.values.flatten().map { item ->
                    if (item.isFree) {
                        item.copy(
                            originalUnitPrice = item.unitPrice,
                            unitPrice = 0
                        )
                    } else {
                        item.copy(
                            originalUnitPrice = item.unitPrice,
                        )
                    }
                }

                val itemsMap = OrderMapper.itemsToMapList(items)
                val subtotal = OrderCalculator.subtotal(items)
                val discount = _discountInput.value
                val total = OrderCalculator.total(subtotal, discount)
                val lastOrder = _lastOrder.value

                val updatedData = mapOf(
                    "id" to queueNumber,
                    "businessDate" to dateKey,
                    "cashierId" to (lastOrder?.cashierId ?: "unknown"),
                    "createdAt" to (lastOrder?.createdAt ?: System.currentTimeMillis()),
                    "queueNumber" to queueNumber,
                    "items" to itemsMap,
                    "subtotal" to subtotal,
                    "discount" to discount,
                    "total" to total,
                    "paymentMethod" to (lastOrder?.paymentMethod ?: "CASH"),
                    "notes" to _notes.value,
                    "status" to (lastOrder?.status ?: "PAID"),
                    "cashReceived" to (lastOrder?.cashReceived ?: 0),
                    "change" to (lastOrder?.change ?: 0),
                    "nonCashAmount" to (lastOrder?.nonCashAmount ?: 0),
                )

                val success = repository.updateOrder(dateKey, queueNumber, updatedData)
                _isLoading.value = false

                if (success) {
                    _isSuccess.value = true
                    onSuccess?.invoke()
                } else {
                    _isSuccess.value = false
                    onError?.invoke(Exception("Gagal menyimpan perubahan"))
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = e.message
                _isSuccess.value = false
                onError?.invoke(e)
            }
        }
    }
}