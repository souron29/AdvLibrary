package com.bkt.advlibrary.bind

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class BinderAdapter<Value : Any, B : ViewDataBinding>(
    @LayoutRes private val layoutId: Int,
    private val areItemsTheSame: (Value, Value) -> Boolean = { v1, v2 -> v1 == v2 },
    private val areContentsTheSame: (Value, Value) -> Boolean = { _, _ -> false }
) : CommonBindAdapter<Value, BinderHolder<B>>(object :
    DiffUtil.ItemCallback<Value>() {
    override fun areItemsTheSame(oldItem: Value, newItem: Value): Boolean {
        return areItemsTheSame.invoke(oldItem, newItem)
    }

    override fun areContentsTheSame(oldItem: Value, newItem: Value): Boolean {
        return areContentsTheSame.invoke(oldItem, newItem)
    }
}) {
    private var onViewCreated = { _: BinderHolder<B> -> }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BinderHolder<B> {
        val inflater = LayoutInflater.from(parent.context)
        val binding: B = DataBindingUtil.inflate(inflater, layoutId, parent, false)
        val holder = BinderHolder(binding)
        onViewCreated.invoke(holder)
        return holder
    }

    override fun onBindViewHolder(holder: BinderHolder<B>, position: Int) {
        val item = getItem(position)
        item?.apply {
            onBind(holder.binding, item, position)
        }
    }

    abstract fun onBind(b: B, item: Value, position: Int)

    /**
     * Here you should set all the listeners
     */
    final fun setOnViewCreated(onViewCreated: (BinderHolder<B>) -> Unit) {
        this.onViewCreated = onViewCreated
    }
}

class BinderHolder<B : ViewDataBinding>(val binding: B) : RecyclerView.ViewHolder(binding.root)