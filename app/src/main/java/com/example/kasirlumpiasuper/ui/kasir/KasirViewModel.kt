package com.example.kasirlumpiasuper.ui.kasir

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class KasirViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    var pelangganHariIni by mutableStateOf(0)
        private set

    var pendapatanHariIni by mutableStateOf(0.0)
        private set

    init {
        loadRingkasanHariIni()
    }

    private fun loadRingkasanHariIni() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        firestore.collection("transactions")
            .whereEqualTo("date", today)
            .get()
            .addOnSuccessListener { docs ->
                pelangganHariIni = docs.size()
                pendapatanHariIni = docs.sumOf { it.getDouble("total") ?: 0.0 }
            }
    }
}