package com.bkt.advlibrary

import android.app.DatePickerDialog
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import com.bkt.advlibrary.DateFormats
import com.bkt.advlibrary.GeneralExtKt.toText
import java.text.SimpleDateFormat
import java.util.*

val dateFormats = mutableMapOf<Int, String>()

var EditText.dateFormat: String
    get() = dateFormats[this.id] ?: DateFormats.DATE
    set(value) {
        dateFormats[this.id] = value
    }

fun EditText.setDate(date: Date, format: String = this.dateFormat) {
    val df = SimpleDateFormat(format, Locale.US)
    df.timeZone = timeZoneIST
    setText(df.format(date))
    this.dateFormat = format
}

fun EditText.getDate(format: String = this.dateFormat): Date? {
    val text = getTrimText()
    if (text.isNotEmpty()) {
        return try {
            val df = SimpleDateFormat(format, Locale.US)
            df.timeZone = timeZoneIST
            df.parse(text)
        } catch (e: Exception) {
            null
        }
    }
    return null
}

fun EditText.setDatePicker(
    startDate: Date = Calendar.getInstance().time,
    setAsMin: Boolean = false,
    onSelect: (Date) -> Unit = {}
) {
    val calendar = Calendar.getInstance()
    calendar.time = startDate
    this.isFocusable = false
    this.setText(startDate.format(dateFormat))
    this.setOnClickListener {
        val date = this.getDate()
        if (date != null) {
            calendar.time = date
        }
        val datePickerDialog = DatePickerDialog(
            it.context,
            { _, year, month, dayOfMonth ->
                calendar[Calendar.YEAR] = year
                calendar[Calendar.MONTH] = month
                calendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                onSelect.invoke(calendar.time)
                this.setText(calendar.time.format(dateFormat))
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


fun EditText.getTrimText(): String {
    return text?.trim().toString() ?: ""
}

fun EditText.getInt(valueIfNull: Int = 0): Int {
    return getTrimText().toIntOrNull() ?: valueIfNull
}

fun EditText.setTextChangeListener(onTextChange: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable?) {
            onTextChange.invoke(s?.toString() ?: "")
        }
    })
}

fun EditText.assign(value: Any?) {
    if (value is String?)
        this.setText(value)
    if (value is Int?)
        this.setText(value?.toString())
    if (value is Double?)
        this.setText(value?.toText())
    if (value is Date? && value != null)
        this.setDate(value)
}

fun TextView.assignIf(condition: Boolean, valueIf: Any?, valueElse: Any? = null) {
    if (condition)
        when (this) {
            is EditText -> this.assign(valueIf)
            else -> text = valueIf?.toString()
        }
    else
        when (this) {
            is EditText -> this.assign(valueElse)
            else -> text = valueElse?.toString()
        }
}

fun EditText?.setTextWatcher(block:(CharSequence?)->Unit){
    this?.addTextChangedListener {
        block.invoke(it)
    }
}