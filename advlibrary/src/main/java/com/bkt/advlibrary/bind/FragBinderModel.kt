package com.bkt.advlibrary.bind

import androidx.annotation.LayoutRes
import androidx.lifecycle.MutableLiveData
import com.bkt.advlibrary.CommonFragment

open class FragBinderModel : BinderModel() {
    internal val popBackStackImmediate = MutableLiveData<Boolean>()
    internal val loadChildFragment = MutableLiveData<Pair<CommonFragment, Int>>()
    internal val loadFragment = MutableLiveData<Pair<CommonFragment, Int>>()
    internal val toast = MutableLiveData<Pair<String, Boolean>>()
    internal val hide = MutableLiveData<Unit>()

    fun popBackStackImmediate() {
        popBackStackImmediate.postValue(true)
    }

    fun popBackStack() {
        popBackStackImmediate.postValue(false)
    }

    fun loadChildFragment(childFragment: CommonFragment, @LayoutRes layoutId: Int) {
        loadChildFragment.postValue(Pair(childFragment, layoutId))
    }

    fun loadFragment(fragment: CommonFragment, @LayoutRes layoutId: Int) {
        loadFragment.postValue(Pair(fragment, layoutId))
    }

    fun toast(text: String, longDuration: Boolean = false) {
        toast.postValue(Pair(text, longDuration))
    }

    fun hideKeyboard() {
        hide.postValue(Unit)
    }
}
