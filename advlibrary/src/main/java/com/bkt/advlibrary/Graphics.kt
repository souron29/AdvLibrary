package com.bkt.advlibrary

import android.R
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.provider.MediaStore
import android.util.TypedValue
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.appcompat.content.res.AppCompatResources
import java.io.IOException

fun getBitmap(context: Context, image_uri: Uri?): Bitmap? {
    return try {
        MediaStore.Images.Media.getBitmap(context.contentResolver, image_uri)
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

fun dpToPx(context: Context, dp: Float): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context.resources.displayMetrics
    )
}

fun Drawable?.getBitmap(): Bitmap? {
    return (this as BitmapDrawable?)?.bitmap
}

fun Drawable?.resize(context: Context, height: Float, width: Float): BitmapDrawable? {
    this?.apply {
        return BitmapDrawable(
            context.resources,
            Bitmap.createScaledBitmap(
                this.getBitmap()!!,
                dpToPx(context, width).toInt(),
                dpToPx(context, height).toInt(),
                true
            )
        )
    }
    return null
}

fun getBitmapMarker(context: Context?, drawable: Int): Bitmap {
    val vectorDrawable = AppCompatResources.getDrawable(context!!, drawable)
    vectorDrawable!!.setBounds(
        0,
        0,
        vectorDrawable.intrinsicWidth,
        vectorDrawable.intrinsicHeight
    )
    val bitmap = Bitmap.createBitmap(
        vectorDrawable.intrinsicWidth,
        vectorDrawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    vectorDrawable.draw(Canvas(bitmap))
    return bitmap
}

fun getDrawable(context: Context, drawable_id: Int, color_id: Int): Drawable? {
    return setColorTint(
        AppCompatResources.getDrawable(context, drawable_id),
        context.getColor(color_id)
    )
}

fun setColorTint(context: Context, drawable_id: Int, color_id: Int): Drawable? {
    return setColorTint(
        AppCompatResources.getDrawable(context, drawable_id),
        context.getColor(color_id)
    )
}

fun setColorTint(drawable: Drawable?, color: Int): Drawable? {
    if (drawable != null) {
        drawable.mutate()
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }
    return drawable
}

fun Context.getColorStates(
    @ColorRes defaultColor: Int,
    @ColorRes disabledColor: Int = -1,
    @ColorRes checkedColor: Int = -1,
    @ColorRes unCheckedColor: Int = -1,
    @ColorRes pressedColor: Int = -1,
    @ColorRes unPressedColor: Int = -1,
    @ColorRes selectedColor: Int = -1,
    @ColorRes unSelectedColor: Int = -1

): ColorStateList {
    val states = ArrayList<IntArray>()
    val colors = ArrayList<Int>()

    if (disabledColor != -1) {
        states.add(intArrayOf(-android.R.attr.state_enabled))
        colors.add(getColor(disabledColor))
    }

    if (checkedColor != -1) {
        states.add(intArrayOf(android.R.attr.state_checked))
        colors.add(getColor(checkedColor))
    }
    if (unCheckedColor != -1) {
        states.add(intArrayOf(-android.R.attr.state_checked))
        colors.add(getColor(unCheckedColor))
    }
    if (pressedColor != -1) {
        states.add(intArrayOf(android.R.attr.state_pressed))
        colors.add(getColor(pressedColor))
    }
    if (unPressedColor != -1) {
        states.add(intArrayOf(-android.R.attr.state_pressed))
        colors.add(getColor(unPressedColor))
    }
    if (selectedColor != -1) {
        states.add(intArrayOf(android.R.attr.state_selected))
        colors.add(getColor(selectedColor))
    }
    if (unSelectedColor != -1) {
        states.add(intArrayOf(-android.R.attr.state_selected))
        colors.add(getColor(unSelectedColor))
    }
    states.add(intArrayOf())
    colors.add(getColor(defaultColor))
    return ColorStateList(states.toTypedArray(), colors.toIntArray())
}

fun getColorStates(
    @ColorInt primaryColor: Int,
    @ColorInt disabledColor: Int = -1,
    @ColorInt checkedColor: Int = -1,
    @ColorInt unCheckedColor: Int = -1,
    @ColorInt pressedColor: Int = -1,
    @ColorInt unPressedColor: Int = -1

): ColorStateList {
    val states = ArrayList<IntArray>()
    val colors = ArrayList<Int>()
    states.add(intArrayOf(R.attr.state_enabled))
    colors.add(primaryColor)

    if (disabledColor != -1) {
        states.add(intArrayOf(-R.attr.state_enabled))
        colors.add(disabledColor)
    }

    if (checkedColor != -1) {
        states.add(intArrayOf(R.attr.state_checked))
        colors.add(primaryColor)
    }
    if (unCheckedColor != -1) {
        states.add(intArrayOf(-R.attr.state_checked))
        colors.add(unCheckedColor)
    }
    if (pressedColor != -1) {
        states.add(intArrayOf(R.attr.state_pressed))
        colors.add(unCheckedColor)
    }
    if (unPressedColor != -1) {
        states.add(intArrayOf(-R.attr.state_pressed))
        colors.add(unCheckedColor)
    }
    return ColorStateList(states.toTypedArray(), colors.toIntArray())
}

fun Drawable.resizeDrawable(context: Context, width: Float, height: Float): Drawable {
    return LayerDrawable(arrayOf(this)).also {
        it.setLayerSize(
            0,
            dpToPx(context, width).toInt(),
            dpToPx(context, height).toInt()
        )
    }
}

fun Drawable.setColor(@ColorInt color: Int): Drawable {
    colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
    return this
}