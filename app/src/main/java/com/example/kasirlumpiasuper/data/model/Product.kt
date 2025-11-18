package com.example.kasirlumpiasuper.data.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Product(
    val id: String = "",
    val name: String = "",
    val price: Int = 0,
//    val imageRes: Int = 0 // sementara pakai R.drawable.lumper_logo
    val imageUrl: String = ""
)
