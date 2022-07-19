package com.bkt.advlibrary

import android.app.DatePickerDialog
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

fun ViewGroup.addEditText(et: EditTextProperty): EditText {
    val style = R.style.Widget_MaterialComponents_TextInputLayout_OutlinedBox
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
    @ColorInt var color: Int = Color.BLACK
) {
    val text = LiveObject("")


    fun setProperties(editText: TextInputEditText) {
        editText.setTextColor(color)
        editText.background = null
        if (isDateField) {
            editText.setDatePicker()
            editText.inputType =
                InputType.TYPE_CLASS_DATETIME or InputType.TYPE_DATETIME_VARIATION_DATE
        }
        if (isMoneyField) {
            editText.inputType =
                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_DECIMAL
        }
    }
}