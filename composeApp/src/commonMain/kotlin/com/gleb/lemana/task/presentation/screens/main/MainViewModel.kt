package com.gleb.lemana.task.presentation.screens.main

import androidx.lifecycle.viewModelScope
import com.gleb.lemana.task.data.database.CartRepository
import com.gleb.lemana.task.data.database.ShoppingListRepository
import com.gleb.lemana.task.domain.model.ProductDomainModel
import com.gleb.lemana.task.domain.service.ProductsService
import com.gleb.lemana.task.presentation.base.BaseViewModel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch

class MainViewModel(
    private val cartRepository: CartRepository,
    private val productsService: ProductsService,
    private val shoppingListRepository: ShoppingListRepository,
) : BaseViewModel<MainViewModel.State, MainViewModel.Intent>(State.Loading) {

    sealed class State {
        data object Loading : State()
        data class Content(
            val products: List<ProductDomainModel>,
            val navigationState: NavigationState? = null
        ) : State()
        data class Error(val message: String) : State()
    }

    sealed class NavigationState {
        data class ToProductDetails(val productId: Int) : NavigationState()
    }

    sealed class Intent {
        data object LoadProducts : Intent()
        data object LoadMoreProducts : Intent()
        data class AddToShoppingList(val productId: Int) : Intent()
        data class RemoveFromShoppingList(val productId: Int) : Intent()
        data class ChangeInCartCount(val productId: Int, val count: Int) : Intent()
        data class NavigateToProduct(val productId: Int) : Intent()
        data object NavigationHandled : Intent()
    }

    private var currentLimit = 10
    private val pageSize = 10
    var isLoadingMore = false
        private set

    private var cartItems: Map<Int, Int> = emptyMap()
    private var shoppingListIds: Set<Int> = emptySet()

    init {
        processIntent(Intent.LoadProducts)
        observeShoppingListChanges()
        observeCartChanges()
    }

    override fun processIntent(intent: Intent) {
        when (intent) {
            is Intent.NavigateToProduct -> {
                val currentState = state.value
                if (currentState is State.Content) {
                    updateState(currentState.copy(
                        navigationState = NavigationState.ToProductDetails(intent.productId)
                    ))
                }
            }
            is Intent.NavigationHandled -> {
                val currentState = state.value
                if (currentState is State.Content) {
                    updateState(currentState.copy(navigationState = null))
                }
            }
            is Intent.LoadProducts -> loadProducts()
            is Intent.LoadMoreProducts -> loadMoreProducts()
            is Intent.AddToShoppingList -> addToShoppingList(intent.productId)
            is Intent.RemoveFromShoppingList -> removeFromShoppingList(intent.productId)
            is Intent.ChangeInCartCount -> changeInCartCount(intent.productId, intent.count)
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            updateState(State.Loading)
            currentLimit = pageSize

            shoppingListIds = shoppingListRepository.getAllProductIds()
            cartItems = cartRepository.getAllCartItems()

            productsService.fetchAll(limit = currentLimit)
                .onSuccess { products ->
                    val updatedProducts = products.map { product ->
                        product.copy(
                            isLiked = product.id in shoppingListIds,
                            inCartCount = cartItems[product.id] ?: 0
                        )
                    }
                    updateState(State.Content(updatedProducts))
                }
                .onFailure { _ ->
                    updateState(State.Error("Uuuups something went wrong"))
                }
        }
    }

    private fun loadMoreProducts() {
        if (isLoadingMore) return
        isLoadingMore = true
        viewModelScope.launch {
            val currentState = state.value
            if (currentState is State.Content) {
                val currentProducts = currentState.products
                currentLimit += pageSize

                shoppingListIds = shoppingListRepository.getAllProductIds()
                cartItems = cartRepository.getAllCartItems()

                productsService.fetchAll(limit = currentLimit)
                    .onSuccess { allProducts ->
                        val newProducts =
                            allProducts.subList(currentProducts.size, allProducts.size)
                        val updatedNewProducts = newProducts.map { product ->
                            product.copy(
                                isLiked = product.id in shoppingListIds,
                                inCartCount = cartItems[product.id] ?: 0
                            )
                        }
                        if (updatedNewProducts.isNotEmpty()) {
                            updateState(State.Content(currentProducts + updatedNewProducts))
                        }
                        isLoadingMore = false
                    }
                    .onFailure { _ ->
                        isLoadingMore = false
                    }
            } else {
                isLoadingMore = false
            }
        }
    }

    private fun observeShoppingListChanges() {
        Napier.d(tag = "LemanaApp") { "observeShoppingListChanges called" }
        viewModelScope.launch {
            shoppingListIds = shoppingListRepository.getAllProductIds().also { savedShoppingList ->
                Napier.d(tag = "LemanaApp") { "observeShoppingListChanges result = $savedShoppingList" }
            }
            updateProductsIsLiked()
        }
    }

    private fun updateProductsIsLiked() {
        Napier.d(tag = "LemanaApp") { "updateProductsIsLiked called" }
        val currentState = state.value
        if (currentState is State.Content) {
            val updatedProducts = currentState.products.map { product ->
                product.copy(isLiked = product.id in shoppingListIds)
            }
            updateState(
                State.Content(updatedProducts).also { updatedProductList ->
                    Napier.d(tag = "LemanaApp") {
                        "updated liked product list =${updatedProductList.products.map { it.isLiked }}"
                    }
                }
            )
        }
    }

    private fun addToShoppingList(productId: Int) {
        Napier.d(tag = "LemanaApp") { "addToShoppingList id = $productId" }
        viewModelScope.launch {
            shoppingListRepository.insertProductId(productId)
            observeShoppingListChanges()
        }
    }

    private fun removeFromShoppingList(productId: Int) {
        Napier.d(tag = "LemanaApp") { "removeFromShoppingList id = $productId" }
        viewModelScope.launch {
            shoppingListRepository.deleteProductId(productId)
            observeShoppingListChanges()
        }
    }

    private fun observeCartChanges() {
        Napier.d(tag = "LemanaApp") { "observeCartChanges called" }
        viewModelScope.launch {
            cartItems = cartRepository.getAllCartItems().also { dbCartItems ->
                Napier.d(tag = "LemanaApp") {
                    "observeCartChanges saved cart items = $dbCartItems"
                }
            }
            updateProductsInCartCount()
        }
    }

    private fun updateProductsInCartCount() {
        Napier.d(tag = "LemanaApp") { "updateProductsInCartCount called" }
        val currentState = state.value
        if (currentState is State.Content) {
            val updatedProducts = currentState.products.map { product ->
                val count = cartItems[product.id] ?: 0
                product.copy(inCartCount = count)
            }
            updateState(
                State.Content(updatedProducts).also { updatedProductList ->
                    Napier.d(tag = "LemanaApp") {
                        "updated cart count product list = ${updatedProductList.products.map { it.inCartCount }}"
                    }
                }
            )
        }
    }

    private fun changeInCartCount(productId: Int, count: Int) {
        viewModelScope.launch {
            when (count) {
                0 -> {
                    cartRepository.removeProductFromCart(productId)
                }

                1 -> {
                    cartRepository.addProductToCart(productId)
                }

                else -> {
                    cartRepository.updateProductQuantity(productId, count)
                }
            }
            observeCartChanges()
        }
    }
}
