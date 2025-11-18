package com.example.kasirlumpiasuper.ui.menu

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kasirlumpiasuper.R
import com.example.kasirlumpiasuper.data.model.Product
import com.example.kasirlumpiasuper.data.repository.MenuRepository
import com.example.kasirlumpiasuper.ui.utils.StorageHelper
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MenuViewModel(
    private val repository: MenuRepository = MenuRepository()
) : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> get() = _products

    private val _currentProduct = MutableStateFlow<Product?>(null)
    val currentProduct: StateFlow<Product?> get() = _currentProduct

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading


    // ------------------------------------------------------
    // 🔹 LOAD ALL PRODUCTS (dipanggil dari MenuManagementScreen)
    // ------------------------------------------------------
    fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            val list = repository.getAllProducts()
            _products.value = list
            _isLoading.value = false
        }
    }

    // ------------------------------------------------------
    // 🔹 LOAD DETAIL PRODUK UNTUK EDIT
    // ------------------------------------------------------
    fun loadProductDetail(productId: String) {
        viewModelScope.launch {
            if (productId == "new") {
                _currentProduct.value = null
                return@launch
            }

            _isLoading.value = true
            _currentProduct.value = repository.getProductById(productId)
            _isLoading.value = false
        }
    }

    // ------------------------------------------------------
    // 🔹 ADD NEW PRODUCT
    // ------------------------------------------------------
    suspend fun saveNewProduct(name: String, price: Int, imageUri: Uri?) {
        val doc = db.collection("products").document()
        val productId = doc.id

        val url = if (imageUri != null) {
            StorageHelper.uploadProductImage(productId, imageUri)
        } else {
            ""  // default local image
        }

        val data = mapOf(
            "id" to productId,
            "name" to name,
            "price" to price,
            "imageUrl" to url
        )

        doc.set(data).await()
    }

    // ------------------------------------------------------
    // 🔹 UPDATE EXISTING PRODUCT
    // ------------------------------------------------------
    suspend fun updateProduct(id: String, name: String, price: Int, imageUri: Uri?) {
        val data = mutableMapOf<String, Any>(
            "name" to name,
            "price" to price
        )

        if (imageUri != null) {
            val url = StorageHelper.uploadProductImage(id, imageUri)
            data["imageUrl"] = url
        }

        db.collection("products").document(id).update(data).await()
    }

    fun deleteProduct(productId: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteProduct(productId)
            loadProducts()
            onDone()    // callback untuk kembali ke screen sebelumnya
        }
    }

}
