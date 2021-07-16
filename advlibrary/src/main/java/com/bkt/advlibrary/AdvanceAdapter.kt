package com.bkt.advlibrary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class AdvanceAdapter<Value>(
    private val activity: AdvActivity?,
    @LayoutRes private val layoutId: Int,
    private val areItemsTheSame: (Value, Value) -> Boolean = { v1, v2 -> v1 == v2 },
    private val areContentsTheSame: (Value, Value) -> Boolean = { _, _ -> false }
) : ListAdapter<Value, AdvanceAdapter.AdvanceHolder<Value>>(object :
    DiffUtil.ItemCallback<Value>() {
    override fun areItemsTheSame(oldItem: Value, newItem: Value): Boolean {
        return areItemsTheSame.invoke(oldItem, newItem)
    }

    override fun areContentsTheSame(oldItem: Value, newItem: Value): Boolean {
        return areContentsTheSame.invoke(oldItem, newItem)
    }
}), Filterable {
    private var dataList = ArrayList<Value>()
    /*var onCLick = { _: Value, position: Int, isLongClick: Boolean -> }*/
    abstract fun onBind(view: View, row: RowObject<Value>)

    fun setList(list: List<Value>) {
        val actualList = ArrayList(list)
        submitList(actualList)
        dataList = actualList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdvanceHolder<Value> {
        val view: View = LayoutInflater.from(activity).inflate(layoutId, parent, false)
        val holder = AdvanceHolder(view, RowObject<Value>())
        onCreate(view, holder)
        return holder
    }

    override fun onBindViewHolder(holder: AdvanceHolder<Value>, position: Int) {
        val row = RowObject(position, getItem(position))
        holder.row = row
        val view: View = holder.itemView
        /*view.setOnClickListener { onCLick.invoke(row.currentItem!!, position, false) }
        view.setOnLongClickListener {
            onCLick.invoke(row.currentItem!!, position, true)
            true
        }*/

        onBind(view, row)
    }

    override fun onBindViewHolder(
        holder: AdvanceHolder<Value>, position: Int, payloads: MutableList<Any>
    ) {
        val rowObject = RowObject(position, getItem(position), payloads)
        holder.row = rowObject
        val view: View = holder.itemView
        onBind(view, rowObject)
    }

    open fun onCreate(view: View, holder: AdvanceHolder<Value>) {

    }

    data class AdvanceHolder<Value>(val view: View, var row: RowObject<Value>) :
        RecyclerView.ViewHolder(view)

    data class RowObject<Value>(
        val currentPosition: Int = -1,
        val currentItem: Value? = null,
        val payloads: List<Any> = ArrayList()
    )

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredValues = ArrayList<Value>()
                val results = FilterResults()
                if (constraint?.isNotEmpty() == true) {
                    for ((index, item) in dataList.withIndex()) {
                        if (onFilterItem(item, index, constraint))
                            filteredValues.add(item)
                    }

                    results.values = filteredValues
                    results.count = filteredValues.size
                    return results
                }
                results.values = dataList
                results.count = dataList.size
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                submitList(results?.values as ArrayList<Value>)
            }
        }
    }

    open fun onFilterItem(value: Value, position: Int, filter: CharSequence?): Boolean {
        return true
    }

}