//package com.example.kasirlumpiasuper.ui.components
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxHeight
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Button
//import androidx.compose.material3.Icon
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import com.example.kasirlumpiasuper.R
//import com.example.kasirlumpiasuper.ui.theme.KasirLumpiaSuperTheme
//import com.example.kasirlumpiasuper.ui.theme.Outline
//import com.example.kasirlumpiasuper.ui.theme.PrimaryBold
//import com.example.kasirlumpiasuper.ui.theme.Secondary
//import com.example.kasirlumpiasuper.ui.theme.Success
//
//@Composable
//fun Test(modifier: Modifier = Modifier) {
//    Surface(
//        modifier = Modifier
//            .fillMaxHeight()
//            .padding(vertical = 16.dp),
//        shape = RoundedCornerShape(16.dp),
//        color = Color.White,
//        shadowElevation = 4.dp
//    ) {
//        // Pakai Box untuk overlay
//        Box(Modifier.fillMaxSize()) {
//            // ==== HEADER: full-bleed, tidak kena padding ====
//            val headerHeight = 64.dp
//
//            Surface(
//                color = Secondary,                         // warna background header
//                shape = RoundedCornerShape(
//                    topStart = 16.dp, topEnd = 16.dp,      // samakan dengan parent
//                    bottomStart = 0.dp, bottomEnd = 0.dp
//                ),
//                shadowElevation = 0.dp,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(headerHeight)
//                    .align(Alignment.TopStart)             // tempel ke atas
//            ) {
//                Row(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(horizontal = 16.dp),      // padding hanya di dalam header
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    // contoh ikon kotak (boleh diganti komponenmu sendiri)
//                    Surface(
//                        color = Secondary,
//                        shape = RoundedCornerShape(8.dp),
//                        modifier = Modifier.size(49.dp)
//                    ) {
//                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                            Icon(
//                                painter = painterResource(R.drawable.baseline_menu_book_24),
//                                contentDescription = null,
//                                tint = PrimaryBold,
//                                modifier = Modifier.size(28.dp)
//                            )
//                        }
//                    }
//                    Spacer(Modifier.width(16.dp))
//                    Text(
//                        "Pesanan Nomor #005",
//                        style = MaterialTheme.typography.titleSmall,
//                        fontWeight = FontWeight.Bold
//                    )
//                }
//            }
//
//            // ==== KONTEN: punya padding 16, diturunkan di bawah header ====
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(16.dp)                         // padding untuk seluruh konten
//                    .padding(top = headerHeight + 12.dp)    // beri jarak di bawah header
//            ) {
//                // … dropdown / list keranjang / subtotal / input / tombol dsb …
//                // contoh list cart-mu:
//                val cartItems = listOf(
//                    Triple(painterResource(R.drawable.lumpia_super), "Lumpia Super", 27000),
//                    Triple(painterResource(R.drawable.lumpia_super), "Lumpia Super (Free)", 0),
//                    Triple(painterResource(R.drawable.mihun), "Mihun Goreng", 15000)
//                )
//
//                LazyColumn(
//                    modifier = Modifier.weight(1f),
//                    verticalArrangement = Arrangement.spacedBy(12.dp)
//                ) {
//                    items(cartItems) { (image, name, price) ->
//                        Row(
//                            modifier = Modifier.fillMaxWidth(),
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Box(
//                                modifier = Modifier
//                                    .size(height = 90.dp, width = 110.dp)
//                                    .background(Outline, RoundedCornerShape(8.dp)),
//                                contentAlignment = Alignment.Center,
//                            ) {
//                                Image(
//                                    modifier = Modifier.padding(16.dp),
//                                    painter = image,
//                                    contentDescription = null
//                                )
//                            }
//                            Spacer(Modifier.width(16.dp))
//                            Column(
//                                modifier = Modifier.weight(1f),
//                                horizontalAlignment = Alignment.Start
//                            ) {
//                                Text(name, fontWeight = FontWeight.SemiBold)
//                                Text("Rp. $price", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
//                            }
//                            AddButtonTransaction(
//                                item = item,
//                                transactionViewModel = TODO()
//                            )
//                        }
//                    }
//                }
//
//                Spacer(Modifier.height(12.dp))
//
//                Surface(
//                    modifier = Modifier.fillMaxWidth(),
//                    shadowElevation = 4.dp,
//                    shape = RoundedCornerShape(8.dp)
//                ) {
//                    Column(Modifier.padding(16.dp)) {
//                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
//                            Text("Subtotal", style = MaterialTheme.typography.labelLarge)
//                            Text("Rp 42.000", style = MaterialTheme.typography.labelLarge)
//                        }
//                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
//                            Text("Discount", style = MaterialTheme.typography.labelLarge, color = Success)
//                            Text("Rp 0", style = MaterialTheme.typography.labelLarge, color = Success)
//                        }
//                        Spacer(Modifier.height(8.dp))
//                        OutlinedTextField(
//                            value = "",
//                            onValueChange = {},
//                            label = { Text("Nama customer") },
//                            modifier = Modifier.fillMaxWidth()
//                        )
//                        Spacer(Modifier.height(16.dp))
//                        Button(
//                            onClick = { /* pilih metode pembayaran */ },
//                            modifier = Modifier.fillMaxWidth(),
//                            shape = RoundedCornerShape(12.dp)
//                        ) {
//                            Text("Pilih Metode Pembayaran")
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//private fun TestPrev() {
//    KasirLumpiaSuperTheme {
//        Test()
//    }
//}
//
//
