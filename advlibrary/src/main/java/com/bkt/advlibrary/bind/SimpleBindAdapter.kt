package com.bkt.advlibrary.bind

import androidx.databinding.ViewDataBinding

class SimpleBindAdapter<Value : Any, B : ViewDataBinding>(
    layoutId: Int,
    private val onBind: (b: B, item: Value, position: Int) -> Unit,
    itemEquals: (item1: Value, item2: Value) -> Boolean = { p0, p1 -> p0 == p1 }
) : BinderAdapter<Value, B>(layoutId, itemEquals) {

    override fun onBind(b: B, item: Value, position: Int) {
        onBind.invoke(b, item, position)
    }
}