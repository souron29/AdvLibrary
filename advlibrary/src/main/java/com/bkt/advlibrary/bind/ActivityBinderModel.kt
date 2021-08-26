package com.bkt.advlibrary.bind

import androidx.annotation.LayoutRes
import androidx.lifecycle.MutableLiveData
import com.bkt.advlibrary.CommonFragment

open class ActivityBinderModel : BinderModel() {
    internal val popBackStackImmediate = MutableLiveData<Boolean>()
    internal val loadFragment = MutableLiveData<Pair<CommonFragment, Int>>()
    internal val toast = MutableLiveData<Pair<String, Boolean>>()

    fun popBackStackImmediate() {
        popBackStackImmediate.postValue(true)
    }

    fun popBackStack() {
        popBackStackImmediate.postValue(false)
    }

    fun loadFragment(childFragment: CommonFragment, @LayoutRes layoutId: Int) {
        loadFragment.postValue(Pair(childFragment, layoutId))
    }

    fun toast(text: String, longDuration: Boolean = false) {
        toast.postValue(Pair(text,longDuration))
    }
}
