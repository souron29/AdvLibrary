package com.bkt.advlibrary.bind

import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.bkt.advlibrary.CommonActivity

abstract class BinderActivity<T : ViewDataBinding, VM : ActivityBinderModel>(val id: Int) :
    CommonActivity(),
    EventListener {

    private var _bind: T? = null
    val binding get() = _bind!!

    lateinit var vm: VM
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _bind = DataBindingUtil.setContentView(this, id)
        vm = setProperties(_bind!!)
        vm.eventListener = this
        setInternalFunctions()
        initialize()
    }

    private fun setInternalFunctions() {
        vm.loadFragment.observe(this) {
            loadFragment(it.first, it.second)
        }
        vm.popBackStackImmediate.observe(this) { immediate ->
            if (immediate)
                supportFragmentManager.popBackStackImmediate()
            else supportFragmentManager.popBackStack()
        }
        vm.toast.observe(this) {
            toast(it.first, it.second)
        }
        vm.activity = { this }
    }

    abstract fun initialize()
    abstract fun setProperties(binder: T): VM

    inline fun <reified VM : BinderModel> getModel(): VM {
        val vm by viewModels<VM>()
        return vm
    }

    override fun onDestroy() {
        super.onDestroy()
        vm.activity = null

    }

    override fun onEvent(event: BinderEvent) {

    }
}

