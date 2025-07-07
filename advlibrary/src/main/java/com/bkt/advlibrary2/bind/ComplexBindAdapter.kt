package com.bkt.advlibrary.bind

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView


/**
 * Param: [layoutConfig] -> Input is Position. Output should be LayoutId
 */
open class ComplexBindAdapter<Value>(
    private val layoutConfig: (Int, Value) -> Int,
    private val areItemsTheSame: (Value, Value) -> Boolean = { v1, v2 -> v1 == v2 },
    private val areContentsTheSame: (Value, Value) -> Boolean = { v1, v2 -> v1 == v2 },
) : CommonBindAdapter<Value, ComplexBinderHolder<ViewDataBinding>>(object :
    DiffUtil.ItemCallback<Value>() {

    override fun areItemsTheSame(oldItem: Value & Any, newItem: Value & Any): Boolean {
        return areItemsTheSame.invoke(oldItem, newItem)
    }

    override fun areContentsTheSame(oldItem: Value & Any, newItem: Value & Any): Boolean {
        return areContentsTheSame.invoke(oldItem, newItem)
    }
}) {
    // mapping between layout id and binding
    private val layoutToBindMap =
        HashMap<Int, (b: ViewDataBinding, item: Value, position: Int) -> Unit>()

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
     * Should be used to set all the listeners
     */
    final fun setOnViewCreated(onViewCreated: (ComplexBinderHolder<ViewDataBinding>) -> Unit) {
        this.onViewCreated = onViewCreated
    }
}

class ComplexBinderHolder<B : ViewDataBinding>(val binding: B) :
    RecyclerView.ViewHolder(binding.root)
