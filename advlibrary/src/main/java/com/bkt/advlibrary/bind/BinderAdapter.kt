package com.bkt.advlibrary.bind

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bkt.advlibrary.bgBlock
import com.bkt.advlibrary.mainLaunch

abstract class BinderAdapter<Value, B : ViewDataBinding>(
    @LayoutRes private val layoutId: Int,
    private val areItemsTheSame: (Value, Value) -> Boolean = { v1, v2 -> v1 == v2 },
    private val areContentsTheSame: (Value, Value) -> Boolean = { _, _ -> false }
) : ListAdapter<Value, BinderAdapter<Value, B>.BinderHolder>(object :
    DiffUtil.ItemCallback<Value>() {
    override fun areItemsTheSame(oldItem: Value, newItem: Value): Boolean {
        return areItemsTheSame.invoke(oldItem, newItem)
    }

    override fun areContentsTheSame(oldItem: Value, newItem: Value): Boolean {
        return areContentsTheSame.invoke(oldItem, newItem)
    }
}) {
    private var dataList = ArrayList<Value>()
    var filterCondition = { _: Value, _: String? -> true }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BinderHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: B = DataBindingUtil.inflate(inflater, layoutId, parent, false)
        val holder = BinderHolder(binding)
        onCreate(holder)
        return holder
    }

    override fun onBindViewHolder(holder: BinderHolder, position: Int) {
        val item = getItem(position)
        item?.apply {
            onBind(holder.binding, item, position)
        }
    }

    abstract fun onBind(b: B, item: Value, position: Int)

    open fun onCreate(holder: BinderHolder) {

    }

    fun setList(list: List<Value>) {
        val actualList = ArrayList(list)
        submitList(actualList)
        dataList = actualList
    }

    inner class BinderHolder(val binding: B) :
        RecyclerView.ViewHolder(binding.root)

    fun filter(constraint: String?) {
        bgBlock {
            val list = dataList.filter { filterCondition.invoke(it, constraint) }
            mainLaunch { submitList(list) }
        }
    }

}