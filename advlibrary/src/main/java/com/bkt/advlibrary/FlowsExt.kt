package com.bkt.advlibrary

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

fun <T> Flow<T>.collectOn(
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

fun <T> Flow<T>.collectOnView(
    fragment: Fragment,
    state: Lifecycle.State = Lifecycle.State.STARTED,
    collector: FlowCollector<T> = FlowCollector { }
) {
    collectOn(fragment.viewLifecycleOwner, state, collector)
}

fun <T> Flow<T>.mutableStateIn(scope: CoroutineScope, initialValue: T): MutableStateFlow<T> {
    val f = MutableStateFlow(initialValue)
    scope.launch {
        this@mutableStateIn.collect(f)
    }
    return f
}

fun Flow<CharSequence>.textStateIn(
    scope: CoroutineScope,
    started: SharingStarted = SharingStarted.WhileSubscribed(5000)
) = this.stateIn(scope, started, "")

fun <T> Flow<List<T>>.listStateIn(
    scope: CoroutineScope,
    started: SharingStarted = SharingStarted.WhileSubscribed(5000)
) = this.stateIn(scope, started, ArrayList())