//package com.example.kasirlumpiasuper.ui.dashboard
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//
//class DashboardViewModelFactory (private val userRole: String) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return DashboardViewModel(userRole = userRole) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
//    }
//}