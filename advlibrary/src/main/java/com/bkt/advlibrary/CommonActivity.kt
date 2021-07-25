package com.bkt.advlibrary

import android.os.Handler
import android.os.Looper
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import java.lang.Exception

open class CommonActivity : AppCompatActivity(), LifecycleOwner {
    var onPermissionsResult: (
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) -> Boolean =
        { _, _, _ ->
            false
        }

    fun loadFragment(fragment: CommonFragment, container_id: Int, removeCurrent: Boolean = false) {
        Handler(Looper.getMainLooper()).post {
            try {
                if (!supportFragmentManager.isDestroyed&&!isDestroyed)
                    supportFragmentManager.beginTransaction()
                        .replace(container_id, fragment as Fragment, fragment.fragmentName)
                        .commitNowAllowingStateLoss()
            }catch (e: Exception){
                GeneralExtKt.logger("Error ${e.message} for ${fragment.fragmentName}")
            }

        }
    }

    fun hideKeyboard() {
        currentFocus?.let { view ->
            val systemService = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            systemService.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun showKeyboard() {
        val service = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        service.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    override fun onBackPressed() {
        val lastFrag = supportFragmentManager.fragments.lastOrNull() as CommonFragment?
        if (lastFrag != null && lastFrag.isAdded && !lastFrag.backPressHandled()) {
            if (lastFrag.stackCount != 0 && !lastFrag.backPressHandled()) {
                lastFrag.childFragmentManager.popBackStackImmediate()
            } else if ( supportFragmentManager.backStackEntryCount == 1) {
                finish()
            } else {
                super.onBackPressed()
            }
        }
    }

    private fun getLastFrag(id: Int): Fragment? {
        return supportFragmentManager.findFragmentById(id)
    }

    fun getFragAt(activity: AdvActivity, position: Int): Fragment? {
        val fm = activity.supportFragmentManager
        val supportFragmentManager = activity.supportFragmentManager
        if (supportFragmentManager.backStackEntryCount <= position) {
            return null
        }
        val backEntry = fm.getBackStackEntryAt(position)
        return fm.findFragmentByTag(backEntry.name)
    }

    fun toast(text: String, longToast: Boolean = true) {
        val duration = if (longToast) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
        Toast.makeText(this, text, duration).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (!onPermissionsResult.invoke(requestCode, permissions, grantResults))
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}