package com.gleb.lemana.task.presentation.screens.cart

import com.gleb.lemana.task.data.database.CartRepository
import com.gleb.lemana.task.domain.model.ProductDomainModel
import com.gleb.lemana.task.domain.service.ProductsService
import com.gleb.lemana.task.presentation.base.BaseViewModel

class CartViewModel(
    private val productsService: ProductsService,
    private val cartRepository: CartRepository
) : BaseViewModel<CartViewModel.State, CartViewModel.Intent>(State.Loading) {

    sealed class State {
        data object Loading : State()
        data class Content(val products: List<ProductDomainModel>) : State()
        data class Error(val message: String) : State()
    }

    sealed class Intent {
        data object LoadCart : Intent()
        data class ChangeItemCount(val productId: Int, val count: Int) : Intent()
        data class RemoveItem(val productId: Int) : Intent()
    }

    init {
        processIntent(Intent.LoadCart)
    }

    override fun processIntent(intent: Intent) {
        when (intent) {
            is Intent.LoadCart -> loadCart()
            is Intent.ChangeItemCount -> changeItemCount(intent.productId, intent.count)
            is Intent.RemoveItem -> removeItem(intent.productId)
        }
    }

    private fun loadCart() {
        launch {
            updateState(State.Loading)

            val cartItems = cartRepository.getAllCartItems()

            if (cartItems.isEmpty()) {
                updateState(State.Content(emptyList()))
                return@launch
            }

            val productIds = cartItems.keys.toList()

            productsService.fetchProductsByIds(productIds)
                .onSuccess { products ->
                    val updatedProducts = products.map { product ->
                        val count = cartItems[product.id] ?: 0
                        product.copy(inCartCount = count)
                    }
                    updateState(State.Content(updatedProducts))
                }
                .onFailure { _ ->
                    updateState(State.Error("Uuuups something went wrong"))
                }
        }
    }

    private fun changeItemCount(productId: Int, count: Int) {
        launch {
            if (count <= 0) {
                cartRepository.removeProductFromCart(productId)
            } else {
                cartRepository.updateProductQuantity(productId, count)
            }
            loadCart()
        }
    }

    private fun removeItem(productId: Int) {
        launch {
            cartRepository.removeProductFromCart(productId)
            loadCart()
        }
    }
}
