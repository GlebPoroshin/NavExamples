import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.gleb.lemana.task.presentation.screens.main.MainViewModel
import com.gleb.lemana.task.presentation.screens.cart.CartViewModel
import com.gleb.lemana.task.presentation.screens.shopping_list.ShoppingListViewModel
import kotlinx.serialization.Serializable

class RootComponent(
    componentContext: ComponentContext,
    private val mainViewModel: MainViewModel,
    private val productDetailsViewModelFactory: (Int) -> ProductDetailsViewModel,
    private val shoppingListViewModel: ShoppingListViewModel,
    private val cartViewModel: CartViewModel
) : ComponentContext by componentContext {

    val navigation = StackNavigation<Config>()

    val childStack: Value<ChildStack<Config, Child>> = childStack(
        source = navigation,
        serializer = Config.serializer(),
        initialConfiguration = Config.Main,
        handleBackButton = true,
        childFactory = ::createChild
    )

    @OptIn(DelicateDecomposeApi::class)
    private fun createChild(
        config: Config,
        componentContext: ComponentContext
    ): Child = when (config) {
        is Config.Main -> Child.Main(
            MainTabComponent(
                componentContext = componentContext,
                onProductSelected = { productId ->
                    navigation.push(Config.ProductDetails(productId))
                },
                viewModel = mainViewModel
            )
        )
        is Config.ProductDetails -> Child.ProductDetails(
            ProductDetailsComponent(
                componentContext = componentContext,
                productId = config.productId,
                onBack = { navigation.pop() },
                viewModel = productDetailsViewModelFactory(config.productId)
            )
        )
        is Config.ShoppingList -> Child.ShoppingList(
            ShoppingListComponent(
                componentContext = componentContext,
                viewModel = shoppingListViewModel
            )
        )
        else -> Child.Cart(
            CartComponent(
                componentContext = componentContext,
                viewModel = cartViewModel
            )
        )
    }

    @Serializable
    sealed interface Config {
        @Serializable
        data object Main : Config
        
        @Serializable
        data class ProductDetails(val productId: Int) : Config
        
        @Serializable
        data object ShoppingList : Config
        
        @Serializable
        data object Cart : Config
    }

    sealed interface Child {
        data class Main(val component: MainTabComponent) : Child
        data class ProductDetails(val component: ProductDetailsComponent) : Child
        data class ShoppingList(val component: ShoppingListComponent) : Child
        data class Cart(val component: CartComponent) : Child
    }
} 