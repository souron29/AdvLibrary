package com.bkt.advlibrary

import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.bkt.advlibrary.ActivityExtKt.toast
import com.bkt.advlibrary.GeneralExtKt.toDouble
import com.bkt.advlibrary.GeneralExtKt.toText

fun shouldNotBeEmpty(
    activity: AppCompatActivity,
    vararg ets: EditText,
    onError: (field: String) -> Unit = { field -> activity.toast("$field cannot be empty") }
): Boolean {
    for (et in ets) {
        if (et.getTrimText().isEmpty()) {
            onError.invoke(et.hint?.toString() ?: "")
            return false
        }
    }
    return true
}

fun shouldNotBeNegative(
    activity: AppCompatActivity,
    vararg ets: EditText,
    onError: (field: String) -> Unit = { field -> activity.toast("$field cannot be zero or negative") }
): Boolean {
    for (et in ets) {
        if (et.getTrimText().toDouble(0.0) <= 0.0) {
            onError.invoke(et.hint?.toString() ?: "")
            return false
        }
    }
    return true
}

fun shouldBeMinimumLength(
    activity: AppCompatActivity, minLength: Int, vararg ets: EditText,
    onError: (field: String) -> Unit = { field -> activity.toast("$field cannot be less than $minLength characters") }
): Boolean {
    for (et in ets) {
        if (et.getTrimText().length < minLength) {
            onError.invoke(et.hint?.toString() ?: "")
            return false
        }
    }
    return true
}

fun <T : Number> shouldBeMinimum(
    activity: AppCompatActivity, min: T, vararg ets: EditText,
    onError: (field: String) -> Unit = { field ->
        activity.toast(
            "$field cannot be less than ${
                min.toDouble().toText()
            } "
        )
    }
): Boolean {
    for (et in ets) {
        if (et.getTrimText().toDouble() < min.toDouble()) {
            onError.invoke(et.hint?.toString() ?: "")
            return false
        }

    }
    return true
}

fun Boolean.ifTrue(block: () -> Boolean): Boolean {
    if (this)
        return block.invoke()
    return this
}