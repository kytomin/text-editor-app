package com.meowplex.text_editor_app.utils

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.meowplex.text_editor_app.BuildConfig

class PermissionManager {

    companion object {

        const val PERMISSION_REQUEST_CODE = 2947

        private var activity: Activity? = null

        fun setActivity(a: Activity) {
            activity = a
        }

    }

    fun checkStoragePermission(): Boolean {
        if (activity == null)
            throw Exception("Activity is null")
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val writePerm =
                ContextCompat.checkSelfPermission(
                    activity!!,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            val readPerm =
                ContextCompat.checkSelfPermission(
                    activity!!,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            writePerm == PackageManager.PERMISSION_GRANTED && readPerm == PackageManager.PERMISSION_GRANTED
        }
    }

    fun requireStoragePermission() {
        if (activity == null)
            throw Exception("Activity is null")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                activity!!.startActivity(intent)
            } catch (e: Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                activity!!.startActivity(intent)
            }
        } else {
            activity!!.requestPermissions(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                ),
                PERMISSION_REQUEST_CODE
            )
        }
    }

}