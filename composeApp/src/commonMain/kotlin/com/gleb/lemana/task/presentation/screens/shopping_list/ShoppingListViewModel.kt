package com.gleb.lemana.task.presentation.screens.shopping_list

import com.gleb.lemana.task.data.database.CartRepository
import com.gleb.lemana.task.data.database.ShoppingListRepository
import com.gleb.lemana.task.domain.model.ProductDomainModel
import com.gleb.lemana.task.domain.service.ProductsService
import com.gleb.lemana.task.presentation.base.BaseViewModel
import io.github.aakira.napier.Napier

class ShoppingListViewModel(
    private val productsService: ProductsService,
    private val shoppingListRepository: ShoppingListRepository,
    private val cartRepository: CartRepository
) : BaseViewModel<ShoppingListViewModel.State, ShoppingListViewModel.Intent>(State.Loading) {

    sealed class State {
        data object Loading : State()
        data class Content(
            val products: List<ProductDomainModel>,
            val selectedItems: Set<Int>
        ) : State()
        data class Error(val message: String) : State()
    }

    sealed class Intent {
        data object LoadShoppingList : Intent()
        data class SelectItem(val productId: Int, val isSelected: Boolean) : Intent()
        data object AddSelectedToCart : Intent()
    }

    private var shoppingListIds: Set<Int> = emptySet()

    init {
        Napier.d(tag = "LemanaApp") { "ShoppingListViewModel init" }
        processIntent(Intent.LoadShoppingList)
    }

    override fun processIntent(intent: Intent) {
        Napier.d(tag = "LemanaApp") { "ShoppingListViewModel processIntent: $intent" }
        when (intent) {
            is Intent.LoadShoppingList -> loadShoppingList()
            is Intent.SelectItem -> selectItem(intent.productId, intent.isSelected)
            is Intent.AddSelectedToCart -> addSelectedItemsToCart()
        }
    }

    private fun loadShoppingList() {
        Napier.d(tag = "LemanaApp") { "ShoppingListViewModel loadShoppingList called" }
        launch {
            updateState(State.Loading)

            shoppingListIds = shoppingListRepository.getAllProductIds()

            if (shoppingListIds.isEmpty()) {
                updateState(State.Content(emptyList(), emptySet()))
                return@launch
            }

            productsService.fetchProductsByIds(shoppingListIds.toList())
                .onSuccess { products ->
                    updateState(State.Content(products, emptySet()))
                }
                .onFailure { _ ->
                    updateState(State.Error("Uuuups something went wrong"))
                }
        }
    }

    private fun selectItem(productId: Int, isSelected: Boolean) {
        val currentState = state.value
        if (currentState is State.Content) {
            val updatedSelectedItems = if (isSelected) {
                currentState.selectedItems + productId
            } else {
                currentState.selectedItems - productId
            }
            updateState(currentState.copy(selectedItems = updatedSelectedItems))
        }
    }

    private fun addSelectedItemsToCart() {
        val currentState = state.value
        if (currentState is State.Content) {
            launch {
                currentState.selectedItems.forEach { productId ->
                    cartRepository.addProductToCart(productId)
                    shoppingListRepository.deleteProductId(productId)
                }
                processIntent(Intent.LoadShoppingList)
            }
        }
    }
}
