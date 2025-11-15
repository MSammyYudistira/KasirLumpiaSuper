package com.example.kasirlumpiasuper.data.repository

import com.example.kasirlumpiasuper.data.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MenuRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val productsRef = db.collection("products")

    // ==========================
    // GET ALL PRODUCTS
    // ==========================
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

    // ==========================
    // GET SINGLE PRODUCT
    // ==========================
    suspend fun getProductById(id: String): Product? {
        return try {
            val doc = productsRef.document(id).get().await()
            doc.toObject(Product::class.java)?.copy(id = doc.id)
        } catch (e: Exception) {
            null
        }
    }

    // ==========================
    // ADD PRODUCT (AUTO ID)
    // ==========================
    suspend fun addProduct(product: Product): Boolean {
        return try {
            val docRef = productsRef.document()  // auto-generate ID
            val newProduct = product.copy(id = docRef.id)
            docRef.set(newProduct).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // ==========================
    // UPDATE PRODUCT
    // ==========================
    suspend fun updateProduct(product: Product): Boolean {
        return try {
            productsRef.document(product.id).set(product).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // ==========================
    // DELETE PRODUCT (opsional)
    // ==========================
    suspend fun deleteProduct(id: String): Boolean {
        return try {
            productsRef.document(id).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }
}