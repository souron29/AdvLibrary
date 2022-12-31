package com.bkt.advlibrary

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar


fun View.hide(makeInvisible: Boolean = false) {
    visibility = if (makeInvisible) {
        View.INVISIBLE
    } else {
        View.GONE
    }
}

fun View.show(value: Any?) {
    this.visibility = View.VISIBLE
    if (this is EditText && value != null)
        this.assign(value)
    else if (this is TextView)
        this.text = value?.toString()
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.isVisible(): Boolean {
    return this.visibility == View.VISIBLE
}

fun View.visibleWhen(vararg visible: Boolean) {
    var showView = true
    visible.forEach { if (!it) showView = false }
    if (showView)
        show()
    else hide()
}

fun Array<View?>?.visibleWhen(vararg visible: Boolean) {
    var showView = true
    visible.forEach { if (!it) showView = false }
    val visibility = if (showView) View.VISIBLE else View.GONE
    this?.forEach { it?.visibility = visibility }
}

fun disableViews(vararg views: View) {
    for (view in views)
        view.isEnabled = false
}

fun hideViews(vararg views: View) {
    for (view in views)
        view.visibility = View.GONE
}

fun setFocus(focus: Boolean, vararg views: View) {
    for (view in views)
        view.isFocusable = focus
}

fun setTextColors(@ColorInt color: Int, vararg tvs: TextView) {
    for (tv in tvs)
        tv.setTextColor(color)
}

fun View.enabledWhen(vararg visible: Boolean) {
    var enableView = true
    visible.forEach { if (!it) enableView = false }
    isEnabled = enableView
}

fun enabledWhen(visible: Boolean, vararg views: View) {
    views.forEach { it.enabledWhen(visible) }
}

fun View.enable() {
    isEnabled = true
}

fun View.disable() {
    isEnabled = false
}

fun View.snack(text: String, length: Int = Snackbar.LENGTH_SHORT, block: Snackbar.() -> Unit = {}) {
    Snackbar.make(this, text, length).apply {
        block.invoke(this)
        show()
    }
}


fun RecyclerView.disableTouchInterceptOnView() {
    this.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {

        override fun onTouchEvent(view: RecyclerView, event: MotionEvent) {}

        override fun onInterceptTouchEvent(view: RecyclerView, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    view.parent?.requestDisallowInterceptTouchEvent(true)
                }
            }
            return false
        }

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
    })
}

@SuppressLint("ClickableViewAccessibility")
fun View.disableTouchInterceptOnView() {
    this.setOnTouchListener { view, event ->
        if (view.id == this.id) {
            view.parent.requestDisallowInterceptTouchEvent(true)
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_UP -> view.parent.requestDisallowInterceptTouchEvent(false)
            }
        }
        false

    }
}