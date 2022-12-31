package com.bkt.advlibrary.bind

import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.bkt.advlibrary.CommonActivity
import com.bkt.advlibrary.LiveObject

sealed class BinderModel : ViewModel() {
    var activity: (() -> CommonActivity)? = null

    internal lateinit var eventListener: EventListener
    val currentlyTransacting = LiveObject(false)

    fun publishEvent(event: String, vararg data: Any, onComplete: () -> Unit = {}) {
        eventListener.onEvent(BinderEvent(event, data))
        onComplete.invoke()
    }

    fun getActivity(): CommonActivity? {
        return activity?.invoke()
    }

    fun doNothing() {

    }

    fun getString(@StringRes id: Int): String? {
        return getActivity()?.getString(id)
    }

    fun getDrawable(@DrawableRes id: Int): Drawable? {
        getActivity()?.let {
            return AppCompatResources.getDrawable(it, id)
        }
        return null
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