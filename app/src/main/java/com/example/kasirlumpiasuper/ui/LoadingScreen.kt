package com.example.kasirlumpiasuper.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.kasirlumpiasuper.ui.auth.AuthState
import com.example.kasirlumpiasuper.ui.auth.AuthViewModel
import com.example.kasirlumpiasuper.ui.navigation.NavRoutes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

//@Composable
//fun LoadingScreen(navController: NavHostController) {
//    val context = LocalContext.current
//
//    LaunchedEffect(Unit) {
//        val user = FirebaseAuth.getInstance().currentUser
//        if (user == null) {
//            // belum login
//            navController.navigate(NavRoutes.Login.route) {
//                popUpTo(0)
//            }
//        } else {
//            val uid = FirebaseAuth.getInstance().currentUser?.uid
//            FirebaseFirestore.getInstance().collection("users")
//                .document(uid!!)
//                .get()
//                .addOnSuccessListener { doc ->
//                    val role = doc.getString("role")
//                    if (role == "admin") {
//                        navController.navigate(NavRoutes.DashboardAdmin.route) {
//                            popUpTo(0)
//                        }
//                    } else {
//                        navController.navigate(NavRoutes.DashboardKasir.route) {
//                            popUpTo(0)
//                        }
//                    }
//                }
//                .addOnFailureListener {
//                    Toast.makeText(context, "Gagal ambil role", Toast.LENGTH_SHORT).show()
//                    navController.navigate(NavRoutes.Login.route) {
//                        popUpTo(0)
//                    }
//                }
//        }
//    }
//
//    // UI sementara
//    Box(
//        modifier = Modifier.fillMaxSize(),
//        contentAlignment = Alignment.Center
//    ) {
//        CircularProgressIndicator()
//    }
//}