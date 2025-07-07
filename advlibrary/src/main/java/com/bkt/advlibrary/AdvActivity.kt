package com.bkt.advlibrary

import android.os.Bundle
import androidx.annotation.LayoutRes

abstract class AdvActivity(
    @LayoutRes private val layoutId: Int
) : CommonActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        afterCreate()
    }

    abstract fun afterCreate()

}