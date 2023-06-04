package com.bkt.advlibrary.bind

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.ColorInt
import androidx.core.widget.ImageViewCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bkt.advlibrary.*
import com.google.android.material.tabs.TabLayout
import java.math.BigDecimal

@BindingAdapter("android:visibility")
fun setVisibility(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter("dateFieldEnabled")
fun setAsDateField(
    et: EditText,
    isDateEnabled: Boolean = false
) {
    if (isDateEnabled)
        et.setDatePicker()

}


@BindingAdapter("linearAdapter")
fun <T, H : RecyclerView.ViewHolder> setLinearAdapter(
    et: RecyclerView,
    adapter: ListAdapter<T, H>
) {
    et.setLinearAdapter(et.context, adapter)
}

@BindingAdapter("horizontalAdapter")
fun <T, H : RecyclerView.ViewHolder> setHorizontalLinearAdapter(
    et: RecyclerView,
    adapter: ListAdapter<T, H>
) {
    et.setLinearAdapter(et.context, adapter, RecyclerView.HORIZONTAL)
}

@BindingAdapter("app:onFocusChange")
fun setEditTextFocusListener(
    et: EditText,
    listener: (hasFocus: Boolean) -> Unit?
) {

    et.setOnFocusChangeListener { _, b ->
        listener.invoke(b)
    }
}

@BindingAdapter("app:verticalGridAdapter", "app:gridCount", requireAll = false)
fun <T, H : RecyclerView.ViewHolder> setVerticalGridAdapter(
    et: RecyclerView,
    adapter: ListAdapter<T, H>,
    span: Int?
) {
    et.setGridAdapter(et.context, adapter, span ?: 2)
}


@BindingAdapter("app:tint")
fun ImageView.setImageTint(@ColorInt color: Int?) {
    if (color != null)
        ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(color))
}

@BindingAdapter("requestFocus")
fun requestFocus(view: TextView, requestFocus: Boolean) {
    if (requestFocus) {
        view.isFocusableInTouchMode = true
        view.requestFocus()
    }
}

@BindingAdapter("android:layout_gravity")
fun setGravity(view: View, gravity: Int) {
    when (val params = view.layoutParams) {
        is LinearLayout.LayoutParams -> params.gravity = gravity
        is FrameLayout.LayoutParams -> params.gravity = gravity
    }
}

@BindingAdapter("app:isBold")
fun setAsBold(view: TextView, isBold: Boolean) {
    if (isBold)
        view.setTypeface(null, Typeface.BOLD)
    else view.setTypeface(null, Typeface.NORMAL)
}

@BindingAdapter("app:onImeAction")
fun onFocusChange(et: EditText, focusChangeListener: View.OnFocusChangeListener) {
    et.onFocusChangeListener = focusChangeListener
}

@BindingAdapter("app:disableTouchInterception")
fun disableTouchInterception(rv: RecyclerView, disable: Boolean) {
    if (disable)
        rv.disableTouchInterceptOnView()
}

@BindingAdapter("android:onCheckedChanged")
fun setOnCheckChanged(cb: CheckBox, onCheckChanged: (Boolean) -> Unit) {
    cb.setOnCheckedChangeListener { _, checked ->
        onCheckChanged.invoke(checked)
    }
}

@BindingAdapter("android:layout_marginBottom")
fun setMargin(view: View, margin: Float) {
    val params = view.layoutParams as ViewGroup.MarginLayoutParams
    params.setMargins(
        params.marginStart,
        params.topMargin,
        params.marginEnd,
        margin.toInt()
    )
    view.layoutParams = params
}

@BindingAdapter("app:currency")
fun setCurrency(view: TextView, value: Double) {
    view.text = value.toCurrency()
}

@BindingAdapter("app:currency")
fun setCurrency(view: TextView, value: BigDecimal) {
    view.text = value.toCurrency()
}

@BindingAdapter("android:transitionName")
fun setTransitionName(view: View, value: String) {
    view.transitionName = value
}

@BindingAdapter("app:tabList", "app:firstTabPosition", requireAll = false)
fun setTabs(tabLayout: TabLayout, listOfTabs: List<TabData>, firstTabPosition: Int?) {
    tabLayout.removeAllTabs()
    listOfTabs.forEachIndexed { index, tab ->
        tabLayout.addTab(tabLayout.newTab().also { it.text = tab.text }, firstTabPosition == index)
    }
}

data class TabData(val text: CharSequence) {
    fun matches(matchingText: CharSequence): Boolean {
        return text.contains(matchingText, ignoreCase = true)
    }
}