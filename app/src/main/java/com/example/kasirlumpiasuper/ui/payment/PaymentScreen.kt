package com.example.kasirlumpiasuper.ui.payment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.kasirlumpiasuper.ui.components.CustomTopBarWithBackAction

@Composable
fun PaymentScreen(navcontroller: NavHostController) {
    Column {
        CustomTopBarWithBackAction(
            onBackClick = {navcontroller.popBackStack()},
            title = "Detail Pambayaran"
        )
    Text("Hello Payment Screen!", modifier = Modifier.padding(16.dp))
    }
}