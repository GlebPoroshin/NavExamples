package com.gleb.lemana.task.presentation.screens.cart

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.lifecycle.DefaultScreenLifecycleOwner.onDispose
import cafe.adriel.voyager.core.lifecycle.LifecycleEffectOnce
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import com.gleb.lemana.task.data.database.CartRepository
import com.gleb.lemana.task.domain.model.ProductDomainModel
import com.gleb.lemana.task.domain.service.ProductsService
import kotlinx.coroutines.launch

class CartScreenModel(
    private val productsService: ProductsService,
    private val cartRepository: CartRepository
) : StateScreenModel<CartScreenModel.State>(State.Loading) {

    sealed class State {
        object Loading : State()
        data class Content(val products: List<ProductDomainModel>) : State()
        data class Error(val message: String) : State()
    }

    sealed class Intent {
        object LoadCart : Intent()
        data class ChangeItemCount(val productId: Int, val count: Int) : Intent()
        data class RemoveItem(val productId: Int) : Intent()
    }

    init {
        processIntent(Intent.LoadCart)
    }

    fun processIntent(intent: Intent) {
        when (intent) {
            is Intent.LoadCart -> loadCart()
            is Intent.ChangeItemCount -> changeItemCount(intent.productId, intent.count)
            is Intent.RemoveItem -> removeItem(intent.productId)
        }
    }

    private fun loadCart() {
        screenModelScope.launch {
            mutableState.value = State.Loading

            val cartItems = cartRepository.getAllCartItems()

            if (cartItems.isEmpty()) {
                mutableState.value = State.Content(emptyList())
                return@launch
            }

            val productIds = cartItems.keys.toList()

            productsService.fetchProductsByIds(productIds)
                .onSuccess { products ->
                    val updatedProducts = products.map { product ->
                        val count = cartItems[product.id] ?: 0
                        product.copy(inCartCount = count)
                    }
                    mutableState.value = State.Content(updatedProducts)
                }
                .onFailure { _ ->
                    mutableState.value = State.Error("Uuuups something went wrong")
                }
        }
    }

    private fun changeItemCount(productId: Int, count: Int) {
        screenModelScope.launch {
            if (count <= 0) {
                cartRepository.removeProductFromCart(productId)
            } else {
                cartRepository.updateProductQuantity(productId, count)
            }
            loadCart()
        }
    }

    private fun removeItem(productId: Int) {
        screenModelScope.launch {
            cartRepository.removeProductFromCart(productId)
            loadCart()
        }
    }
}
