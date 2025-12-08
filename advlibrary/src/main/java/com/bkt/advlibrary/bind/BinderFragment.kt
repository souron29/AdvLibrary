package com.bkt.advlibrary.bind

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bkt.advlibrary.CommonFragment
import com.bkt.advlibrary.FragProperties
import com.bkt.advlibrary.launchAndRepeat
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.io.Serializable

abstract class BinderFragment<T : ViewDataBinding, VM : FragBinderModel>() : CommonFragment() {

    constructor(vararg params: Serializable) : this() {
        passArguments(params)
    }

    private var _bind: T? = null
    private val fragCreatedAction = MutableSharedFlow<() -> Unit>(0, 100)
    val binding: T
        get() {
            if (_bind == null)
                throw NullPointerException("View has not been attached yet. Should be invoked after onCreateView")
            return _bind!!
        }

    private val bindProperties by lazy { getFragBindProperties() }
    val vm by lazy { this.bindProperties.vm }

    final override fun getFragmentProperties(): FragProperties {
        return FragProperties(this.bindProperties.layoutId, this.bindProperties.name)
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

    final override fun initializeViews() {
        vm.activity = { advActivity }
        viewLifecycleOwner.launchAndRepeat {
            vm.navCommand.collect {
                it.onCommandReceived(this@BinderFragment)
            }
        }
        viewLifecycleOwner.launchAndRepeat(state = Lifecycle.State.CREATED){
            vm.navCommandOnCreate.collect {
                it.onCommandReceived(this@BinderFragment)
            }
        }
        initialize()
    }

    abstract fun initialize()

    /**
     * Must be called from onCreate. This should not include any view/binding related tasks
     * as those are not yet created during onCreate
     */
    private fun setInternalFunctions() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                fragCreatedAction.collect {
                    it.invoke()
                }
            }
        }
        vm.onFragReceivedMutable.tryEmit(this)
        vm.activity = { advActivity }
    }

    abstract fun getFragBindProperties(): FragBindProperties<VM>

    inline fun <reified VM : BinderModel> getModel(): VM {
        val vm by viewModels<VM>()
        return vm
    }

    /**
     * Views should not be accessed inside this method
     */
    protected fun afterSettingVM(block: () -> Unit) {
        // vm cannot be accessed from detached fragment
        if (this.isAdded)
            block.invoke()
        else
            fragCreatedAction.tryEmit(block)
    }

    override fun onDestroy() {
        super.onDestroy()
        vm.activity = null
    }
}

data class FragBindProperties<VM : FragBinderModel>(
    @LayoutRes val layoutId: Int,
    val name: String = "",
    val vm: VM
)
