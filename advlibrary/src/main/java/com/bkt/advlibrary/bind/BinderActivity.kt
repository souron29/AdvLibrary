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

    lateinit var binding: T
        private set
    lateinit var vm: VM
        private set

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        binding = DataBindingUtil.setContentView(this, id)
        vm = setProperties(binding)
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
    }

    abstract fun setProperties(binder: T): VM

    inline fun <reified VM : BinderModel> getModel(clazz: Class<VM>): VM {
        val vm by viewModels<VM>()
        return vm
    }
}

