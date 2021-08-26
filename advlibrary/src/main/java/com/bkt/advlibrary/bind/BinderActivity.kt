package com.bkt.advlibrary.bind

import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import com.bkt.advlibrary.CommonActivity

abstract class BinderActivity<T : ViewDataBinding, VM : BinderModel>(val id: Int) :
    CommonActivity(),
    EventListener {

    lateinit var binder: T
        private set
    lateinit var vm: VM
        private set

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        binder = DataBindingUtil.setContentView(this, id)
        vm = setProperties(binder)
        vm.eventListener = this
    }

    abstract fun setProperties(binder: T): VM

    inline fun <reified VM : BinderModel> getModel(clazz: Class<VM>): VM {
        val vm by viewModels<VM>()
        return vm
    }
}

