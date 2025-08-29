package com.example.kasirlumpiasuper.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class MonthlyStats(
    val monthId: String = "",          // "2025-08"
    val totalSales: Int = 0,           // total pendapatan 1 bulan
    val averageSales: Int = 0,         // rata-rata pendapatan harian
    val growth: Double = 0.0,          // pertumbuhan % dibanding bulan lalu

    @ServerTimestamp
    val createdAt: Timestamp? = null
)