package library.extensions

import android.icu.text.NumberFormat
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import library.AdvActivity
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.pow
import kotlin.math.roundToLong

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

    fun View.hide(makeInvisible: Boolean = false) {
        visibility = if (makeInvisible) {
            View.INVISIBLE
        } else {
            View.GONE
        }
    }

    fun View.show() {
        this.visibility = View.VISIBLE
    }

    fun View.isVisible(): Boolean {
        return this.visibility == View.VISIBLE
    }

    fun <VH : RecyclerView.ViewHolder> RecyclerView.setLinearAdapter(
        activity: AdvActivity,
        adapter: RecyclerView.Adapter<VH>,
        direction: Int = RecyclerView.VERTICAL
    ) {
        this.adapter = adapter
        this.layoutManager = LinearLayoutManager(activity, direction, false)
    }

    fun log(log_message: Any?, tag: String = "ZTAG") {
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

    fun <T> nvl(obj: T?, nonNull: T): T {
        return obj ?: nonNull
    }
}