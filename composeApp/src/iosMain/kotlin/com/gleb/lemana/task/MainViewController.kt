package com.gleb.lemana.task

import RootComponent
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

fun MainViewController() = ComposeUIViewController {
    val lifecycle = LifecycleRegistry()
    val rootComponent = koinInject<RootComponent> {
        parametersOf(DefaultComponentContext(lifecycle)) 
    }
    App(rootComponent)
}
