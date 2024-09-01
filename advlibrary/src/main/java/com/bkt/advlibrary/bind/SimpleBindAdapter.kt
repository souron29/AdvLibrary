package com.bkt.advlibrary.bind

import androidx.databinding.ViewDataBinding

class SimpleBindAdapter<Value : Any, B : ViewDataBinding>(
    layoutId: Int,
    onBind: (b: B, item: Value, position: Int) -> Unit,
    itemEquals: (item1: Value, item2: Value) -> Boolean = { p0, p1 -> p0 == p1 }
) : ComplexBindAdapter<Value>(layoutConfig = { _, _ -> layoutId }, areItemsTheSame = itemEquals) {

    init {
        addLayoutBind(layoutId, onBind)
    }
}

/*
{

    override fun onBind(b: B, item: Value, position: Int) {
        onBind.invoke(b, item, position)
    }
}*/
