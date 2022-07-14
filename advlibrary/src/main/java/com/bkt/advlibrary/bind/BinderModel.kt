package com.bkt.advlibrary.bind

import androidx.lifecycle.ViewModel

sealed class BinderModel : ViewModel() {
    internal lateinit var eventListener: EventListener

    fun publishEvent(event: String, vararg data: Any, onComplete: () -> Unit = {}) {
        eventListener.onEvent(BinderEvent(event, data))
        onComplete.invoke()
    }

    fun doNothing() {

    }
}