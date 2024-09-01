package com.bkt.advlibrary

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import java.io.File

object IntentActions {
    /**
     * Shows a list of messaging apps
     */
    fun sendTextMessageTo(context: Context, number: String) {
        val uri = Uri.parse("smsto:+91$number")
        val i = Intent(Intent.ACTION_SENDTO, uri)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(Intent.createChooser(i, "Send Message"))
    }

    /**
     * Send text messages. Only use Strings and not Char sequences as some apps don't read char sequence
     */
    fun sendText(context: Context, body: String, subject: String = "") {
        val i = Intent(Intent.ACTION_SEND)
        i.type = MimeType.TEXT_PLAIN.mimeTypeText
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        i.putExtra(Intent.EXTRA_SUBJECT, subject)
        i.putExtra(Intent.EXTRA_TEXT, body)
        context.startActivity(Intent.createChooser(i, "Send Text"))
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

    fun Uri.getMimeType(): String? {
        val m = MimeTypeMap.getSingleton()
        val extension = MimeTypeMap.getFileExtensionFromUrl(this.toString())
        return m.getMimeTypeFromExtension(extension)
    }

    fun Uri.getExtension(): String? {
        return MimeTypeMap.getFileExtensionFromUrl(this.toString())
    }

    fun String.getExtensionFromMimeType(): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this)
    }


    /**
     * Simple method to open any file
     */
    fun openFile(activity: CommonActivity, uri: Uri) {
        val mimeType = uri.getMimeType()
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.setDataAndType(uri, mimeType)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        activity.startActivity(intent)
    }

    /**
     * Overloaded function with [File] based parameter
     */
    fun openFile(activity: CommonActivity, file: File, authority: String) {
        openFile(activity, FileProvider.getUriForFile(activity, authority, file))
    }

    fun share(
        activity: CommonActivity,
        authority: String,
        title: String? = null,
        mimeTypeText: String? = null,
        subject: String? = null,
        body: String? = null,
        htmlBody: String? = null,
        vararg attachments: File
    ): ShareCompat.IntentBuilder {
        return share(
            activity,
            title = title,
            mimeTypeText = mimeTypeText,
            subject = subject,
            body = body,
            htmlBody = htmlBody,
            *attachments.map { FileProvider.getUriForFile(activity, authority, it) }.toTypedArray()
        )
    }

    fun share(
        activity: CommonActivity,
        title: String? = null,
        mimeTypeText: String? = null,
        subject: String? = null,
        body: String? = null,
        htmlBody: String? = null,
        vararg attachmentUris: Uri
    ): ShareCompat.IntentBuilder {
        val b = ShareCompat.IntentBuilder(activity)
        // get the mime of first file
        var mime = mimeTypeText
        attachmentUris.forEach { uri ->
            if (mime == null)
                mime = uri.getMimeType()
            b.addStream(uri)
        }
        title?.let { b.setChooserTitle(it) }
        subject?.let { b.setSubject(it) }
        body?.let { b.setText(it) }
        htmlBody?.let { b.setHtmlText(it) }
        return b
    }

    enum class MimeType(val mimeTypeText: String) {
        ALL("*/*"), IMAGE("image/*"), TEXT_PLAIN("text/plain"),
        DOCUMENT_ALL("application/*"), DOCUMENT_PDF("application/pdf")

        ;

        fun getSpecificType(type: String): String {
            return this.mimeTypeText.replace("/*", "/$type")
        }
    }
}

class SharingOptions private constructor() {
    internal var emailsTo = ArrayList<String>()
    internal var subject = ""
    internal var title = "Share"
    internal var body = ""
    internal var applicationPackage = ""

    fun addToEmail(email: String) {
        this.emailsTo.add(email)
    }

    fun addToEmails(emails: List<String>) {
        this.emailsTo.addAll(emails)
    }

    fun addSubject(subject: String) {
        this.subject = subject
    }

    fun addBody(body: String) {
        this.body = body
    }

    fun addChooserTitle(title: String) {
        this.title = title
    }

    fun specifyApplicationPackage(applicationPackage: String) {
        this.applicationPackage = applicationPackage
    }

    companion object {
        fun get(): SharingOptions {
            return SharingOptions()
        }
    }
}