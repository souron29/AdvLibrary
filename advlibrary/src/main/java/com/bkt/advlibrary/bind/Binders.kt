package com.bkt.advlibrary.bind

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.ImageViewCompat
import androidx.databinding.BindingAdapter
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.*
import androidx.recyclerview.widget.ListAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.bkt.advlibrary.*
import com.bkt.advlibrary.ActivityExtKt.scanForActivity
import com.google.android.material.tabs.TabLayout
import java.math.BigDecimal
import java.util.*

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

@BindingAdapter("app:dateTimeFieldEnabled")
fun setAsDateAndTimeField(
    et: EditText,
    isDateEnabled: Boolean = false
) {
    if (isDateEnabled)
        et.setDateAndTimePicker()
}

fun EditText.setDateAndTimePicker(
    startDate: Date = Calendar.getInstance().time,
    format: String = DateFormats.DD_MMM_YYYY_TIME,
    setAsMin: Boolean = false,
    onSelect: (Date) -> Unit = {}
) {
    val calendar = Calendar.getInstance()
    calendar.time = startDate
    this.isFocusable = false
    this.setText(startDate.format(format))
    this.setOnClickListener {
        val currentDate = this.getTrimText().toDate(format)!!
        calendar.time = currentDate
        val datePickerDialog = DatePickerDialog(
            it.context,
            { _, year, month, dayOfMonth ->
                calendar[Calendar.YEAR] = year
                calendar[Calendar.MONTH] = month
                calendar[Calendar.DAY_OF_MONTH] = dayOfMonth

                this.startTimePicker(calendar) { date ->
                    this.setText(date.format(format))
                    onSelect.invoke(date)
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        if (setAsMin) {
            val datePicker = datePickerDialog.datePicker
            datePicker.minDate = startDate.time
        }
        datePickerDialog.show()
    }
}

fun EditText.startTimePicker(calendar: Calendar, onSelect: (Date) -> Unit) {
    val timePicker = TimePickerDialog(
        this.context,
        { _, h, m ->
            calendar[Calendar.HOUR_OF_DAY] = h
            calendar[Calendar.MINUTE] = m
            onSelect.invoke(calendar.time)
        },
        calendar[Calendar.HOUR_OF_DAY],
        calendar[Calendar.MINUTE],
        false
    )
    timePicker.show()
}

@BindingAdapter("app:timeFieldEnabled")
fun setAsTimeField(
    et: EditText,
    isTimePicker: Boolean = false
) {
    if (isTimePicker) {
        val calendar = Calendar.getInstance()
        et.isFocusable = false
        if (et.getTrimText().isEmpty())
            et.setText(calendar.time.format(DateFormats.TIME))
        else
            calendar.time = et.getTrimText().toDate(DateFormats.TIME)!!
        et.setOnClickListener {
            val currentDate = et.getTrimText().toDate(DateFormats.TIME)!!
            calendar.time = currentDate
            et.startTimePicker(calendar) {
                et.setText(calendar.time.format(DateFormats.TIME))
            }
        }
    }
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
    "app:reverseLayout",
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