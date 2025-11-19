package com.example.kasirlumpiasuper.data.model

data class StockInputItem(
    val productId: String = "",
    val name: String = "",
    val initialStock: Int = 0,
    val damagedStock: Int = 0
)

data class StockMeta(
    val cashOpening: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val createdBy: Map<String, String>? = null)

data class RecapInput(
    val bigCash: Int = 0,
    val smallCash: Int = 0,
    val extraCash: Int = 0,
    val location: String = "",
    val mineralWaterExpense: Int = 0,
    val otherExpense: Int = 0,
    val notes: String = ""
)

data class ProductRecapRow(
    val productId: String = "",
    val name: String = "",
    val initialStock: Int = 0,
    val endingStock: Int = 0,
    val damagedStock: Int = 0,
    val sold: Int = 0,
    val revenue: Int = 0
)

data class FreeSummary(
    val totalNominal: Int = 0,
    val totalItems: Int = 0
)

data class ExpenseSummary(
    val freeNominal: Int = 0,
    val discountTotal: Int = 0,
    val mineralWater: Int = 0,
    val otherExpense: Int = 0,
    val sum: Int = 0
)

data class GrossSection(
    val sum1: Int = 0,          // total pendapatan dari table makanan
    val nonCash: Int = 0,       // total transaksi non tunai
    val expenseToday: Int = 0,
    val sum2: Int = 0,          // sum1 - nonCash - expenseToday
    val sum3: Int = 0,          // sum 2 + cashOpening = sum3
    val cashOpening: Int = 0    // dari StockMeta hari pertama, atau dari “Uang Kecil” hari kemarin
)

data class CashAtRegister(
    val bigCash: Int = 0,
    val smallCash: Int = 0,
    val extraCash: Int = 0,
    val sum: Int = 0,
    val diff: Int = 0           // sum3 - sumCash
)

data class DailyRecap(
    val dateLabel: String = "",
    val location: String = "",
    val cashierId: String = "",
    val userName: String = "",
    val notes: String = "",
    val productRows: List<ProductRecapRow> = emptyList(),
    val freeSummary: FreeSummary = FreeSummary(),
    val expenseSummary: ExpenseSummary = ExpenseSummary(),
    val grossSection: GrossSection = GrossSection(),
    val cashAtRegister: CashAtRegister = CashAtRegister()
)