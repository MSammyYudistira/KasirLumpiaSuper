package com.example.kasirlumpiasuper.ui.recap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kasirlumpiasuper.data.model.DailyRecap
import com.example.kasirlumpiasuper.data.model.RecapInput
import com.example.kasirlumpiasuper.data.model.StockInputItem
import com.example.kasirlumpiasuper.data.model.StockMeta
import com.example.kasirlumpiasuper.data.repository.RecapRepository
import com.example.kasirlumpiasuper.ui.utils.DateUtils
import com.example.kasirlumpiasuper.ui.utils.RecapUtils
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

    fun load(dateLabel: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val orders = repo.getOrdersByDate(dateLabel)
                val stockItems = repo.getStockItemsByDate(dateLabel)
                val stockMeta = repo.getStockMetaByDate(dateLabel)
                val recapInput = repo.getRecapInputByDate(dateLabel)
//                val prevEnding = repo.getPrevEndingStock(dateLabel)

                val inputs = RecapUtils.Inputs(
                    dateLabel = dateLabel,
                    orders = orders,
                    stockItems = stockItems,
                    stockMeta = stockMeta,
                    recapInput = recapInput
                )

                val dailyRecap = RecapUtils.compute(inputs)

                // 4) Set state
                _recap.value = dailyRecap
            } catch (e: Exception) {
                _error.value = e.message ?: "Gagal memuat rekap"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** Simpan hasil rekap harian ke Firestore */
    fun saveCurrentRecap() {
        val current = _recap.value ?: return
        viewModelScope.launch {
            try {
                repo.saveDailyRecap(current.dateLabel, current)
            } catch (e: Exception) {
                _error.value = "Gagal simpan rekap: ${e.message}"
            }
        }
    }

    fun saveDailyRecap(dateKey: String, recap: DailyRecap) {
        val db = FirebaseFirestore.getInstance()
        db.collection("recaps")
            .document(dateKey)
            .set(recap)
            .addOnSuccessListener {
                // sukses simpan
            }
            .addOnFailureListener {
                // bisa kasih log error
            }
    }

    fun fetchDailyRecap(dateLabel: String, onResult: (DailyRecap?) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("recaps")
            .document(dateLabel)
            .get()
            .addOnSuccessListener { snapshot ->
                val recap = snapshot.toObject(DailyRecap::class.java)
                onResult(recap)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun saveStockInput(
        items: List<StockInputItem>,
        meta: StockMeta,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val dateLabel = DateUtils.getBusinessDateLabel()
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
                repo.saveRecapInput(dateLabel, input)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Gagal menyimpan input recap")
            }
        }
    }

    suspend fun hasRecapInput(dateLabel: String): Boolean {
        return repo.hasRecapInput(dateLabel)
    }

}