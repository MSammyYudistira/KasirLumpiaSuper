package com.example.kasirlumpiasuper.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kasirlumpiasuper.R
import com.example.kasirlumpiasuper.domain.model.OrderItem
import com.example.kasirlumpiasuper.ui.theme.Outline
import com.example.kasirlumpiasuper.ui.theme.Primary
import com.example.kasirlumpiasuper.ui.theme.Surface
import com.example.kasirlumpiasuper.ui.transaction.TransactionViewModel

@Composable
fun CustomActionButton(onClicked: () -> Unit, text: String, enabled: Boolean = true) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        shape = RoundedCornerShape(8.dp),
        onClick = onClicked,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled) Primary else Color.Gray.copy(alpha = 0.5f)
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )
    }
}

@Composable
fun AddButtonTransaction(
    item: OrderItem,
    transactionViewModel: TransactionViewModel = viewModel()
) {

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Outline
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(4.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(Surface, CircleShape)
                    .clickable { transactionViewModel.decQty(item) }
            ) {
                Icon(
                    painter = painterResource(R.drawable.outline_minus_24),
                    contentDescription = "Remove"
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Jumlah
            Text(
                text = item.qty.toString(),
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(Surface, CircleShape)
                    .clickable { transactionViewModel.incQty(item) }
            ) {
                Icon(
                    painter = painterResource(R.drawable.outline_add_24),
                    contentDescription = "Add"
                )
            }
        }
    }
}

fun queueLabel(n: Int?): String =
    if (n == null) "---" else n.toString().padStart(3, '0')