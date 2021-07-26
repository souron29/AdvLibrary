package com.bkt.advlibrary

import android.Manifest
import android.content.Intent
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresPermission
import androidx.core.content.FileProvider
import com.bkt.advlibrary.FilesExtKt.getFileName
import java.io.File


object FilesExtKt {
    fun File.openInFileManager(activity: CommonActivity) {
        val path = if (isDirectory) {
            canonicalPath
        } else {
            parentFile?.canonicalPath
        }
        val selectedUri = Uri.parse(path)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(selectedUri, "resource/folder")
        activity.startActivity(intent)
    }

    fun File.child(name: String): File {
        return File(this, name.trim())
    }

    fun File.open(activity: CommonActivity) {
        val intent = Intent(Intent.ACTION_VIEW)
        val uri = FileProvider.getUriForFile(
            activity,
            "com.bkt.bum.FileProviderDefault",
            this
        )
        val mime = uri.getMime(activity)
        intent.setDataAndType(uri, mime)
        intent.flags = FLAG_GRANT_READ_URI_PERMISSION or FLAG_GRANT_WRITE_URI_PERMISSION
        activity.startActivity(intent)
    }

    fun Uri?.getMime(activity: CommonActivity): String {
        val cR = activity.contentResolver
        //val mime = MimeTypeMap.getSingleton()
        return if (this != null)
            cR.getType(this) ?: ""
        else ""
    }

    fun Uri?.getExtension(activity: CommonActivity): String {
        val cR = activity.contentResolver
        val mime = MimeTypeMap.getSingleton()
        return if (this != null)
            mime.getExtensionFromMimeType(cR.getType(this)) ?: ""
        else ""
    }

    fun Uri?.getFileName(activity: CommonActivity): String {
        this?.apply {
            val returnCursor = activity.contentResolver.query(this, null, null, null, null)!!
            val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor.moveToFirst()
            val name = returnCursor.getString(nameIndex)
            returnCursor.close()
            return name
        }
        return ""
    }
}