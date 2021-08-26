package com.bkt.advlibrary.bind

import androidx.lifecycle.ViewModel

open class BinderModel : ViewModel() {
    internal lateinit var eventListener: EventListener

    fun publishEvent(event: String, data: Any? = null, onComplete: () -> Unit) {
        eventListener.onEvent(BinderEvent(event, data))
        onComplete.invoke()
    }
}