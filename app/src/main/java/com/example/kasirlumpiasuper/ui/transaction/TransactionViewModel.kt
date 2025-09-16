package com.example.kasirlumpiasuper.ui.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kasirlumpiasuper.data.model.Order
import com.example.kasirlumpiasuper.data.model.OrderItem
import com.example.kasirlumpiasuper.data.model.PaymentMethod
import com.example.kasirlumpiasuper.data.model.Serving
import com.example.kasirlumpiasuper.data.repository.FirestoreRepository
import com.example.kasirlumpiasuper.ui.utils.DateUtils.getBusinessDate
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

    private val _cartItems = MutableStateFlow<List<OrderItem>>(emptyList())
    val cartItems: StateFlow<List<OrderItem>> = _cartItems

    private val _customerName = MutableStateFlow("")
    val customerName: StateFlow<String> = _customerName

    private val _discountInput = MutableStateFlow("")
    val discountInput: StateFlow<String> = _discountInput

    private val _queuePreview = MutableStateFlow<Int?>(null)
    val queuePreview: StateFlow<Int?> = _queuePreview

//    private val _currentServing = MutableStateFlow(Serving.CUP)
//    val currentServing: StateFlow<Serving> = _currentServing


    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _isSuccess = MutableStateFlow<Boolean?>(null)
    val isSuccess: StateFlow<Boolean?> = _isSuccess
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage


    val subtotal: StateFlow<Int> = cups.map { allCups ->
        allCups.values.flatten()
            .sumOf { if (it.isFree) 0 else it.unitPrice * it.qty }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val discount: StateFlow<Int> = discountInput.map {
        it.toIntOrNull() ?: 0
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val total: StateFlow<Int> = combine(subtotal, discount) { sub, disc ->
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
        val all = _cups.value.toMutableMap()
        val list = all[cup]?.toMutableList() ?: mutableListOf()

        val idx = list.indexOfFirst {
            it.productId == item.productId && it.isFree == item.isFree
        }
        if (idx >= 0) {
            list[idx] = list[idx].copy(qty = list[idx].qty + 1)
        } else {
            list.add(item)
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

//    fun setCurrentServing(p: Serving) { _currentServing.value = p }

    fun setCustomerName(name: String) {
        _customerName.value = name
    }

    fun setDiscount(input: String) {
        _discountInput.value = input.filter { it.isDigit() }
    }

    fun addProduct(productId: String, name: String, unitPrice: Int, isFree: Boolean) {
        val targetCup = _currentCupIndex.value
//        val targetServing = _currentServing.value

        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst {
            it.productId == productId &&
                    it.cupIndex == targetCup &&
//                    it.serving == targetServing &&
                    it.isFree == isFree
        }
        if (index >= 0) {
            current[index] = current[index].copy(qty = current[index].qty + 1)
        } else {
            current.add(
                OrderItem(
                    productId = productId,
                    name = name,
                    unitPrice = unitPrice,
                    qty = 1,
//                    serving = targetServing,
                    cupIndex = targetCup,
                    isFree = isFree
                )
            )
        }
        _cartItems.value = current
    }

//    fun incQty(key: OrderItem) {
//        _cartItems.value = _cartItems.value.map {
//            if (it == key) it.copy(qty = it.qty + 1) else it
//        }
//    }
//
//    fun decQty(key: OrderItem) {
//        _cartItems.value = _cartItems.value.mapNotNull {
//            if (it == key) {
//                if (it.qty > 1) it.copy(qty = it.qty - 1) else null
//            } else it
//        }
//    }

    fun moveToCup(item: OrderItem, newCup: Int) {
        removeItem(item)
        addExplicit(item.copy(cupIndex = newCup))
    }
    fun moveToServing(item: OrderItem, newP: Serving) {
        removeItem(item)
        addExplicit(item.copy(serving = newP))
    }
    private fun addExplicit(item: OrderItem) {
        val current = _cartItems.value.toMutableList()
        val idx = current.indexOfFirst {
            it.productId == item.productId &&
                    it.cupIndex == item.cupIndex &&
                    it.serving == item.serving &&
                    it.isFree == item.isFree
        }
        if (idx >= 0) current[idx] = current[idx].copy(qty = current[idx].qty + item.qty)
        else current.add(item)
        _cartItems.value = current
    }

//    fun changeServing(item: OrderItem, newServing: Serving) {
//        removeItem(item)
//        addProduct(item.copy(serving = newServing))
//    }

    private fun removeItem(item: OrderItem) {
        _cartItems.value = _cartItems.value.filterNot { it == item }
    }

    suspend fun fetchQueuePreview() {
        val date = getBusinessDate()
        val next = repository.getNextQueueNumber(date)
        _queuePreview.value = next
    }

    fun createTransaction(
        cashierId: String,
        customerName: String,
        items: List<OrderItem>,
        subtotal: Int,
        paymentMethod: PaymentMethod,
        cashReceived: Int?,
        change: Int?
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                // Hitung tanggal bisnis hari ini
                val businessDate = getBusinessDate()

                val queue = repository.getNextQueueNumber(businessDate)

                val total = subtotal

                val order = Order(
                    queueNumber = queue,
                    businessDate = businessDate,
                    cashierId = cashierId,
                    customerName = customerName,
                    items = items,
                    subtotal = subtotal,
                    cashReceived = cashReceived,
                    change = change,
                    total = total,
                    paymentMethod = paymentMethod,
                )

                val result = repository.createOrder(order)

                _isSuccess.value = result
                if (!result) _errorMessage.value = "Gagal menyimpan Transaksi"
            } catch (e: Exception) {
                _errorMessage.value = e.message
                _isSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }


}