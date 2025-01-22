package com.gleb.lemana.task.presentation.navigation

import kotlinx.serialization.Serializable

sealed class NavigationRoute {

    sealed class Main : NavigationRoute() {

        @Serializable
        data object ProductList : Main()

        @Serializable
        data class ProductDetails(val productId: Int) : Main()
    }

    @Serializable
    data object ShoppingList : NavigationRoute()

    @Serializable
    data object Cart : NavigationRoute()
}
