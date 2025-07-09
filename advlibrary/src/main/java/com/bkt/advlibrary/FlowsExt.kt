package com.bkt.advlibrary

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
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

fun <T> LifecycleOwner.collectFrom(
    flow: Flow<T>,
    state: Lifecycle.State = Lifecycle.State.STARTED,
    collector: FlowCollector<T> = FlowCollector { }
) {
    launch {
        repeatOnLifecycle(state) {
            // The collection below will run when the fragment's view is STARTED (visible)
            // and will be cancelled when the view goes STOPPED, and relaunched when STARTED again.
            flow.collect(collector)
        }
    }
}

fun <T> Fragment.collectFromView(
    flow: Flow<T>,
    state: Lifecycle.State = Lifecycle.State.STARTED,
    collector: FlowCollector<T> = FlowCollector { }
) {
    viewLifecycleOwner.collectFrom(flow, state, collector)
}

fun Flow<CharSequence>.textStateIn(scope: CoroutineScope) = this.mutableStateIn(scope, "")
fun Flow<CharSequence>.textStateIn(model: ViewModel) = this.mutableStateIn(model.viewModelScope, "")
fun <T> Flow<List<T>>.listStateIn(scope: CoroutineScope) = this.mutableStateIn(scope, ArrayList())