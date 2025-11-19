package com.example.kasirlumpiasuper.data.repository

import com.example.kasirlumpiasuper.data.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MenuRepository(
    db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val productsRef = db.collection("products")

    suspend fun getAllProducts(): List<Product> {
        return try {
            val snapshot = productsRef.get().await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Product::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getProductById(id: String): Product? {
        return try {
            val doc = productsRef.document(id).get().await()
            doc.toObject(Product::class.java)?.copy(id = doc.id)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deleteProduct(id: String): Boolean {
        return try {
            productsRef.document(id).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }
}