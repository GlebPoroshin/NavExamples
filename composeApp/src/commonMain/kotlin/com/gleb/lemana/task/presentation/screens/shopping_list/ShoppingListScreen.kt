package com.gleb.lemana.task.presentation.screens.shopping_list

import ShoppingListComponent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gleb.lemana.task.presentation.components.ErrorDisplayingComponent
import com.gleb.lemana.task.presentation.components.PrimaryButton
import com.gleb.lemana.task.presentation.components.ShoppingListItem
import com.gleb.lemana.task.presentation.utils.Colors.primary

@Composable
fun ShoppingListScreen(
    component: ShoppingListComponent
) {
    val state by component.state.collectAsState()

    when (val currentState = state) {
        is ShoppingListViewModel.State.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = primary)
            }
        }

        is ShoppingListViewModel.State.Content -> {
            val products = currentState.products
            val selectedItems = currentState.selectedItems

            if (products.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.Top)
                ) {
                    item {
                        Text(
                            text = "Shopping List",
                            style = TextStyle(
                                color = primary,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(vertical = 24.dp)
                        )
                    }

                    items(products) { product ->
                        ShoppingListItem(
                            title = product.title,
                            price = product.price,
                            imageUri = product.image,
                            description = product.description,
                            isSelected = selectedItems.contains(product.id),
                            onSelectedChange = { isSelected ->
                                component.onIntent(
                                    ShoppingListViewModel.Intent.SelectItem(
                                        productId = product.id,
                                        isSelected = isSelected
                                    )
                                )
                            }
                        )
                    }

                    if (selectedItems.isNotEmpty()) {
                        item {
                            PrimaryButton(
                                text = "Add selected to cart",
                                trailingIcon = Icons.Outlined.ShoppingCart,
                                modifier = Modifier.padding(bottom = 6.dp),
                                onClick = {
                                    component.onIntent(ShoppingListViewModel.Intent.AddSelectedToCart)
                                }
                            )
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Shopping list is empty",
                        style = TextStyle(
                            color = primary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        is ShoppingListViewModel.State.Error -> {
            ErrorDisplayingComponent(
                message = currentState.message,
                onClick = { component.onIntent(ShoppingListViewModel.Intent.LoadShoppingList) }
            )
        }
    }
}
