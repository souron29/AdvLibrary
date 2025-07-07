package com.bkt.advlibrary.bind

import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bkt.advlibrary.CommonActivity
import com.bkt.advlibrary.LiveObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

sealed class BinderModel : ViewModel() {
    var activity: (() -> CommonActivity)? = null
    val currentlyTransacting = LiveObject(false)

    fun getActivity(): CommonActivity? {
        return activity?.invoke()
    }

    fun doNothing() {

    }

    fun getString(@StringRes id: Int): String? {
        return getActivity()?.getString(id)
    }

    fun getDrawable(@DrawableRes id: Int): Drawable? {
        return getActivity()?.let { AppCompatResources.getDrawable(it, id) }
    }

    fun getColor(@ColorRes id: Int): Int? {
        getActivity()?.let {
            return ContextCompat.getColor(it, id)
        }
        return null
    }

    fun getDimen(@DimenRes id: Int): Float? {
        getActivity()?.let {
            return it.resources.getDimension(id)
        }
        return null
    }
}

fun ViewModel.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
) = viewModelScope.launch(context, start, block)