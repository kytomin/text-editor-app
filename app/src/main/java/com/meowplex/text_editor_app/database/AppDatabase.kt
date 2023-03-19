package com.meowplex.text_editor_app.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.meowplex.text_editor_app.database.converters.Converters
import com.meowplex.text_editor_app.database.dao.FilesDao
import com.meowplex.text_editor_app.database.entities.FileEntity

@Database(entities = [FileEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun filesDao(): FilesDao

    companion object {

        private const val databaseName = "app"
        const val tableName = "files"

        private var INSTANCE: AppDatabase? = null

        fun setContext(context: Context) {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        AppDatabase::class.java, databaseName).allowMainThreadQueries()
                        .build()
                }
            }
        }

        fun getInstance(): AppDatabase {
            if (INSTANCE == null) throw Exception("App Database is not initialized. Call setContext(context) before")
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}

