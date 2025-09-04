package com.example.kasirlumpiasuper.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kasirlumpiasuper.R
import com.example.kasirlumpiasuper.ui.theme.KasirLumpiaSuperTheme
import androidx.compose.foundation.lazy.grid.items

@Composable
fun AddButton(
    title: String,
) {
    var count by remember { mutableStateOf(0) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(title, style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = Color.LightGray,
                    shape = RoundedCornerShape(8.dp)
                ),
            shape = RoundedCornerShape(8.dp),
            color = Color.Transparent
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Tombol minus
                Box(
                    modifier = Modifier
                        .background(Color(0xFFE0F2FF), shape = RoundedCornerShape(8.dp))
                ) {
                    IconButton(onClick = { if (count > 0) count-- }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_minus_24),
                            contentDescription = "Minus Button",
                            tint = Color(0xFF1565C0) // biru bold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(96.dp))

                // Jumlah
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.width(96.dp))

                // Tombol plus
                Box(
                    modifier = Modifier
                        .background(Color(0xFFE0F2FF), shape = RoundedCornerShape(8.dp))
                ) {
                    IconButton(onClick = { count++ }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_add_24),
                            contentDescription = "Add Button",
                            tint = Color(0xFF1565C0) // biru bold
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StockGridStatic(items: List<String>, modifier: Modifier = Modifier) {
    // Grid non-scroll: 3 item per baris, jarak rapi
    FlowRow(
        maxItemsInEachRow = 3,
        horizontalArrangement = Arrangement.spacedBy(46.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth().padding(24.dp)
    ) {
        items.forEach { title ->
            // Biarkan komponen menentukan lebarnya; FlowRow yang membungkus jadi 3 kolom
            AddButton(title = title)
        }
    }
}

//@Composable
//fun StockGrid(modifier: Modifier = Modifier) {
//    val items = listOf("Lumpia", "Tahu", "Siomay", "Aqua", "Mihun", "Singkong", "Kacang Merah")
//
//    LazyVerticalGrid(
//        columns = GridCells.Fixed(3),
//        modifier = Modifier.padding(16.dp),
//        verticalArrangement = Arrangement.spacedBy(16.dp),
//        horizontalArrangement = Arrangement.spacedBy(16.dp),
//    ) {
//        items(items) { item ->
//            AddButton(title = item)
//        }
//    }
//}

@Preview(showBackground = true)
@Composable
private fun AddButtonPreview() {
    KasirLumpiaSuperTheme {
        AddButton(
            title = "Lumpia"
        )
    }
}