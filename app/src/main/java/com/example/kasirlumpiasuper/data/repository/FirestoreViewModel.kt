package com.example.kasirlumpiasuper.data.repository

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kasirlumpiasuper.data.model.Users
import com.example.kasirlumpiasuper.ui.utils.BusinessDateManager
import com.example.kasirlumpiasuper.ui.utils.DateUtils
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FirestoreViewModel(
    private val repository: FirestoreRepository = FirestoreRepository()
) : ViewModel() {

    private val _user = MutableStateFlow<Users?>(null)
    val user: StateFlow<Users?> = _user

    private val _isStockFilled = MutableStateFlow(false)
    val isStockFilled: StateFlow<Boolean> = _isStockFilled

    fun isStockFilledToday() {
        viewModelScope.launch {
            val today = BusinessDateManager.getBusinessDateLabel()
            _isStockFilled.value = repository.isStockFilled(today)
        }
    }

    fun loadUser() {
        viewModelScope.launch {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                _user.value = repository.getUserData()
            } else {
                Log.d("FirestoreVM", "Lewati loadUser(): User sudah logout atau belum login.")
            }

        }
    }
}