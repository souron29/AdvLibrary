package com.bkt.advlibrary

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.view.setPadding
import androidx.core.widget.addTextChangedListener
import com.bkt.advlibrary.ActivityExtKt.scanForActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/*val dateFormats = mutableMapOf<Int, String>()

var EditText.dateFormat: String
    get() = dateFormats[this.id] ?: DateFormats.DATE.format
    set(value) {
        dateFormats[this.id] = value
    }*/

fun EditText.setDate(date: Date, format: String = DateFormats.DATE.format) {
    val df = SimpleDateFormat(format, Locale.US)
    df.timeZone = timeZoneIST
    setText(df.format(date))
}

fun EditText.getDate(format: String = DateFormats.DATE.format): Date? {
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

fun EditText.setupDatePicker(
    startDate: Date?,
    showTimePicker: Boolean = false,
    minDate: Date? = null,
    clickable: Boolean = true,
    format: String = DateFormats.DD_MMM_YYYY.format,
    onSelect: (Date) -> Unit = {}
) {
    this.setText(startDate?.format(format) ?: "")
    this.isFocusable = false

    if (!clickable)
        return

    val calendar = Calendar.getInstance()
    startDate?.let { calendar.time = it }

    val showPicker = {
        this.getDate(format)?.let {
            calendar.time = it
        }

        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar[Calendar.YEAR] = year
                calendar[Calendar.MONTH] = month
                calendar[Calendar.DAY_OF_MONTH] = dayOfMonth

                // Show time picker if required
                if (showTimePicker) {
                    val timePicker = TimePickerDialog(
                        this.context,
                        { _, h, m ->
                            calendar[Calendar.HOUR_OF_DAY] = h
                            calendar[Calendar.MINUTE] = m
                            onSelect.invoke(calendar.time)
                            this.setText(calendar.time.format(format))
                        },
                        calendar[Calendar.HOUR_OF_DAY],
                        calendar[Calendar.MINUTE],
                        false
                    )
                    timePicker.show()
                } else {
                    onSelect.invoke(calendar.time)
                    this.setText(calendar.time.format(format))
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        minDate?.time?.let {
            val datePicker = datePickerDialog.datePicker
            datePicker.minDate = minDate.time
        }
        datePickerDialog.show()
    }

    setOnClickListener {
        showPicker.invoke()
    }
    setOnFocusChangeListener { _, hasFocus ->
        if (hasFocus)
            showPicker.invoke()
    }
}


fun EditText.getTrimText(): String {
    return text?.trim().toString()
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

fun EditText?.setTextWatcher(block: (CharSequence?) -> Unit) {
    this?.addTextChangedListener {
        block.invoke(it)
    }
}

fun ViewGroup.addEditText(et: EditTextProperty): EditText {
    val style = com.google.android.material.R.style.Widget_MaterialComponents_TextInputLayout_OutlinedBox
    val textInputLayout = TextInputLayout(context.scanForActivity()!!, null, style)

    val layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )
    textInputLayout.hint = et.hint
    textInputLayout.layoutParams = layoutParams
    textInputLayout.setPadding(20)
    textInputLayout.boxBackgroundMode = TextInputLayout.BOX_BACKGROUND_OUTLINE
    textInputLayout.boxStrokeColor = et.color

    val textInputEditText = TextInputEditText(context.scanForActivity()!!)
    textInputEditText.layoutParams = layoutParams
    textInputEditText.setPadding(20)

    et.setProperties(textInputEditText)

    textInputLayout.addView(textInputEditText, layoutParams)
    this.addView(textInputLayout)
    return textInputEditText
}

data class EditTextProperty(
    var hint: String = "",
    var isDateField: Boolean = false,
    var isMoneyField: Boolean = false,
    @ColorInt var color: Int = Color.BLACK,
    val onFieldCreated: (EditText) -> Unit = {}
) {
    val text = LiveObject("")

    fun setProperties(editText: TextInputEditText) {
        editText.setTextColor(color)
        editText.background = null
        if (isDateField) {
            editText.setupDatePicker(null)
            editText.inputType =
                InputType.TYPE_CLASS_DATETIME or InputType.TYPE_DATETIME_VARIATION_DATE
        }
        if (isMoneyField) {
            editText.inputType =
                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_DECIMAL
        }

        if (text.value.isNotEmpty())
            editText.setText(text.value)
        onFieldCreated.invoke(editText)
    }
}