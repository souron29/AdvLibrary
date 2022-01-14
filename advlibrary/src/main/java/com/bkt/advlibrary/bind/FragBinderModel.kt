package com.bkt.advlibrary.bind

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

    fun loadChildFragment(childFragment: CommonFragment, @LayoutRes layoutId: Int) {
        fragLoad?.invoke(childFragment, layoutId, false, true)
    }

    fun replaceChildFragment(childFragment: CommonFragment, @LayoutRes layoutId: Int) {
        fragLoad?.invoke(childFragment, layoutId, false, false)
    }

    fun loadFragment(fragment: CommonFragment, @LayoutRes layoutId: Int) {
        fragLoad?.invoke(fragment, layoutId, true, true)
    }

    fun replaceFragment(fragment: CommonFragment, @LayoutRes layoutId: Int) {
        fragLoad?.invoke(fragment, layoutId, true, false)
    }

    fun toast(text: String, longDuration: Boolean = false) {
        toast?.invoke(text, longDuration)
    }

    fun hideKeyboard() {
        hide?.invoke()
    }
}
