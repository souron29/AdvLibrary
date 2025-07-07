package com.bkt.advlibrary

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

fun <T> MutableSharedFlow<T>.collectOn(
    owner: LifecycleOwner,
    state: Lifecycle.State = Lifecycle.State.STARTED,
    collector: FlowCollector<T>
) {
    owner.lifecycleScope.launch {
        owner.repeatOnLifecycle(state) {
            this@collectOn.collect(collector)
        }
    }
}

fun <T> Flow<T>.mutableStateIn(scope: CoroutineScope, initialValue: T): MutableStateFlow<T> {
    val f = MutableStateFlow(initialValue)
    scope.launch {
        this@mutableStateIn.collect(f)
    }
    return f
}

fun Flow<CharSequence>.textStateIn(scope: CoroutineScope) = this.mutableStateIn(scope, "")
fun Flow<CharSequence>.textStateIn(model: ViewModel) = this.mutableStateIn(model.viewModelScope, "")