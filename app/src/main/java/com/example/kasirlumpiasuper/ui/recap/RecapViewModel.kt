package com.example.kasirlumpiasuper.ui.recap

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kasirlumpiasuper.data.model.DailyRecap
import com.example.kasirlumpiasuper.data.model.RecapInput
import com.example.kasirlumpiasuper.data.model.StockInputItem
import com.example.kasirlumpiasuper.data.model.StockMeta
import com.example.kasirlumpiasuper.data.repository.FirestoreRepository
import com.example.kasirlumpiasuper.data.repository.RecapRepository
import com.example.kasirlumpiasuper.ui.utils.BusinessDateManager
import com.example.kasirlumpiasuper.ui.utils.DateUtils
import com.example.kasirlumpiasuper.ui.utils.RecapUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecapViewModel(
    private val repo: RecapRepository = RecapRepository()
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _recap = MutableStateFlow<DailyRecap?>(null)
    val recap: StateFlow<DailyRecap?> = _recap

    private val _userName = MutableStateFlow<String>("")
    val userName: StateFlow<String> = _userName

    private val _hasRecapInput = MutableStateFlow(false)
    val hasRecapInput: StateFlow<Boolean> = _hasRecapInput

    fun loadUserName(cashierId: String) {
        viewModelScope.launch {
            val name = repo.getUserNameById(cashierId)
            _userName.value = name
        }
    }

    fun load(dateLabel: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val orders = repo.getOrdersByDate(dateLabel)
                val stockItems = repo.getStockItemsByDate(dateLabel)
                val stockMeta = repo.getStockMetaByDate(dateLabel)
                val recapInput = repo.getRecapInputByDate(dateLabel)

                val inputs = RecapUtils.Inputs(
                    dateLabel = dateLabel,
                    orders = orders,
                    stockItems = stockItems,
                    stockMeta = stockMeta,
                    recapInput = recapInput
                )

                val dailyRecap = RecapUtils.compute(inputs)

                val cashierId = dailyRecap.cashierId
                val userName = if (cashierId.isNotBlank()) {
                    try {
                        repo.getUserNameById(cashierId) ?: "" // kamu tambahkan fungsi ini di repo (lihat di bawah)
                    } catch (e: Exception) {
                        ""
                    }
                } else ""

                // 4) Set state
                _recap.value = dailyRecap.copy(userName = userName)
            } catch (e: Exception) {
                _error.value = e.message ?: "Gagal memuat rekap"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveStockInput(
        items: List<StockInputItem>,
        cashOpening: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // ✅ Ambil profil user lengkap dari Firestore (punya nama dan role)
                val profile = repo.getCurrentUserProfile()

                // ✅ Buat meta dengan data lengkap
                val meta = StockMeta(
                    cashOpening = cashOpening,
                    createdAt = System.currentTimeMillis(),
                    createdBy = profile
                )

                // ✅ Simpan ke Firestore
                val dateLabel = BusinessDateManager.getBusinessDateLabel()
                repo.saveStockInputs(dateLabel, items, meta)

                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Gagal menyimpan stok")
            }
        }
    }

    fun saveRecapInput(
        input: RecapInput,
        dateLabel: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repo.saveRecapInput(input, dateLabel)
                onSuccess() // ✅ hanya terpanggil kalau Firestore berhasil
            } catch (e: Exception) {
                Log.e("RecapViewModel", "Gagal simpan recap", e)
                onError(e.message ?: "Gagal menyimpan data recap")
            }
        }
    }

    suspend fun hasRecapInput(dateLabel: String): Boolean {
        return repo.hasRecapInput(dateLabel)
    }
}