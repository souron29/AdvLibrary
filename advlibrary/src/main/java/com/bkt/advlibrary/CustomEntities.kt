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

    fun setValueWithoutNotifying(value: T){
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

class AdvMediatorLiveObject<Type> : MediatorLiveData<Type>() {
    val currentDataList = java.util.HashMap<Int, Any?>()
    private var counter = 0

    fun <S : Any?> addSources(vararg sources: LiveData<S>) {
        for (source in sources) {
            super.addSource(source) {
                currentDataList[counter] = it
            }
            counter++
        }
    }

    fun <S : Any?> addSource(source: LiveData<S>) {
        super.addSource(source) {
            currentDataList[counter] = it
        }
        counter++
    }

    fun observe(owner: LifecycleOwner, observer: (java.util.HashMap<Int, Any?>, Type) -> Unit) {
        super.observe(owner) {
            observer.invoke(currentDataList, it)
        }
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