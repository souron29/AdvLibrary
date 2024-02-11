package com.bkt.advlibrary

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import com.bkt.advlibrary.ActivityExtKt.toast
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


fun File.openInFileManager(context: Context) {
    val intentForFileManager = Intent(Intent.ACTION_VIEW)

    val path = if (this.isDirectory) {
        this.canonicalPath
    } else {
        this.parentFile?.canonicalPath
    }
    val selectedUri = Uri.parse(path)
    intentForFileManager.setDataAndType(selectedUri, "resource/folder")
    if (intentForFileManager.resolveActivity(context.packageManager) != null) {
        context.startActivity(intentForFileManager)
    } else {
        context.toast("No File Manager found")
        val intentGeneric = Intent(Intent.ACTION_VIEW)
        val filePath = FileProvider.getUriForFile(
            context,
            "com.bkt.bum.FileProviderDefault",
            this
        )
        val mime = filePath.getMime(context)
        intentGeneric.flags = FLAG_GRANT_READ_URI_PERMISSION
        intentGeneric.setDataAndType(filePath, mime)
        context.startActivity(intentGeneric)
    }
}

fun File.openInFileManager2(context: Context) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.setDataAndType(Uri.parse(canonicalPath), "resource/folder")
    intent.flags = FLAG_GRANT_READ_URI_PERMISSION
    context.startActivity(intent)
}

fun File.child(name: String): File {
    return File(this, name.trim())
}

fun File.getUri(context: Context, authority: String): Uri? {
    return FileProvider.getUriForFile(
        context,
        authority,
        this
    )
}

fun File.open(context: Context, authority: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    val uri = FileProvider.getUriForFile(
        context,
        authority,
        this
    )
    val mime = uri.getMime(context)
    intent.setDataAndType(uri, mime)
    intent.flags = FLAG_GRANT_READ_URI_PERMISSION or FLAG_GRANT_WRITE_URI_PERMISSION
    context.startActivity(intent)
}

fun File.shareFile(context: Context, authority: String) {
    val share = Intent(Intent.ACTION_SEND)
    val uri = FileProvider.getUriForFile(
        context,
        authority,
        this
    )
    val mime = uri.getMime(context)
    share.setDataAndType(uri, mime)
    //share.setPackage("com.whatsapp")
    context.startActivity(share)
}

fun Uri?.getMime(context: Context): String {
    val cR = context.contentResolver
    //val mime = MimeTypeMap.getSingleton()
    return if (this != null)
        cR.getType(this) ?: ""
    else ""
}

fun Uri?.getExtension(context: Context): String {
    val fileName = this?.getFileName(context) ?: ""
    return if (fileName.contains("."))
        fileName.substring(fileName.lastIndexOf("."))
    else ""
}

fun Uri?.getFileName(context: Context): String {
    this?.apply {
        val returnCursor = context.contentResolver.query(this, null, null, null, null)!!
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }
    return ""
}

fun File.cleanFolder() {
    if (this.isDirectory) {
        for (file in this.listFiles() ?: arrayOf()) {
            if (file.isDirectory)
                file.cleanFolder()
            else file.delete()
        }
    }
}

fun List<File>.zipFiles(folder: File, fileName: String): File? {
    try {
        val zipFilePath = "${folder.absolutePath}/$fileName"
        var origin: BufferedInputStream?
        val dest = FileOutputStream(zipFilePath)
        val out = ZipOutputStream(
            BufferedOutputStream(
                dest
            )
        )
        val bufferSize = 2048
        val data = ByteArray(bufferSize)
        for (file in this) {
            val fi = FileInputStream(file)
            origin = BufferedInputStream(fi, bufferSize)
            val entry =
                ZipEntry(file.absolutePath.substring(file.absolutePath.lastIndexOf("/") + 1))
            out.putNextEntry(entry)
            var count: Int
            while (origin.read(data, 0, bufferSize).also { count = it } != -1) {
                out.write(data, 0, count)
            }
            origin.close()
        }

        out.close()
        return File(zipFilePath)
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}

fun Context.getFileUri(file: File, authority: String): Uri? {
    return FileProvider.getUriForFile(this, authority, file)
}

fun Context.shareFiles(onGenerate: (ShareDetails) -> Unit) {
    val shareDetails = ShareDetails()
    onGenerate.invoke(shareDetails)
    // using different intents else activity will search for parcelable when we send list of parcelables
    val intentBuilder = ShareCompat.IntentBuilder(this)

    intentBuilder.setType(shareDetails.fileType)
    shareDetails.fileUris.forEach { uri ->
        intentBuilder.addStream(uri)
    }

    intentBuilder.addEmailTo(shareDetails.emailsTo.toTypedArray())
    intentBuilder.addEmailCc(shareDetails.emailsCC.toTypedArray())
    intentBuilder.addEmailBcc(shareDetails.emailsBCC.toTypedArray())
    if (shareDetails.subject.isNotEmpty())
        intentBuilder.setSubject(shareDetails.subject.toString())
    // Avoid Can't share empty message in whatsapp when sharing only attachments.
    // Whatsapp doesn't accept empty body. So do not set it
    if (shareDetails.body.isNotEmpty())
        intentBuilder.setText(shareDetails.body.toString())
    val chooserIntent = intentBuilder.intent

    if (shareDetails.targetPackage.isNotEmpty() && shareDetails.targetClass.isNotEmpty())
        chooserIntent.setClassName(shareDetails.targetPackage, shareDetails.targetClass)
    if (shareDetails.targetPackage.isNotEmpty())
        chooserIntent.setPackage(shareDetails.targetPackage)
    startActivity(Intent.createChooser(chooserIntent, "Share ${shareDetails.subject}"))
}

class ShareDetails {
    var body: CharSequence = ""
    var subject: CharSequence = ""
    val emailsTo = ArrayList<String>()
    val emailsCC = ArrayList<String>()
    val emailsBCC = ArrayList<String>()

    var fileType = "*/*"
    var targetClass = ""
    var targetPackage = ""
    var authority = "com.bkt.bum.FileProviderDefault"
    val fileUris = ArrayList<Uri>()

    fun addFiles(context: Context, authority: String, vararg files: File) {
        val uris = files.map { context.getFileUri(it, authority)!! }
        fileUris.addAll(uris)
    }
}