import com.arkivanov.decompose.ComponentContext

class ProductDetailsComponent(
    componentContext: ComponentContext,
    private val productId: Int,
    private val onBack: () -> Unit,
    private val viewModel: ProductDetailsViewModel
) : ComponentContext by componentContext {

    init {
        viewModel.processIntent(ProductDetailsViewModel.Intent.LoadProduct)
    }

    val state = viewModel.state

    fun onIntent(intent: ProductDetailsViewModel.Intent) {
        viewModel.processIntent(intent)
    }

    fun onBackClick() {
        onBack()
    }
} 