package com.gleb.lemana.task

import RootComponent
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import platform.UIKit.UIViewController

@Composable
fun MainViewController(): UIViewController {
    val root = DefaultComponentContext(LifecycleRegistry())
    val rootComponent = koinInject<RootComponent> { parametersOf(root) }

    return ComposeUIViewController {
        App(rootComponent)
    }
}