package com.example.kasirlumpiasuper.domain.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class Users(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "kasir",
    val profileImageUrl: String = "",

    @ServerTimestamp
    val createdAt: Timestamp? = null
)
