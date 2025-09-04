package com.example.kasirlumpiasuper.data.repository

import com.example.kasirlumpiasuper.data.model.MonthlyStats
import com.example.kasirlumpiasuper.data.model.Report
import com.example.kasirlumpiasuper.data.model.Stock
import com.example.kasirlumpiasuper.data.model.Transaction
import com.example.kasirlumpiasuper.data.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

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

    suspend fun addTransaction(transaction: Transaction) {
        firestore.collection("transactions").add(transaction).await()
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

    suspend fun getTransactionsByDate(date: String): List<Transaction> {
        val snapshot = firestore.collection("transactions")
            .whereEqualTo("date", date)
            .get()
            .await()
        return snapshot.toObjects(Transaction::class.java)
    }
}