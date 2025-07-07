package com.bkt.advlibrary.bind

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import com.bkt.advlibrary.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BindSheet<T : ViewDataBinding>(
    @LayoutRes private val layoutId: Int,
    private val onCreate: (T, BindSheet<T>) -> Unit = { _, _ -> }
) :
    BottomSheetDialogFragment() {

    private var _bind: T? = null
    val binding get() = _bind!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = DataBindingUtil.inflate(inflater, layoutId, container, false)
        _bind!!.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    inline fun <reified VM : FragBinderModel> getModel(java: Class<VM>): VM {
        val vm by viewModels<VM>()
        return vm
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setStyle(STYLE_NO_FRAME, R.style.BottomSheetDialog)
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        onCreate.invoke(binding, this)
    }
}