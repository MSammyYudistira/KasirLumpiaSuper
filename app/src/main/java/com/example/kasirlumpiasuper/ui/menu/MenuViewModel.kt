package com.example.kasirlumpiasuper.ui.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kasirlumpiasuper.R
import com.example.kasirlumpiasuper.data.model.Product
import com.example.kasirlumpiasuper.data.repository.MenuRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MenuViewModel(
    private val repository: MenuRepository = MenuRepository()
) : ViewModel() {

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
    fun saveNewProduct(name: String, price: Int) {
        viewModelScope.launch {
            val product = Product(
                id = "",
                name = name,
                price = price,
                imageRes = R.drawable.lumper_logo
            )
            repository.addProduct(product)
            loadProducts() // refresh after save
        }
    }

    // ------------------------------------------------------
    // 🔹 UPDATE EXISTING PRODUCT
    // ------------------------------------------------------
    fun updateProduct(productId: String, name: String, price: Int) {
        viewModelScope.launch {

            val product = Product(
                id = productId,
                name = name,
                price = price,
                imageRes = R.drawable.lumper_logo // sementara static
            )

            repository.updateProduct(product)
            loadProducts() // refresh
        }
    }

    fun deleteProduct(productId: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteProduct(productId)
            loadProducts()
            onDone()    // callback untuk kembali ke screen sebelumnya
        }
    }

}
