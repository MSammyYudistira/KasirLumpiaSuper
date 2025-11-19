package com.example.kasirlumpiasuper.ui.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.kasirlumpiasuper.R
import com.example.kasirlumpiasuper.data.model.Product
import com.example.kasirlumpiasuper.ui.components.CustomTopBarWithBackAction
import com.example.kasirlumpiasuper.ui.navigation.NavRoutes
import com.example.kasirlumpiasuper.ui.theme.Outline
import com.example.kasirlumpiasuper.ui.theme.Primary
import com.example.kasirlumpiasuper.ui.theme.Surface

@Composable
fun MenuManagementScreen(
    navController: NavController,
    viewModel: MenuViewModel = viewModel()
) {
    val products by viewModel.products.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadProducts()
    }

    Scaffold(
        topBar = {
            CustomTopBarWithBackAction(
                onBackClick = { navController.popBackStack() },
                title = "Kelola Menu"
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 72.dp, vertical = 24.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.fillMaxSize()
            ) {

                // Produk normal
                items(products) { product ->
                    ProductCardItem(
                        product = product,
                        onClick = {
                            navController.navigate(NavRoutes.MenuDetail.build(product.id))
                        }
                    )
                }

                // Card Tambah Produk
                item {
                    AddProductCard(
                        onClick = {
                            navController.navigate(NavRoutes.MenuDetail.build("new"))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ProductCardItem(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .heightIn(min = 140.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(Surface)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Outline, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {

                AsyncImage(
                    model = if (product.imageUrl.isNotBlank()) product.imageUrl else R.drawable.lumper_logo,
                    contentDescription = null,
                    modifier = Modifier
                        .size(125.dp)
                        .padding(8.dp),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.lumper_logo),
                    error = painterResource(R.drawable.lumper_logo)
                )

            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Rp ${product.price}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun AddProductCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .heightIn(min = 140.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(Surface)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Outline, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_add_24),
                    contentDescription = "Tambah Produk",
                    tint = Primary,
                    modifier = Modifier
                        .size(125.dp)
                        .padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Tambah Produk Baru",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Primary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

