package com.meowplex.text_editor_app.repository

import com.meowplex.text_editor_app.database.AppDatabase
import com.meowplex.text_editor_app.database.entities.FileEntity
import com.meowplex.text_editor_app.model.FileModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DbRepository {

    private val db = AppDatabase.getInstance().filesDao()

    suspend fun insertFile(file: FileModel) = withContext(Dispatchers.IO) {
        db.insertAll(FileEntity(file.path, file.lastOpeningDate))
    }

    suspend fun getAllFiles() = withContext(Dispatchers.IO) {
        val files: List<FileModel> =
            db.getAll().map { FileModel(it.path, it.lastOpeningDate) }.toList()
        return@withContext files
    }

    suspend fun getFileByPath(path: String) = withContext(Dispatchers.IO) {
        return@withContext db.findByPath(path)
    }

    suspend fun deleteFile(file: FileModel) = deleteFileByPath(file.path)

    suspend fun deleteFileByPath(path: String) = withContext(Dispatchers.IO) {
        db.deleteByPath(path)
    }

    suspend fun deleteFiles(files: List<FileModel>) = withContext(Dispatchers.IO) {
        db.deleteMany(*files.map { FileEntity(it.path, it.lastOpeningDate) }.toTypedArray())
    }

    suspend fun updateFile(file: FileModel) = withContext(Dispatchers.IO) {
        db.updateFile(FileEntity(file.path, file.lastOpeningDate))
    }

    suspend fun updateOrInsertFile(file: FileModel) = withContext(Dispatchers.IO) {
        val temp = db.findByPath(file.path)
        if (temp == null) insertFile(file)
        else updateFile(file)
    }

    suspend fun updateFileByPath(path: String, file: FileModel) = withContext(Dispatchers.IO) {
        db.updateFileByPath(path, FileEntity(file.path, file.lastOpeningDate))
    }

}