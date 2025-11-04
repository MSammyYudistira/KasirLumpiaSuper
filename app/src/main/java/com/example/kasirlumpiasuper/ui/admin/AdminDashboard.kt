//package com.example.kasirlumpiasuper.ui.admin
//
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextButton
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavHostController
//import com.example.kasirlumpiasuper.ui.navigation.NavRoutes
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import kotlinx.coroutines.tasks.await
//
//@Composable
//fun AdminDashboard(modifier: Modifier = Modifier, navController: NavHostController) {
//    val currentUser = FirebaseAuth.getInstance().currentUser
//    val firestore = FirebaseFirestore.getInstance()
//    var role by remember { mutableStateOf<String?>(null) }
//
//    LaunchedEffect(currentUser) {
//        if (currentUser != null) {
//            val doc = firestore.collection("users").document(currentUser.uid).get().await()
//            role = doc.getString("role")
//
//            if (role != "admin") {
//                // 🚫 Bukan admin → arahkan ke Dashboard Kasir
//                navController.navigate(NavRoutes.DashboardKasir.route) {
//                    popUpTo(NavRoutes.DashboardAdmin.route) { inclusive = true }
//                }
//            }
//        }
//    }
//
//    // Selama data role belum didapat → tampilkan loading kecil
//    if (role == null) {
//        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//            CircularProgressIndicator()
//        }
//        return
//    }
//
//    // Kalau role sudah diketahui dan memang admin:
//    if (role == "admin") {
//        Column(
//            modifier.fillMaxSize(),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            Text("Hello Admin!", style = MaterialTheme.typography.headlineMedium)
//            Spacer(Modifier.height(16.dp))
//            TextButton(
//                colors = ButtonDefaults.buttonColors(Color.Red),
//                shape = RoundedCornerShape(8.dp),
//                onClick = {
//                    FirebaseAuth.getInstance().signOut()
//                    navController.navigate(NavRoutes.Login.route) {
//                        popUpTo(NavRoutes.DashboardAdmin.route) { inclusive = true }
//                    }
//                },
//                modifier = Modifier
//                    .padding(horizontal = 32.dp)
//                    .fillMaxWidth(0.5f)
//            ) {
//                Text("Log Out", style = MaterialTheme.typography.titleMedium)
//            }
//        }
//    }
//}
