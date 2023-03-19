package com.meowplex.text_editor_app.database.dao

import androidx.room.*
import com.meowplex.text_editor_app.database.AppDatabase
import com.meowplex.text_editor_app.database.entities.FileEntity

@Dao
interface FilesDao {
    @Query("SELECT * FROM ${AppDatabase.tableName}")
    suspend fun getAll(): List<FileEntity>


    @Query("SELECT * FROM ${AppDatabase.tableName} WHERE path = :path")
    suspend fun findByPath(path: String): FileEntity

    @Update
    suspend fun updateFile(file: FileEntity)

    @Transaction
    suspend fun updateFileByPath(path: String, file: FileEntity){
        insert(file)
        deleteByPath(path)
    }
    @Insert
    suspend fun insert(file: FileEntity)

    @Insert
    suspend fun insertAll(vararg files: FileEntity)

    @Query("DELETE FROM ${AppDatabase.tableName} WHERE path = :path")
    suspend fun deleteByPath(path: String)

    @Delete
    suspend fun deleteMany(vararg files: FileEntity)

    @Query("DELETE FROM ${AppDatabase.tableName}")
    fun deleteAll()
}