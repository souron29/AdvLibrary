package com.bkt.advlibrary

import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun AppCompatActivity.showConfirmDialog(
    title: String,
    body: String = "",
    yesButtonText: String = "Yes",
    noButtonText: String = "No",
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