import com.arkivanov.decompose.ComponentContext
import com.gleb.lemana.task.presentation.screens.shopping_list.ShoppingListViewModel

class ShoppingListComponent(
    componentContext: ComponentContext,
    private val viewModel: ShoppingListViewModel
) : ComponentContext by componentContext {

    val state = viewModel.state

    fun onIntent(intent: ShoppingListViewModel.Intent) {
        viewModel.processIntent(intent)
    }
} 