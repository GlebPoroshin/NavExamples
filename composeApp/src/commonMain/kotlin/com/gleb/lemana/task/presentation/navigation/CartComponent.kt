import com.arkivanov.decompose.ComponentContext
import com.gleb.lemana.task.presentation.screens.cart.CartViewModel

class CartComponent(
    componentContext: ComponentContext,
    private val viewModel: CartViewModel
) : ComponentContext by componentContext {

    val state = viewModel.state

    fun onIntent(intent: CartViewModel.Intent) {
        viewModel.processIntent(intent)
    }
} 