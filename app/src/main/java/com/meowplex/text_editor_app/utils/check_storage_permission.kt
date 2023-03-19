package com.meowplex.text_editor_app.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import androidx.core.content.ContextCompat.checkSelfPermission

fun Activity.checkStoragePermission(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        Environment.isExternalStorageManager()
    } else {
        val writePerm = checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val readPerm = checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        writePerm == PackageManager.PERMISSION_GRANTED && readPerm == PackageManager.PERMISSION_GRANTED
    }
}