package com.gleb.lemana.task.presentation.components

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
import com.gleb.lemana.task.presentation.navigation.NavigationRoute
import com.gleb.lemana.task.presentation.utils.Colors.onPrimary
import com.gleb.lemana.task.presentation.utils.Colors.primary

@Composable
fun BottomBar(
    selectedTab: NavigationRoute,
    onTabSelected: (NavigationRoute) -> Unit,
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
                        imageVector = if (selectedTab == item.route) {
                            item.filledIcon
                        } else item.outlinedIcon,
                        contentDescription = null
                    )
                },
                selected = selectedTab == item.route,
                onClick = { onTabSelected(item.route) },
                selectedContentColor = onPrimary,
                unselectedContentColor = onPrimary.copy(alpha = 0.6f)
            )
        }
    }
}

private data class BottomBarItem(
    val route: NavigationRoute,
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector
)

private val bottomBarItems = listOf(
    BottomBarItem(
        route = NavigationRoute.Main.ProductList,
        filledIcon = Icons.Filled.Home,
        outlinedIcon = Icons.Outlined.Home
    ),
    BottomBarItem(
        route = NavigationRoute.ShoppingList,
        filledIcon = Icons.Filled.Favorite,
        outlinedIcon = Icons.Outlined.FavoriteBorder
    ),
    BottomBarItem(
        route = NavigationRoute.Cart,
        filledIcon = Icons.Filled.ShoppingCart,
        outlinedIcon = Icons.Outlined.ShoppingCart
    )
)
