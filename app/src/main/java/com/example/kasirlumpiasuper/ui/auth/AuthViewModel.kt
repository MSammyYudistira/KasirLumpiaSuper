package com.example.kasirlumpiasuper.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Loading : AuthState()
    object LoggedOut : AuthState()
    data class LoggedIn(val role: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        if (user == null) {
            Log.d("AuthDebug", "No current user → LoggedOut")
            _authState.value = AuthState.LoggedOut
        } else {
            Log.d("AuthDebug", "User detected → fetch role for ${user.uid}")
            fetchUserRole(user.uid)
        }
    }

//    fun checkAuthStatus() {
//        val user = auth.currentUser
//        if (user == null) {
//            _authState.value = AuthState.LoggedOut
//            Log.d("AuthDebug", "CurrentUser: ${auth.currentUser?.uid}")
//        } else {
//            firestore.collection("users").document(user.uid)
//                .get()
//                .addOnSuccessListener { doc ->
//                    val role = doc.getString("role") ?: "kasir"
//                    _authState.value = AuthState.LoggedIn(role)
//                }
//                .addOnFailureListener {
//                    _authState.value = AuthState.LoggedOut
//                }
//        }
//    }

    fun checkAuthStatus() {
        val user = auth.currentUser
        if (user == null) {
            Log.d("AuthDebug", "No current user → LoggedOut")
            _authState.value = AuthState.LoggedOut
        } else {
            Log.d("AuthDebug", "User detected → fetch role for ${user.uid}")
            fetchUserRole(user.uid)
        }
    }

    fun logout() {
        auth.signOut()
        Log.d("AuthDebug", "Logout → LoggedOut")
        _authState.value = AuthState.LoggedOut
    }

    init {
        Log.d("AuthDebug", "AuthViewModel init → attach listener")
        auth.addAuthStateListener(authListener)

        // 🧩 Tambahkan pengecekan cepat untuk currentUser
        val currentUser = auth.currentUser
        if (currentUser != null) {
            Log.d("AuthDebug", "Init restore user: ${currentUser.uid}")
            fetchUserRole(currentUser.uid)
        } else {
            Log.d("AuthDebug", "Init no user → LoggedOut")
            _authState.value = AuthState.LoggedOut
        }
    }

    private fun fetchUserRole(uid: String) {
        firestore.collection("users").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                val role = doc.getString("role") ?: "kasir"
                Log.d("AuthDebug", "User Restored: $uid, role: $role")
                _authState.value = AuthState.LoggedIn(role)
            }
            .addOnFailureListener { e ->
                Log.e("AuthDebug", "Failed fetch role: ${e.message}")
                _authState.value = AuthState.LoggedOut
            }
    }

//    override fun onCleared() {
//        super.onCleared()
//        auth.removeAuthStateListener(authListener)
//    }
}

