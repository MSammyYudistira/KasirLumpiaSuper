package com.example.kasirlumpiasuper.ui.utils

import com.example.kasirlumpiasuper.data.model.OrderItem
import kotlin.collections.map

    object OrderCalculator {

        /** Subtotal = sum(qty * unitPrice) tapi item free dihitung 0 */
        fun subtotal(items: List<OrderItem>): Int =
            items.sumOf { if (it.isFree) 0 else it.unitPrice * it.qty }

        /** Total = (subtotal - discount).coerceAtLeast(0) */
        fun total(subtotal: Int, discount: Int): Int =
            (subtotal - discount).coerceAtLeast(0)
    }

    object OrderMapper {

        /**
         * Map satu item → Map<String, Any> untuk Firestore.
         * Catatan: gunakan key "free" (bukan "isFree") agar konsisten
         * dengan dokumen yang sudah ada.
         */
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

        /** List<OrderItem> → List<Map<String, Any>> */
        fun itemsToMapList(items: List<OrderItem>): List<Map<String, Any>> =
            items.map(::toMap)

        /** Any (Firestore) → List<OrderItem> */
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

        // ---------- helpers null-safe ----------
        private fun Map<String, Any?>.getString(key: String): String =
            (this[key] as? String) ?: ""

        private fun Map<String, Any?>.getInt(key: String): Int =
            when (val v = this[key]) {
                is Int -> v
                is Long -> v.toInt()
                is Double -> v.toInt()
                is Float -> v.toInt()
                is String -> v.toIntOrNull() ?: 0
                else -> 0
            }

        private fun Map<String, Any?>.getBool(key: String): Boolean =
            when (val v = this[key]) {
                is Boolean -> v
                is String -> v.equals("true", ignoreCase = true)
                is Number -> v.toInt() != 0
                else -> false
            }
    }