package com.bkt.advlibrary

import android.content.Context
import android.net.Uri
import android.graphics.Bitmap
import android.provider.MediaStore
import java.io.IOException
import android.graphics.drawable.Drawable
import android.content.res.Resources.Theme
import java.lang.Exception
import android.graphics.drawable.BitmapDrawable
import android.graphics.Canvas
import androidx.appcompat.content.res.AppCompatResources
import android.graphics.PorterDuff
import android.util.TypedValue
import androidx.core.content.res.ResourcesCompat
import com.bkt.advlibrary.Images.resize

object Images {
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
}