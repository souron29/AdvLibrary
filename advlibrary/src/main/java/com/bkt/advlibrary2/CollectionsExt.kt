package com.bkt.advlibrary2

import java.math.BigDecimal


class HashList<K, V> : LinkedHashMap<K, ArrayList<V>>() {
    fun addValue(key: K, value: V) {
        val currentList = get(key) ?: ArrayList()
        currentList.add(value)
        put(key, currentList)
    }

    fun addValues(key: K, vararg values: Collection<V>) {
        val list = this[key] ?: ArrayList()
        list.addAll(*values)
        this[key] = list
    }

    fun getList(key: K): ArrayList<V> {
        return this[key] ?: ArrayList()
    }
}

fun <T> List<T>.ifNotEmpty(doOnNotEmpty: (List<T>) -> Unit) {
    if (this.isNotEmpty())
        doOnNotEmpty.invoke(this)
}

fun <Item, Key> List<Item>.toSummationMapOfBigDecimal(generator: (Item) -> Pair<Key, BigDecimal>): HashMap<Key, BigDecimal> {
    val map = HashMap<Key, BigDecimal>()
    forEach { item ->
        val pair = generator.invoke(item)
        var summation = map[pair.first] ?: BigDecimal.ZERO
        summation += pair.second
        map[pair.first] = summation
    }
    return map
}

/**
 * Generic summation method that can be used to get sum of elements of map[K,V]
 * S is the current summation value
 */
fun <K, V, S> Map<K, V>?.sumOfAll(block: (K?, V, S?) -> S): S? {
    var value: S? = null
    this?.forEach { (k, v) -> value = block.invoke(k, v, value) }
    return value
}

/**
 * Finds the sum of the bigdecimal values provided by iterator
 */
fun <K> List<K>.sumOfBig(value: (K) -> BigDecimal?): BigDecimal {
    return sumOf { value.invoke(it) ?: BigDecimal.ZERO }
}

fun <K> List<K>.sumOfStringToBig(value: (K) -> String?): BigDecimal {
    return sumOf { value.invoke(it)?.toBigDecimalOrNull() ?: BigDecimal.ZERO }
}

fun <Item, Key> List<Item>.toBDSummationMap(generator: (Item) -> Pair<Key, BigDecimal>): LinkedHashMap<Key, BigDecimal> {
    val map = LinkedHashMap<Key, BigDecimal>()
    forEach { item ->
        val pair = generator.invoke(item)
        var summation = map[pair.first] ?: BigDecimal.ZERO
        summation += pair.second
        map[pair.first] = summation
    }
    return map
}

class SummationMapBig<K> : LinkedHashMap<K, BigDecimal>() {
    fun addValue(key: K, value: BigDecimal) {
        val currentValue = get(key) ?: BigDecimal.ZERO
        put(key, currentValue + value)
    }

    fun getValue(key: K): BigDecimal {
        return get(key) ?: BigDecimal.ZERO
    }
}

class SummationMapInt<K> : LinkedHashMap<K, Int>() {
    fun addValue(key: K, value: Int) {
        val currentValue = get(key) ?: 0
        put(key, currentValue + value)
    }

    fun addOne(key: K) {
        addValue(key, 1)
    }

    fun getValue(key: K): Int {
        return get(key) ?: 0
    }
}


fun <E, V> Collection<E>.toCollectionOfLists(method: (E) -> Collection<V>): List<V> {
    val newList = ArrayList<V>()
    forEach {
        newList.addAll(method.invoke(it))
    }
    return newList
}

fun <E, V> Collection<E>.toUniqueCollectionOfLists(method: (E) -> Collection<V>): List<V> {
    val newList = HashSet<V>()
    forEach {
        newList.addAll(method.invoke(it))
    }
    return newList.toList()
}

fun <T> ArrayList<T>.addAll(vararg collections: Collection<T>): ArrayList<T> {
    for (collection in collections)
        this.addAll(collection)
    return this
}

infix fun <T> List<T>.moveUp(item: T): MutableList<T> {
    val mutableList = this.toMutableList()
    val currentPosition = mutableList.indexOf(item)
    val newPosition = currentPosition - 1
    if (newPosition < 0 || newPosition > mutableList.lastIndex) {
        return mutableList
    }
    val movedItem = mutableList.removeAt(currentPosition)
    mutableList.add(newPosition, movedItem)
    return mutableList
}

infix fun <T> List<T>.moveDown(item: T): MutableList<T> {
    val mutableList = this.toMutableList()
    val currentPosition = mutableList.indexOf(item)
    val newPosition = currentPosition + 1
    if (newPosition < 0 || newPosition > mutableList.lastIndex) {
        return mutableList
    }
    val movedItem = mutableList.removeAt(currentPosition)
    mutableList.add(newPosition, movedItem)
    return mutableList
}

/**
 * Returns true if Element was Added, else False
 */
fun <E> MutableCollection<E>.addOrRemove(element: E): Boolean {
    val fileRemoved = remove(element)
    if (!fileRemoved)
        add(element)
    return !fileRemoved
}

fun <E> List<E>.toNewList():MutableList<E> = ArrayList<E>().also { it.addAll(this) }