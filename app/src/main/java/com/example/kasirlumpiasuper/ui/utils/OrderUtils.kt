package com.example.kasirlumpiasuper.ui.utils

import com.example.kasirlumpiasuper.R
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
            "imageRes" to item.imageRes // ✅ tambahkan ini
        )

        /**
         * Map mentah Firestore → OrderItem (null-safe + tipe aman).
         * Field yang tidak ada akan diisi default yang aman.
         */
        fun fromMap(m: Map<String, Any?>): OrderItem = OrderItem(
            productId = m.getString("productId"),
            name = m.getString("name"),
            qty = m.getInt("qty"),
            unitPrice = m.getInt("unitPrice"),
            isFree = m.getBool("free"),
            cupIndex = m.getInt("cupIndex").takeIf { it > 0 } ?: 1,
            originalUnitPrice = m.getInt("originalUnitPrice")
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
                    imageRes = (map["imageRes"] as? Long)?.toInt()
                        ?: resolveImageResByName(map["name"] as? String ?: "") // ✅ fallback jika tidak tersimpan
                )
            }
        }

        // 🔹 fallback resolver untuk gambar berdasarkan nama produk
        private fun resolveImageResByName(name: String): Int {
            return when (name) {
                "Lumpia" -> R.drawable.lumpia
                "Tahu Lumpia" -> R.drawable.tahu_lumpia_3
                "Siomay" -> R.drawable.siomay_goreng
                "Siomay Basah" -> R.drawable.siomay_basah_2
                "Singkong Goreng" -> R.drawable.singkong_goreng
                "Mihun" -> R.drawable.mihun_2
                "Es Kacang Merah" -> R.drawable.es_kacang_merah
                "Air Mineral" -> R.drawable.air_mineral
                else -> R.drawable.lumper_logo
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