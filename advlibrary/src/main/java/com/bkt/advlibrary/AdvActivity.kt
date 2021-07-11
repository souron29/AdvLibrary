package library

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.os.Handler
import android.os.Looper
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

abstract class AdvActivity(
    @LayoutRes private val layoutId: Int,
    @IdRes private val containerId: Int = -1
) : AppCompatActivity(),
    LifecycleOwner {
    abstract fun afterCreate()
    var onPermissionsResult: (
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) -> Boolean =
        { _, _, _ ->
            false
        }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        afterCreate()
    }

    fun loadFragment(fragment: AdvFragment?, container_id2: Int, removeCurrent: Boolean = false) {
        Handler(Looper.getMainLooper()).post {
            supportFragmentManager.beginTransaction()
                .replace(container_id2, fragment as Fragment, fragment.fragmentName)
                .commit()
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
        val fm = supportFragmentManager
        val lastFrag = getLastFrag(fm, containerId) as AdvFragment?
        if (lastFrag != null && lastFrag.isAdded && !lastFrag.backPressHandled()) {
            if (lastFrag.stackCount != 0 && !lastFrag.backPressHandled()) {
                lastFrag.childFragmentManager.popBackStackImmediate()
            } else if (lastFrag.isHome || fm.backStackEntryCount == 1) {
                finish()
            } else {
                super.onBackPressed()
            }
        }
    }

    fun getLastFrag(fm: FragmentManager, id: Int): Fragment? {
        return if (fm.backStackEntryCount > 0) {
            fm.findFragmentById(id)
        } else null
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (!onPermissionsResult.invoke(requestCode, permissions, grantResults))
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}