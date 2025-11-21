package com.example.kasirlumpiasuper.helper.recap

import com.example.kasirlumpiasuper.domain.model.CashAtRegister
import com.example.kasirlumpiasuper.domain.model.DailyRecap
import com.example.kasirlumpiasuper.domain.model.ExpenseSummary
import com.example.kasirlumpiasuper.domain.model.FreeSummary
import com.example.kasirlumpiasuper.domain.model.GrossSection
import com.example.kasirlumpiasuper.domain.model.Order
import com.example.kasirlumpiasuper.domain.model.PaymentMethod
import com.example.kasirlumpiasuper.domain.model.ProductRecapRow
import com.example.kasirlumpiasuper.domain.model.RecapInput
import com.example.kasirlumpiasuper.domain.model.StockInputItem
import com.example.kasirlumpiasuper.domain.model.StockMeta
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

        val sortedRows = productRows.sortedBy { it.name }
        val free = computeFree(inputs.orders)
        val discountTotal = inputs.orders.sumOf { it.discount ?: 0 }
        val recapIn = inputs.recapInput

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

        // Hitung total pendapatan dari seluruh produk
        val sum1 = revenueAgg.values.sum()

        // Total transaksi non tunai (QRIS / CASHLESS)
        val nonCash = inputs.orders
            .filter { it.paymentMethod == PaymentMethod.CASHLESS }
            .sumOf { it.total }

        // Uang kas awal
        val cashOpening = inputs.stockMeta?.cashOpening ?: 0

        // Hasil dari sum1 - Uang Non tunai & Pengeluaran
        val sum2 = sum1 - nonCash - expense.sum

        // Hasil dari sum2 + Uang kas
        val sum3 = sum2 + cashOpening

        val gross = GrossSection(
            sum1 = sum1,
            nonCash = nonCash,
            expenseToday = expense.sum,
            sum2 = sum2,
            cashOpening = cashOpening,
            sum3 = sum3,

            )

        // Hitung uang di kasir (dari InputRecapScreen)
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

    // Agregasi jumlah terjual
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

    // Agregasi pendapatan per produk
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

    // Hitung barang gratis
    private fun computeFree(orders: List<Order>): FreeSummary {
        var totalItems = 0
        var totalNominal = 0

        orders.forEach { order ->
            order.items.forEach { item ->
                if (item.isFree) {
                    totalItems += item.qty
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