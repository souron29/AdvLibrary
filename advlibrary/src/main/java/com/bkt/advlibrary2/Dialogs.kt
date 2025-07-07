package com.bkt.advlibrary

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import java.util.*


fun Context.showDatePicker(startDate: Date? = null, onSelect: (Date) -> Unit) {
    val calendar = startDate?.getCalendar() ?: Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        this,
        { _, year, month, dayOfMonth ->
            calendar[Calendar.YEAR] = year
            calendar[Calendar.MONTH] = month
            calendar[Calendar.DAY_OF_MONTH] = dayOfMonth
            onSelect.invoke(calendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    datePickerDialog.show()
}

fun Context.showTimePicker(
    title: CharSequence = "Select Time",
    startDate: Date? = null,
    is24HourView: Boolean = false,
    onSelect: (Date) -> Unit
) {
    val calendar = startDate?.getCalendar() ?: Calendar.getInstance()
    val hour = calendar[Calendar.HOUR_OF_DAY]
    val minute = calendar[Calendar.MINUTE]
    val mTimePicker = TimePickerDialog(
        this,
        { _, selectedHour, selectedMinute ->

            calendar[Calendar.HOUR_OF_DAY] = selectedHour
            calendar[Calendar.MINUTE] = selectedMinute
            onSelect.invoke(calendar.time)
        },
        hour,
        minute,
        is24HourView
    )
    mTimePicker.setTitle(title)
    mTimePicker.show()

    val datePickerDialog = DatePickerDialog(
        this,
        { _, year, month, dayOfMonth ->
            calendar[Calendar.YEAR] = year
            calendar[Calendar.MONTH] = month
            calendar[Calendar.DAY_OF_MONTH] = dayOfMonth
            onSelect.invoke(calendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    datePickerDialog.show()
}