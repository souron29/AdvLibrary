package com.bkt.advlibrary2

import android.util.SparseArray
import java.util.*

operator fun <E> SparseArray<E>.set(key: Int, value: E) {
    put(key, value)
}

fun <K> SparseArray<K>.getValueList(): ArrayList<K> {
    val array = ArrayList<K>()
    for (i in 0 until size()) {
        array.add(this[this.keyAt(i)])
    }
    return array
}

fun <K> SparseArray<K>.getKeyList(): ArrayList<Int> {
    val array = ArrayList<Int>()
    for (i in 0 until size()) {
        array.add(keyAt(i))
    }
    return array
}