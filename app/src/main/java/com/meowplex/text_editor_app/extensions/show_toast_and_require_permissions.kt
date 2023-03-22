package com.meowplex.text_editor_app.extensions

import android.content.Context
import android.widget.Toast
import com.meowplex.text_editor_app.R
import com.meowplex.text_editor_app.utils.PermissionManager

fun Context.showToastAndRequirePermissions() {
    PermissionManager().requireStoragePermission()
    Toast.makeText(this, getString(R.string.give_permissions), Toast.LENGTH_SHORT).show()
}