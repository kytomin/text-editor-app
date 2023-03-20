package com.meowplex.text_editor_app


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.meowplex.text_editor_app.database.AppDatabase
import com.meowplex.text_editor_app.repository.PermissionRepository


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppDatabase.setContext(this)
        PermissionRepository.setActivity(this)
        val permRepository = PermissionRepository()
        if (!permRepository.checkStoragePermission())
            permRepository.requireStoragePermission()
        setContentView(R.layout.activity_main)
    }

    override fun onDestroy() {
        AppDatabase.destroyInstance()
        super.onDestroy()
    }

}