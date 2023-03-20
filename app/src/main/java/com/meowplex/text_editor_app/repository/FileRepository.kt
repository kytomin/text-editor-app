package com.meowplex.text_editor_app.repository

import android.os.Environment
import com.meowplex.text_editor_app.BuildConfig
import com.meowplex.text_editor_app.model.FileModel
import java.io.File

class FileRepository {

    private val appDir = File(Environment.getExternalStorageDirectory(), BuildConfig.APPLICATION_ID)

    init {
        if (!appDir.exists()) {
            appDir.mkdir()
        }
    }

    fun writeFile(path: String, text: String) {
        val file = File(path)
        file.writeText(text)
    }

    fun readFile(path: String): String {
        val file = File(path)
        if (!file.exists()) {
            writeFile(path, "")
            return ""
        }

        return file.bufferedReader().readText()
    }

    fun createFile(name: String = "new_file", extension: String = "txt"): String {
        val path = getUniqueFile(name, extension)
        writeFile(path, "")
        return path
    }

    fun deleteFile(path: String) {
        val file = File(path)
        if (file.exists())
            file.delete()
    }

    fun deleteFiles(files: List<FileModel>) {
        files.forEach { deleteFile(it.path) }
    }

    private fun getUniqueFile(name: String, extension: String, index: Int? = null): String {
        val suffix = index?.let { " ($it)." } ?: "."
        val fullname = name + suffix + extension
        val file = File(appDir, fullname)
        if (!file.exists())
            return file.path
        else
            return getUniqueFile(name, extension, index?.plus(1) ?: 1)
    }

}
