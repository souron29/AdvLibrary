package com.bkt.advlibrary.bind

import android.content.res.ColorStateList
import android.view.View
import android.widget.*
import androidx.annotation.ColorInt
import androidx.core.widget.ImageViewCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bkt.advlibrary.setDatePicker
import com.bkt.advlibrary.setGridAdapter
import com.bkt.advlibrary.setLinearAdapter

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