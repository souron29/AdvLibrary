package com.bkt.advlibrary.bind

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BinderModel : ViewModel() {
    internal lateinit var eventListener: EventListener

    fun publishEvent(event: String, vararg data: Any, onComplete: () -> Unit) {
        eventListener.onEvent(BinderEvent(event, data))
        onComplete.invoke()
    }


}