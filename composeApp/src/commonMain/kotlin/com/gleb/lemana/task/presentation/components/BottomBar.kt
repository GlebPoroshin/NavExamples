package com.gleb.lemana.task.presentation.components

import RootComponent
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.gleb.lemana.task.presentation.utils.Colors.onPrimary
import com.gleb.lemana.task.presentation.utils.Colors.primary

@Composable
fun BottomBar(
    selectedTab: RootComponent.Config,
    onTabSelected: (RootComponent.Config) -> Unit,
    modifier: Modifier = Modifier
) {
    BottomNavigation(
        modifier = modifier,
        backgroundColor = primary
    ) {
        bottomBarItems.forEach { item ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        imageVector = when {
                            selectedTab is RootComponent.Config.Main && item.config is RootComponent.Config.Main -> item.filledIcon
                            selectedTab == item.config -> item.filledIcon
                            else -> item.outlinedIcon
                        },
                        contentDescription = null
                    )
                },
                selected = when {
                    selectedTab is RootComponent.Config.Main && item.config is RootComponent.Config.Main -> true
                    selectedTab == item.config -> true
                    else -> false
                },
                onClick = { onTabSelected(item.config) },
                selectedContentColor = onPrimary,
                unselectedContentColor = onPrimary.copy(alpha = 0.6f)
            )
        }
    }
}

private data class BottomBarItem(
    val config: RootComponent.Config,
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector
)

private val bottomBarItems = listOf(
    BottomBarItem(
        config = RootComponent.Config.Main,
        filledIcon = Icons.Filled.Home,
        outlinedIcon = Icons.Outlined.Home
    ),
    BottomBarItem(
        config = RootComponent.Config.ShoppingList,
        filledIcon = Icons.Filled.Favorite,
        outlinedIcon = Icons.Outlined.FavoriteBorder
    ),
    BottomBarItem(
        config = RootComponent.Config.Cart,
        filledIcon = Icons.Filled.ShoppingCart,
        outlinedIcon = Icons.Outlined.ShoppingCart
    )
)
