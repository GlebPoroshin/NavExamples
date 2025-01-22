import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.gleb.lemana.task.presentation.components.AddToCartComponent
import com.gleb.lemana.task.presentation.components.ErrorDisplayingComponent
import com.gleb.lemana.task.presentation.utils.Colors.primary
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import androidx.navigation.NavController

@Composable
fun ProductDetailsScreen(
    productId: Int,
    navController: NavController
) {
    val viewModel: ProductDetailsViewModel = koinInject { parametersOf(productId) }
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state) {
        val currentState = state
        if (currentState is ProductDetailsViewModel.State.Content) {
            currentState.navigationState?.let { navState ->
                when (navState) {
                    is ProductDetailsViewModel.NavigationState.Back -> {
                        navController.popBackStack()
                        viewModel.processIntent(ProductDetailsViewModel.Intent.NavigationHandled)
                    }
                    else -> { }
                }
            }
        }
    }

    when (val currentState = state) {
        is ProductDetailsViewModel.State.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = primary)
            }
        }

        is ProductDetailsViewModel.State.Content -> {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.processIntent(ProductDetailsViewModel.Intent.NavigateBack) }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
                
                AsyncImage(
                    model = currentState.product.image,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentScale = ContentScale.Fit
                )
                
                // Product Info
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = currentState.product.title,
                        style = MaterialTheme.typography.h5,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = currentState.product.description,
                        style = MaterialTheme.typography.body1
                    )
                    
                    Text(
                        text = "$${currentState.product.price}",
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.Bold,
                        color = primary
                    )
                    
                    // Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (currentState.isInShoppingList) {
                            Button(
                                onClick = { 
                                    viewModel.processIntent(
                                        ProductDetailsViewModel.Intent.RemoveFromShoppingList
                                    )
                                }
                            ) {
                                Text("Remove from Shopping List")
                            }
                        } else {
                            Button(
                                onClick = { 
                                    viewModel.processIntent(
                                        ProductDetailsViewModel.Intent.AddToShoppingList
                                    )
                                }
                            ) {
                                Text("Add to Shopping List")
                            }
                        }

                        AddToCartComponent(
                            inCartCount = currentState.inCartCount,
                            onCartCountChange = { count ->
                                viewModel.processIntent(
                                    ProductDetailsViewModel.Intent.ChangeCartCount(count)
                                )
                            }
                        )
                    }
                }
            }
        }
        
        is ProductDetailsViewModel.State.Error -> {
            ErrorDisplayingComponent(
                message = currentState.message,
                onClick = { viewModel.processIntent(ProductDetailsViewModel.Intent.LoadProduct) }
            )
        }

        else -> {}
    }
} 