package com.gleb.lemana.task

import ProductDetailsScreen
import RootComponent
import RootComponent.Config
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.gleb.lemana.task.presentation.components.BottomBar
import com.gleb.lemana.task.presentation.screens.cart.CartScreen
import com.gleb.lemana.task.presentation.screens.main.MainScreen
import com.gleb.lemana.task.presentation.screens.shopping_list.ShoppingListScreen
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

@Composable
fun App(root: RootComponent) {
    MaterialTheme {
        Napier.base(DebugAntilog())

        var selectedTab: Config by remember { mutableStateOf(Config.Main) }

        Scaffold(
            bottomBar = {
                BottomBar(
                    selectedTab = selectedTab,
                    onTabSelected = { config ->
                        if (config != selectedTab) {
                            selectedTab = config
                            val currentStack = root.childStack.value
                            val currentConfig = currentStack.items.last().configuration
                            if (currentConfig != config) {
                                when (config) {
                                    is Config.Main -> { root.navigation.replaceCurrent(config) }
                                    is Config.ShoppingList -> { root.navigation.replaceCurrent(config) }
                                    is Config.Cart -> { root.navigation.replaceCurrent(config) }
                                    is Config.ProductDetails -> {/* Игнорируем, так как это не таб */ }
                                    else -> {}
                                }
                            }
                        }
                    }
                )
            }
        ) { paddingValues ->
            Children(
                stack = root.childStack,
                modifier = Modifier.padding(paddingValues),
                animation = stackAnimation(fade() + scale())
            ) { child ->
                when (val instance = child.instance) {
                    is RootComponent.Child.Main -> MainScreen(instance.component)
                    is RootComponent.Child.ProductDetails -> ProductDetailsScreen(instance.component)
                    is RootComponent.Child.ShoppingList -> ShoppingListScreen(instance.component)
                    is RootComponent.Child.Cart -> CartScreen(instance.component)
                    else -> {}
                }
            }

            LaunchedEffect(root.childStack.value) {
                val currentConfig = root.childStack.value.items.last().configuration
                if (currentConfig !is Config.ProductDetails) {
                    selectedTab = currentConfig
                }
            }
        }
    }
}
