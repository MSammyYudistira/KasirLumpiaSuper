package com.example.kasirlumpiasuper.ui.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kasirlumpiasuper.data.Result
import com.example.kasirlumpiasuper.data.model.Order
import com.example.kasirlumpiasuper.data.model.OrderItem
import com.example.kasirlumpiasuper.data.model.PaymentMethod
import com.example.kasirlumpiasuper.data.repository.FirestoreRepository
import com.example.kasirlumpiasuper.domain.error.DomainError
import com.example.kasirlumpiasuper.domain.error.ErrorMapper
import com.example.kasirlumpiasuper.ui.utils.DateUtils
import com.example.kasirlumpiasuper.ui.utils.DateUtils.getBusinessDateLabel
import com.google.firebase.firestore.FirebaseFirestoreException
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

//    private val appContext = getApplication<Application>().applicationContext

    private val _cups = MutableStateFlow<Map<Int, List<OrderItem>>>(mapOf(1 to emptyList()))
    val cups: StateFlow<Map<Int, List<OrderItem>>> = _cups

    private val _currentCupIndex = MutableStateFlow(1)
    val currentCupIndex: StateFlow<Int> = _currentCupIndex

    private val _customerName = MutableStateFlow("")
    val customerName: StateFlow<String> = _customerName

    private val _notes = MutableStateFlow("")
    val notes: StateFlow<String> = _notes

    private val _discountInput = MutableStateFlow(0)
    val discountInput: StateFlow<Int> = _discountInput

    private val _queuePreview = MutableStateFlow<Int?>(null)
    val queuePreview: StateFlow<Int?> = _queuePreview

    private val _lastOrder = MutableStateFlow<Order?>(null)
    val lastOrder: StateFlow<Order?> = _lastOrder


    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isSuccess = MutableStateFlow<Boolean?>(null)
    val isSuccess: StateFlow<Boolean?> = _isSuccess

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

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

    fun commitTransaction(order: Order) {
        viewModelScope.launch(SupervisorJob()) {
            val result = tryCommitWithRetry(order)
            _saveOrderState.value = result
        }
    }

    private suspend fun tryCommitWithRetry(order: Order): Result<Unit> {
        val maxAttempts = 3
        val delays = listOf(200L, 500L, 1000L)

        repeat(maxAttempts) { attempt ->
            when (val result = repository.saveOrder(order)) {
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

    suspend fun saveOrder(order: Order): Result<Unit> {
            return try {
                repository.saveOrder(order)
                Result.Success(Unit)
            } catch (e: FirebaseFirestoreException) {
                Result.Error(ErrorMapper.mapFirestoreException(e))
            } catch (e: Exception) {
                Result.Error(DomainError.UnknownError)
            }
    }

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
                // Kalau gagal ambil queue number, fallback ke 1 (biar gak crash)
                _queuePreview.value = 1

                // Kamu juga bisa tampilkan error log kalau mau
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
        cashierId: String,
        queueNumber: Int,
        paymentMethod: PaymentMethod,
        cashReceived: Int? = null,
        change: Int? = null,
        nonCashAmount: Int? = null
    ): Order {
        val businessDate = getBusinessDateLabel()

        val itemsFlat = cups.value.flatMap { (cupIndex, items) ->
            items.map { item ->
                // mapping tiap item agar harga efektif sesuai status isFree
                if (item.isFree) {
                    item.copy(
                        originalUnitPrice = item.unitPrice, // simpan harga asli
                        unitPrice = 0,                      // ubah harga efektif jadi 0
                        isFree = true,
                        cupIndex = cupIndex                 // pastikan cupIndex ikut
                    )
                } else {
                    item.copy(
                        originalUnitPrice = item.unitPrice, // harga normal
                        isFree = false,
                        cupIndex = cupIndex
                    )
                }
            }
        }

        val sub = subtotal.value
        val disc = discountInput.value
        val tot = total.value

        return Order(
            queueNumber = queueNumber,
            createdAt = System.currentTimeMillis(),
            businessDate = businessDate,
            cashierId = cashierId,
            customerName = customerName.value,
            items = itemsFlat,   // ⬅️ penting biar struk ada isinya
            subtotal = sub,
            discount = disc,
            total = tot,
            cashReceived = cashReceived,
            change = change,
            nonCashAmount = nonCashAmount,
            paymentMethod = paymentMethod,
            cupsRaw = cups.value.map { (idx, items) -> mapOf("index" to idx, "items" to items) },
            itemsAggRaw = emptyList(),
            notes = notes.value
        )
    }
}