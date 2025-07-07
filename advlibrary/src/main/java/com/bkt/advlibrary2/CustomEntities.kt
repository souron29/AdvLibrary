package com.bkt.advlibrary2

import android.os.Looper
import androidx.lifecycle.*
import java.io.Serializable

class LiveObject<T>(initial: T) : MutableLiveData<T>(initial), Serializable {
    private var actualValue: T = initial

    override fun getValue(): T {
        return actualValue
    }

    /**
     * Make it thread safe
     */
    override fun setValue(value: T) {
        this.actualValue = value
        if (Looper.myLooper() == Looper.getMainLooper())
            super.setValue(value)
        else
            mainLaunch { super.setValue(value) }
    }

    override fun postValue(value: T) {
        this.actualValue = value
        super.postValue(value)
    }

    fun setValueWithoutNotifying(value: T) {
        this.actualValue = value
    }

    override fun toString(): String {
        return "$value"
    }


}

fun <T> LiveData<T>.observeAlongWith(
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

fun <T> LiveData<T>.scan(
    owner: LifecycleOwner,
    ignoreFirstTime: Boolean = true,
    observer: Observer<T>
) {
    var firstTime = true
    this.observe(owner) {
        if (firstTime && ignoreFirstTime) {
            firstTime = false
            return@observe
        }
        observer.onChanged(it)
    }
}

fun <T> LiveData<T>.scan(owner: LifecycleOwner, skipCount: Int = 0, observer: Observer<T>) {
    var currentCount = skipCount
    this.observe(owner) {
        if (currentCount <= 0)
            observer.onChanged(it)
        currentCount--
    }
}

fun <T> LiveData<T>.scanAlongWith(
    owner: LifecycleOwner,
    vararg objects: LiveObject<T>,
    skipCount: Int = 0,
    observer: () -> Unit
) {
    var currentCount = skipCount
    this.observe(owner) {
        if (currentCount <= 0)
            observer.invoke()
        currentCount--
    }
    objects.forEach {
        it.observe(owner) {
            if (currentCount <= 0)
                observer.invoke()
            currentCount--
        }
    }
}

class MediatorLiveObject : MediatorLiveData<Unit>(), Serializable {
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

class CyclicalData<T>(vararg values: T) : Serializable {
    private val dataList by lazy { ArrayList<T>() }
    private var index = -1

    val value: T
        get() = dataList[minOf(maxOf(index, 0), dataList.lastIndex)]

    init {
        if (values.isNotEmpty())
            dataList.addAll(values)
    }

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

fun <K> HashMap<K, Int>.addToKeyValue(key: K, value: Int) {
    val currentValue = get(key) ?: 0
    put(key, currentValue + value)
}