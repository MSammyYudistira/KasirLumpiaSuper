package com.example.kasirlumpiasuper.ui.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kasirlumpiasuper.data.MidtransService
import com.example.kasirlumpiasuper.data.model.CreateQrisRequest
import com.google.firebase.Firebase
import com.google.firebase.functions.functions
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

    fun createQris(orderId: String, amount: Int) {
        viewModelScope.launch {
            try {
                Firebase.functions
                    .getHttpsCallable("createQris")
                    .call(
                        mapOf(
                            "orderId" to orderId,
                            "amount" to amount,
                        )
                    )
                    .addOnSuccessListener { result ->

                        val data = result.data as Map<*, *>

                        _qrUrl.value = data["qrUrl"] as? String
                        _paymentStatus.value = data["status"] as? String ?: "pending"
                    }
                    .addOnFailureListener {
                        _qrUrl.value = null
                        _paymentStatus.value = "error"
                    }

            } catch (e: Exception) {
                _qrUrl.value = null
                _paymentStatus.value = "error"
            }
        }
    }

    fun startPollingStatus(
        orderId: String,
        intervalMs: Long = 2500L
    ) {
        if (polling) return
        polling = true

        viewModelScope.launch {
            try {
                while (polling) {

                    Firebase.functions
                        .getHttpsCallable("checkPaymentStatus")
                        .call(mapOf("orderId" to orderId))
                        .addOnSuccessListener { result ->

                            val data = result.data as Map<*, *>
                            val status = data["transaction_status"] as? String ?: "error"

                            _paymentStatus.value = status

                            if (status in listOf("settlement", "expire", "cancel")) {
                                polling = false
                            }
                        }
                        .addOnFailureListener {
                            _paymentStatus.value = "error"
                            polling = false
                        }

                    delay(intervalMs)
                }

            } catch (e: Exception) {
                _paymentStatus.value = "error"
                polling = false
            }
        }
    }

    fun stopPolling() {
        polling = false
    }

    fun resetPayment() {
        _qrUrl.value = null
        _paymentStatus.value = null
        polling = false
    }
}

