package library

import androidx.lifecycle.ViewModel
import java.util.*


abstract class DataProvider<VALUE>(private val mapper: (VALUE) -> String = { it -> it.toString() }) :
    ViewModel() {
    private val dataList = ArrayList<VALUE>()
    private val dataMap = HashMap<String, VALUE>()
    private val subscriptions = ArrayList<(MutableList<VALUE>)->Unit>()

    fun getDataList(): List<VALUE> {
        return this.dataList
    }

    /*fun setDataList(list: List<VALUE>) {
        this.dataList.clear()
        this.dataList.addAll(list)
    }*/

    fun getDataMap(): HashMap<String, VALUE> {
        return this.dataMap
    }

    fun setList(list: List<VALUE>) {
        this.dataList.clear()
        this.dataList.addAll(list)
        createMap()
        notifySubscribers()
    }

    fun subscribe(subscriber: (MutableList<VALUE>) -> Unit) {
        this.subscriptions.add(subscriber)
        subscriber.invoke(this.dataList)
    }

    abstract fun onRefresh()

    fun refresh() {
        onRefresh()
    }

    fun addData(value: VALUE, onlyIfAbsent: Boolean) {
        var shouldAdd = true
        if (onlyIfAbsent && this.dataList.contains(value)) {
            shouldAdd = false
        }
        if (shouldAdd) {
            this.dataList.add(value)
        }
        createMap()
        notifySubscribers()
    }

    /*fun addData(newList: List<VALUE?>, onlyIfAbsent: Boolean) {
        if (onlyIfAbsent) {
            CoroutineExtKt.bgBlock(`DataProvider$addData$1`(this, newList, null as Continuation?))
        } else {
            val list: MutableList<VALUE> = this.dataList
            list.addAll(list)
        }
        createMap()
        notifySubscribers()
    }*/

    fun isEmpty(): Boolean {
        return this.dataList.isEmpty()
    }

    fun getValue(code: String): VALUE? {
        return this.dataMap[code]
    }

    private fun createMap() {
        bgLaunch {
            dataMap.clear()
            dataList.forEach { item-> dataMap[mapper.invoke(item)] = item }
        }
    }

    /* access modifiers changed from: protected */
    fun notifySubscribers() {
        for(subscriber in subscriptions)
            subscriber.invoke(this.dataList)
    }

    companion object {
        /*operator fun <T : DataProvider> get(activity: AdvActivity, clazz: Class<T>): T {
            return ViewModelProvider(activity).get(clazz)
        }*/
    }
}