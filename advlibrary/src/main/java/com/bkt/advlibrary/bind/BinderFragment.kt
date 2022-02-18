package com.bkt.advlibrary.bind

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import com.bkt.advlibrary.CommonFragment

abstract class BinderFragment<T : ViewDataBinding, VM : FragBinderModel>(
    private val layoutId: Int,
    name: String
) :
    CommonFragment(name),
    EventListener {
    private var _bind: T? = null
    private var onVmSet = ArrayList<() -> Unit>()
    val binding get() = _bind!!

    lateinit var vm: VM
        private set

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _bind = DataBindingUtil.inflate(inflater, layoutId, container, false)
        _bind!!.lifecycleOwner = viewLifecycleOwner
        vm = setProperties(_bind!!)
        onVmSet.forEach { it.invoke() }
        vm.eventListener = this
        setInternalFunctions()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
    }

    private fun setInternalFunctions() {
        vm.popBackStackImmediate.observe(viewLifecycleOwner) { immediate ->
            if (immediate)
                popBackStackImmediate()
            else popBackStack()
        }

    }

    abstract fun setProperties(binder: T): VM

    inline fun <reified VM : BinderModel> getModel(java: Class<VM>): VM {
        val vm by viewModels<VM>()
        return vm
    }

    override fun onEvent(event: BinderEvent) {

    }

    override fun onStart() {
        super.onStart()
        vm.fragLoad =
            { fragment: CommonFragment, layoutId: Int, onParent: Boolean, addCurrentToStack: Boolean ->
                loadFragment(fragment, layoutId, onParent, addCurrentToStack)
            }
        vm.toast = { text: String, longDuration: Boolean ->
            toast(text, longDuration)
        }
        vm.hide = {
            hideKeyboard()
        }
    }

    override fun onStop() {
        super.onStop()
        vm.fragLoad = null
        vm.toast = null
        vm.hide = null
    }

    protected fun afterSettingVM(block: () -> Unit) {
        onVmSet.add(block)
    }
}

