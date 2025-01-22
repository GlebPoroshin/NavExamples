package com.gleb.lemana.task

import RootComponent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.arkivanov.decompose.DefaultComponentContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf

class MainActivity : ComponentActivity(), KoinComponent {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = DefaultComponentContext(lifecycle = lifecycle)
        val rootComponent = get<RootComponent> { parametersOf(root) }

        setContent {
            App(rootComponent)
        }
    }
}
