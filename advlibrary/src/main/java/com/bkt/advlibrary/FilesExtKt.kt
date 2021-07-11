package library.extensions

import android.content.Intent
import android.net.Uri
import library.AdvActivity
import java.io.File

object FilesExtKt {
    fun File.openInFileManager(activity: AdvActivity) {
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
}