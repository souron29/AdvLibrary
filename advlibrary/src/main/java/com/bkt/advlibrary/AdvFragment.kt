package library

import android.app.Activity
import kotlin.jvm.internal.Intrinsics

import kotlin.TypeCastException

import android.view.inputmethod.InputMethodManager

import android.view.View

import androidx.core.view.ViewCompat

import android.os.Looper

import kotlin.Unit

import android.content.Context

import android.os.Bundle
import android.os.Handler

import android.view.ViewGroup

import android.view.LayoutInflater
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

import java.util.HashMap

import androidx.lifecycle.LifecycleOwner


abstract class AdvFragment(val fragmentName: String, @LayoutRes private val layoutId: Int) :
    Fragment(), LifecycleOwner {
    private lateinit var mActivity: AdvActivity
    private var childFragmentHolderID = -1
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
        childFragmentHolderID = id
        Handler(Looper.getMainLooper()).postDelayed(
            { loadChildFragment(fragment, id) }, 300
        )
    }

    fun <T : AdvFragment?> loadChildFragment(fragment: T, id: Int, sharedElement: View?) {
        if (isAdded()) {
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
        childFragmentManager.beginTransaction().replace(id, fragment as Fragment, name)
            .addToBackStack(name).commit()
    }

    fun loadFragment(fragment: AdvFragment?, id: Int, popLast: Boolean) {
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

    val lastChildFrag: AdvFragment?
        get() {
            return childFragManager?.findFragmentById(childFragmentHolderID) as AdvFragment?
        }

    fun backPressHandled(): Boolean {
        if (childFragmentHolderID <= -1)
            return false

        val frag = childFragmentManager.findFragmentById(
            childFragmentHolderID
        ) as AdvFragment? ?: return false
        if (frag.backPressHandled()) {
            return true
        }
        frag.popBackStackImmediate()
        return true
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
}
