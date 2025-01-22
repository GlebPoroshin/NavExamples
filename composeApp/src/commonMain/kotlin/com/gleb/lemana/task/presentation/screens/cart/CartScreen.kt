package com.gleb.lemana.task.presentation.screens.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gleb.lemana.task.presentation.components.CartItem
import com.gleb.lemana.task.presentation.components.ErrorDisplayingComponent
import com.gleb.lemana.task.presentation.utils.Colors.primary
import org.koin.compose.koinInject

@Composable
fun CartScreen() {
    val viewModel: CartViewModel = koinInject()
    val state by viewModel.state.collectAsState()

    when (val currentState = state) {
        is CartViewModel.State.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = primary)
            }
        }

        is CartViewModel.State.Content -> {
            val products = currentState.products

            if (products.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.Top)
                ) {
                    item {
                        Text(
                            text = "Cart",
                            style = TextStyle(
                                color = primary,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.padding(vertical = 24.dp)
                        )
                    }
                    items(products) { product ->
                        CartItem(
                            count = product.inCartCount,
                            title = product.title,
                            price = product.price,
                            imageUri = product.image,
                            onCountChange = { count ->
                                viewModel.processIntent(
                                    CartViewModel.Intent.ChangeItemCount(
                                        productId = product.id,
                                        count = count
                                    )
                                )
                            }
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Cart is empty",
                        style = TextStyle(
                            color = primary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        is CartViewModel.State.Error -> {
            ErrorDisplayingComponent(
                message = currentState.message,
                onClick = { viewModel.processIntent(CartViewModel.Intent.LoadCart) }
            )
        }
    }
}
