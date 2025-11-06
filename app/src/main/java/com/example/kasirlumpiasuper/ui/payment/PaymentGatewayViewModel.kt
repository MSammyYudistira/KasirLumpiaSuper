package com.example.kasirlumpiasuper.ui.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kasirlumpiasuper.data.MidtransService
import com.example.kasirlumpiasuper.data.model.CreateQrisRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PaymentGatewayViewModel : ViewModel() {
    private val _qrUrl = MutableStateFlow<String?>(null)
    val qrUrl: StateFlow<String?> = _qrUrl

    private val _paymentStatus = MutableStateFlow<String?>(null)
    val paymentStatus: StateFlow<String?> = _paymentStatus

    private var polling = false

    fun createQris(orderId: String, amount: Int, customerName: String?) {
        viewModelScope.launch {
            try {
                val resp = MidtransService.api.createQris(
                    CreateQrisRequest(orderId, amount, customerName)
                )
                _qrUrl.value = resp.qrUrl
                _paymentStatus.value = resp.status // pending
            } catch (e: Exception) {
                _qrUrl.value = null
                _paymentStatus.value = "error"
            }
        }
    }

    fun startPollingStatus(orderId: String, intervalMs: Long = 2500L) {
        if (polling) return
        polling = true
        viewModelScope.launch {
            try {
                while (polling) {
                    val status = MidtransService.api.getStatus(orderId).status
                    _paymentStatus.value = status
                    if (status in listOf("settlement", "expire", "cancel")) {
                        polling = false
                        break
                    }
                    delay(intervalMs)
                }
            } catch (e: Exception) {
                _paymentStatus.value = "error"
                polling = false
            }
        }
    }

    fun stopPolling() { polling = false }

    fun resetPayment() {
        _qrUrl.value = null
        _paymentStatus.value = null
        polling = false
    }
}

