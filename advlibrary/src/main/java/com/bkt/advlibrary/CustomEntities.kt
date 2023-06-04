package com.bkt.advlibrary

import androidx.lifecycle.*
import java.util.HashMap

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

    fun setValueWithoutNotifying(value: T) {
        this.actualValue = value
    }

    fun observeAlongWith(
        owner: LifecycleOwner,
        vararg objects: LiveObject<T>,
        observer: () -> Unit
    ) {
        this.observe(owner) {
            observer.invoke()
        }
        objects.forEach {
            it.observe(owner) {
                observer.invoke()
            }
        }
    }
}

class MediatorLiveObject : MediatorLiveData<Unit>() {
    fun <S : Any?> addSources(vararg sources: LiveData<S>) {
        for (source in sources) {
            super.addSource(source) { value = Unit }
        }
    }

    fun <S : Any?> addSource(source: LiveData<S>) {
        super.addSource(source) { value = Unit }
    }

    fun observe(owner: LifecycleOwner, observer: () -> Unit) {
        super.observe(owner) {
            observer.invoke()
        }
    }
}

class CyclicalData<T> {
    private val dataList by lazy { java.util.ArrayList<T>() }
    private var index = -1

    fun add(value: T) {
        dataList.add(value)
    }

    fun remove(value: T) {
        dataList.remove(value)
    }

    fun removeAt(index: Int) {
        dataList.removeAt(index)
    }

    fun reset() {
        index = -1
    }

    fun next(): T? {
        index++
        if (index > dataList.lastIndex)
            index = 0
        return dataList.getOrNull(index)
    }

    fun clear() {
        dataList.clear()
        index = -1
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

fun <K> HashMap<K, Int>.addToKeyValue(key: K, value: Int) {
    val currentValue = get(key) ?: 0
    put(key, currentValue + value)
}

