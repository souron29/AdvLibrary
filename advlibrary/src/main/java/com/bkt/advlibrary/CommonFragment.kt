package com.bkt.advlibrary

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner

abstract class CommonFragment(open val fragmentName: String) : Fragment(), LifecycleOwner {
    val stackCount: Int
        get() = if (isAdded) childFragmentManager.backStackEntryCount else 0

    private var onClose = {}
    val advActivity by lazy { activity as CommonActivity }

    fun onClosed(onClose: () -> Unit) {
        this.onClose = onClose
    }

    override fun onDestroy() {
        super.onDestroy()
        this.onClose.invoke()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
    }

    abstract fun initializeViews()


    fun loadChildFragment(
        fragment: CommonFragment,
        id: Int,
        name: String = fragment.fragmentName
    ) {
        childFragmentManager.beginTransaction().replace(id, fragment as Fragment, name)
            .addToBackStack(name).commit()
    }

    fun loadFragment(fragment: CommonFragment, id: Int) {
        advActivity.loadFragment(fragment, id)
    }

    fun hideKeyboard() {
        advActivity.hideKeyboard()
    }

    fun showKeyboard() {
        advActivity.showKeyboard()
    }

    open fun backPressHandled(): Boolean {
        val fragment = childFragmentManager.fragments.lastOrNull() as CommonFragment?
        val handled = fragment?.backPressHandled() ?: false
        if (!handled) {
            if (fragment != null) {
                fragment.popBackStackImmediate()
                return true
            }
        } else {
            return true
        }
        return false
    }

    fun popBackStackImmediate(): Boolean {
        if (!isAdded) {
            return false
        }
        return parentFragmentManager.popBackStackImmediate()
    }

    fun popBackStack() {
        if (isAdded) {
            parentFragmentManager.popBackStack()
        }
    }

    fun popChildFragment() {
        if (isAdded) {
            childFragmentManager.popBackStack()
        }
    }

    fun toast(text: String, longToast: Boolean = true) {
        if (isAdded)
            (activity as CommonActivity).toast(text, longToast)
    }

}