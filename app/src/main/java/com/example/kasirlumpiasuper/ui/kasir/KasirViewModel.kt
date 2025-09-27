package com.example.kasirlumpiasuper.ui.kasir

import androidx.lifecycle.ViewModel
import com.example.kasirlumpiasuper.ui.utils.DateUtils
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Date
import java.util.Locale

class KasirViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _stockFilledToday = MutableStateFlow(false)
    val stockFilledToday: StateFlow<Boolean> = _stockFilledToday

    fun setStockFilled(filled: Boolean) {
        _stockFilledToday.value = filled
    }

    private val _customerCountToday = MutableStateFlow(0)
    val customerCountToday: StateFlow<Int> = _customerCountToday

    fun checkStockForToday() {
        val now = LocalDateTime.now()
        val businessDate = getBusinessDate(now)

        val stockHariIniAda = false
        _stockFilledToday.value = stockHariIniAda
    }

    fun checkCustomerCountToday() {
        val dateKey = DateUtils.getBusinessDate()
        db.collection("orders")
            .document(dateKey)
            .collection("transactions")
            .get()
            .addOnSuccessListener { snapshot ->
                _customerCountToday.value = snapshot.size()
            }
            .addOnFailureListener {
                _customerCountToday.value = 0
            }
    }

    fun getBusinessDate (now: LocalDateTime): LocalDate {
        val cutoff = now.toLocalDate().atTime(5,0)
        return if (now.isBefore(cutoff)) {
            now.toLocalDate().minusDays(1)
        } else {
            now.toLocalDate()
        }
    }
}