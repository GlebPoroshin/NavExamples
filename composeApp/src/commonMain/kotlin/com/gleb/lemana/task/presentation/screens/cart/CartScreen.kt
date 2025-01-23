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
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.lifecycle.LifecycleEffectOnce
import cafe.adriel.voyager.core.lifecycle.ScreenLifecycleOwner
import cafe.adriel.voyager.core.lifecycle.ScreenLifecycleProvider
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.koin.koinScreenModel
import com.gleb.lemana.task.presentation.components.CartItem
import com.gleb.lemana.task.presentation.components.ErrorDisplayingComponent
import com.gleb.lemana.task.presentation.utils.Colors.primary

class CartScreenLifecycleOwner : ScreenLifecycleOwner {
    override fun onDispose(screen: Screen) {
        println("CartScreen is being disposed")
    }
}

class CartScreen : Screen, ScreenLifecycleProvider {

    override fun getLifecycleOwner(): ScreenLifecycleOwner {
        return CartScreenLifecycleOwner()
    }

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Content() {
        val screenModel: CartScreenModel = koinScreenModel()
        val state by screenModel.state.collectAsState()

        LifecycleEffectOnce {
            // Do something
        }

        when (val currentState = state) {
            is CartScreenModel.State.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = primary)
                }
            }

            is CartScreenModel.State.Content -> {
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
                                )
                            )
                            Spacer(Modifier.height(32.dp))
                        }
                        items(products, key = { it.id }) { item ->
                            CartItem(
                                price = item.price,
                                title = item.title,
                                imageUri = item.image,
                                count = item.inCartCount,
                                onCountChange = { count ->
                                    screenModel.processIntent(
                                        if (count == 0) {
                                            CartScreenModel.Intent.RemoveItem(
                                                productId = item.id
                                            )
                                        } else {
                                            CartScreenModel.Intent.ChangeItemCount(
                                                productId = item.id,
                                                count = count
                                            )
                                        }
                                    )
                                },
                            )
                        }
                        item { Spacer(Modifier.height(64.dp)) }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            text = "Cart is empty :(",
                            style = TextStyle(
                                color = primary,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
            }

            is CartScreenModel.State.Error -> {
                ErrorDisplayingComponent(
                    message = currentState.message,
                    onClick = {
                        screenModel.processIntent(CartScreenModel.Intent.LoadCart)
                    }
                )
            }
        }
    }
}
