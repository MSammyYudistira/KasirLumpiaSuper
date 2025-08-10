package com.example.kasirlumpiasuper.data

import java.util.Date

data class DailyReport(
    val id: String = "",
    val date: Date? = null,
    val location: String = "Toko Utama",
    val initialCash: Double = 0.0,
    val grossRevenue: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val netRevenue: Double = 0.0,
    val stockSummary: List<StockReportItem> = emptyList()
)

data class StockReportItem(
    val productId: String = "",
    val name: String = "",
    val initialStock: Int = 0,
    val damagedStock: Int = 0,
    val soldQuantity: Int = 0,
    val finalStock: Int = 0
)