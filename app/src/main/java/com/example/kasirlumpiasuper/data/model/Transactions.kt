package com.example.kasirlumpiasuper.data.model

enum class PaymentMethod {
    CASH, CASHLESS
}

enum class TransactionStatus {
    PAID, CANCELLED
}

data class Order(
    val id: String = "",
    val queueNumber: Int = 0,
    val businessDate: String = "",
    val createdAt: Long = 0L,
    val cashierId: String = "",
    val customerName: String = "",
    val notes: String = "",

    val items: List<OrderItem> = emptyList(),
    val cupsRaw: List<Map<String, Any>> = emptyList(),
    val itemsAggRaw: List<Map<String, Any>> = emptyList(),

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
    val originalUnitPrice: Int = 0,
    val qty: Int = 1,
    val cupIndex: Int = 1, // Cup-1, Cup-2 ...
    val isFree: Boolean = false,
    val imageRes: Int = 0
)
