package com.example.kasirlumpiasuper.helper.order

import com.example.kasirlumpiasuper.domain.model.OrderItem

object OrderCalculator {

        fun subtotal(items: List<OrderItem>): Int =
            items.sumOf { if (it.isFree) 0 else it.unitPrice * it.qty }

        fun total(subtotal: Int, discount: Int): Int =
            (subtotal - discount).coerceAtLeast(0)
    }

    object OrderMapper {
        fun toMap(item: OrderItem): Map<String, Any> = mapOf(
            "productId" to item.productId,
            "name" to item.name,
            "qty" to item.qty,
            "unitPrice" to item.unitPrice,
            "free" to item.isFree,
            "cupIndex" to item.cupIndex,
            "originalUnitPrice" to (item.originalUnitPrice ?: item.unitPrice),
            "imageUrl" to (item.imageUrl ?: "")
        )

        fun itemsToMapList(items: List<OrderItem>): List<Map<String, Any>> =
            items.map(::toMap)

        fun mapListToItems(rawItems: Any?): List<OrderItem> {
            val list = rawItems as? List<Map<String, Any?>> ?: return emptyList()

            return list.map { map ->
                OrderItem(
                    productId = map["productId"] as? String ?: "",
                    name = map["name"] as? String ?: "",
                    qty = (map["qty"] as? Long)?.toInt() ?: 0,
                    unitPrice = if (map["free"] as? Boolean == true) 0
                    else (map["unitPrice"] as? Long)?.toInt() ?: 0,
                    originalUnitPrice = (map["originalUnitPrice"] as? Long)?.toInt(),
                    isFree = map["free"] as? Boolean ?: false,
                    cupIndex = (map["cupIndex"] as? Long)?.toInt() ?: 1,
                    imageUrl = (map["imageUrl"] as? String) ?: "",
                )
            }
        }
    }