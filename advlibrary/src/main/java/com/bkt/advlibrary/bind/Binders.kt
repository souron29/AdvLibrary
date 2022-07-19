package com.bkt.advlibrary.bind

import android.view.View
import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bkt.advlibrary.setDatePicker
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