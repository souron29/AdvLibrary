package com.bkt.advlibrary.bind

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Filter
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.ImageViewCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.bkt.advlibrary.ActivityExtKt.scanForActivity
import com.bkt.advlibrary.CommonActivity
import com.bkt.advlibrary.DateFormats
import com.bkt.advlibrary.LiveObject
import com.bkt.advlibrary.PagerAdapter
import com.bkt.advlibrary.bgLaunch
import com.bkt.advlibrary.disableTouchInterceptOnView
import com.bkt.advlibrary.format
import com.bkt.advlibrary.getDate
import com.bkt.advlibrary.getTrimText
import com.bkt.advlibrary.setColorTint
import com.bkt.advlibrary.setTextChangeListener
import com.bkt.advlibrary.setupDatePicker
import com.bkt.advlibrary.setupTimePicker
import com.bkt.advlibrary.sysdate
import com.bkt.advlibrary.toCurrency
import com.bkt.advlibrary.toDate
import com.bkt.advlibrary.toDateOrNull
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import java.math.BigDecimal
import java.util.Date

@BindingAdapter("android:visibility")
fun setVisibility(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}

fun Context.showKeyboard(editText: EditText) {
    editText.requestFocus()
    val service = getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
    service.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
}

/**
 * Recyclerview Related Binders
 */

@BindingAdapter("app:linearAdapter", "app:reverseLayout", requireAll = false)
fun <T, H : RecyclerView.ViewHolder> setLinearAdapter(
    et: RecyclerView,
    adapter: ListAdapter<T, H>?,
    reverseLayout: Boolean?
) {
    et.adapter = adapter
    et.layoutManager =
        LinearLayoutManager(et.context, RecyclerView.VERTICAL, reverseLayout ?: false)
}

@BindingAdapter("app:horizontalAdapter", "app:reverseLayout", requireAll = false)
fun <T, H : RecyclerView.ViewHolder> setHorizontalLinearAdapter(
    et: RecyclerView,
    adapter: ListAdapter<T, H>?,
    reverseLayout: Boolean?
) {
    et.adapter = adapter
    et.layoutManager =
        LinearLayoutManager(et.context, RecyclerView.HORIZONTAL, reverseLayout ?: false)
}

@BindingAdapter("app:verticalStagAdapter", "app:gridCount", requireAll = false)
fun <T, H : RecyclerView.ViewHolder> setVerticalStagAdapter(
    et: RecyclerView,
    adapter: ListAdapter<T, H>?,
    span: Int?
) {
    et.layoutManager = StaggeredGridLayoutManager(span ?: 1, StaggeredGridLayoutManager.VERTICAL)
    et.adapter = adapter
}

@BindingAdapter(
    "app:horizontalStagAdapter",
    "app:gridCount",
    requireAll = false
)
fun <T, H : RecyclerView.ViewHolder> setHorizontalStagAdapter(
    et: RecyclerView,
    adapter: ListAdapter<T, H>?,
    span: Int?
) {
    et.layoutManager = StaggeredGridLayoutManager(span ?: 1, StaggeredGridLayoutManager.HORIZONTAL)
    et.adapter = adapter
}

@BindingAdapter("app:verticalGridAdapter", "app:gridCount", "app:reverseLayout", requireAll = false)
fun <T, H : RecyclerView.ViewHolder> setVerticalGridAdapter(
    et: RecyclerView,
    adapter: ListAdapter<T, H>?,
    span: Int?,
    reverseLayout: Boolean?
) {
    et.adapter = adapter
    et.layoutManager =
        GridLayoutManager(et.context, span ?: 1, RecyclerView.VERTICAL, reverseLayout ?: false)
}

@BindingAdapter(
    "app:horizontalGridAdapter",
    "app:gridCount",
    "app:reverseLayout",
    requireAll = false
)
fun <T, H : RecyclerView.ViewHolder> setHorizontalGridAdapter(
    et: RecyclerView,
    adapter: ListAdapter<T, H>?,
    span: Int?,
    reverseLayout: Boolean?
) {
    et.adapter = adapter
    et.layoutManager =
        GridLayoutManager(et.context, span ?: 1, RecyclerView.HORIZONTAL, reverseLayout ?: false)
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

@BindingAdapter("app:drawableColor")
fun ImageView.setImageTint(@ColorInt color: Int?) {
    if (color != null)
        ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(color))
}

@BindingAdapter("app:drawableId", "app:drawableColorId", requireAll = false)
fun ImageView.setImage(@DrawableRes imageId: Int?, @ColorRes colorId: Int?) {
    if (imageId == null)
        return
    try {
        var drawable = AppCompatResources.getDrawable(this.context, imageId)
        if (colorId != null) {
            val color = context.getColor(colorId)
            drawable = drawable?.setColorTint(color)
        }
        this.setImageDrawable(drawable)
    } catch (e: Resources.NotFoundException) {
        return
    }
}

