package com.example.kasirlumpiasuper.data

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Transaction(
    val id: String = "",
    @ServerTimestamp
    val date: Date? = null, // Menggunakan Date dari java.util
    val queueNumber: Int = 0,
    val totalAmount: Double = 0.0,
    val paymentMethod: String = "Uang Tunai",
    val discount: Double = 0.0,
    val processedBy: Map<String, String> = emptyMap(), // Contoh: mapOf("uid" to "xxx", "name" to "Sammy")
    val items: List<TransactionItem> = emptyList()
)

data class TransactionItem(
    val productId: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val quantity: Int = 0,
    val isFree: Boolean = false
)