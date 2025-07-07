package com.bkt.advlibrary2

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

abstract class AdvFragment :
    CommonFragment() {
    var fragmentView: View? = null
        private set

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflate = inflater.inflate(super.properties.layoutId, container, false)
        fragmentView = inflate
        return inflate
    }
}
