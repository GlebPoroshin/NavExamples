import com.arkivanov.decompose.ComponentContext
import com.gleb.lemana.task.presentation.screens.main.MainViewModel

class MainTabComponent(
    componentContext: ComponentContext,
    private val onProductSelected: (Int) -> Unit,
    private val viewModel: MainViewModel
) : ComponentContext by componentContext {

    val state = viewModel.state

    fun onProductClick(productId: Int) {
        onProductSelected(productId)
    }

    fun onIntent(intent: MainViewModel.Intent) {
        viewModel.processIntent(intent)
    }
} 