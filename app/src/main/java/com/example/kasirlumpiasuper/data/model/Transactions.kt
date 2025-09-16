package com.example.kasirlumpiasuper.data.model

enum class Serving {
    CUP, BUNGKUS, MENTAH
}

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
    val qty: Int = 0,
    val serving: Serving = Serving.CUP,
    val cupIndex: Int = 1, // Cup-1, Cup-2 ...
    val isFree: Boolean = false
)

//data class Transaction(
//    val transactionId: String = "",
//    val orderNumber: Int = 0,
//    val date: String = "",
//    val paymentMethod: String = "",
//    val subtotal: Int = 0,
//    val total: Int = 0,
//    val userId: String = "",           // kasir siapa
//    val status: String = "completed",  // "completed" | "void"
//    val items: List<TransactionItem> = emptyList(),
//
//    @ServerTimestamp
//    val createdAt: Timestamp? = null
//)
//
//data class TransactionItem(
//    val name: String = "",
//    val price: Double = 0.0,
//    val quantity: Int = 0,
//    val isFree: Boolean = false,
//    val type: String = ""
//)