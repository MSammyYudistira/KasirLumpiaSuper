package com.example.kasirlumpiasuper.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class Expense(
    val name: String = "",
    val amount: Int = 0
)

data class FreeItem(
    val name: String = "",
    val qty: Int = 0,
    val price: Int = 0
)

data class CashSummary(
    val uangBesar: Int = 0,
    val uangKecil: Int = 0,
    val uangLebihan: Int = 0,
    val total: Int = 0
)

data class Report(
    val reportId: String = "",         // ID unik, bisa pakai tanggal
    val date: String = "",
    val totalSales: Int = 0,
    val totalOrders: Int = 0,
    val expenses: List<Expense> = listOf(),
    val freeItems: List<FreeItem> = listOf(),
    val cashSummary: CashSummary = CashSummary(),
    val netIncome: Int = 0,
    val userId: String = "",

    @ServerTimestamp
    val createdAt: Timestamp? = null
)