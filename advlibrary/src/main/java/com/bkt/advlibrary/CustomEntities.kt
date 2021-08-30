package com.bkt.advlibrary

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData

class LiveObject<T>(default: T) : MutableLiveData<T>(default) {

    override fun getValue(): T {
        return super.getValue()!!
    }

    override fun setValue(value: T) {
        super.setValue(value)
    }
}

class MediatorLiveObject<T, M : Any>(vararg objects: LiveObject<M>, private val block: () -> T) :
    MediatorLiveData<T>() {
    init {
        for (obj in objects) {
            addSource(obj) {
                value = block.invoke()
            }
        }
    }

    fun <N> addSource(obj: LiveObject<N>): MediatorLiveObject<T, M> {
        super.addSource(obj) {
            value = block.invoke()
        }
        return this
    }
}