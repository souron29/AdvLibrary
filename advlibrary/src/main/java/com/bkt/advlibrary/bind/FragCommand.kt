package com.bkt.advlibrary.bind

import androidx.annotation.IdRes
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.bkt.advlibrary.CommonFragment
import com.bkt.advlibrary.popBackStackImmediate

sealed class FragCommand {
    abstract suspend fun onCommandReceived(parent: CommonFragment)
}

internal class NavigateCommand(private val dir: NavDirections) : FragCommand() {
    override suspend fun onCommandReceived(parent: CommonFragment) {
        parent.findNavController().navigate(dir)
    }
}

internal data object NavigateBackCommand : FragCommand() {
    override suspend fun onCommandReceived(parent: CommonFragment) {
        if (!parent.popBackStackImmediate())
            parent.findNavController().navigateUp()
    }
}

internal class ToastCommand(private val message: String, private val longToast: Boolean = true) :
    FragCommand() {
    override suspend fun onCommandReceived(parent: CommonFragment) {
        parent.toast(message, longToast)
    }
}

internal class LoadChildCommand(
    private val frag: CommonFragment,
    @IdRes private val id: Int,
    private val onParent: Boolean,
    private val addCurrentToStack: Boolean
) : FragCommand() {
    override suspend fun onCommandReceived(parent: CommonFragment) {
        parent.loadFragment(frag, id, onParent, addCurrentToStack)
    }
}

class CustomActionCommand(private val onFragReceived: (CommonFragment) -> Unit) : FragCommand() {
    override suspend fun onCommandReceived(parent: CommonFragment) {
        onFragReceived.invoke(parent)
    }
}

internal data object HideKeyboardCommand : FragCommand() {
    override suspend fun onCommandReceived(parent: CommonFragment) {
        parent.hideKeyboard()
    }
}