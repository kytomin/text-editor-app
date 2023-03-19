package com.meowplex.text_editor_app.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.meowplex.text_editor_app.database.AppDatabase
import java.util.Date

@Entity(AppDatabase.tableName)
data class FileEntity(
    @PrimaryKey val path: String,
    @ColumnInfo(name = "last_opening_date") val lastOpeningDate: Date,
)