package com.example.kasirlumpiasuper.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class Transaction(
    val transactionId: String = "",
    val orderNumber: Int = 0,
    val date: String = "",
    val paymentMethod: String = "",
    val subtotal: Int = 0,
    val total: Int = 0,
    val userId: String = "",           // kasir siapa
    val status: String = "completed",  // "completed" | "void"
    val items: List<TransactionItem> = emptyList(),

    @ServerTimestamp
    val createdAt: Timestamp? = null
)

data class TransactionItem(
    val name: String = "",
    val price: Double = 0.0,
    val quantity: Int = 0,
    val isFree: Boolean = false,
    val type: String = ""
)