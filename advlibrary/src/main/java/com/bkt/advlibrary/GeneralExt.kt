package com.bkt.advlibrary

import android.content.Context
import android.icu.text.NumberFormat
import android.util.Log
import android.util.SparseArray
import androidx.collection.ArraySet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap
import kotlin.math.abs

fun <T> getLast(list: List<T>): T {
    return list[list.size - 1]
}

fun <E, V> extract(list: List<E>, onTransform: (E) -> V): List<V> {
    val output = ArrayList<V>()
    for (entry in list) {
        output.add(onTransform.invoke(entry))
    }
    return output
}

fun ifEmpty(text: String, value: String): String {
    return if (text.isBlank()) {
        value
    } else text
}

fun Double.toText(decimalPlaces: Int = this.precision()): String {
    return BigDecimal(this).setScale(decimalPlaces, RoundingMode.HALF_UP).toString()
}

fun Double.precision(): Int {
    val text = abs(this).toString()
    return text.substringAfter(".").length - 1
}

fun String?.toDoubleOr(value: Double): Double {
    return if (this == null || this.isBlank()) {
        value
    } else this.toDouble()
}

fun String.toInt(valueIfNull: Int): Int {
    return if (this.isBlank()) {
        valueIfNull
    } else this.toInt()
}

fun Double.toCurrency(): String {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "in"))
    val text = format.format(this)
    return text.replace(".00", "")
}

fun Any?.isNotNull(): Boolean {
    return this != null
}

fun <VH : RecyclerView.ViewHolder> RecyclerView.setLinearAdapter(
    context: Context,
    adapter: RecyclerView.Adapter<VH>,
    direction: Int = RecyclerView.VERTICAL
) {
    this.adapter = adapter
    this.layoutManager = LinearLayoutManager(context, direction, false)
}

fun <VH : RecyclerView.ViewHolder> RecyclerView.setGridAdapter(
    context: Context,
    adapter: RecyclerView.Adapter<VH>,
    span: Int = 2,
    direction: Int = RecyclerView.VERTICAL
) {
    this.adapter = adapter
    this.layoutManager = GridLayoutManager(context, span, direction, false)
}

fun logger(log_message: Any?, tag: String = "ZTAG") {
    Log.i(tag, log_message?.toString() ?: "null")
    logit(log_message?.toString() ?: "")
}

fun <Item, Key> List<Item>.toMap(key: (Item) -> Key): LinkedHashMap<Key, Item> {
    val map = LinkedHashMap<Key, Item>()
    forEach { map[key.invoke(it)] = it }
    return map
}

fun <Item> List<Item>.toSparseArray(key: (Item) -> Int): SparseArray<Item> {
    val map = SparseArray<Item>()
    forEach { map[key.invoke(it)] = it }
    return map
}

fun <Item> List<List<Item>>.toSimpleList(): ArrayList<Item> {
    val list = ArrayList<Item>()
    forEach { subList ->
        subList.forEach {
            list.add(it)
        }
    }
    return list
}

fun <Item, Key> List<Item>.toHashList(keyGenerator: (Item) -> Key): LinkedHashMap<Key, ArrayList<Item>> {
    val map = LinkedHashMap<Key, ArrayList<Item>>()
    forEach { item ->
        val key = keyGenerator.invoke(item)
        val list = map[key] ?: ArrayList()
        list.add(item)
        map[key] = list
    }
    return map
}

fun <Item, Key> List<Item>.toSummationMap(generator: (Item) -> Pair<Key, Double>): HashMap<Key, Double> {
    val map = HashMap<Key, Double>()
    forEach { item ->
        val pair = generator.invoke(item)
        var summation = map[pair.first] ?: 0.0
        summation += pair.second
        map[pair.first] = summation
    }
    return map
}

fun <Item, Key> List<Item>.distinctValue(distinctBy: (Item) -> Key): List<Key> {
    val list = ArraySet<Key>()
    forEach { item ->
        list.add(distinctBy.invoke(item))
    }
    return list.toList()
}

fun <Item, NewItem> List<Item>.convert(converter: (Item) -> NewItem): List<NewItem> {
    val list = ArrayList<NewItem>()
    forEach { item ->
        list.add(converter.invoke(item))
    }
    return list
}

fun <K, V, T> Map<K, V>.toNewList(converter: (K, V) -> T): ArrayList<T> {
    val list = ArrayList<T>()
    forEach { (key, value) ->
        list.add(converter.invoke(key, value))
    }
    return list
}

fun <K> Map<K, Double>?.sumOfValues(): Double {
    var value = 0.0
    this?.forEach { (_, u) -> value += u }
    return value
}

fun <K> Map<K, Int>?.sumOfValues(): Int {
    var value = 0
    this?.forEach { (_, u) -> value += u }
    return value
}

fun <K, V> Map<K, V>?.sumOf(block: (K, V) -> Double): Double {
    var value = 0.0
    this?.forEach { (k, v) -> value += block.invoke(k, v) }
    return value
}

fun <E, V> E?.evaluate(ifNull: () -> V? = { null }, ifNotNull: (E) -> V): V? {
    return if (this != null)
        ifNotNull.invoke(this)
    else
        ifNull.invoke()

}

fun <E> E?.ifNull(block: () -> Unit): E? {
    if (this == null)
        block.invoke()
    return this
}

fun <E> E?.ifNotNull(block: (E) -> Unit): E? {
    if (this != null)
        block.invoke(this)
    return this
}

fun <T> T.chains(vararg args: T.() -> Unit) {
    for (arg in args)
        arg.invoke(this)
}

fun <T> T.chain(arg: T.() -> Unit): T {
    arg.invoke(this)
    return this
}

fun String?.ifNotEmpty(block: () -> Unit) {
    if (this?.isNotEmpty() == true)
        block.invoke()
}

fun Int.toOrdinal(): String {
    return "$this" + if (this in 11..13) {
        "th"
    } else when (this % 10) {
        1 -> "st"
        2 -> "nd"
        3 -> "rd"
        else -> "th"
    }
}


fun <T> applyToAll(vararg items: T, block: T.() -> Unit) {
    for (item in items)
        block.invoke(item)
}

val internalLog = StringBuilder()
fun logit(text: String) {
    internalLog.append(text).append("\n")
}

fun <T> T?.nvlF(todoIfNull: () -> Unit, todoIfNotNull: () -> Unit) {
    if (this == null)
        todoIfNull.invoke()
    else todoIfNotNull.invoke()
}

inline fun <T> tryOrNull(block: () -> T): T? {
    return try {
        block()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}