import com.gleb.lemana.task.data.database.CartRepository
import com.gleb.lemana.task.data.database.ShoppingListRepository
import com.gleb.lemana.task.domain.model.ProductDomainModel
import com.gleb.lemana.task.domain.service.ProductsService
import com.gleb.lemana.task.presentation.base.BaseViewModel

class ProductDetailsViewModel(
    private val productId: Int,
    private val productsService: ProductsService,
    private val cartRepository: CartRepository,
    private val shoppingListRepository: ShoppingListRepository
) : BaseViewModel<ProductDetailsViewModel.State, ProductDetailsViewModel.Intent>(State.Loading) {

    sealed class State {
        data object Loading : State()
        data class Content(
            val product: ProductDomainModel,
            val isInShoppingList: Boolean,
            val inCartCount: Int,
            val navigationState: NavigationState? = null
        ) : State()
        data class Error(val message: String) : State()
    }

    sealed class NavigationState {
        data object Back : NavigationState()
    }

    sealed class Intent {
        data object LoadProduct : Intent()
        data object AddToShoppingList : Intent()
        data object RemoveFromShoppingList : Intent()
        data class ChangeCartCount(val count: Int) : Intent()
        data object NavigateBack : Intent()
        data object NavigationHandled : Intent()
    }

    init {
        processIntent(Intent.LoadProduct)
    }

    override fun processIntent(intent: Intent) {
        when (intent) {
            is Intent.NavigateBack -> {
                val currentState = state.value
                if (currentState is State.Content) {
                    updateState(currentState.copy(navigationState = NavigationState.Back))
                }
            }
            is Intent.NavigationHandled -> {
                val currentState = state.value
                if (currentState is State.Content) {
                    updateState(currentState.copy(navigationState = null))
                }
            }
            is Intent.LoadProduct -> loadProduct()
            is Intent.AddToShoppingList -> addToShoppingList()
            is Intent.RemoveFromShoppingList -> removeFromShoppingList()
            is Intent.ChangeCartCount -> changeCartCount(intent.count)
            else -> { /* Do nothing */ }
        }
    }

    private fun loadProduct() {
        launch {
            updateState(State.Loading)
            
            productsService.getProduct(productId)
                .onSuccess { product ->
                    val isInShoppingList = shoppingListRepository.getAllProductIds().contains(productId)
                    val cartCount = cartRepository.getAllCartItems()[productId] ?: 0
                    updateState(State.Content(product, isInShoppingList, cartCount))
                }
                .onFailure {
                    updateState(State.Error("Failed to load product"))
                }
        }
    }

    private fun addToShoppingList() {
        launch {
            shoppingListRepository.insertProductId(productId)
            loadProduct()
        }
    }

    private fun removeFromShoppingList() {
        launch {
            shoppingListRepository.deleteProductId(productId)
            loadProduct()
        }
    }

    private fun changeCartCount(count: Int) {
        launch {
            if (count <= 0) {
                cartRepository.removeProductFromCart(productId)
            } else {
                cartRepository.updateProductQuantity(productId, count)
            }
            loadProduct()
        }
    }
} 