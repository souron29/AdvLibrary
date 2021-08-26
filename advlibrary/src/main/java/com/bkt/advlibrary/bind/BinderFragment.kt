package com.bkt.advlibrary.bind

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import com.bkt.advlibrary.CommonFragment

abstract class BinderFragment<T : ViewDataBinding, VM : BinderModel>(private val layoutId: Int) :
    CommonFragment("NAME"),
    EventListener {
    private lateinit var binder: T
    private lateinit var vm: VM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binder = DataBindingUtil.inflate(inflater, layoutId, container, false)
        vm = setProperties(binder)
        return binder.root
    }

    abstract fun setProperties(binder: T): VM

    inline fun <reified VM : BinderModel> getModel(java: Class<VM>): VM {
        val vm by viewModels<VM>()
        return vm
    }
}
