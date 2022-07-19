package com.bkt.advlibrary

import android.graphics.Color
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import androidx.core.view.setPadding
import com.bkt.advlibrary.ActivityExtKt.scanForActivity
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


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

fun <T> ChipGroup.addChips(
    @LayoutRes chipLayout: Int,
    vararg mapping: Pair<T, String>,
    chipModifier: (Chip, index: Int) -> Unit = { _: Chip, _: Int -> }
): HashMap<Int, T> {
    val inflater = LayoutInflater.from(context)
    val map = HashMap<Int, T>()
    for ((index, item) in mapping.withIndex()) {
        val chip = inflater.inflate(
            chipLayout,
            this,
            false
        ) as Chip
        chip.text = item.second
        chipModifier.invoke(chip, index)
        addView(chip)
        map[chip.id] = item.first
    }
    return map
}