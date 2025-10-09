package com.example.kasirlumpiasuper.data.repository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kasirlumpiasuper.data.model.Users
import com.example.kasirlumpiasuper.ui.utils.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FirestoreViewModel(
    private val repository: FirestoreRepository = FirestoreRepository()
) : ViewModel() {

    private val _user = MutableStateFlow<Users?>(null)
    val user: StateFlow<Users?> = _user

    private val _quote = MutableStateFlow<String?>(null)
    val quote: StateFlow<String?> = _quote

    private val _isStockFilled = MutableStateFlow(false)
    val isStockFilled: StateFlow<Boolean> = _isStockFilled

    fun isStockFilledToday() {
        viewModelScope.launch {
            val today = DateUtils.getBusinessDateLabel()
            _isStockFilled.value = repository.isStockFilled(today)
        }
    }

    fun loadUser() {
        viewModelScope.launch {
            _user.value = repository.getUserName()
        }
    }

    fun loadQuote() {
        viewModelScope.launch {
            _quote.value = repository.getUserQuote().toString()
        }
    }
}