package com.example.kasirlumpiasuper.data.repository

import android.util.Log
import com.example.kasirlumpiasuper.data.model.MonthlyStats
import com.example.kasirlumpiasuper.data.model.Order
import com.example.kasirlumpiasuper.data.model.Report
import com.example.kasirlumpiasuper.data.model.Stock
import com.example.kasirlumpiasuper.data.model.Users
import com.example.kasirlumpiasuper.domain.DomainError
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

    suspend fun getNextQueueNumber(date: String): Int {
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

            lastQueue + 1
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error fetching queue number: ${e.message}", e)
            1 // fallback jika belum ada transaksi
        }
    }


    suspend fun saveOrder(order: Order) {
        try {
            val dateKey = order.businessDate // "2025-09-28"
            val queueNumber = order.queueNumber.toString() // "001", "002", dst.

            // Path: /orders/{dateKey}/transactions/{queueNumber}
            val docRef = db.collection("orders")
                .document(dateKey)
                .collection("transactions")
                .document(queueNumber)

            docRef.set(order).await()

            Log.d("FirestoreRepository", "Order saved: $dateKey/$queueNumber")
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error saving order: ${e.message}", e)
            throw e
        }
    }


    suspend fun createOrder(order: Order): Boolean {
        return try {
            val docRef = ordersCollection.document()
            val map = hashMapOf(
                "id" to docRef.id,
                "queueNumber" to order.queueNumber,
                "businessDate" to order.businessDate,
                "createdAt" to FieldValue.serverTimestamp(),
                "cashierId" to order.cashierId,
                "customerName" to order.customerName,
                "notes" to order.notes,

                "items" to order.items.map {
                    mapOf(
                        "productId" to it.productId,
                        "name" to it.name,
                        "unitPrice" to it.unitPrice,
                        "qty" to it.qty,
                        "cupIndex" to it.cupIndex
                    )
                },

                "subtotal" to order.subtotal,
                "discount" to order.discount,
                "nonCashAmount" to order.nonCashAmount,
                "cashReceived" to order.cashReceived,
                "change" to order.change,
                "total" to order.total,
                "paymentMethod" to order.paymentMethod.name,
                "status" to order.status.name
            )
            docRef.set(map).await()
            true
        } catch (e: Exception) {
            false
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

    fun mapFirestoreException(e: FirebaseFirestoreException): DomainError {
        return when (e.code) {
            FirebaseFirestoreException.Code.UNAVAILABLE,
            FirebaseFirestoreException.Code.DEADLINE_EXCEEDED -> DomainError.NetworkError
            FirebaseFirestoreException.Code.PERMISSION_DENIED -> DomainError.PermissionDenied
            FirebaseFirestoreException.Code.FAILED_PRECONDITION -> DomainError.PreconditionFailed
            FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED -> DomainError.RateLimited
            else -> DomainError.UnknownError
        }
    }

suspend fun addUser(user: Users) {
    firestore.collection("users").document(user.uid).set(user).await()
}

suspend fun addStock(stock: Stock) {
    firestore.collection("stocks").add(stock).await()
}

suspend fun addReport(report: Report) {
    firestore.collection("reports").add(report).await()
}

suspend fun addMonthlyStats(monthlyStats: MonthlyStats) {
    firestore.collection("monthly_stats").add(monthlyStats).await()
}

suspend fun getStocksByDate(date: String): List<Stock> {
    val snapshot = firestore.collection("stocks")
        .whereEqualTo("date", date)
        .get()
        .await()
    return snapshot.toObjects(Stock::class.java)
}

}