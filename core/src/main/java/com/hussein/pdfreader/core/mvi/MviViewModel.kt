package com.hussein.pdfreader.core.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class MviViewModel<S, I, E>(initialState: S) : ViewModel() {
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    private val _effect = Channel<E>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    protected val currentState: S
        get() = _state.value

    abstract fun onIntent(intent: I)

    protected fun updateState(reducer: S.() -> S) {
        _state.value = currentState.reducer()
    }

    protected fun sendEffect(effect: E) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}