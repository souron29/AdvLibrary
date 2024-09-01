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
open class ComplexBindAdapter<Value>(
    private val layoutConfig: (Int, Value) -> Int,
    private val areItemsTheSame: (Value, Value) -> Boolean = { v1, v2 -> v1 == v2 },
    private val areContentsTheSame: (Value, Value) -> Boolean = { v1, v2 -> v1 == v2 },
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
    private var filterCondition = { _: Value, _: String -> true }
    private var onViewCreated = { _: ComplexBinderHolder<ViewDataBinding> -> }

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

        return ComplexBinderHolder(binding).also { holder ->
            this.onViewCreated.invoke(holder)
        }
    }

    override fun onBindViewHolder(holder: ComplexBinderHolder<ViewDataBinding>, position: Int) {
        val item = getItem(position)
        val layoutId = layoutConfig.invoke(position, item)
        layoutToBindMap[layoutId]?.invoke(holder.binding, item, position)
    }

    /**
     * [submit] - can be false when we do not need to display the data to user immediately
     * e.g. Show data to user on searching at least 3 characters
     */
    fun setList(list: List<Value>, submit: Boolean = true) {
        val actualList = ArrayList<Value>().also {
            it.addAll(list)
        }
        if (submit)
            submitList(actualList)
        dataList = actualList
    }

    /**
     * Should be used to set all the listeners
     */
    final fun setOnViewCreated(onViewCreated: (ComplexBinderHolder<ViewDataBinding>) -> Unit) {
        this.onViewCreated = onViewCreated
    }

    final fun setFilterCondition(filterCondition: (Value, String?) -> Boolean) {
        this.filterCondition = filterCondition
    }

    fun filter(constraint: CharSequence) {
        filter.filter(constraint)
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
