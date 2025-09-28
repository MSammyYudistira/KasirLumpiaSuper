package com.example.kasirlumpiasuper.ui.history

import com.example.kasirlumpiasuper.data.model.Order
import com.example.kasirlumpiasuper.data.model.OrderItem

object OrderSummaryFormatter {

    /** "Cup 1: Lumpia Super x2, Siomay x2 • 18:25" (jam di UI nanti) */
    fun buildCupSummary(order: Order, maxItemsPerCup: Int = 3): List<String> {
        val byCup = order.items.groupBy { it.cupIndex }
            .toSortedMap()

        return byCup.map { (cup, items) ->
            val parts = items
                .sortedBy { it.name }
                .take(maxItemsPerCup)
                .map { nameWithQty(it) }

            val extra = items.size - parts.size
            val itemsStr = if (extra > 0) parts.joinToString(", ") + ", +$extra item"
            else parts.joinToString(", ")

            "Cup $cup: $itemsStr"
        }
    }

    private fun nameWithQty(it: OrderItem): String {
        val qty = it.qty
        val nm = it.name
        return if (it.isFree) "$nm x$qty (free)" else "$nm x$qty"
    }
}