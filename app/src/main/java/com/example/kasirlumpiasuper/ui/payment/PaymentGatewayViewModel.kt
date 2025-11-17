package com.example.kasirlumpiasuper.ui.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.functions.functions
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PaymentGatewayViewModel : ViewModel() {

    private val _qrUrl = MutableStateFlow<String?>(null)
    val qrUrl: StateFlow<String?> = _qrUrl

    private val _paymentStatus = MutableStateFlow<String?>(null)
    val paymentStatus: StateFlow<String?> = _paymentStatus

    private var polling = false

    // =====================================================================
    // CREATE QRIS
    // =====================================================================
    fun createQris(orderId: String, amount: Int) {
        viewModelScope.launch {
            try {
                // LOG
                println("PG: createQris START orderId=$orderId amount=$amount")

                val result = Firebase.functions
                    .getHttpsCallable("createQris")
                    .call(
                        mapOf(
                            "orderId" to orderId,
                            "amount" to amount
                        )
                    )
                    .await()

                val data = result.data as Map<*, *>

                val url = data["qrUrl"] as? String
                val status = data["status"] as? String ?: "pending"

                // LOG
                println("PG: createQris RESULT data=$data")
                println("PG: QR URL = $url status=$status")

                _qrUrl.value = url
                _paymentStatus.value = status

            } catch (e: Exception) {
                println("PG: createQris ERROR = ${e.message}")
                _qrUrl.value = null
                _paymentStatus.value = "error"
            }
        }
    }

    // =====================================================================
    // POLLING STATUS
    // =====================================================================
    fun startPollingStatus(orderId: String, intervalMs: Long = 2500L) {
        if (polling) return
        polling = true

        viewModelScope.launch {
            println("PG: Polling START for orderId=$orderId")

            try {
                while (polling) {

                    val result = Firebase.functions
                        .getHttpsCallable("checkPaymentStatus")
                        .call(mapOf("orderId" to orderId))
                        .await()

                    val data = result.data as Map<*, *>
                    val status = data["transaction_status"] as? String ?: "error"

                    // LOG
                    println("PG: Polling RESULT=$data")
                    println("PG: Polling STATUS=$status")

                    _paymentStatus.value = status

                    if (status in listOf("settlement", "expire", "cancel")) {
                        polling = false
                        println("PG: Polling STOP (final status=$status)")
                        break
                    }

                    delay(intervalMs)
                }
            } catch (e: Exception) {
                println("PG: Polling ERROR = ${e.message}")
                _paymentStatus.value = "error"
                polling = false
            }
        }
    }

    // =====================================================================
    // STOP POLLING
    // =====================================================================
    fun stopPolling() {
        println("PG: stopPolling CALLED")
        polling = false
    }

    // =====================================================================
    // RESET PAYMENT
    // =====================================================================
    fun resetPayment() {
        println("PG: resetPayment CALLED")
        _qrUrl.value = null
        _paymentStatus.value = null
        polling = false
    }
}