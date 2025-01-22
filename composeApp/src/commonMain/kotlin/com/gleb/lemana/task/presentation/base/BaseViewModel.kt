package com.gleb.lemana.task.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<State, Intent>(initialState: State) : ViewModel() {
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    protected fun updateState(newState: State) {
        _state.value = newState
    }

    abstract fun processIntent(intent: Intent)

    protected fun launch(block: suspend () -> Unit) {
        viewModelScope.launch {
            block()
        }
    }
} 