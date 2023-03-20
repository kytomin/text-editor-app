package com.meowplex.text_editor_app.extensions

import android.content.Context
import android.widget.Toast
import com.meowplex.text_editor_app.R

fun Context.showPermissionsToast() {
    Toast.makeText(this, getString(R.string.give_permissions), Toast.LENGTH_SHORT).show()
}