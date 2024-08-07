package com.bkt.advlibrary

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.*
import androidx.lifecycle.LifecycleOwner
import androidx.viewpager2.widget.ViewPager2
import java.io.Serializable

abstract class CommonFragment(open val fragmentName: String) : Fragment(), LifecycleOwner {
    val stackCount: Int
        get() = if (isAdded) childFragmentManager.backStackEntryCount else 0
    lateinit var advActivity: CommonActivity

    private var pagerDetails: PagerDetails? = null
    private var onClose = {}

    fun onClosed(onClose: () -> Unit) {
        this.onClose = onClose
    }

    fun setAsPagerFragment(pager: ViewPager2, adapter: PagerAdapter, defaultItem: Int = 0) {
        this.pagerDetails = PagerDetails(pager, adapter, defaultItem)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.advActivity = activity as CommonActivity

    }

    override fun onDestroy() {
        super.onDestroy()
        this.onClose.invoke()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.advActivity = activity as CommonActivity
        initializeViews()
    }
    /**
     * Invoked from onCreateView method of fragment lifecycle
     * Called every time view is created.
     * Views can be destroyed if you are moving to another fragment, and then recreated when you return
     * to current fragment
     */
    abstract fun initializeViews()

    /**
     * Invoked from onCreate method of fragment lifecycle
     * Called once in whole lifecycle, so this can be used to initialize data listeners
     *
     */
    open fun onSetupData() {

    }

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
        if (indexOfFrag < 0)
            return popBackStackImmediate()
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

    fun passArguments(vararg args: Serializable?) {
        val arguments = this.arguments ?: Bundle()
        for ((count, arg) in args.withIndex()) {
            val param = "ParamArg$count"
            if (arg != null)
                arguments.putSerializable(param, arg)
        }
        this.arguments = arguments
    }

    fun <T : Serializable> getArgument(argIndex: Int, clazz: Class<T>? = null): T? {
        val param = "ParamArg$argIndex"
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && clazz != null) {
            arguments?.getSerializable(param, clazz)
        } else {
            arguments?.getSerializable(param) as T?
        }
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

/**
 * Used to send data back to the calling fragment
 *
 * We can listen for result data using
 * fragment.setFragmentResultListener(requestKey) { key, bundle -> }
 *
 */
fun <V : Serializable> Fragment.sendResultData(
    requestKey: String,
    vararg keyValuePairs: Pair<String, V>
) {
    val result = Bundle()
    for ((resultKey, value) in keyValuePairs) {
        result.putSerializable(resultKey, value)
    }
    result.putInt(FragmentResult.PARAM_RESULT_COUNT_KEY, keyValuePairs.size)
    this.setFragmentResult(requestKey, result)
}

/**
 * [passResults], [getResults], [getResultsFromChild] methods are used to transfer data between fragments
 * A [resultKey] is used to uniquely identify the data
 * Choice of use of [getResults] or [getResultsFromChild] in the destination fragment is based on the type of
 * Source Fragment
 */
fun Fragment.passResults(resultKey: String, vararg results: Serializable) {
    val bundle = Bundle()

    for ((index, result) in results.withIndex()) {
        bundle.putSerializable("${FragmentResult.PARAM_RESULT_KEY}$index", result)
    }
    bundle.putInt(FragmentResult.PARAM_RESULT_COUNT_KEY, results.size)
    setFragmentResult(resultKey, bundle)
}

fun Fragment.getResults(resultKey: String, onResult: (FragmentResult) -> Unit) {
    this.setFragmentResultListener(requestKey = resultKey) { key, result ->
        if (key == resultKey)
            onResult.invoke(FragmentResult(result))
        result.remove(key)
    }
}

fun Fragment.getResultsFromChild(resultKey: String, onResult: (FragmentResult) -> Unit) {
    childFragmentManager.setFragmentResultListener(resultKey, this) { key, result ->
        if (key == resultKey)
            onResult.invoke(FragmentResult(result))
        result.remove(key)
    }
}


data class FragmentResult(private val result: Bundle) {
    companion object {
        const val PARAM_RESULT_KEY = "ParamResultKey"
        const val PARAM_RESULT_COUNT_KEY = "ParamPassResultsCount"
    }

    fun <T : Serializable> getResult(argIndex: Int, clazz: Class<T>? = null): T? {
        val resultKey = "$PARAM_RESULT_KEY$argIndex"
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && clazz != null) {
            result.getSerializable(resultKey, clazz)
        } else {
            result.getSerializable(resultKey) as T?
        }
    }

    fun <T : Serializable> getResult(resultKey: String, clazz: Class<T>? = null): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && clazz != null) {
            result.getSerializable(resultKey, clazz)
        } else {
            result.getSerializable(resultKey) as T?
        }
    }

    fun <T : Serializable> getResults(clazz: Class<T>? = null): List<T> {
        val count = result.getInt(PARAM_RESULT_COUNT_KEY, 0)
        if (count == 0)
            return ArrayList()
        val resultList = ArrayList<T>()
        for (index in 0 until count) {
            getResult(index, clazz)?.let { result ->
                resultList.add(result)
            }
        }
        return resultList
    }
}

/**
 * [passArgument] & [getArgument] methods are used to transfer data from Parent fragment to child fragment
 */
fun Fragment.passArgument(key: String, arg: Serializable) {
    val arguments = this.arguments ?: Bundle()
    arguments.putSerializable(key, arg)
    this.arguments = arguments
}

fun <T : Serializable> Fragment.getArgument(key: String, clazz: Class<T>? = null): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && clazz != null) {
        arguments?.getSerializable(key, clazz)
    } else {
        arguments?.getSerializable(key) as T?
    }
}
