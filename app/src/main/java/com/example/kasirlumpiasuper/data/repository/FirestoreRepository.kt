package com.example.kasirlumpiasuper.data.repository

import com.example.kasirlumpiasuper.data.model.MonthlyStats
import com.example.kasirlumpiasuper.data.model.Report
import com.example.kasirlumpiasuper.data.model.Stock
import com.example.kasirlumpiasuper.data.model.Transaction
import com.example.kasirlumpiasuper.data.model.Users
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun addUser(user: Users) {
        db.collection("users").document(user.uid).set(user).await()
    }

    suspend fun addStock(stock: Stock) {
        db.collection("stocks").add(stock).await()
    }

    suspend fun addTransaction(transaction: Transaction) {
        db.collection("transactions").add(transaction).await()
    }

    suspend fun addReport(report: Report) {
        db.collection("reports").add(report).await()
    }

    suspend fun addMonthlyStats(monthlyStats: MonthlyStats) {
        db.collection("monthly_stats").add(monthlyStats).await()
    }

    suspend fun getStocksByDate(date: String): List<Stock> {
        val snapshot = db.collection("stocks")
            .whereEqualTo("date", date)
            .get()
            .await()
        return snapshot.toObjects(Stock::class.java)
    }

    suspend fun getTransactionsByDate(date: String): List<Transaction> {
        val snapshot = db.collection("transactions")
            .whereEqualTo("date", date)
            .get()
            .await()
        return snapshot.toObjects(Transaction::class.java)
    }
}