package com.example.kasirlumpiasuper.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class Users(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "kasir",
//    val quote: String = "",
    val profileImageUrl: String = "", // <— WAJIB ada

    @ServerTimestamp
    val createdAt: Timestamp? = null
)
