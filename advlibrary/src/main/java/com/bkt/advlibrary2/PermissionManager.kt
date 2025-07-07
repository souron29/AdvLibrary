package com.bkt.advlibrary2

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import java.util.*

class PermissionManager private constructor(
    private val activity: CommonActivity,
    private vararg val permissions: String
) {
    private var onResult: (allGranted: Boolean, grantedPermissions: ArrayList<String>, allDenied: Boolean, deniedPermissions: ArrayList<String>) -> Unit =
        { _, _, _, _ -> }
    private val requestCode by lazy { Random().nextInt(99999) }

    companion object {
        operator fun get(activity: CommonActivity, vararg permissions: String): PermissionManager {
            return PermissionManager(activity, *permissions)
        }
    }

    fun ask(onResponse: (Boolean, ArrayList<String>, Boolean, ArrayList<String>) -> Unit = { _, _, _, _ -> }) {
        onResult = onResponse
        bgLaunch {
            val hasPermission = permissions.all {
                ActivityCompat.checkSelfPermission(
                    activity,
                    it
                ) == PackageManager.PERMISSION_GRANTED
            }
            if (hasPermission) {
                mainLaunch {
                    onResult.invoke(
                        true,
                        ArrayList(permissions.asList()),
                        false,
                        ArrayList()
                    )
                }
            } else {
                setResultProcessor()
                ActivityCompat.requestPermissions(activity, permissions, requestCode)
            }
        }
    }

    private fun setResultProcessor() {
        activity.addPermissionResultListener { requestCode, permissions, grantResults ->
            var output = false
            if (requestCode == this.requestCode && grantResults.isNotEmpty()) {
                val grantedPermissions = ArrayList<String>()
                val deniedPermissions = ArrayList<String>()
                permissions.forEachIndexed { index, permission ->
                    if (grantResults[index] == PackageManager.PERMISSION_GRANTED)
                        grantedPermissions.add(permission)
                    else deniedPermissions.add(permission)
                }
                output = true
                val allGranted = grantedPermissions.size == permissions.size
                val allDenied = deniedPermissions.size == permissions.size
                this.onResult.invoke(allGranted, grantedPermissions, allDenied, deniedPermissions)
            }
            output
        }
    }

}

fun Context.hasPermissions(vararg permissions: String) = permissions.all { permission ->
    checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
}