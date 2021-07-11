package library

import android.view.View

/*fun View.disableIf(condition: Boolean) {
    if (condition)
        isEnabled = false
}

fun View.hideIf(
    condition: Boolean,
    @IntRange(
        from = View.INVISIBLE.toLong(),
        to = View.GONE.toLong()
    ) visibilityModifier: Int = View.GONE
) {
    if (condition)
        visibility = visibilityModifier
}*/

fun disableViews(vararg views: View) {
    for (view in views)
        view.isEnabled = false
}

fun hideViews(vararg views: View) {
    for (view in views)
        view.visibility = View.GONE
}