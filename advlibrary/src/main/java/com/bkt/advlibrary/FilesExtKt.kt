package com.bkt.advlibrary

import android.Manifest
import android.content.Intent
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bkt.advlibrary.FilesExtKt.getFileName
import com.bkt.advlibrary.FilesExtKt.open
import java.io.File


object FilesExtKt {
    fun File.openInFileManager(activity: AppCompatActivity) {
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

    fun File.getUri(activity: AppCompatActivity, authority: String): Uri? {
        return FileProvider.getUriForFile(
            activity,
            authority,
            this
        )
    }

    fun File.open(activity: AppCompatActivity, authority: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        val uri = FileProvider.getUriForFile(
            activity,
            authority,
            this
        )
        val mime = uri.getMime(activity)
        intent.setDataAndType(uri, mime)
        intent.flags = FLAG_GRANT_READ_URI_PERMISSION or FLAG_GRANT_WRITE_URI_PERMISSION
        activity.startActivity(intent)
    }

    fun File.shareFile(activity: AppCompatActivity, authority: String) {
        val share = Intent(Intent.ACTION_SEND)
        val uri = FileProvider.getUriForFile(
            activity,
            authority,
            this
        )
        val mime = uri.getMime(activity)
        share.setDataAndType(uri, mime)
        //share.setPackage("com.whatsapp")
        activity.startActivity(share)
    }

    fun Uri?.getMime(activity: AppCompatActivity): String {
        val cR = activity.contentResolver
        //val mime = MimeTypeMap.getSingleton()
        return if (this != null)
            cR.getType(this) ?: ""
        else ""
    }

    fun Uri?.getExtension(activity: AppCompatActivity): String {
        val fileName = this?.getFileName(activity) ?: ""
        return if (fileName.contains("."))
            fileName.substring(fileName.lastIndexOf("."))
        else ""
    }

    fun Uri?.getFileName(activity: AppCompatActivity): String {
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