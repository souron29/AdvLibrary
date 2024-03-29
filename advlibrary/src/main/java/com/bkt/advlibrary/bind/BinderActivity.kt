package com.bkt.advlibrary.bind

import android.os.Bundle
import android.os.PersistableBundle
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

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        _bind = DataBindingUtil.setContentView(this, id)
        vm = setProperties(_bind!!)
        vm.eventListener = this
        setInternalFunctions()
    }

    private fun setInternalFunctions() {
        vm.loadFragment.observe(binding.lifecycleOwner!!) {
            loadFragment(it.first, it.second)
        }
        vm.popBackStackImmediate.observe(binding.lifecycleOwner!!) { immediate ->
            if (immediate)
                supportFragmentManager.popBackStackImmediate()
            else supportFragmentManager.popBackStack()
        }
        vm.toast.observe(binding.lifecycleOwner!!) {
            toast(it.first, it.second)
        }
        vm.activity = { this }
    }

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

