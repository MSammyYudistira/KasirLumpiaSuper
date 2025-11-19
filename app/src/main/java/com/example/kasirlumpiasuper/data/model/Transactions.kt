package com.example.kasirlumpiasuper.data.model

import com.google.firebase.firestore.PropertyName

enum class PaymentMethod {
    CASH, CASHLESS
}

enum class TransactionStatus {
    PAID, CANCELLED
}

data class Order(
    val id: Any? = null,
    val queueNumber: Int = 0,
    val businessDate: String = "",
    val createdAt: Long = 0L,
    val cashierId: String = "",
    val notes: String = "",
    val items: List<OrderItem> = emptyList(),
    val subtotal: Int = 0,
    val discount: Int? = null,
    val nonCashAmount: Int? = null,
    val cashReceived: Int? = null,
    val change: Int? = null,
    val total: Int = 0,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val status: TransactionStatus = TransactionStatus.PAID
)

data class OrderItem(
    val productId: String = "",
    val name: String = "",
    val unitPrice: Int = 0,
    val originalUnitPrice: Int? = null,
    val qty: Int = 1,
    val cupIndex: Int = 1, // Cup-1, Cup-2 ...
    @get:PropertyName("free") @set:PropertyName("free")
    var isFree: Boolean = false,
    val imageUrl: String = ""

)
