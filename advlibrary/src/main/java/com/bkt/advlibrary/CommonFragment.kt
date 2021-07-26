package com.bkt.advlibrary

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.viewpager2.widget.ViewPager2

abstract class CommonFragment(open val fragmentName: String) : Fragment(), LifecycleOwner {
    val stackCount: Int
        get() = if (isAdded) childFragmentManager.backStackEntryCount else 0

    private var pagerDetails: PagerDetails? = null
    private var onClose = {}

    val advActivity by lazy { activity as CommonActivity }

    fun onClosed(onClose: () -> Unit) {
        this.onClose = onClose
    }

    fun setAsPagerFragment(pager: ViewPager2, adapter: PagerAdapter, defaultItem: Int = 0) {
        this.pagerDetails = PagerDetails(pager, adapter, defaultItem)
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
        pagerDetails?.let { (pager, adapter, default) ->
            if (pager.currentItem == default) {
                val frag = adapter.getFragment(default)
                if (frag.childFragmentManager.fragments.isNotEmpty()) {
                    frag.childFragmentManager.popBackStack()
                    return true
                }
            } else {
                pager.setCurrentItem(default, true)
                return true
            }
        }
        val child = childFragmentManager.fragments.lastOrNull() as CommonFragment?
        val childHandled = child?.backPressHandled() ?: false
        if (!childHandled) {
            if (child != null) {
                child.popBackStackImmediate()
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

internal data class PagerDetails(
    val pager: ViewPager2,
    val adapter: PagerAdapter,
    val defaultItem: Int = 0
)