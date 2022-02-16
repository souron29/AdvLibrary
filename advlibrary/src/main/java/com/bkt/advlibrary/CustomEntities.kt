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

class MediatorLiveObject<T, M : Any>(
    vararg objects: LiveObject<M>,
    block: (() -> T)? = null
) :
    MediatorLiveData<T>() {
    init {
        for (obj in objects) {
            addLiveSource(obj, block)
        }
    }

    fun <N> addLiveSource(
        obj: LiveObject<N>,
        onChanged: (() -> T)? = null
    ): MediatorLiveObject<T, M> {
        var first = true
        super.addSource(obj) {
            if (!first) {
                value = onChanged?.invoke()
            }
            first = false
        }
        return this
    }
}

class SummationMap<K> : LinkedHashMap<K, Double>() {
    fun addValue(key: K, value: Double) {
        val currentValue = get(key) ?: 0.0
        put(key, currentValue + value)
    }
}

class HashList<K, V> : LinkedHashMap<K, ArrayList<V>>() {
    fun addValue(key: K, value: V) {
        val currentList = get(key) ?: ArrayList()
        currentList.add(value)
        put(key, currentList)
    }
}