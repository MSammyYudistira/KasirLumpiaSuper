package com.example.kasirlumpiasuper.data.repository

import android.util.Log
import com.example.kasirlumpiasuper.data.model.Order
import com.example.kasirlumpiasuper.data.model.RecapInput
import com.example.kasirlumpiasuper.data.model.StockInputItem
import com.example.kasirlumpiasuper.data.model.StockMeta
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await


class RecapRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private fun currentUserId(): String =
        FirebaseAuth.getInstance().currentUser?.uid ?: "unknown"

    suspend fun getUserNameById(uid: String): String {
        return try {
            val doc = db.collection("users").document(uid).get().await()
            doc.getString("name") ?: "Tidak Diketahui"
        } catch (e: Exception) {
            "Tidak Diketahui"
        }
    }

    suspend fun getCurrentUserProfile(): Map<String, String> {
        val userId = currentUserId()
        val snapshot = db.collection("users").document(userId).get().await()
        return mapOf(
            "uid" to userId,
            "name" to (snapshot.getString("name") ?: "Nama Tidak Diketahui"),
            "role" to (snapshot.getString("role") ?: "kasir")
        )
    }

    suspend fun getOrdersByDate(dateLabel: String): List<Order> {
        val userId = currentUserId()
        return try {
            db.collection("users")
                .document(userId)
                .collection("orders")
                .document(dateLabel)
                .collection("entries")
                .get()
                .await()
                .toObjects(Order::class.java)
        } catch (e: Exception) {
            Log.e("RecapRepo", "getOrdersByDate error: ${e.message}")
            emptyList()
        }
    }

    suspend fun getStockItemsByDate(dateLabel: String): List<StockInputItem> {
        val userId = currentUserId()
        return try {
            db.collection("users")
                .document(userId)
                .collection("stock_inputs")
                .document(dateLabel)
                .collection("items")
                .get()
                .await()
                .toObjects(StockInputItem::class.java)
        } catch (e: Exception) {
            Log.e("RecapRepo", "getStockItemsByDate error: ${e.message}")
            emptyList()
        }
    }

    suspend fun getStockMetaByDate(dateLabel: String): StockMeta? {
        val userId = currentUserId()
        return try {
            val doc = db.collection("users")
                .document(userId)
                .collection("stock_inputs")
                .document(dateLabel)
                .get()
                .await()
            doc.toObject(StockMeta::class.java)
        } catch (e: Exception) {
            Log.e("RecapRepo", "getStockMetaByDate error: ${e.message}")
            null
        }
    }

    suspend fun saveStockInputs(
        dateLabel: String,
        items: List<StockInputItem>,
        meta: StockMeta
    ) {
        val userId = currentUserId()
        val batch = db.batch()

        val parentRef = db.collection("users")
            .document(userId)
            .collection("stock_inputs")
            .document(dateLabel)

        batch.set(
            parentRef,
            meta,
            SetOptions.merge()
        )

        val itemsRef = parentRef.collection("items")
        items.forEach {
            val doc = itemsRef.document(it.productId)
            batch.set(doc, it)
        }

        batch.set(
            parentRef,
            mapOf("hasData" to true, "updatedAt" to FieldValue.serverTimestamp()),
            SetOptions.merge()
        )

        batch.commit().await()
    }

    suspend fun getRecapInputByDate(dateLabel: String): RecapInput? {
        val userId = currentUserId()
        return try {
            db.collection("users")
                .document(userId)
                .collection("recap_inputs")
                .document(dateLabel)
                .get()
                .await()
                .toObject(RecapInput::class.java)
        } catch (e: Exception) {
            Log.e("RecapRepo", "getRecapInputByDate error: ${e.message}")
            null
        }
    }

    suspend fun hasRecapInput(dateLabel: String): Boolean {
        val userId = currentUserId()
        return try {
            val doc = db.collection("users")
                .document(userId)
                .collection("recap_inputs")
                .document(dateLabel)
                .get()
                .await()
            doc.exists()
        } catch (e: Exception) {
            Log.e("RecapRepo", "Error cek recap: ${e.message}")
            false
        }
    }

    suspend fun saveRecapInput(input: RecapInput, dateLabel: String) {
        val userId = currentUserId()
        try {
            db.collection("users")
                .document(userId)
                .collection("recap_inputs")
                .document(dateLabel)
                .set(input)
                .await()
        } catch (e: Exception) {
            Log.e("RecapRepo", "saveRecapInput error: ${e.message}")
        }
    }
}