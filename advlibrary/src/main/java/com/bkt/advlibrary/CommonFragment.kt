package com.bkt.advlibrary

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.lifecycle.LifecycleOwner
import androidx.viewpager2.widget.ViewPager2

abstract class CommonFragment(open val fragmentName: String) : Fragment(), LifecycleOwner {
    val stackCount: Int
        get() = if (isAdded) childFragmentManager.backStackEntryCount else 0
    val advActivity by lazy { activity as CommonActivity }

    private var pagerDetails: PagerDetails? = null
    private var onClose = {}


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

    internal fun loadFragment(
        fragment: CommonFragment,
        layoutId: Int,
        onParent: Boolean,
        addCurrentToStack: Boolean = true,
        body: FragmentTransaction.() -> Unit = {}
    ) {
        val manager = if (onParent) {
            parentFragmentManager
        } else {
            childFragmentManager
        }
        manager.commit {
            body.invoke(this)
            replace(layoutId, fragment)
            if (addCurrentToStack)
                addToBackStack(fragment.fragmentName)
        }
    }

    fun loadChildFragment(
        fragment: CommonFragment,
        id: Int,
        body: FragmentTransaction.() -> Unit = {}
    ) {
        loadFragment(fragment, id, onParent = false, addCurrentToStack = true, body)
    }

    fun replaceChildFragment(
        fragment: CommonFragment,
        id: Int,
        body: FragmentTransaction.() -> Unit = {}
    ) {
        loadFragment(fragment, id, onParent = false, addCurrentToStack = false, body)
    }

    fun loadFragment(
        fragment: CommonFragment, id: Int, body: FragmentTransaction.() -> Unit = {}
    ) {
        loadFragment(fragment, id, onParent = true, addCurrentToStack = true, body)
    }

    fun replaceFragment(
        fragment: CommonFragment, id: Int, body: FragmentTransaction.() -> Unit = {}
    ) {
        loadFragment(fragment, id, onParent = true, addCurrentToStack = false, body)
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
                if (frag.isAdded && frag.childFragmentManager.fragments.isNotEmpty()) {
                    val size = frag.childFragmentManager.fragments.size
                    val childFrag = frag.childFragmentManager.fragments[size - 1] as CommonFragment?
                    if (childFrag != null && childFrag.backPressHandled())
                        return true
                    else if (childFrag != null) {
                        childFrag.popBackStack()
                        return true
                    }
                    frag.childFragmentManager.popBackStack()
                    return true
                } else if (!frag.isAdded) {
                    return false
                }
            } else {
                pager.setCurrentItem(default, true)
                return true
            }
        }

        if (pagerDetails == null) {
            return handleChildPop()
        }
        return false
    }

    private fun handleChildPop(indexOfFrag: Int = childFragmentManager.fragments.lastIndex): Boolean {
        return when (val child = childFragmentManager.fragments.getOrNull(indexOfFrag)) {
            null -> return false
            is CommonFragment -> {
                val childHandled = child.backPressHandled()
                if (!childHandled)
                    child.popBackStackImmediate()
                true
            }
            else -> {
                val popped = child.popBackStackImmediate()
                if (popped)
                    true
                else {
                    // may be received glide fragment
                    handleChildPop(indexOfFrag - 1)
                }
            }
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

fun Fragment.popBackStackImmediate(): Boolean {
    if (!isAdded) {
        return false
    }
    return parentFragmentManager.popBackStackImmediate()
}

fun Fragment.popBackStack() {
    if (isAdded) {
        parentFragmentManager.popBackStack()
    }
}

fun Fragment.popChildFragment() {
    if (isAdded) {
        childFragmentManager.popBackStack()
    }
}