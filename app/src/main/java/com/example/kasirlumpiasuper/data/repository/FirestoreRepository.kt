package com.example.kasirlumpiasuper.data.repository

import android.util.Log
import com.example.kasirlumpiasuper.data.Result
import com.example.kasirlumpiasuper.data.model.Order
import com.example.kasirlumpiasuper.data.model.OrderItem
import com.example.kasirlumpiasuper.data.model.Users
import com.example.kasirlumpiasuper.domain.error.DomainError
import com.example.kasirlumpiasuper.domain.error.ErrorMapper
import com.example.kasirlumpiasuper.ui.utils.OrderMapper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class FirestoreRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val firestore = FirebaseFirestore.getInstance()

    private fun currentUserId(): String =
        FirebaseAuth.getInstance().currentUser?.uid ?: "unknown"


    suspend fun getNextQueueNumber(date: String): Result<Int> {
        val userId = currentUserId()
        return try {
            val snapshot = db.collection("users")
                .document(userId)
                .collection("orders")
                .document(date)
                .collection("entries")
                .orderBy("queueNumber", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .await()

            val lastQueue = snapshot.documents.firstOrNull()?.getLong("queueNumber")?.toInt() ?: 0
            Result.Success(lastQueue + 1)
        } catch (e: FirebaseFirestoreException) {
            Result.Error(ErrorMapper.mapFirestoreException(e))
        } catch (e: Exception) {
            Result.Error(DomainError.UnknownError)
        }
    }

    suspend fun saveOrder(order: Order): Result<Unit> {
        val userId = currentUserId()
        return try {
            val dateKey = order.businessDate
            val queueNumber = order.queueNumber.toString()

            // Path: /users/{uid}/orders/{dateKey}/{queueNumber}
            val docRef = db.collection("users")
                .document(userId)
                .collection("orders")
                .document(dateKey)
                .collection("entries")
                .document(queueNumber)

            docRef.set(order).await()
            Result.Success(Unit)
        } catch (e: FirebaseFirestoreException) {
            Result.Error(ErrorMapper.mapFirestoreException(e))
        } catch (e: Exception) {
            Result.Error(DomainError.UnknownError)
        }
    }

    suspend fun deleteOrder(dateKey: String, queueNumber: Int): Boolean {
        val userId = currentUserId()
        return try {
            db.collection("users")
                .document(userId)
                .collection("orders")
                .document(dateKey)
                .collection("entries")
                .document(queueNumber.toString())
                .delete()
                .await()
            true
        } catch (e: Exception) {
            Log.e("FirestoreRepo", "deleteOrder error: ${e.message}", e)
            false
        }
    }

    suspend fun getOrderByQueue(dateKey: String, queueNumber: Int): Order? {
        val userId = currentUserId()
        return try {
            val doc = db.collection("users")
                .document(userId)
                .collection("orders")
                .document(dateKey)
                .collection("entries")
                .document(queueNumber.toString())
                .get()
                .await()

            if (!doc.exists()) return null
            doc.toObject(Order::class.java)
        } catch (e: Exception) {
            Log.e("FirestoreRepo", "getOrderByQueue error: ${e.message}", e)
            null
        }
    }

    suspend fun getOrderItems(dateKey: String, queueNumber: Int): List<OrderItem> {
        val userId = currentUserId()
        return try {
            val doc = db.collection("users")
                .document(userId)
                .collection("orders")
                .document(dateKey)
                .collection("entries")
                .document(queueNumber.toString())
                .get()
                .await()

            if (!doc.exists()) emptyList()
            else {
                val rawItems = doc.get("items")
                OrderMapper.mapListToItems(rawItems)
            }
        } catch (e: Exception) {
            Log.e("FirestoreRepo", "getOrderItems error: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun updateOrder(
        dateKey: String,
        queueNumber: Int,
        updatedOrder: Map<String, Any>
    ): Boolean {
        val userId = currentUserId()
        return try {
            val docRef = db.collection("users")
                .document(userId)
                .collection("orders")
                .document(dateKey)
                .collection("entries")
                .document(queueNumber.toString())

            docRef.set(updatedOrder).await()
            true
        } catch (e: Exception) {
            Log.e("FirestoreRepo", "updateOrder error: ${e.message}", e)
            false
        }
    }

    /** 🔹 Ambil total pendapatan dari semua transaksi di tanggal tertentu */
    suspend fun getDailyRevenue(dateKey: String): Int {
        return try {
            val userId = currentUserId()
            val snapshot = db.collection("users")
                .document(userId)
                .collection("orders")
                .document(dateKey)
                .collection("entries")
                .get()
                .await()

            snapshot.documents.sumOf { it.getLong("total")?.toInt() ?: 0 }
        } catch (e: Exception) {
            Log.e("FirestoreRepo", "getDailyRevenue error: ${e.message}", e)
            0 // kalau gagal, kembalikan 0 supaya tidak crash
        }
    }

    // =============================================================
    //  USER DATA
    // =============================================================

    suspend fun getUserData(): Users? {
        val userId = currentUserId()
        val snapshot = firestore.collection("users").document(userId).get().await()
        return Users(
            name = snapshot.getString("name") ?: "",
            role = snapshot.getString("role") ?: ""
        )
    }

    suspend fun getUserQuote(): String? {
        val userId = currentUserId()
        val snapshot = firestore.collection("users").document(userId).get().await()
        return snapshot.getString("quote")
    }

    // =============================================================
    //  STOK / CASH
    // =============================================================

    suspend fun isStockFilled(dateKey: String): Boolean {
        val userId = currentUserId()
        return try {
            val doc = db.collection("users")
                .document(userId)
                .collection("stock_inputs")
                .document(dateKey)
                .get()
                .await()
            doc.exists()
        } catch (e: Exception) {
            false
        }
    }

    suspend fun resetStockForDate(dateKey: String) {
        val userId = currentUserId()
        try {
            db.collection("users")
                .document(userId)
                .collection("stock_inputs")
                .document(dateKey)
                .delete()
                .await()
        } catch (e: Exception) {
            Log.e("FirestoreRepo", "resetStockForDate error: ${e.message}", e)
        }
    }

    suspend fun resetCashForDate(dateKey: String) {
        val userId = currentUserId()
        try {
            db.collection("users")
                .document(userId)
                .collection("cash")
                .document(dateKey)
                .delete()
                .await()
        } catch (e: Exception) {
            Log.e("FirestoreRepo", "resetCashForDate error: ${e.message}", e)
        }
    }

    suspend fun getDailyCashAtRegister(dateKey: String): Int {
        return try {
            val userId = currentUserId()
            val snap = db.collection("users")
                .document(userId)
                .collection("recap_inputs")
                .document(dateKey)
                .get()
                .await()

            val big = snap.getLong("uangBesar")?.toInt() ?: 0
            val small = snap.getLong("uangKecil")?.toInt() ?: 0
            val extra = snap.getLong("uangLebihan")?.toInt() ?: 0
            big + small + extra
        } catch (e: Exception) {
            0
        }
    }

    suspend fun getDailyExpense(dateKey: String): Int {
        return try {
            val userId = currentUserId()
            val snap = db.collection("users")
                .document(userId)
                .collection("recap_inputs")
                .document(dateKey)
                .get()
                .await()

            val mineralWater = snap.getLong("mineralWaterExpense")?.toInt() ?: 0
            val otherExpense = snap.getLong("otherExpense")?.toInt() ?: 0
            val freeNominal = snap.getLong("freeNominal")?.toInt() ?: 0  // opsional kalau mau tambah free cost

            mineralWater + otherExpense
        } catch (e: Exception) {
            Log.e("FirestoreRepo", "getDailyExpense error: ${e.message}")
            0
        }
    }



}