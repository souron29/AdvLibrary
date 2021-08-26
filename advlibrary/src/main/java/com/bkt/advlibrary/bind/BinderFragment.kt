package com.bkt.advlibrary.bind

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import com.bkt.advlibrary.CommonFragment

abstract class BinderFragment<T : ViewDataBinding, VM : FragBinderModel>(private val layoutId: Int) :
    CommonFragment("NAME"),
    EventListener {
    lateinit var binding: T
        private set
    lateinit var vm: VM
        private set

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        binding.lifecycleOwner = this
        vm = setProperties(binding)
        vm.eventListener = this
        setInternalFunctions()
        return binding.root
    }

    private fun setInternalFunctions() {
        vm.loadChildFragment.observe(binding.lifecycleOwner!!) {
            loadChildFragment(it.first, it.second)
        }
        vm.popBackStackImmediate.observe(binding.lifecycleOwner!!) { immediate ->
            if (immediate)
                popBackStackImmediate()
            else popBackStack()
        }
        vm.toast.observe(binding.lifecycleOwner!!) {
            toast(it.first, it.second)
        }
    }

    abstract fun setProperties(binder: T): VM

    inline fun <reified VM : BinderModel> getModel(java: Class<VM>): VM {
        val vm by viewModels<VM>()
        return vm
    }
}

