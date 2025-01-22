package com.gleb.lemana.task.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gleb.lemana.task.domain.model.ProductDomainModel
import com.gleb.lemana.task.presentation.utils.Colors.primary
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Composable
fun ProductList(
    isLoadingMore: Boolean,
    products: List<ProductDomainModel>,
    onLoadMore: () -> Unit,
    onProductClick: (Int) -> Unit,
    onAddToShoppingList: (Int) -> Unit,
    onRemoveFromShoppingList: (Int) -> Unit,
    onCartCountChange: (Int, Int) -> Unit,
) {
    val gridState = rememberLazyStaggeredGridState()

    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo }
            .map { layoutInfo ->
                val totalItems = layoutInfo.totalItemsCount
                val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.maxOfOrNull { it.index } ?: 0
                lastVisibleItemIndex >= totalItems - 5
            }
            .distinctUntilChanged()
            .collect { shouldLoadMore ->
                if (shouldLoadMore) {
                    onLoadMore()
                }
            }
    }

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        state = gridState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalItemSpacing = 8.dp,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(products) { product ->
            ProductCard(
                price = product.price,
                title = product.title,
                rating = product.rating,
                imageUri = product.image,
                isLiked = product.isLiked,
                description = product.description,
                inCartCount = product.inCartCount,
                onCartCountChanged = { count ->
                    onCartCountChange(product.id, count)
                },
                onIsLikedChanged = { isLiked ->
                    if (isLiked) onAddToShoppingList(product.id)
                    else onRemoveFromShoppingList(product.id)
                },
                modifier = Modifier.clickable {
                    onProductClick(product.id)
                }
            )
        }

        if (isLoadingMore) {
            item(span = StaggeredGridItemSpan.FullLine) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = primary)
                }
            }
        }
    }
}
