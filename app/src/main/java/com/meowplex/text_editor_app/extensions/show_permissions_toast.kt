package com.meowplex.text_editor_app.extensions

import android.content.Context
import android.widget.Toast
import com.meowplex.text_editor_app.R
import com.meowplex.text_editor_app.repository.PermissionRepository

fun Context.showToastAndRequirePermissions() {
    PermissionRepository().requireStoragePermission()
    Toast.makeText(this, getString(R.string.give_permissions), Toast.LENGTH_SHORT).show()
}