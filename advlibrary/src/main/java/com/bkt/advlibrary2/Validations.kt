package com.bkt.advlibrary

import android.content.Context
import android.widget.EditText
import com.bkt.advlibrary.ActivityExtKt.toast

fun shouldNotBeEmpty(
    context: Context,
    vararg ets: EditText,
    onError: (field: String) -> Unit = { field -> context.toast("$field cannot be empty") }
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
    context: Context,
    vararg ets: EditText,
    onError: (field: String) -> Unit = { field -> context.toast("$field cannot be zero or negative") }
): Boolean {
    for (et in ets) {
        if (et.getTrimText().toDoubleOr(0.0) <= 0.0) {
            onError.invoke(et.hint?.toString() ?: "")
            return false
        }
    }
    return true
}

fun shouldBeMinimumLength(
    context: Context, minLength: Int, vararg ets: EditText,
    onError: (field: String) -> Unit = { field -> context.toast("$field cannot be less than $minLength characters") }
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
    context: Context, min: T, vararg ets: EditText,
    onError: (field: String) -> Unit = { field ->
        context.toast(
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