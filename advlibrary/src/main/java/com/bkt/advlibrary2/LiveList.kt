package com.bkt.advlibrary2

import androidx.lifecycle.LiveData

class LiveList<K> : LiveData<List<K>>() {
    private var list: MutableList<K> = ArrayList<K>()
    val currentList: List<K>
        get() = list

    private fun post() {
        postValue(list)
    }

    fun add(k: K) {
        list = ArrayList(list)
        list.add(k)
        post()
    }

    fun add(index: Int, k: K) {
        list = ArrayList(list)
        list.add(index, k)
        post()
    }

    fun addAll(collection: Collection<K>) {
        list = ArrayList(list)
        list.addAll(collection)
        post()
    }

    fun addAll(index: Int, collection: Collection<K>) {
        list = ArrayList(list)
        list.addAll(index, collection)
        post()
    }

    fun remove(k: K) {
        list = ArrayList(list)
        list.remove(k)
        post()
    }

    fun removeAt(index: Int) {
        list = ArrayList(list)
        list.removeAt(index)
        post()
    }

    fun replace(list2: MutableList<K>) {
        list = ArrayList(list2)
        post()
    }

    fun clear() {
        list = ArrayList()
        post()
    }

    fun size(): Int {
        return list.size
    }

    fun contains(k: K): Boolean {
        return list.contains(k)
    }
}