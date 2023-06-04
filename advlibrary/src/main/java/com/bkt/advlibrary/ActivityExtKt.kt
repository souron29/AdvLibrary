package com.bkt.advlibrary

import android.Manifest
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.TypedValue
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.*
import androidx.annotation.IntRange
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.FragmentActivity
import com.bkt.advlibrary.ActivityExtKt.vibrateOnce

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
        return setColorTint(
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
    fun Context.vibrateOnce(
        time: Long,
        @IntRange(
            from = 1,
            to = 255
        ) amplitude: Int = 255
    ) {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT < 26) {
            vibrator.vibrate(time)
        } else {
            vibrator.vibrate(VibrationEffect.createOneShot(time, amplitude))
        }
    }

    @RequiresPermission(Manifest.permission.VIBRATE)
    fun Context.vibrateWaveform(timings: LongArray, amplitudes: IntArray, repeat: Int = -1) {
        val vibrator = if (Build.VERSION.SDK_INT > 31) {
            val vibratorManager =
                this.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        vibrator.cancel()
        vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, repeat))
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

    fun Context.scanForActivity(): AppCompatActivity? {
        return when (this) {
            is AppCompatActivity -> this
            is ContextWrapper -> baseContext.scanForActivity()
            else -> null
        }
    }

    fun AppCompatActivity.setStatusBarColorTo(@ColorRes colorId: Int) {
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        // finally change the color
        window.statusBarColor = ContextCompat.getColor(this, colorId)
    }
}

fun Context.copyToClipboard(content: String) {
    val clipboardManager = ContextCompat.getSystemService(this, ClipboardManager::class.java)!!
    val clip = ClipData.newPlainText("clipboard", content)
    clipboardManager.setPrimaryClip(clip)
}


fun Context.dpToPx(dp: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
}

fun Context.spToPx(sp: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, resources.displayMetrics)
}

fun Context.getDimen(@DimenRes id: Int): Float {
    return resources.getDimension(id) / resources.displayMetrics.density
}

@RequiresPermission(Manifest.permission.VIBRATE)
fun Context.errorVibrate() {
    vibrateOnce(500, 255)
}

@RequiresPermission(Manifest.permission.VIBRATE)
fun Context.successVibrate() {
    vibrateOnce(100, 100)
}

object ClipManager {

    fun getText(context: Context): CharSequence? {
        val manager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        return manager.primaryClip?.getItemAt(0)?.coerceToText(context)
    }

    fun pasteText(context: Context, text: CharSequence, label: CharSequence = "Text") {
        val clip = ClipData.newPlainText(label, text)
        val manager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        manager.setPrimaryClip(clip)
    }

}