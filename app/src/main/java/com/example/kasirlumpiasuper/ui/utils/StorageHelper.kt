package com.example.kasirlumpiasuper.ui.utils

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

object StorageHelper {
    private val storage = FirebaseStorage.getInstance()

    suspend fun uploadProductImage(productId: String, fileUri: Uri): String {
        val ref = storage.reference.child("products/$productId/image.jpg")
        ref.putFile(fileUri).await()
        return ref.downloadUrl.await().toString()
    }

    suspend fun uploadProfileImage(uid: String, fileUri: Uri): String {
        val ref = storage.reference.child("profile/$uid/profile.jpg")
        ref.putFile(fileUri).await()
        return ref.downloadUrl.await().toString()
    }
}
