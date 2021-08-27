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
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity

object ActivityExtKt {

    fun Context.toast(string: Any, longToast: Boolean = false) {
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

    fun Context.getTintedDrawable(@DrawableRes id: Int, @ColorRes color_id: Int): Drawable? {
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
    fun Context.vibrateOnce(time: Long) {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT < 26) {
            vibrator.vibrate(time)
        } else {
            vibrator.vibrate(VibrationEffect.createOneShot(time, -1))
        }
    }

    fun AppCompatActivity.restart() {
        finish()
        overridePendingTransition(0, 0)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    fun AppCompatActivity.switchToNightTheme() {
        delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
    }

    fun AppCompatActivity.switchToDayTheme() {
        delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
    }

    fun AppCompatActivity.switchToSystemTheme() {
        delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }
}