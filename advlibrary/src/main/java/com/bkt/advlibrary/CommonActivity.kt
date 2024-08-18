package com.bkt.advlibrary

import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner

open class CommonActivity : AppCompatActivity(), LifecycleOwner {
    private var onPermissionsResultListeners =
        ArrayList<(Int, Array<out String>, IntArray) -> Boolean>()

    /**
     * Single usage listener to be used and destroyed
     */
    private var multipleContentListener: ((List<Uri>) -> Unit)? = null
    private val multipleContentLauncher = registerForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { result ->
        this.multipleContentListener?.invoke(result)
        this.multipleContentListener = null
    }

    /**
     * Single usage content listener. Thrown away after use
     */
    private var singleContentListener: ((Uri?) -> Unit)? = null
    private val startActivityForContentLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) {
        this.singleContentListener?.invoke(it)
        this.singleContentListener = null
    }

    /**
     * We can crete custom contracts by inheriting class ActivityResultContract
     */
    private var activityResultListener: ((ActivityResult) -> Unit)? = null
    private val startActivityForResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        this.activityResultListener?.invoke(it)
        this.activityResultListener = null
    }

    private var singlePermissionListener: ((Boolean) -> Unit)? = null
    private val startActivityForSinglePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        this.singlePermissionListener?.invoke(it)
        this.singlePermissionListener = null
    }

    private var multiplePermissionListener: ((Map<String, Boolean>) -> Unit)? = null
    private val startActivityForMultiplePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        this.multiplePermissionListener?.invoke(it)
        this.multiplePermissionListener = null
    }

    fun replaceFragment(fragment: CommonFragment, container_id: Int) {
        Handler(Looper.getMainLooper()).post {
            try {
                if (!supportFragmentManager.isDestroyed && !isDestroyed)
                    supportFragmentManager.beginTransaction()
                        .replace(container_id, fragment as Fragment, fragment.fragmentName)
                        .commitNowAllowingStateLoss()
            } catch (e: Exception) {
                logger("Error ${e.message} for ${fragment.fragmentName}")
            }

        }
    }

    fun loadFragment(
        fragment: CommonFragment,
        container_id: Int,
        addCurrentToBackStack: Boolean = true
    ) {
        Handler(Looper.getMainLooper()).post {
            try {
                if (!supportFragmentManager.isDestroyed && !isDestroyed) {
                    val txn = supportFragmentManager.beginTransaction()
                        .replace(container_id, fragment as Fragment)
                    if (addCurrentToBackStack)
                        txn.addToBackStack(fragment.fragmentName)
                    txn.commit()
                }
            } catch (e: Exception) {
                logger("Error ${e.message} for ${fragment.fragmentName}")
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
        checkAndPopFrag()
    }

    private fun checkAndPopFrag(
        indexOfFragToPop: Int = supportFragmentManager.fragments.lastIndex
    ) {
        val fragList = supportFragmentManager.fragments
        val lastFrag = fragList.getOrNull(indexOfFragToPop)

        if (lastFrag == null)
            super.onBackPressed()
        else if (lastFrag !is CommonFragment) {

            val popped = supportFragmentManager.popBackStackImmediate()
            // using to detect glide SupportRequestManagerFragment (sticky fragment)
            if (!popped) {
                checkAndPopFrag(indexOfFragToPop - 1)
            }
        } else if (lastFrag.isAdded && !lastFrag.backPressHandled()) {
            val count = supportFragmentManager.backStackEntryCount
            when {
                count > 1 -> supportFragmentManager.popBackStack()
                else -> {
                    finish()
                }
            }
        }
    }

    private fun getLastFrag(id: Int): Fragment? {
        return supportFragmentManager.findFragmentById(id)
    }

    fun getFragAt(activity: CommonActivity, position: Int): Fragment? {
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

    fun addPermissionResultListener(listener: (Int, Array<out String>, IntArray) -> Boolean) {
        onPermissionsResultListeners.add(listener)
    }

    /**
     * Since your process and activity can be destroyed between when you call launch() and
     * when the onActivityResult() callback is triggered, any additional state needed to handle the
     * result must be saved and restored separately from these APIs.
     */
    fun launchForMultipleContent(
        mimeType: IntentActions.MimeType,
        listener: ((List<Uri>) -> Unit)? = null
    ) {
        this.multipleContentListener = listener
        this.multipleContentLauncher.launch(mimeType.mimeTypeText)
    }

    fun launchForSingleContent(mime: String, listener: (Uri?) -> Unit) {
        this.singleContentListener = listener
        this.startActivityForContentLauncher.launch(mime)
    }

    /**
     * Pass in the mime type you want to let the user select
     * as the input
     */
    fun launchForSingleContent(mimeType: IntentActions.MimeType, listener: (Uri?) -> Unit) {
        this.singleContentListener = listener
        this.startActivityForContentLauncher.launch(mimeType.mimeTypeText)
    }

    fun launchForResult(intent: Intent, listener: (ActivityResult) -> Unit) {
        this.activityResultListener = listener
        this.startActivityForResultLauncher.launch(intent)
    }

    fun launchForSinglePermission(permission: String, listener: ((Boolean) -> Unit)) {
        this.singlePermissionListener = listener
        this.startActivityForSinglePermissionLauncher.launch(permission)
    }

    fun launchForMultiplePermission(
        permissions: Array<String>,
        listener: ((Map<String, Boolean>) -> Unit)
    ) {
        this.multiplePermissionListener = listener
        this.startActivityForMultiplePermissionLauncher.launch(permissions)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val handled =
            onPermissionsResultListeners.any { it.invoke(requestCode, permissions, grantResults) }

        if (!handled)
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    // Sometimes activity might be destroyed due to low memory

    /*override fun onDestroy() {
        super.onDestroy()
        this.singleContentListener = null
        this.primaryActivityResultListener = null
    }*/
}