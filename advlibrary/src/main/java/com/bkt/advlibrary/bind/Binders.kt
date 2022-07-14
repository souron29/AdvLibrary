package com.bkt.advlibrary.bind

import android.view.View
import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bkt.advlibrary.GeneralExtKt.setLinearAdapter
import com.bkt.advlibrary.setDatePicker

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