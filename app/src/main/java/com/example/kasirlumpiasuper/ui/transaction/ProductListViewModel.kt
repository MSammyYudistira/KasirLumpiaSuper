package com.example.kasirlumpiasuper.ui.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kasirlumpiasuper.data.model.Product
import com.example.kasirlumpiasuper.data.repository.MenuRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductListViewModel(
    private val repository: MenuRepository = MenuRepository()
) : ViewModel() {

    private val _productList = MutableStateFlow<List<Product>>(emptyList())
    val productList: StateFlow<List<Product>> get() = _productList

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            _productList.value = repository.getAllProducts()
        }
    }
}
