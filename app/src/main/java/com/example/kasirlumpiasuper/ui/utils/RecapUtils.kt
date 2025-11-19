package com.example.kasirlumpiasuper.ui.utils

import com.example.kasirlumpiasuper.data.model.CashAtRegister
import com.example.kasirlumpiasuper.data.model.DailyRecap
import com.example.kasirlumpiasuper.data.model.ExpenseSummary
import com.example.kasirlumpiasuper.data.model.FreeSummary
import com.example.kasirlumpiasuper.data.model.GrossSection
import com.example.kasirlumpiasuper.data.model.Order
import com.example.kasirlumpiasuper.data.model.PaymentMethod
import com.example.kasirlumpiasuper.data.model.ProductRecapRow
import com.example.kasirlumpiasuper.data.model.RecapInput
import com.example.kasirlumpiasuper.data.model.StockInputItem
import com.example.kasirlumpiasuper.data.model.StockMeta
import com.google.firebase.auth.FirebaseAuth

object RecapUtils {

    data class Inputs(
        val dateLabel: String,
        val orders: List<Order>,
        val stockItems: List<StockInputItem>,
        val stockMeta: StockMeta?,
        val recapInput: RecapInput?
    )

    fun compute(inputs: Inputs): DailyRecap {

        // 🔹 Agregasi hasil penjualan dari transaksi
        val soldAgg = aggregateSold(inputs.orders)
        val revenueAgg = aggregateRevenue(inputs.orders)

        val baseProducts = if (inputs.stockItems.isEmpty()) {

            val uniqueProducts = inputs.orders
                .flatMap { it.items }
                .groupBy { it.productId }
                .map { (productId, items) ->
                    val name = items.first().name
                    StockInputItem(
                        productId = productId,
                        name = name,
                        initialStock = 0,
                        damagedStock = 0
                    )
                }

            uniqueProducts

        } else inputs.stockItems

        val productMap = baseProducts.associateBy { it.productId }

        val productRows = productMap.values.map { si ->

            val soldByOrder = soldAgg[si.productId] ?: 0
            val revenue = revenueAgg[si.productId] ?: 0

            val ending = when {
                si.initialStock > 0 -> (si.initialStock - si.damagedStock - soldByOrder).coerceAtLeast(0)
                soldByOrder > 0 -> 0
                else -> 0
            }

            ProductRecapRow(
                productId = si.productId,
                name = si.name,
                initialStock = si.initialStock,
                endingStock = ending,
                damagedStock = si.damagedStock,
                sold = soldByOrder,
                revenue = revenue
            )
        }

//        // ✅ Urutan manual sesuai daftar produk kamu
//        val customOrder = listOf(
//            "Lumpia",
//            "Tahu Lumpia",
//            "Siomay",
//            "Singkong Goreng",
//            "Mihun",
//            "Es Kacang Merah",
//            "Air Mineral",
//            "Siomay Basah"
//        )

//        val sortedRows = productRows.sortedBy { it.name }

        // Produk yang sudah ada di daftar
//        val knownRows = productRows.filter { it.name in customOrder }
//            .sortedBy { customOrder.indexOf(it.name) }
//
//        // Produk baru / tak dikenal ditaruh di bawah, urut alfabet
//        val unknownRows = productRows.filter { it.name !in customOrder }
//            .sortedBy { it.name }

        // Gabungkan kembali
        val sortedRows = productRows.sortedBy { it.name }

        // 🔹 Hitung barang gratis
        val free = computeFree(inputs.orders)

        // 🔹 Hitung total diskon dari semua transaksi
        val discountTotal = inputs.orders.sumOf { it.discount ?: 0 }

        // 🔹 Ambil data input recap dari InputRecapScreen
        val recapIn = inputs.recapInput

        // 🔹 Ringkasan pengeluaran
        val expense = ExpenseSummary(
            freeNominal = free.totalNominal,
            discountTotal = discountTotal,
            mineralWater = recapIn?.mineralWaterExpense ?: 0,
            otherExpense = recapIn?.otherExpense ?: 0,
            sum = free.totalNominal +
                    discountTotal +
                    (recapIn?.mineralWaterExpense ?: 0) +
                    (recapIn?.otherExpense ?: 0)
        )

        // 🔹 Hitung total pendapatan dari seluruh produk
        val sum1 = revenueAgg.values.sum()

        // 🔹 Total transaksi non tunai (QRIS / CASHLESS)
        val nonCash = inputs.orders
            .filter { it.paymentMethod == PaymentMethod.CASHLESS }
            .sumOf { it.total }

        // 🔹 Uang kas awal
        val cashOpening = inputs.stockMeta?.cashOpening ?: 0

        // 🔹 Laba bersih tunai
        val sum2 = sum1 - nonCash - expense.sum

        val sum3 = sum2 + cashOpening

        val gross = GrossSection(
            sum1 = sum1,
            nonCash = nonCash,
            expenseToday = expense.sum,
            sum2 = sum2,
            cashOpening = cashOpening,
            sum3 = sum3,

        )

        // 🔹 Hitung uang di kasir (dari InputRecapScreen)
        val bigCash = recapIn?.bigCash ?: 0
        val smallCash = recapIn?.smallCash ?: 0
        val extraCash = recapIn?.extraCash ?: 0
        val sumCash = bigCash + smallCash + extraCash
        val diff = sum3 - sumCash
        val currentCashierId = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown"

        val cash = CashAtRegister(
            bigCash = bigCash,
            smallCash = smallCash,
            extraCash = extraCash,
            sum = sumCash,
            diff = diff
        )

        // 🔹 Return hasil rekap harian
        return DailyRecap(
            dateLabel = inputs.dateLabel,
            location = inputs.recapInput?.location.orEmpty(),
            cashierId = currentCashierId,
            userName = "",
            notes = inputs.recapInput?.notes.orEmpty(),
            productRows = sortedRows,
            freeSummary = free,
            expenseSummary = expense,
            grossSection = gross,
            cashAtRegister = cash
        )
    }

    // 🔸 Fungsi bantu: Agregasi jumlah terjual
    private fun aggregateSold(orders: List<Order>): Map<String, Int> {
        val map = mutableMapOf<String, Int>()
        orders.forEach { order ->
            order.items.forEach { item ->
                val key = item.productId
                map[key] = (map[key] ?: 0) + item.qty
            }
        }
        return map
    }

    // 🔸 Fungsi bantu: Agregasi pendapatan per produk
    private fun aggregateRevenue(orders: List<Order>): Map<String, Int> {
        val map = mutableMapOf<String, Int>()

        orders.forEach { order ->
            order.items.forEach { item ->

                val basePrice = when {
                    item.isFree && item.originalUnitPrice != null -> item.originalUnitPrice
                    item.isFree -> item.unitPrice
                    else -> item.unitPrice
                } ?: 0

                val itemRevenue = basePrice * item.qty

                map[item.productId] = (map[item.productId] ?: 0) + itemRevenue
            }
        }

        return map
    }


    // 🔸 Fungsi bantu: Hitung barang gratis
    private fun computeFree(orders: List<Order>): FreeSummary {
        var totalItems = 0
        var totalNominal = 0

        orders.forEach { order ->
            order.items.forEach { item ->
                if (item.isFree) {
                    totalItems += item.qty
                    // 🔹 gunakan originalUnitPrice bukan unitPrice
                    totalNominal += (item.originalUnitPrice ?: item.unitPrice) * item.qty
                }
            }
        }

        return FreeSummary(
            totalItems = totalItems,
            totalNominal = totalNominal
        )
    }
}