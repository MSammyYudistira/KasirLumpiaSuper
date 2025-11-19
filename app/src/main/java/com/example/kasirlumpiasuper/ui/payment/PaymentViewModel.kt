package com.example.kasirlumpiasuper.ui.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kasirlumpiasuper.data.model.PaymentMethod
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class PaymentViewModel : ViewModel() {
    private val _inputAmount = MutableStateFlow(0)
    val inputAmount: StateFlow<Int> = _inputAmount

    private val _totalOrder = MutableStateFlow(0)
    val totalOrder: StateFlow<Int> = _totalOrder

    private val _selectedPaymentMethod = MutableStateFlow<PaymentMethod?>(null)
    val selectedPaymentMethod: StateFlow<PaymentMethod?> = _selectedPaymentMethod

    val change: StateFlow<Int> = combine (_inputAmount, totalOrder) { amount, total ->
        (amount - total).coerceAtLeast(0)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    fun setInputAmount(value: String) {
        _inputAmount.value = value.toIntOrNull() ?: 0
    }

    fun setPaymentMethod(method: PaymentMethod) {
        _selectedPaymentMethod.value = method
    }

    fun reset() {
        _inputAmount.value = 0
        _selectedPaymentMethod.value = null
    }

    fun setTotalOrder(value: Int) {
        _totalOrder.value = value
    }

}