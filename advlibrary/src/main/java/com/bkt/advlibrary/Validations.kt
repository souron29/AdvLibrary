package library.extensions

import android.widget.EditText
import library.extensions.ActivityExtKt.toast
import library.extensions.GeneralExtKt.toDouble
import library.extensions.GeneralExtKt.toText
import java.lang.Exception

fun shouldNotBeEmpty(
    activity: AdvActivity,
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
    activity: AdvActivity,
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
    activity: AdvActivity, minLength: Int, vararg ets: EditText,
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
    activity: AdvActivity, min: T, vararg ets: EditText,
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