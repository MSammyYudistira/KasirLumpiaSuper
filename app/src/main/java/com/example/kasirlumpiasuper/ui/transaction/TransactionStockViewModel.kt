package com.example.kasirlumpiasuper.ui.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kasirlumpiasuper.data.firestore.RecapRepository
import com.example.kasirlumpiasuper.helper.date.BusinessDateManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TransactionStockViewModel(
    private val recapRepository: RecapRepository = RecapRepository()
) : ViewModel() {

    private val _remainingStock = MutableStateFlow<Map<String, Int>>(emptyMap())
    val remainingStock: StateFlow<Map<String, Int>> = _remainingStock

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadTodayStock() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val dateKey = BusinessDateManager.getBusinessDateLabel()
                val map = recapRepository.getRemainingStockByDate(dateKey)
                _remainingStock.value = map
            } finally {
                _isLoading.value = false
            }
        }
    }
}
