package com.bkt.advlibrary.bind

interface EventListener {
    fun onEvent(event: BinderEvent)
}

data class BinderEvent(private val event: String, val data: Any) {
    fun matches(e: String): Boolean {
        return e == event
    }
}