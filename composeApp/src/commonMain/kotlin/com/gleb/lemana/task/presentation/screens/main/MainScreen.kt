package com.gleb.lemana.task.presentation.screens.main

import MainTabComponent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.gleb.lemana.task.presentation.components.ErrorDisplayingComponent
import com.gleb.lemana.task.presentation.components.ProductList
import com.gleb.lemana.task.presentation.utils.Colors.primary

@Composable
fun MainScreen(
    component: MainTabComponent
) {
    val state by component.state.collectAsState()

    when (val currentState = state) {
        is MainViewModel.State.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = primary)
            }
        }
        is MainViewModel.State.Content -> {
            ProductList(
                products = currentState.products,
                onAddToShoppingList = { productId ->
                    component.onIntent(MainViewModel.Intent.AddToShoppingList(productId))
                },
                onRemoveFromShoppingList = { productId ->
                    component.onIntent(MainViewModel.Intent.RemoveFromShoppingList(productId))
                },
                onCartCountChange = { productId, count ->
                    component.onIntent(MainViewModel.Intent.ChangeInCartCount(productId, count))
                },
                onProductClick = { productId ->
                    component.onProductClick(productId)
                },
                onLoadMore = {
                    component.onIntent(MainViewModel.Intent.LoadMoreProducts)
                },
                isLoadingMore = false // TODO: Add this to state
            )
        }
        is MainViewModel.State.Error -> {
            ErrorDisplayingComponent(
                message = currentState.message,
                onClick = { component.onIntent(MainViewModel.Intent.LoadProducts) }
            )
        }
    }
}
