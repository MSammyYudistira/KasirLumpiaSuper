package com.example.kasirlumpiasuper.data.repository

import com.example.kasirlumpiasuper.data.model.MonthlyStats
import com.example.kasirlumpiasuper.data.model.Order
import com.example.kasirlumpiasuper.data.model.Report
import com.example.kasirlumpiasuper.data.model.Stock
import com.example.kasirlumpiasuper.data.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val ordersCollection = db.collection("orders")

    suspend fun getNextQueueNumber(businessDate: String): Int {
        val snapshot = ordersCollection
            .whereEqualTo("businessDate", businessDate)
            .get()
            .await()

        val count = snapshot.size()
        return count + 1
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

                "items" to order.items.map {
                    mapOf(
                        "productId" to it.productId,
                        "name" to it.name,
                        "unitPrice" to it.unitPrice,
                        "qty" to it.qty,
                        "serving" to it.serving.name,
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


suspend fun addUser(user: Users) {
    firestore.collection("users").document(user.uid).set(user).await()
}

suspend fun addStock(stock: Stock) {
    firestore.collection("stocks").add(stock).await()
}

//    suspend fun addTransaction(transaction: Transaction) {
//        firestore.collection("transactions").add(transaction).await()
//    }

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

//    suspend fun getTransactionsByDate(date: String): List<Transaction> {
//        val snapshot = firestore.collection("transactions")
//            .whereEqualTo("date", date)
//            .get()
//            .await()
//        return snapshot.toObjects(Transaction::class.java)
//    }
}