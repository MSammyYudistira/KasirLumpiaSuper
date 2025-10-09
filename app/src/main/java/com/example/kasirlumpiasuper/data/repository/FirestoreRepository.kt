package com.example.kasirlumpiasuper.data.repository

import com.example.kasirlumpiasuper.data.Result
import com.example.kasirlumpiasuper.data.model.Order
import com.example.kasirlumpiasuper.data.model.Users
import com.example.kasirlumpiasuper.domain.error.DomainError
import com.example.kasirlumpiasuper.domain.error.ErrorMapper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class FirestoreRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val ordersCollection = db.collection("orders")

    suspend fun getNextQueueNumber(date: String): Result<Int> {
        return try {
            val snapshot = db.collection("orders")
                .document(date)
                .collection("transactions")
                .orderBy("queueNumber", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .await()

            val lastQueue = snapshot.documents
                .firstOrNull()
                ?.getLong("queueNumber")
                ?.toInt() ?: 0
            Result.Success(lastQueue + 1)

        } catch (e: FirebaseFirestoreException) {
            Result.Error(ErrorMapper.mapFirestoreException(e))
        } catch (e: Exception) {
            Result.Error(DomainError.UnknownError)
        }
    }


    suspend fun saveOrder(order: Order): Result<Unit> {
        return try {
            val dateKey = order.businessDate // "2025-09-28"
            val queueNumber = order.queueNumber.toString() // "001", "002", dst.

            // Path: /orders/{dateKey}/transactions/{queueNumber}
            val docRef = db.collection("orders")
                .document(dateKey)
                .collection("transactions")
                .document(queueNumber)

            docRef.set(order).await()

            Result.Success(Unit)
        } catch (e: FirebaseFirestoreException) {
            Result.Error(ErrorMapper.mapFirestoreException(e))
        } catch (e: Exception) {
            Result.Error(DomainError.UnknownError)
        }
    }

    suspend fun getUserName(): Users? {
        val userId = auth.currentUser?.uid ?: return null
        val snapshot = firestore
            .collection("users")
            .document(userId)
            .get()
            .await()

        return Users(
            name = snapshot.getString("name") ?: "",
        )

    }

    suspend fun getUserQuote(): String? {
        val uid = auth.currentUser?.uid ?: return null
        val snapshot = firestore
            .collection("users")
            .document(uid)
            .get()
            .await()

        return snapshot.getString("quote")
    }

    suspend fun isStockFilled(dateKey: String): Boolean {
        return try {
            val parent = db.collection("stock_inputs").document(dateKey).get().await()
            if (parent.exists()) return true

            val itemsAny = db.collection("stock_inputs").document(dateKey)
                .collection("items").limit(1).get().await()
            val metaDoc = db.collection("stock_inputs").document(dateKey)
                .collection("meta").document("default").get().await()

            (itemsAny.size() > 0) || metaDoc.exists()
        } catch (e: Exception) {
            false
        }
    }

    suspend fun resetStockForDate(dateKey: String) {
        val parent = db.collection("stock_inputs").document(dateKey)
        // hapus items
        val itemsSnap = parent.collection("items").get().await()
        itemsSnap.documents.forEach { it.reference.delete().await() }
        // hapus meta
        parent.collection("meta").document("default").delete().await()
        // terakhir hapus doc induk
        parent.delete().await()
    }

    suspend fun resetCashForDate(datekey: String) {
        try {
            db.collection("cash")
                .document(datekey)
                .delete()
                .await()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getOrderByQueue(dateKey: String, queueNumber: Int): Order? {
        return try {
            val docRef = db.collection("orders")
                .document(dateKey)
                .collection("transactions")
                .document(queueNumber.toString())

            val snapshot = docRef.get().await()

            if (snapshot.exists()) {
                snapshot.toObject(Order::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            println("❌ getOrderByQueue error: ${e.message}")
            null
        }
    }
}