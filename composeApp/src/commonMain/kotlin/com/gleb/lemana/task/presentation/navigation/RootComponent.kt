import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.gleb.lemana.task.presentation.navigation.MainStackComponent
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

    private fun createChild(
        config: Config,
        componentContext: ComponentContext
    ): Child = when (config) {
        is Config.Main -> Child.Main(
            MainStackComponent(
                componentContext = componentContext,
                mainViewModel = mainViewModel,
                productDetailsViewModelFactory = productDetailsViewModelFactory
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
        data object ShoppingList : Config

        @Serializable
        data object Cart : Config
    }

    sealed interface Child {
        data class Main(val component: MainStackComponent) : Child
        data class ShoppingList(val component: ShoppingListComponent) : Child
        data class Cart(val component: CartComponent) : Child
    }
} 
