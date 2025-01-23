package com.gleb.lemana.task

import ProductDetailsScreen
import RootComponent
import RootComponent.Child
import RootComponent.Config
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.gleb.lemana.task.presentation.components.BottomBar
import com.gleb.lemana.task.presentation.navigation.MainStackComponent
import com.gleb.lemana.task.presentation.screens.cart.CartScreen
import com.gleb.lemana.task.presentation.screens.main.MainScreen
import com.gleb.lemana.task.presentation.screens.shopping_list.ShoppingListScreen

@OptIn(DelicateDecomposeApi::class)
@Composable
fun App(root: RootComponent) {
    MaterialTheme {
        var selectedTab: Config by remember { mutableStateOf(Config.Main) }

        Scaffold(
            bottomBar = {
                BottomBar(
                    selectedTab = selectedTab,
                    onTabSelected = { config ->
                        if (config != selectedTab) {
                            selectedTab = config
                            val currentStack = root.childStack.value
                            
                            val existingIndex = currentStack.items.indexOfLast { it.configuration == config }
                            
                            if (existingIndex != -1) {
                                repeat(currentStack.items.size - existingIndex - 1) {
                                    root.navigation.pop()
                                }
                            } else {
                                root.navigation.push(config)
                            }
                        }
                    }
                )
            }
        ) { paddingValues ->
            Children(
                stack = root.childStack,
                modifier = Modifier.padding(paddingValues)
            ) { child ->
                when (val instance = child.instance) {
                    is Child.Main -> {
                        Children(
                            stack = instance.component.childStack,
                            animation = stackAnimation(fade() + scale())
                        ) { mainChild ->
                            when (val mainInstance = mainChild.instance) {
                                is MainStackComponent.MainChild.ProductList ->
                                    MainScreen(mainInstance.component)
                                is MainStackComponent.MainChild.ProductDetails ->
                                    ProductDetailsScreen(mainInstance.component)
                            }
                        }
                    }
                    is Child.ShoppingList -> ShoppingListScreen(instance.component)
                    is Child.Cart -> CartScreen(instance.component)
                    else -> { /* Do nothing*/ }
                }
            }
        }
    }
}
