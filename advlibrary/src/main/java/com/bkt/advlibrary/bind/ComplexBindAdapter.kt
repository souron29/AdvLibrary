package com.bkt.advlibrary.bind

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class ComplexBindAdapter<Value, B : ViewDataBinding>(
    private val layoutId: Int,
    private val onBind: (b: B, item: Value, position: Int) -> Unit,
    itemEquals: (item1: Value, item2: Value) -> Boolean = { p0, p1 -> p0 == p1 }
) : BinderAdapter<Value, B>(layoutId, itemEquals) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BinderHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: B = DataBindingUtil.inflate(inflater, layoutId, parent, false)
        val holder = BinderHolder(binding)
        onCreate(holder)
        return holder
    }

    abstract fun <B : ViewDataBinding> onGenerateBinding(): ViewType<B>

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun onBind(b: B, item: Value, position: Int) {
        onBind.invoke(b, item, position)
    }
}

data class ViewType<B : ViewDataBinding>(
    val layoutId: Int,
    val bindingGenerator: (B) -> Unit
)