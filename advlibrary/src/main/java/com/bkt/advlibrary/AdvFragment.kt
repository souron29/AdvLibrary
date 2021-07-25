package com.bkt.advlibrary

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

abstract class AdvFragment(override val fragmentName: String, @LayoutRes private val layoutId: Int) :
    CommonFragment(fragmentName) {
    private var currentChildFrag: AdvFragment? = null
    var fragmentView: View? = null
        private set
    var isHome = false
        private set
    var isParent = false
        private set
    var isPhantom = false
        private set

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflate = inflater.inflate(layoutId, container, false)
        fragmentView = inflate
        return inflate
    }

    fun setAsPhantom(): AdvFragment {
        isPhantom = true
        return this
    }

    fun setAsHome(): AdvFragment {
        isHome = true
        isParent = true
        return this
    }

    fun setAsParent(): AdvFragment {
        isParent = true
        return this
    }

    fun <T : AdvFragment> loadChildFragmentDelayed(fragment: T, id: Int) {
        this.currentChildFrag = fragment
        Handler(Looper.getMainLooper()).postDelayed(
            { loadChildFragment(fragment, id) }, 300
        )
    }
}