@BindingAdapter("app:iconId")
fun MaterialButton.setImage(@DrawableRes iconId: Int) {
    try {
        val drawable = AppCompatResources.getDrawable(this.context, iconId)
        this.icon = drawable
    } catch (e: Resources.NotFoundException) {
        return
    }
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

@BindingAdapter("app:onFocusChange")
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

/**
 * View Pager related binders
 */

@BindingAdapter("app:adapter")
fun setPagerAdapter(pager: ViewPager2, adapter: PagerAdapter) {
    pager.adapter = adapter
}

@BindingAdapter("app:offscreenLimit")
fun setPagerLimit(pager: ViewPager2, limit: Int) {
    pager.offscreenPageLimit = limit
}

@BindingAdapter("app:pagePosition")
fun setPagerPosition(pager: ViewPager2, pos: Int) {
    pager.currentItem = pos
}


/**
 * Textview Related Binders
 */

/*@BindingAdapter("app:autoCompleteList")
fun setAutoCompleteAdapter(view: AutoCompleteTextView, listOfText: List<String>) {
    val adapter = ArrayAdapter(view.context, android.R.layout.simple_list_item_1, listOfText)
    view.setAdapter(adapter)
}*/

@BindingAdapter("app:autoCompleteList", "app:selectedItem", requireAll = false)
fun <E> setAutoAdapterCompleteList(
    view: AutoCompleteTextView,
    listOfItems: List<DropdownItem<E>>,
    selectedItem: LiveObject<E>?
) {
    val activity = view.context.scanForActivity() as CommonActivity
    val layoutId = android.R.layout.simple_spinner_dropdown_item
    var displayableListOfItems = listOfItems

    val adapter =
        object : ArrayAdapter<DropdownItem<E>>(activity, layoutId, listOfItems) {
            private val filter = createFilterForArrayAdapter(listOfItems) {
                displayableListOfItems = it
            }

            override fun getCount(): Int {
                return displayableListOfItems.size
            }

            override fun getItem(position: Int): DropdownItem<E> {
                return displayableListOfItems[position]
            }

            override fun getItemId(position: Int): Long {
                return position.toLong()
            }

            override fun getView(position: Int, view: View?, parent: ViewGroup): View {
                val inflater = activity.layoutInflater
                val convertView = view ?: inflater.inflate(layoutId, parent, false)

                try {
                    val item = getItem(position)
                    val textView =
                        convertView.findViewById<TextView>(android.R.id.text1)
                    textView.text = item.text
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return convertView
            }

            override fun getFilter(): Filter {
                return filter
            }
        }

    view.setAdapter(adapter)
    view.setOnItemClickListener { _, _, position, _ ->
        // find the actual selected item from the displayed items
        val item = displayableListOfItems[position]
        if (selectedItem != null)
            selectedItem.value = item.mainItem

        view.setText(item.text)
        bgLaunch {
            listOfItems.forEach {
                it.isSelected = item == it
            }
        }
        activity.hideKeyboard()
    }
}

fun <E> createFilterForArrayAdapter(
    listOfItems: List<DropdownItem<E>>,
    onListChanged: (List<DropdownItem<E>>) -> Unit
): Filter {
    return object : Filter() {
        override fun performFiltering(filterText: CharSequence?): FilterResults {
            val result = FilterResults()
            if (filterText.isNullOrEmpty()) {
                result.values = listOfItems
                result.count = listOfItems.size
            } else {
                val filteredList = listOfItems.filter { it.text.contains(filterText) }
                result.values = filteredList
                result.count = filteredList.size
            }
            return result
        }

        override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
            (p1?.values as List<DropdownItem<E>>?)?.let(onListChanged)
        }

    }
}

@BindingAdapter("app:listOfItems", "app:selectedItem", requireAll = false)
fun <E> setSpinnerAdapter(
    spinner: Spinner,
    listOfItems: List<DropdownItem<E>>,
    selectedItem: LiveObject<E>?
) {
    val activity = spinner.context.scanForActivity() as FragmentActivity
    val layoutId = android.R.layout.simple_spinner_dropdown_item

    val adapter =
        object : ArrayAdapter<DropdownItem<E>>(activity, layoutId, listOfItems) {
            override fun getCount(): Int {
                return listOfItems.size
            }

            override fun getItem(position: Int): DropdownItem<E> {
                return listOfItems[position]
            }

            override fun getItemId(position: Int): Long {
                return position.toLong()
            }

            override fun getView(position: Int, view: View?, parent: ViewGroup): View {
                val inflater = activity.layoutInflater
                val convertView = view ?: inflater.inflate(layoutId, parent, false)

                try {
                    val item = getItem(position)
                    val textView =
                        convertView.findViewById<TextView>(android.R.id.text1)
                    textView.text = item.text
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return convertView
            }
        }
    spinner.adapter = adapter
    spinner.setOnItemClickListener { _, _, position, _ ->
        val item = listOfItems[position]
        if (selectedItem != null)
            selectedItem.value = item.mainItem

        bgLaunch {
            listOfItems.forEach {
                it.isSelected = item == it
            }
        }
    }
}

data class DropdownItem<E>(val text: CharSequence, val mainItem: E, var isSelected: Boolean = false)


@BindingAdapter("app:onRefresh")
fun onRefresh(swipeRefreshLayout: SwipeRefreshLayout, doThis: () -> Unit) {
    swipeRefreshLayout.setOnRefreshListener {
        doThis.invoke()
        swipeRefreshLayout.isRefreshing = false
    }
}

/**
 * Below binders is required for date setters
 */

@BindingAdapter("app:date", "app:dateFormat", "app:dateEnabled", requireAll = false)
fun setDateValue(
    tv: TextView,
    date: Date?,
    dateFormat: String?,
    enabled: Boolean?
) {
    val format = dateFormat ?: DateFormats.DATE.format
    tv.tag = format
    if (tv is EditText && tv.getTrimText().isNotEmpty()) {
        //assuming date and format has been already been set
        val currentDate = tv.getDate(format)
        if (date == currentDate) return
    }
    val clickable = enabled ?: true
    if (tv is EditText) {
        tv.setupDatePicker(date, showTimePicker = false, clickable = clickable, format = format)
    } else {
        // only edittext
        tv.text = (date ?: sysdate()).format(format)
    }
}

@InverseBindingAdapter(attribute = "app:date", event = "app:dateAttrChanged")
fun getDateValue(et: EditText): Date? {
    val text = et.getTrimText()
    val format = et.tag as String
    return text.toDateOrNull(format)
}

@BindingAdapter("app:dateAttrChanged")
fun setDateChanged(et: EditText, listener: InverseBindingListener) {
    et.setTextChangeListener {
        listener.onChange()
    }
}

/**
 * Date time picker
 */

@BindingAdapter("app:dateTime", "app:dateTimeFormat", "app:dateEnabled", requireAll = false)
fun setDateTimeValue(
    et: EditText,
    date: Date?,
    dateTimeFormat: String?,
    enabled: Boolean?
) {
    val format = dateTimeFormat ?: DateFormats.DD_MMM_YYYY_TIME.format
    if (et.getTrimText().isNotEmpty()) {
        //assuming date and format has been already been set
        val currentDate = et.getDate(format)
        if (date == currentDate) return
    }
    et.tag = format
    val clickable = enabled ?: true
    et.setupDatePicker(date, showTimePicker = true, clickable = clickable, format = format)
}

@InverseBindingAdapter(attribute = "app:dateTime", event = "app:dateTimeAttrChanged")
fun getDateTimeValue(et: EditText): Date? {
    val text = et.getTrimText()
    val format = et.tag as String
    return text.toDateOrNull(format)
}

@BindingAdapter("app:dateTimeAttrChanged")
fun setDateTimeChanged(et: EditText, listener: InverseBindingListener) {
    et.setTextChangeListener {
        listener.onChange()
    }
}

@BindingAdapter("app:time", "app:timeFormat", "app:timeEnabled", requireAll = false)
fun setTimeValue(
    et: EditText,
    time: Date?,
    timeFormat: String?,
    enabled: Boolean?
) {
    val format = timeFormat ?: DateFormats.TIME.format
    if (et.getTrimText().isNotEmpty()) {
        //assuming date and format has been already been set
        val currentDateText = et.text?.toString()
        val incomingDateText = time?.format(format)
        if (currentDateText == incomingDateText) return
    }
    et.tag = format
    val clickable = enabled ?: true
    et.setupTimePicker(time, clickable = clickable, format = format)
}

@InverseBindingAdapter(attribute = "app:time", event = "app:timeAttrChanged")
fun getTimeValue(et: EditText): Date? {
    val text = et.getTrimText()
    val format = et.tag as String
    return text.toDateOrNull(format)
}

@BindingAdapter("app:timeAttrChanged")
fun setTimeChanged(et: EditText, listener: InverseBindingListener) {
    et.setTextChangeListener {
        listener.onChange()
    }
}

@BindingAdapter("app:onImeOptionAction")
fun TextView.onImeAction(onAction: () -> Boolean) {
    this.setOnEditorActionListener { _, _, _ ->
        onAction.invoke()
    }
}

@BindingAdapter("android:minHeight")
fun setMinHeight(view: View, height: Float) {
    view.minimumHeight = height.toInt()
}

@BindingAdapter(
    "android:layout_marginStart",
    "android:layout_marginTop",
    "android:layout_marginEnd",
    "android:layout_marginBottom",
    requireAll = false
)
fun setMargin(
    view: View,
    marginStart: Float?,
    marginTop: Float?,
    marginEnd: Float?,
    marginBottom: Float?
) {
    val params = view.layoutParams
    if (params is ViewGroup.MarginLayoutParams) {
        if (marginStart != null)
            params.marginStart = marginStart.toInt()
        if (marginEnd != null)
            params.marginEnd = marginEnd.toInt()
        if (marginTop != null)
            params.topMargin = marginTop.toInt()
        if (marginBottom != null)
            params.bottomMargin = marginBottom.toInt()
    }
}