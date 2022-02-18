package com.bkt.advlibrary.bind

import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.lifecycle.MutableLiveData
import com.bkt.advlibrary.CommonFragment
import com.bkt.advlibrary.LiveObject

open class FragBinderModel : BinderModel() {
    internal val popBackStackImmediate = MutableLiveData<Boolean>()
    internal var fragLoad: ((fragment: CommonFragment, layoutId: Int, onParent: Boolean, addCurrentToStack: Boolean) -> Unit)? =
        null
    internal var toast: ((String, Boolean) -> Unit)? = null
    internal var hide: (() -> Unit)? = null

    fun popBackStackImmediate() {
        popBackStackImmediate.postValue(true)
    }

    fun popBackStack() {
        popBackStackImmediate.postValue(false)
    }

    fun loadChildFragment(childFragment: CommonFragment, @IdRes id: Int) {
        fragLoad?.invoke(childFragment, id, false, true)
    }

    fun replaceChildFragment(childFragment: CommonFragment, @IdRes id: Int) {
        fragLoad?.invoke(childFragment, id, false, false)
    }

    fun loadFragment(fragment: CommonFragment, @IdRes id: Int) {
        fragLoad?.invoke(fragment, id, true, true)
    }

    fun replaceFragment(fragment: CommonFragment, @IdRes id: Int) {
        fragLoad?.invoke(fragment, id, true, false)
    }

    fun toast(text: String, longDuration: Boolean = false) {
        toast?.invoke(text, longDuration)
    }

    fun hideKeyboard() {
        hide?.invoke()
    }
}
