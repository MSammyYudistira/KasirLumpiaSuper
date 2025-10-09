package com.example.kasirlumpiasuper.data.repository

import com.example.kasirlumpiasuper.data.model.DailyRecap
import com.example.kasirlumpiasuper.data.model.Order
import com.example.kasirlumpiasuper.data.model.RecapInput
import com.example.kasirlumpiasuper.data.model.StockInputItem
import com.example.kasirlumpiasuper.data.model.StockMeta
import com.example.kasirlumpiasuper.ui.utils.DateUtils
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import kotlin.jvm.java


class RecapRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun getOrdersByDate(dateLabel: String): List<Order> {
        return db.collection("orders")
            .document(dateLabel)
            .collection("transactions")
            .get()
            .await()
            .toObjects(Order::class.java)
    }

    suspend fun getStockItemsByDate(dateLabel: String): List<StockInputItem> {
        return db.collection("stock_inputs")
            .document(dateLabel)
            .collection("items")
            .get()
            .await()
            .toObjects(StockInputItem::class.java)
    }

    suspend fun getStockMetaByDate(dateLabel: String): StockMeta? {
        val parentRef = db.collection("stock_inputs").document(dateLabel)
        val metaSnap = parentRef.collection("meta").document("default").get().await()
        return metaSnap.toObject(StockMeta::class.java)
    }

    suspend fun getRecapInputByDate(dateLabel: String): RecapInput? {
        return db.collection("recap_inputs")
            .document(dateLabel)
            .get()
            .await()
            .toObject(RecapInput::class.java)
    }

//    suspend fun getPrevEndingStock(dateLabel: String): Map<String, Int> {
//        val prevDate = DateUtils.prevBusinessDateLabel(dateLabel) ?: return emptyMap()
//        val prevStocks = db.collection("stock_inputs")
//            .document(prevDate)
//            .collection("items")
//            .get()
//            .await()
//            .toObjects(StockInputItem::class.java)
//
//        // mapping nama produk -> stok akhir
//        return prevStocks.associate { it.name to it.initialStock }
//    }

    /** 🔹 Ambil semua transaksi pada tanggal tertentu */
    suspend fun getOrders(dateLabel: String): List<Order> {
        val snap = db.collection("orders")
            .document(dateLabel)
            .collection("transactions")
            .get()
            .await()
        return snap.documents.mapNotNull { it.toObject(Order::class.java) }
    }

    /** 🔹 Ambil stok awal & stok rusak + uang kas awal (cashOpening) dari StockScreen */
    suspend fun getStockInputs(dateLabel: String): Pair<List<StockInputItem>, StockMeta?> {
        val parentRef = db.collection("stock_inputs").document(dateLabel)

        // Ambil items dari stock_inputs/{dateLabel}/items
        val itemsSnap = parentRef.collection("items").get().await()
        val items = itemsSnap.documents.mapNotNull { it.toObject(StockInputItem::class.java) }

        // Ambil meta dari stock_inputs/{dateLabel}/meta/default
        val metaSnap = parentRef.collection("meta").document("default").get().await()
        val meta = metaSnap.toObject(StockMeta::class.java)

        return items to meta
    }

    /** 🔹 Ambil data input rekapan dari InputRecapScreen */
    suspend fun getRecapInput(dateLabel: String): RecapInput? {
        val doc = db.collection("recap_inputs")
            .document(dateLabel)
            .get()
            .await()
        return doc.toObject(RecapInput::class.java)
    }

    suspend fun hasRecapInput(dateLabel: String): Boolean {
        return try {
            val doc = FirebaseFirestore.getInstance()
                .collection("recap_inputs")
                .document(dateLabel)
                .get()
                .await()
            doc.exists()
        } catch (e: Exception) {
            false
        }
    }

    /** 🔹 Ambil stok akhir dari rekap kemarin (untuk dijadikan endingStock hari ini) */
    suspend fun getPreviousRecapEndingStocks(prevDateLabel: String): Map<String, Int> {
        val doc = db.collection("recaps")
            .document(prevDateLabel)
            .get()
            .await()

        val recap = doc.toObject(DailyRecap::class.java) ?: return emptyMap()
        return recap.productRows.associate { it.productId to it.endingStock }
    }

    /** 🔹 Ambil small cash dari rekap kemarin (untuk cashOpening hari ini, hari ke-2 dst) */
    suspend fun getPreviousSmallCash(prevDateLabel: String): Int {
        val doc = db.collection("recaps")
            .document(prevDateLabel)
            .get()
            .await()
        val recap = doc.toObject(DailyRecap::class.java)
        return recap?.cashAtRegister?.smallCash ?: 0
    }

    /** 🔹 Simpan hasil rekapan harian ke Firestore */
    suspend fun saveDailyRecap(dateLabel: String, recap: DailyRecap) {
        db.collection("recaps")
            .document(dateLabel)
            .set(recap)
            .await()
    }

    /** 🔹 Ambil rekap harian (jika perlu menampilkan ulang di History/Detail) */
    suspend fun getRecap(dateLabel: String): DailyRecap? {
        val doc = db.collection("recaps")
            .document(dateLabel)
            .get()
            .await()
        return doc.toObject(DailyRecap::class.java)
    }

    suspend fun saveStockInputs(
        dateLabel: String,
        items: List<StockInputItem>,
        meta: StockMeta
    ) {
        val batch = db.batch()
        val parentRef = db.collection("stock_inputs").document(dateLabel)

        // ✅ sentuh dokumen induk (marker + timestamp)
        batch.set(parentRef, mapOf("hasData" to true, "updatedAt" to FieldValue.serverTimestamp()), SetOptions.merge())

        // items
        val itemsRef = parentRef.collection("items")
        items.forEach {
            val doc = itemsRef.document(it.productId)
            batch.set(doc, it)
        }

        // meta
        val metaRef = parentRef.collection("meta").document("default")
        batch.set(metaRef, meta)

        batch.commit().await()
    }

    suspend fun saveRecapInput(dateLabel: String, input: RecapInput) {
        db.collection("recap_inputs").document(dateLabel).set(input).await()
    }
}