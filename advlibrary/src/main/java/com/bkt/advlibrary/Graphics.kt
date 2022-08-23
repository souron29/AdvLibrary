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
import androidx.core.content.ContextCompat
import java.io.IOException
import java.util.ArrayList

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

fun Context.getColorStateList(vararg colorStates: ColorState): ColorStateList {
    val statesList = ArrayList<IntArray>()
    val colorList = ArrayList<Int>()

    colorStates.forEach {
        statesList.add(it.states.map { state -> state.value }.toIntArray())
        colorList.add(ContextCompat.getColor(this, it.colorId))
    }
    return ColorStateList(statesList.toTypedArray(), colorList.toIntArray())
}

class ColorState(@ColorRes val colorId: Int, vararg val states: State)

enum class State(val value: Int) {
    STATE_ENABLED(android.R.attr.state_enabled),
    STATE_NOT_ENABLED(-android.R.attr.state_enabled),
    STATE_CHECKED(android.R.attr.state_checked),
    STATE_NOT_CHECKED(-android.R.attr.state_checked),
    STATE_PRESSED(android.R.attr.state_pressed),
    STATE_NOT_PRESSED(-android.R.attr.state_pressed),
    STATE_CHECKABLE(android.R.attr.state_checkable),
    STATE_NOT_CHECKABLE(-android.R.attr.state_checkable),
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