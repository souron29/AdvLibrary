package com.bkt.advlibrary

import android.icu.text.NumberFormat
import android.util.Log
import android.util.SparseArray
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.collection.ArraySet
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap

object GeneralExtKt {

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

    fun String.toDouble(value: Double): Double {
        return if (this.isBlank()) {
            value
        } else this.toDouble()
    }

    fun String.toInt(valueIfNull: Int): Int {
        return if (this.isBlank()) {
            valueIfNull
        } else this.toInt()
    }

    fun Double.toText(): String {
        return DecimalFormat("0.##").format(this)
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
        activity: AppCompatActivity,
        adapter: RecyclerView.Adapter<VH>,
        direction: Int = RecyclerView.VERTICAL
    ) {
        this.adapter = adapter
        this.layoutManager = LinearLayoutManager(activity, direction, false)
    }

    fun <VH : RecyclerView.ViewHolder> RecyclerView.setGridAdapter(
        activity: AppCompatActivity,
        adapter: RecyclerView.Adapter<VH>,
        span: Int = 2,
        direction: Int = RecyclerView.VERTICAL
    ) {
        this.adapter = adapter
        this.layoutManager = GridLayoutManager(activity, span, direction, false)
    }

    fun logger(log_message: Any?, tag: String = "ZTAG") {
        Log.i(tag, log_message?.toString() ?: "null")
    }


    /*fun <K, O> QuerySnapshot.toObjectMap(
        qs: QuerySnapshot,
        clazz: Class<O>?,
        primaryKey: Function1<O, K>
    ): HashMap<K, O> {
        Intrinsics.checkParameterIsNotNull(`$this$toObjectMap`, "\$this\$toObjectMap")
        Intrinsics.checkParameterIsNotNull(clazz, "clazz")
        Intrinsics.checkParameterIsNotNull(primaryKey, "primaryKey")
        val hashMap = HashMap<K, O>()
        val it: Iterator<QueryDocumentSnapshot> = `$this$toObjectMap`.iterator()
        while (it.hasNext()) {
            val obj: Any = it.next().toObject(clazz)
            hashMap[primaryKey.invoke(obj)] = obj
        }
        return hashMap
    }

    fun <O> toObjectSparse(
        `$this$toObjectSparse`: QuerySnapshot,
        clazz: Class<O>?,
        primaryKey: Function1<O, Int>
    ): SparseArray<O> {
        Intrinsics.checkParameterIsNotNull(`$this$toObjectSparse`, "\$this\$toObjectSparse")
        Intrinsics.checkParameterIsNotNull(clazz, "clazz")
        Intrinsics.checkParameterIsNotNull(primaryKey, "primaryKey")
        val array: SparseArray<*> = SparseArray<Any?>()
        val it: Iterator<QueryDocumentSnapshot> = `$this$toObjectSparse`.iterator()
        while (it.hasNext()) {
            val obj: Any = it.next().toObject(clazz)
            SparseExtKt.set(array, primaryKey.invoke(obj), obj)
        }
        return array
    }*/

    /*fun <T> observe(
        `$this$observe`: MutableLiveData<T>,
        owner: LifecycleOwner?,
        block: Function1<T?, Unit>?
    ) {
        `$this$observe`.observe(owner, `GeneralExtKt$observe$1`<Any?>(block))
    }*/
    fun <Item, Key> List<Item>.toMap(key: (Item) -> Key): HashMap<Key, Item> {
        val map = HashMap<Key, Item>()
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

}