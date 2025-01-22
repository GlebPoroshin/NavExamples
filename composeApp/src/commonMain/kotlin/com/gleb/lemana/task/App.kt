package com.gleb.lemana.task

import ProductDetailsScreen
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.gleb.lemana.task.presentation.components.BottomBar
import com.gleb.lemana.task.presentation.navigation.NavigationRoute
import com.gleb.lemana.task.presentation.screens.cart.CartScreen
import com.gleb.lemana.task.presentation.screens.main.MainScreen
import com.gleb.lemana.task.presentation.screens.shopping_list.ShoppingListScreen
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        Napier.base(DebugAntilog())

        val navController = rememberNavController()
        var selectedTab by remember {
            mutableStateOf<NavigationRoute>(NavigationRoute.Main.ProductList)
        }

        Scaffold(
            bottomBar = {
                BottomBar(
                    selectedTab = selectedTab,
                    onTabSelected = { route ->
                        selectedTab = route
                        navController.navigate(route) {
                            popUpTo(NavigationRoute.Main.ProductList) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = NavigationRoute.Main.ProductList,
                modifier = Modifier.padding(paddingValues)
            ) {
//                navigation<NavigationRoute.Main.ProductList>(
//                    startDestination = NavigationRoute.Main.ProductList
//                ) {
                    composable<NavigationRoute.Main.ProductList> {
                        MainScreen(navController = navController)
                    }
                    composable<NavigationRoute.Main.ProductDetails> { backStackEntry ->
                        val productDetails: NavigationRoute.Main.ProductDetails =
                            backStackEntry.toRoute()

                        ProductDetailsScreen(
                            productId = productDetails.productId,
                            navController = navController
                        )
                    }
//                }
                composable<NavigationRoute.ShoppingList> {
                    ShoppingListScreen()
                }
                composable<NavigationRoute.Cart> {
                    CartScreen()
                }
            }
        }
    }
}
