package com.bkt.advlibrary.bind

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.bkt.advlibrary.CommonActivity

abstract class BinderActivity<T : ViewDataBinding>(val id: Int) : CommonActivity() {

    private var _bind: T? = null
    val binding: T
        get() {
            return _bind
                ?: throw NullPointerException("View has not been attached yet. Should be invoked after onCreateView")
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _bind = DataBindingUtil.setContentView(this, id)
        // vm = setProperties(_bind!!)
        //vm.eventListener = this
        //setInternalFunctions()
        initialize()
    }

    /*private fun setInternalFunctions() {
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
    }*/

    abstract fun initialize()
}

