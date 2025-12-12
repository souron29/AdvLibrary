package com.bkt.advlibrary.bind

import androidx.annotation.IdRes
import androidx.navigation.NavDirections
import com.bkt.advlibrary.CommonFragment
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

open class FragBinderModel : BinderModel() {
    internal val navCommand = MutableSharedFlow<FragCommand>(0, 1)

    /**
     * Actions Performed
     * 1) Load Fragments
     * 2) Get references to fragment
     */
    internal val navCommandOnCreate = MutableSharedFlow<FragCommand>(0, 1)

    /**
     * 1) Navigation - Doesn't work when using fragment's created state
     */
    internal val navCommandOnViewCreate = MutableSharedFlow<FragCommand>(0, 1)

    /**
     * [onFragReceivedMutable] - Invoked from [BinderFragment] during [BinderFragment.initializeViews]
     * replay = 1 so that doesn't matter when someone listens, they will always receive a copy
     * We don't need buffer
     */
    internal val onFragReceivedMutable = MutableSharedFlow<CommonFragment>(1)
    val withFragment = onFragReceivedMutable.asSharedFlow()

    fun navigateBack(): Boolean {
        return navCommand.tryEmit(NavigateBackCommand)
    }

    /**
     * [destinationId] - The topmost destination to retain
     * [inclusive] - Whether the given destination should also be popped.
     * [saveState] - Whether the back stack and the state of all destinations between the current destination
     * and the destinationId should be saved for later restoration via NavOptions.Builder.setRestoreState
     * or the restoreState attribute using the same destinationId
     * (note: this matching ID is true whether inclusive is true or false).
     */
    fun popBackStack(@IdRes destinationId: Int, inclusive: Boolean, saveState: Boolean) {
        navCommandOnCreate.tryEmit(PopBackStackCommand(destinationId, inclusive, saveState))
    }

    fun loadChildFragment(childFragment: CommonFragment, @IdRes id: Int) {
        navCommandOnCreate.tryEmit(
            LoadChildCommand(
                childFragment, id,
                onParent = false,
                addCurrentToStack = true
            )
        )
    }

    fun replaceChildFragment(childFragment: CommonFragment, @IdRes id: Int) {
        navCommandOnCreate.tryEmit(
            LoadChildCommand(
                childFragment, id,
                onParent = false,
                addCurrentToStack = false
            )
        )
    }

    fun loadFragment(fragment: CommonFragment, @IdRes id: Int) {
        navCommandOnCreate.tryEmit(
            LoadChildCommand(
                fragment, id,
                onParent = true,
                addCurrentToStack = true
            )
        )
    }

    fun replaceFragment(fragment: CommonFragment, @IdRes id: Int) {
        navCommandOnCreate.tryEmit(
            LoadChildCommand(
                fragment, id,
                onParent = true,
                addCurrentToStack = false
            )
        )
    }

    fun toast(text: String, longDuration: Boolean = false) {
        navCommand.tryEmit(ToastCommand(text, longDuration))
    }

    fun hideKeyboard() {
        navCommand.tryEmit(HideKeyboardCommand)
    }

    fun navigate(dir: NavDirections) = navCommandOnViewCreate.tryEmit(NavigateCommand(dir))

    /**
     * Fragment can be received on create. So use [navCommandOnCreate]
     */
    fun withFragment(onFragReceived: CommonFragment.() -> Unit) =
        navCommandOnCreate.tryEmit(CustomActionCommand(onFragReceived))
}
