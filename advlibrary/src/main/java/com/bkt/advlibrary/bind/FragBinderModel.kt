package com.bkt.advlibrary.bind

import androidx.annotation.IdRes
import androidx.navigation.NavDirections
import com.bkt.advlibrary.CommonFragment
import kotlinx.coroutines.flow.MutableSharedFlow

open class FragBinderModel : BinderModel() {
    internal val navCommand = MutableSharedFlow<FragCommand>(0, 1)

    fun popBackStackImmediate(): Boolean {
        return navCommand.tryEmit(NavigateBackCommand)
    }

    fun loadChildFragment(childFragment: CommonFragment, @IdRes id: Int) {
        navCommand.tryEmit(
            LoadChildCommand(
                childFragment, id,
                onParent = false,
                addCurrentToStack = true
            )
        )
    }

    fun replaceChildFragment(childFragment: CommonFragment, @IdRes id: Int) {
        navCommand.tryEmit(
            LoadChildCommand(
                childFragment, id,
                onParent = false,
                addCurrentToStack = false
            )
        )
    }

    fun loadFragment(fragment: CommonFragment, @IdRes id: Int) {
        navCommand.tryEmit(
            LoadChildCommand(
                fragment, id,
                onParent = true,
                addCurrentToStack = true
            )
        )
    }

    fun replaceFragment(fragment: CommonFragment, @IdRes id: Int) {
        navCommand.tryEmit(
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

    fun navigate(dir: NavDirections) = navCommand.tryEmit(NavigateCommand(dir))

    fun fragAction(onFragReceived: (CommonFragment) -> Unit) =
        navCommand.tryEmit(CustomActionCommand(onFragReceived))
}
