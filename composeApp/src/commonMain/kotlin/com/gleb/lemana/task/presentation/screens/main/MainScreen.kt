package com.gleb.lemana.task.presentation.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.gleb.lemana.task.presentation.components.ErrorDisplayingComponent
import com.gleb.lemana.task.presentation.components.ProductList
import com.gleb.lemana.task.presentation.navigation.NavigationRoute
import com.gleb.lemana.task.presentation.utils.Colors.primary
import org.koin.compose.koinInject

@Composable
fun MainScreen(
    navController: NavController
) {
    val viewModel: MainViewModel = koinInject()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state) {
        val currentState = state
        if (currentState is MainViewModel.State.Content) {
            currentState.navigationState?.let { navState ->
                when (navState) {
                    is MainViewModel.NavigationState.ToProductDetails -> {
                        navController.navigate(
                            NavigationRoute.Main.ProductDetails(navState.productId)
                        )
                        viewModel.processIntent(MainViewModel.Intent.NavigationHandled)
                    }
                }
            }
        }
    }

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
                    viewModel.processIntent(MainViewModel.Intent.AddToShoppingList(productId))
                },
                onRemoveFromShoppingList = { productId ->
                    viewModel.processIntent(MainViewModel.Intent.RemoveFromShoppingList(productId))
                },
                onCartCountChange = { productId, count ->
                    viewModel.processIntent(MainViewModel.Intent.ChangeInCartCount(productId, count))
                },
                onProductClick = { productId ->
                    viewModel.processIntent(MainViewModel.Intent.NavigateToProduct(productId))
                },
                onLoadMore = {
                    viewModel.processIntent(MainViewModel.Intent.LoadMoreProducts)
                },
                isLoadingMore = viewModel.isLoadingMore
            )
        }
        is MainViewModel.State.Error -> {
            ErrorDisplayingComponent(
                message = currentState.message,
                onClick = { viewModel.processIntent(MainViewModel.Intent.LoadProducts) }
            )
        }
    }
}
