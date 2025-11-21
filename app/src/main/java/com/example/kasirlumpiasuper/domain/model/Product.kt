package com.example.kasirlumpiasuper.domain.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Product(
    val id: String = "",
    val name: String = "",
    val price: Int = 0,
    val imageUrl: String = ""
)
