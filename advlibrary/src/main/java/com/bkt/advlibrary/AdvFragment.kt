package library

import android.app.Activity

import android.view.View

import androidx.core.view.ViewCompat

import android.os.Looper

import kotlin.Unit

import android.os.Bundle
import android.os.Handler

import android.view.ViewGroup

import android.view.LayoutInflater
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

import androidx.lifecycle.LifecycleOwner
import library.extensions.ActivityExtKt.toast
import library.extensions.AdvActivity


abstract class AdvFragment(val fragmentName: String, @LayoutRes private val layoutId: Int) :
    Fragment(), LifecycleOwner {
    private lateinit var mActivity: AdvActivity
    private var currentChildFrag: AdvFragment? = null
    var fragmentView: View? = null
        private set
    var isHome = false
        private set
    var isParent = false
        private set
    var isPhantom = false
        private set
    private var onClose = {}

    abstract fun initializeViews()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflate = inflater.inflate(layoutId, container, false)
        fragmentView = inflate
        return inflate
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initializeViews()
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.mActivity = activity as AdvActivity
    }

    override fun onResume() {
        advActivity.supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(!isHome)
        }
        super.onResume()
    }

    fun onClosed(onClose: () -> Unit) {
        this.onClose = onClose
    }

    override fun onDestroy() {
        super.onDestroy()
        this.onClose.invoke()
    }

    fun setAsPhantom(): AdvFragment {
        isPhantom = true
        return this
    }

    fun setAsHome(): AdvFragment {
        isHome = true
        isParent = true
        return this
    }

    fun setAsParent(): AdvFragment {
        isParent = true
        return this
    }

    val advActivity: AdvActivity
        get() {
            return mActivity
        }

    fun <T : AdvFragment> loadChildFragmentDelayed(fragment: T, id: Int) {
        this.currentChildFrag = fragment
        Handler(Looper.getMainLooper()).postDelayed(
            { loadChildFragment(fragment, id) }, 300
        )
    }

    fun <T : AdvFragment> loadChildFragment(fragment: T, id: Int, sharedElement: View?) {
        if (isAdded()) {
            currentChildFrag = fragment
            val beginTransaction: FragmentTransaction = childFragmentManager.beginTransaction()
            val transitionName = ViewCompat.getTransitionName(sharedElement!!)
            beginTransaction.addSharedElement(sharedElement, transitionName!!)
                .replace(id, fragment as Fragment, fragment.tag)
                .addToBackStack(fragment.tag).commit()
        }
    }

    fun <T : AdvFragment> loadChildFragment(
        fragment: T,
        id: Int,
        name: String = fragment.fragmentName
    ) {
        currentChildFrag = fragment
        childFragmentManager.beginTransaction().replace(id, fragment as Fragment, name)
            .addToBackStack(name).commit()
    }

    fun loadFragment(fragment: AdvFragment, id: Int, popLast: Boolean) {
        advActivity.loadFragment(fragment, id, popLast)
    }

    fun hideKeyboard() {
        advActivity.hideKeyboard()
    }

    fun showKeyboard() {
        advActivity.showKeyboard()
    }

    val childFragManager: FragmentManager?
        get() = if (isAdded) {
            childFragmentManager
        } else null
    val stackCount: Int
        get() {
            return childFragManager?.backStackEntryCount ?: 0
        }

    open fun backPressHandled(): Boolean {
        val handled = currentChildFrag?.backPressHandled() ?: false
        if (!handled) {
            if (currentChildFrag != null) {
                currentChildFrag?.popBackStackImmediate()
                currentChildFrag = null
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
        return fragmentManager?.popBackStackImmediate() ?: false
    }

    fun popBackStack() {
        if (isAdded) {
            popBackStack()
        }
    }

    fun popChildFragment() {
        if (isAdded) {
            childFragmentManager.popBackStack()
        }
    }

    fun toast(text: String, longToast: Boolean = false) {
        advActivity.toast(text, longToast)
    }
}
