package com.example.kasirlumpiasuper.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kasirlumpiasuper.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val _user = MutableStateFlow(
        User(
            name = "",
            email = "",
            quote = ""
        )
    )

    val user: StateFlow<User> = _user

    init {
        loadUserFromAuth()
    }


    private fun loadUserFromAuth() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        firebaseUser?.let { user ->
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val name = document.getString("name") ?: ""
                        val email = document.getString("email") ?: user.email ?: ""

                        _user.value = User(
                            name = name,
                            email = email,
                            quote = ""
                        )
                    }
                }
        }
    }

    fun updateQuote(newQuote: String) {
        viewModelScope.launch {
            _user.value = _user.value.copy(quote = newQuote)
        }
    }
}