package com.bkt.advlibrary.bind

import androidx.lifecycle.ViewModel
import com.bkt.advlibrary.CommonActivity

sealed class BinderModel : ViewModel() {
    var activity: (() -> CommonActivity)? = null

    internal lateinit var eventListener: EventListener

    fun publishEvent(event: String, vararg data: Any, onComplete: () -> Unit = {}) {
        eventListener.onEvent(BinderEvent(event, data))
        onComplete.invoke()
    }

    fun getActivity(): CommonActivity? {
        return activity?.invoke()
    }

    fun doNothing() {

    }
}