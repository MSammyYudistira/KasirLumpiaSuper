package com.example.kasirlumpiasuper.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class StockItem(
    val name: String = "",
    val stokAwal: Int = 0,
    val stokRusak: Int = 0
)

data class Stock(
    val stockId: String = "",          // ID unik, bisa pakai tanggal atau UUID
    val date: String = "",             // "2025-08-26"
    val items: List<StockItem> = listOf(),
    val cashOnHand: Int = 0,           // uang kas awal
    val userId: String = "",           // siapa yang input

    @ServerTimestamp
    val createdAt: Timestamp? = null
)