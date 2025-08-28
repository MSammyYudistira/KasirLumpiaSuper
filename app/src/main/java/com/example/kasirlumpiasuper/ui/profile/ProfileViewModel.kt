package com.example.kasirlumpiasuper.ui.profile

import androidx.lifecycle.ViewModel
import com.example.kasirlumpiasuper.data.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel : ViewModel() {


    private val _user = MutableStateFlow(
        Users(
            name = "",
            email = "",
            quote = ""
        )
    )

    val user: StateFlow<Users> = _user

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
                        val quote = document.getString("quote") ?: ""

                        _user.value = Users(
                            name = name,
                            email = email,
                            quote = quote
                        )
                    }
                }
        }
    }

    fun updateUser(newUser: Users, onResult: (Boolean) -> Unit) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser ?: return

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(firebaseUser.uid)
            .set(newUser)
            .addOnSuccessListener {
                _user.value = newUser
                onResult(true)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                onResult(false)
            }
    }
}

fun logoutUser() {
    val logout = FirebaseAuth.getInstance().signOut()
}