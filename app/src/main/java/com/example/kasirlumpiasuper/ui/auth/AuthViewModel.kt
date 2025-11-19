package com.example.kasirlumpiasuper.ui.auth

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kasirlumpiasuper.ui.utils.DataStoreKeys
import com.example.kasirlumpiasuper.ui.utils.datastore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed class AuthState {
    object Loading : AuthState()
    object LoggedOut : AuthState()
    data class LoggedIn(val role: String) : AuthState()
}

class AuthViewModel(private val context: Context) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        if (user == null) {
            Log.d("AuthDebug", "AuthStateListener → LoggedOut")
            viewModelScope.launch {
                val prefs = context.datastore.data.first()
                val savedUid = prefs[DataStoreKeys.User_UID]
                val savedRole = prefs[DataStoreKeys.User_ROLE]

                if (savedUid != null && !savedRole.isNullOrBlank()) {
                    _authState.value = AuthState.LoggedIn(savedRole)

                    fetchUserRole(savedUid)
                } else if (savedUid != null) {
                    fetchUserRole(savedUid)
                } else {
                    _authState.value = AuthState.LoggedOut
                }
            }
        } else {
            Log.d("AuthDebug", "AuthStateListener → LoggedIn, fetch role for ${user.uid}")
            fetchUserRole(user.uid)
        }
    }

    init {
        Log.d("AuthDebug", "AuthViewModel init → attach listener")
        Log.d("AuthDebug", "init: currentUser = ${auth.currentUser?.email}")
        auth.addAuthStateListener(authListener)
    }

    private fun fetchUserRole(uid: String) {
        Log.d("AuthDebug", "fetchUserRole() dipanggil untuk UID: $uid")

        _authState.value = AuthState.Loading

        firestore.collection("users").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val role = doc.getString("role") ?: "kasir"
                    Log.d("AuthDebug", "Firestore success → role: $role")

                    val name = doc.getString("name") ?: "(tidak ada nama)"
                    val email = doc.getString("email") ?: "(tidak ada email)"
                    Log.d(
                        "AuthDebug",
                        "UserData: name=$name, email=$email, role=$role"
                    )
                    _authState.value = AuthState.LoggedIn(role)
                    Log.d("AuthDebug", "_authState diubah ke LoggedIn($role)")
                } else {
                    Log.w("AuthDebug", "Dokumen user tidak ditemukan di Firestore (uid=$uid)")
                    _authState.value = AuthState.LoggedOut
                }
            }
            .addOnFailureListener { e ->
                Log.e("AuthDebug", "Gagal ambil role dari Firestore: ${e.message}", e)
                _authState.value = AuthState.LoggedOut
            }
    }


    fun logoutUser() {
        viewModelScope.launch {
            Log.d("AuthDebug", "Logout user dan bersihkan cache")

            context.datastore.edit { prefs ->
                prefs.remove(DataStoreKeys.User_UID)
                prefs.remove(DataStoreKeys.User_ROLE)
                prefs.remove(DataStoreKeys.User_NAME)
            }

            val currentUser = auth.currentUser
            if (currentUser != null) {
                try {
                    auth.signOut()
                    Log.d("AuthDebug", "FirebaseAuth signOut() berhasil")
                } catch (e: Exception) {
                    Log.e("AuthDebug", "Gagal signOut Firebase: ${e.message}")
                }
            } else {
                Log.w("AuthDebug", "Tidak ada user Firebase untuk signOut, lewati")
            }

            _authState.value = AuthState.LoggedOut
        }
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authListener)
    }
}
