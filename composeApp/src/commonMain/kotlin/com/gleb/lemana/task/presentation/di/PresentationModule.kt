package com.gleb.lemana.task.presentation.di

import ProductDetailsViewModel
import com.gleb.lemana.task.presentation.screens.cart.CartViewModel
import com.gleb.lemana.task.presentation.screens.main.MainViewModel
import com.gleb.lemana.task.presentation.screens.shopping_list.ShoppingListViewModel
import org.koin.dsl.module

val presentationModule = module {

    factory {
        MainViewModel(
            cartRepository = get(),
            productsService = get(),
            shoppingListRepository = get()
        )
    }

    factory { (productId: Int) ->
        ProductDetailsViewModel(
            productId = productId,
            productsService = get(),
            cartRepository = get(),
            shoppingListRepository = get()
        )
    }

    factory {
        ShoppingListViewModel(
            cartRepository = get(),
            productsService = get(),
            shoppingListRepository = get()
        )
    }

    factory {
        CartViewModel(
            cartRepository = get(),
            productsService = get(),
        )
    }
}
