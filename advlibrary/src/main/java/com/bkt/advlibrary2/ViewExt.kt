package com.bkt.advlibrary2

import android.annotation.SuppressLint
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

fun View.hide(makeInvisible: Boolean = false) {
    visibility = if (makeInvisible) {
        View.INVISIBLE
    } else {
        View.GONE
    }
}

fun <T : View> Iterable<T>.hide(makeInvisible: Boolean = false) {
    forEach {
        it.hide(makeInvisible)
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

fun <T : View> Iterable<T>.show() {
    forEach {
        it.show()
    }
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

fun <T : View> Iterable<T>.visibleWhen(vararg visibilityPredicates: Boolean) {
    val isVisible = visibilityPredicates.all { it }
    forEach {
        if (isVisible)
            it.show()
        else it.hide()
    }
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

fun TextView.topDrawable(
    @DrawableRes drawableId: Int = 0,
    @DimenRes sizeRes: Int = 0,
    @ColorInt color: Int = 0,
    @ColorRes colorRes: Int = 0
) {
    this.setCompoundDrawables(
        null,
        context.getAdvDrawable(drawableId, sizeRes, color, colorRes),
        null,
        null
    )
}

fun TextView.rightDrawable(
    @DrawableRes drawableId: Int = 0,
    @DimenRes sizeRes: Int = 0,
    @ColorInt color: Int = 0,
    @ColorRes colorRes: Int = 0
) {
    this.setCompoundDrawables(
        null,
        null,
        context.getAdvDrawable(drawableId, sizeRes, color, colorRes),
        null
    )
}

fun TextView.leftDrawable(
    @DrawableRes drawableId: Int = 0,
    @DimenRes sizeRes: Int = 0,
    @ColorInt color: Int = 0,
    @ColorRes colorRes: Int = 0
) {
    this.setCompoundDrawables(
        context.getAdvDrawable(drawableId, sizeRes, color, colorRes),
        null,
        null,
        null
    )
}

@BindingAdapter("app:onActionListener")
fun onKeyboardAction(view: View, listener: ViewActionListener?) {
    if (listener == null)
        return
    view.setOnClickListener {
        listener.onClick.invoke(it)
    }
    view.setOnLongClickListener {
        listener.onLongClick.invoke(it)
    }
    view.setOnFocusChangeListener { v, hasFocus ->
        listener.onFocusChanged.invoke(v, hasFocus)
    }
    if (view is EditText) {
        view.setOnEditorActionListener { _, actionId, keyEvent ->
            listener.onImeAction.invoke(actionId, keyEvent)
        }
        view.setTextChangeListener {
            listener.onTextChanged.invoke(it)
        }
    }
}

class ViewActionListener {
    var onClick: (View) -> Unit = {}
    var onLongClick: (View) -> Boolean = { false }
    var onImeAction: (Int, KeyEvent?) -> Boolean = { _, _ -> false }
    var onTextChanged: (CharSequence) -> Unit = {}
    var onFocusChanged: (View, Boolean) -> Unit = { _, _ -> }

    fun setOnClick(function: (View) -> Unit): ViewActionListener {
        this.onClick = function
        return this
    }

    fun setOnLongClick(function: (View) -> Boolean): ViewActionListener {
        this.onLongClick = function
        return this
    }

    fun setOnImeAction(function: (Int, KeyEvent?) -> Boolean): ViewActionListener {
        this.onImeAction = function
        return this
    }

    fun setOnTextChanged(function: (CharSequence) -> Unit): ViewActionListener {
        this.onTextChanged = function
        return this
    }

    fun setOnFocusChanged(function: (View, Boolean) -> Unit): ViewActionListener {
        this.onFocusChanged = function
        return this
    }

    companion object {
        fun setOnClick(function: (View) -> Unit): ViewActionListener {
            return ViewActionListener().setOnClick(function)
        }

        fun setOnLongClick(function: (View) -> Boolean): ViewActionListener {
            return ViewActionListener().setOnLongClick(function)
        }

        fun setOnAnyClick(function: (View) -> Unit): ViewActionListener {
            return ViewActionListener().also { listener ->
                listener.setOnClick(function)
                listener.setOnLongClick {
                    function.invoke(it)
                    true
                }
            }
        }

        fun setOnImeAction(function: (Int, KeyEvent?) -> Boolean): ViewActionListener {
            return ViewActionListener().setOnImeAction(function)
        }

        fun setOnTextChanged(function: (CharSequence) -> Unit): ViewActionListener {
            return ViewActionListener().setOnTextChanged(function)
        }

        fun setOnFocusChanged(function: (View, Boolean) -> Unit): ViewActionListener {
            return ViewActionListener().setOnFocusChanged(function)
        }
    }
}