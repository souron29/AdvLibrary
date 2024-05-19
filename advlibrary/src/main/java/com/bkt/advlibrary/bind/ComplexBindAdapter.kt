package com.bkt.advlibrary.bind

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView


/**
 * Param: [layoutConfig] -> Input is Position. Output should be LayoutId
 */
class ComplexBindAdapter<Value>(
    private val areItemsTheSame: (Value, Value) -> Boolean = { v1, v2 -> v1 == v2 },
    private val areContentsTheSame: (Value, Value) -> Boolean = { v1, v2 -> v1 == v2 },
    private val layoutConfig: (Int, Value) -> Int
) : ListAdapter<Value, ComplexBinderHolder<ViewDataBinding>>(object :
    DiffUtil.ItemCallback<Value>() {

    override fun areItemsTheSame(oldItem: Value & Any, newItem: Value & Any): Boolean {
        return areItemsTheSame.invoke(oldItem, newItem)
    }

    override fun areContentsTheSame(oldItem: Value & Any, newItem: Value & Any): Boolean {
        return areContentsTheSame.invoke(oldItem, newItem)
    }
}), Filterable {
    // mapping between layout id and binding
    private val layoutToBindMap =
        HashMap<Int, (b: ViewDataBinding, item: Value, position: Int) -> Unit>()

    private var dataList = ArrayList<Value>()
    var filterCondition = { _: Value, _: String -> true }

    override fun getItemViewType(position: Int): Int {
        return layoutConfig.invoke(position, getItem(position))
    }

    fun <B : ViewDataBinding> addLayoutBind(
        @LayoutRes layoutId: Int,
        onBind: (b: B, item: Value, position: Int) -> Unit
    ) {
        layoutToBindMap[layoutId] = onBind as (ViewDataBinding, Value, Int) -> Unit
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ComplexBinderHolder<ViewDataBinding> {
        val inflater = LayoutInflater.from(parent.context)
        // viewType is the layoutId in our case
        val binding: ViewDataBinding = DataBindingUtil.inflate(inflater, viewType, parent, false)
        return ComplexBinderHolder(binding)
    }

    override fun onBindViewHolder(holder: ComplexBinderHolder<ViewDataBinding>, position: Int) {
        val item = getItem(position)
        val layoutId = layoutConfig.invoke(position, item)
        layoutToBindMap[layoutId]?.invoke(holder.binding, item, position)
    }

    fun setList(list: List<Value>) {
        val actualList = ArrayList(list)
        submitList(actualList)
        dataList = actualList
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = ArrayList<Value>()
                filteredList.addAll(dataList.filter {
                    constraint.isNullOrEmpty() ||
                            filterCondition.invoke(it, constraint.toString())
                })
                val results = FilterResults()
                results.values = filteredList
                return results
            }

            override fun publishResults(p0: CharSequence?, filterResults: FilterResults?) {
                submitList(filterResults?.values as List<Value>)
            }
        }
    }
}

class ComplexBinderHolder<B : ViewDataBinding>(val binding: B) :
    RecyclerView.ViewHolder(binding.root)
