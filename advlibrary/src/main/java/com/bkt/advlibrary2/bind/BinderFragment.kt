package com.bkt.advlibrary.bind

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import com.bkt.advlibrary.CommonFragment
import com.bkt.advlibrary.FragProperties
import com.bkt.advlibrary.popBackStack
import com.bkt.advlibrary.popBackStackImmediate
import java.io.Serializable

abstract class BinderFragment<T : ViewDataBinding, VM : FragBinderModel>() :
    CommonFragment(),
    EventListener {

    constructor(vararg params: Serializable) : this() {
        passArguments(params)
    }

    private var _bind: T? = null
    private var onVmSet = ArrayList<() -> Unit>()
    val binding: T
        get() {
            if (_bind == null)
                throw NullPointerException("View has not been attached yet. Should be invoked after onCreateView")
            return _bind!!
        }

    /**
     * When overriding this method, invoke super method first
     */
    override fun onSetupData() {
        setInternalFunctions()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _bind = DataBindingUtil.inflate(inflater, this.bindProperties.layoutId, container, false)
        _bind!!.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
    }

    /**
     * Must be called from onCreate. This should not include any view/binding related tasks
     * as those are not yet created during onCreate
     */
    private fun setInternalFunctions() {
        vm.eventListener = this
        onVmSet.forEach { it.invoke() }

        vm.popBackStackImmediate.observe(this) { immediate ->
            if (immediate)
                popBackStackImmediate()
            else popBackStack()
        }
        vm.fragment = { this }
        vm.activity = { advActivity }
    }

    abstract fun getFragBindProperties(): FragBindProperties<VM>

    inline fun <reified VM : BinderModel> getModel(): VM {
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
        /*if (this::vm.isInitialized) {
            vm.fragLoad = null
            vm.toast = null
            vm.hide = null
        }*/
        vm.fragLoad = null
        vm.toast = null
        vm.hide = null
    }

    /**
     * Views should not be accessed inside this method
     */
    protected fun afterSettingVM(block: () -> Unit) {
        // vm cannot be accessed from detached fragment
        if (this.isAdded)
            block.invoke()
        else
            onVmSet.add(block)
    }

    override fun onDestroy() {
        super.onDestroy()
        vm.fragment = null
        vm.activity = null
        vm.popBackStackImmediate.removeObservers(this)
    }
}