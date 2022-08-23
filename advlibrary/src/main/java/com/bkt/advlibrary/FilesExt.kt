package com.bkt.advlibrary

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import com.bkt.advlibrary.ActivityExtKt.toast
import java.io.File


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
    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
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