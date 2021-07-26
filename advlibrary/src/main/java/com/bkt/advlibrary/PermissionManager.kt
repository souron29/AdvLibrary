package com.bkt.advlibrary

import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import java.util.*
import kotlin.collections.ArrayList

class PermissionManager private constructor(
    private val activity: CommonActivity,
    private vararg val permissions: String
) {
    private var onResult: (Boolean, ArrayList<String>, Boolean, ArrayList<String>) -> Unit =
        { _, _, _, _ -> }
    private val requestCode by lazy { Random().nextInt(99999) }

    companion object {
        operator fun get(activity: CommonActivity, vararg permissions: String): PermissionManager {
            return PermissionManager(activity, *permissions)
        }
    }

    fun ask(onResponse: (Boolean, ArrayList<String>, Boolean, ArrayList<String>) -> Unit = { _, _, _, _ -> }) {
        this.onResult = onResponse
        bgLaunch {
            val hasPermission = permissions.all {
                ActivityCompat.checkSelfPermission(
                    activity,
                    it
                ) == PackageManager.PERMISSION_GRANTED
            }
            if (hasPermission) {
                mainLaunch {
                    this.onResult.invoke(
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
        activity.onPermissionsResult = { requestCode, permissions, grantResults ->
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