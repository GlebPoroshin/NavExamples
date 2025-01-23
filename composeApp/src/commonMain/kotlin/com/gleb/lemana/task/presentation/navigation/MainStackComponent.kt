package com.gleb.lemana.task.presentation.navigation

import MainTabComponent
import ProductDetailsComponent
import ProductDetailsViewModel
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.gleb.lemana.task.presentation.screens.main.MainViewModel
import kotlinx.serialization.Serializable

class MainStackComponent(
    componentContext: ComponentContext,
    private val mainViewModel: MainViewModel,
    private val productDetailsViewModelFactory: (Int) -> ProductDetailsViewModel
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<MainConfig>()

    val childStack: Value<ChildStack<MainConfig, MainChild>> = childStack(
        source = navigation,
        serializer = MainConfig.serializer(),
        initialConfiguration = MainConfig.ProductList,
        handleBackButton = true,
        childFactory = ::createChild
    )

    @Serializable
    sealed interface MainConfig {

        @Serializable
        data object ProductList : MainConfig

        @Serializable
        data class ProductDetails(val productId: Int) : MainConfig
    }

    @OptIn(DelicateDecomposeApi::class)
    private fun createChild(
        config: MainConfig,
        componentContext: ComponentContext
    ): MainChild = when (config) {
        is MainConfig.ProductList -> MainChild.ProductList(
            MainTabComponent(
                componentContext = componentContext,
                onProductSelected = { productId ->
                    navigation.push(MainConfig.ProductDetails(productId))
                },
                viewModel = mainViewModel
            )
        )
        is MainConfig.ProductDetails -> MainChild.ProductDetails(
            ProductDetailsComponent(
                componentContext = componentContext,
                productId = config.productId,
                onBack = { navigation.pop() },
                viewModel = productDetailsViewModelFactory(config.productId)
            )
        )
    }

    sealed interface MainChild {
        data class ProductList(val component: MainTabComponent) : MainChild
        data class ProductDetails(val component: ProductDetailsComponent) : MainChild
    }
}