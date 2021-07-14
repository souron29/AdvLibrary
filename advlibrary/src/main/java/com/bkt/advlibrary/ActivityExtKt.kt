package com.bkt.advlibrary

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresPermission
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import com.bkt.advlibrary.AdvActivity

object ActivityExtKt {

    fun AdvActivity.toast(string: Any, longToast: Boolean = false) {
        val time = if (longToast) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
        if (string is Exception) {
            Toast.makeText(this, string.message, time).show()
        } else {
            Toast.makeText(this, string.toString(), time).show()
        }
    }

    fun Context.checkPermission(vararg permissions: String): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    fun Context.getTintedDrawable(@DrawableRes id: Int, @ColorRes color_id: Int): Drawable {
        return Images.setColorTint(
            AppCompatResources.getDrawable(this, id), getColor(color_id)
        )
    }

    fun Context.getApplicationName(): String {
        val stringId: Int = applicationInfo.labelRes
        if (stringId == 0) {
            return applicationInfo.nonLocalizedLabel.toString()
        }
        return getString(stringId)
    }
    @RequiresPermission(Manifest.permission.VIBRATE)
    fun AdvActivity.vibrateOnce(time: Long) {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT < 26) {
            vibrator.vibrate(time)
        } else {
            vibrator.vibrate(VibrationEffect.createOneShot(time, -1))
        }
    }

    fun AdvActivity.restart() {
        finish()
        overridePendingTransition(0, 0)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }
}