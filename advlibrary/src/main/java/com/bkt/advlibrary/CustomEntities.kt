package com.bkt.advlibrary

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData

class LiveObject<T>(initial: T) : MutableLiveData<T>(initial) {
    var actualValue: T = initial

    override fun getValue(): T {
        return actualValue
    }

    override fun setValue(value: T) {
        this.actualValue = value
        super.setValue(value)
    }

    override fun postValue(value: T) {
        this.actualValue = value
        super.postValue(value)
    }

    fun setValueWithoutNotifying(value: T){
        this.actualValue = value
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