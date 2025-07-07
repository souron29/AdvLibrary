package com.bkt.advlibrary2.bind

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import com.bkt.advlibrary2.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AdvSheet<T : ViewDataBinding>(
    @LayoutRes private val layoutId: Int,
    private val onCreate: (b: T, sheet: BottomSheetDialogFragment) -> Unit = { _, _ -> }
) :
    BottomSheetDialogFragment() {
    private var _bind: T? = null
    val binding get() = _bind!!

    private var onCancelled: ((DialogInterface) -> Unit)? = null
    private var onDismissed: ((DialogInterface) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = DataBindingUtil.inflate(inflater, layoutId, container, false)
        _bind!!.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    inline fun <reified VM : BinderModel> getModel(): VM {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
    }

    final fun setOnCancel(onCancelListener: (DialogInterface) -> Unit) {
        this.onCancelled = onCancelListener
    }

    /**
     * This is invoked when
     * 1) The user presses back
     * 2) The user presses outside of the dialog
     *
     * This is not invoked on screen rotation and activity recreation
     */
    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        this.onCancelled?.invoke(dialog)
    }

    final fun setOnDismissed(onDismissListener: (DialogInterface) -> Unit) {
        this.onDismissed = onDismissListener
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        this.onDismissed?.invoke(dialog)
    }
}