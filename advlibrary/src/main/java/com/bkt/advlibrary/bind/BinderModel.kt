package com.bkt.advlibrary.bind

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModel
import com.bkt.advlibrary.CommonActivity

sealed class BinderModel : ViewModel() {
    var activity: (() -> CommonActivity)? = null

    internal lateinit var eventListener: EventListener

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
}