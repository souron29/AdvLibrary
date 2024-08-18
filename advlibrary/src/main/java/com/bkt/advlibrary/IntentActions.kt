package com.bkt.advlibrary

import android.content.Context
import android.content.Intent
import android.net.Uri

object IntentActions {
    /**
     * Shows a list of messaging apps
     */
    fun sendTextMessage(context: Context, number: String) {
        val uri = Uri.parse("smsto:+91$number")
        val i = Intent(Intent.ACTION_SENDTO, uri)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(Intent.createChooser(i, "Send Message"))
    }

    /**
     * Displays the dialer with [number] typed in
     */
    fun showDialer(context: Context, number: String) {
        val uri = "tel:$number"
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse(uri)
        context.startActivity(intent)
    }

    /**
     * Displays an UI to select single or Multiple Files
     */
    fun showMultipleFileChooser(
        activity: CommonActivity,
        mimeType: MimeType = MimeType.ALL,
        onFilesReceived: (List<Uri>) -> Unit
    ) {
        /*val chooseFile = Intent(Intent.ACTION_GET_CONTENT)
        chooseFile.type = MimeType.ALL.mimeTypeText
        chooseFile.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        val intent = Intent.createChooser(chooseFile, "Choose a file")*/

        activity.launchForMultipleContent(mimeType) { listOfUris ->
            onFilesReceived.invoke(listOfUris)
        }
    }

    enum class MimeType(val mimeTypeText: String) {
        ALL("*/*"), IMAGE("image/*"), TEXT_PLAN("text/plain"),
        DOCUMENT_ALL("application/*"), DOCUMENT_PDF("application/pdf")

        ;

        fun getSpecificType(type: String): String {
            return this.mimeTypeText.replace("/*", "/$type")
        }
    }
}