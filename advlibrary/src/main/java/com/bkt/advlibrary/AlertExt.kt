package com.bkt.advlibrary

import android.content.Context
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.bkt.advlibrary.databinding.DialogEdittextBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun Context.showConfirmDialog(
    title: CharSequence,
    body: CharSequence = "",
    yesButtonText: CharSequence = "Yes",
    noButtonText: CharSequence = "No",
    onConfirmed: () -> Unit
) {
    val dialog =
        MaterialAlertDialogBuilder(this).setTitle(title)
            .setMessage(body)
            .setPositiveButton(yesButtonText) { _, _ ->
                onConfirmed.invoke()
            }.setNegativeButton(noButtonText, null)
            .setCancelable(false)
    dialog.show()
}

fun Context.showFieldDialog(
    title: CharSequence,
    body: CharSequence = "",
    vararg params: EditTextProperty,
    onConfirmed: (List<String>) -> Boolean
) {
    val binding = DataBindingUtil.inflate(
        LayoutInflater.from(this),
        R.layout.dialog_edittext,
        null,
        false
    ) as DialogEdittextBinding
    val etList = ArrayList<EditText>()
    for (property in params) {
        val et = binding.mainContainer.addEditText(property)
        etList.add(et)
    }

    val dialog =
        MaterialAlertDialogBuilder(this).setTitle(title)
            .setMessage(body)
            .setView(binding.root)
            .setPositiveButton("Yes", null)
            .setNegativeButton("No", null)
            .setCancelable(false)
    val d = dialog.show()
    d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
        if (onConfirmed.invoke(etList.map { it.getTrimText() }))
            d.dismiss()
    }
}