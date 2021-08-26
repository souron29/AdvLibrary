package com.bkt.advlibrary.bind

import android.os.Bundle
import android.os.PersistableBundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.bkt.advlibrary.CommonActivity

abstract class BinderActivity<T : ViewDataBinding>(val id: Int) : CommonActivity(),
    EventListener {
    private lateinit var binder: T

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        binder = DataBindingUtil.setContentView(this, id)
        setProperties(binder)
    }
    abstract fun setProperties(binder: T)
}

