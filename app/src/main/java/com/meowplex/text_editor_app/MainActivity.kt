package com.meowplex.text_editor_app


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.meowplex.text_editor_app.database.AppDatabase
import com.meowplex.text_editor_app.repository.PermissionRepository
import com.meowplex.text_editor_app.ui.EditFileFragment
import com.meowplex.text_editor_app.utils.FileUtils


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppDatabase.setContext(this)
        PermissionRepository.setActivity(this)
        val permRepository = PermissionRepository()
        if (!permRepository.checkStoragePermission())
            permRepository.requireStoragePermission()

        setContentView(R.layout.activity_main)

        if (intent.action == Intent.ACTION_VIEW) {

            val path = FileUtils.getPath(this, intent.data!!)
            val bundle = Bundle()
            bundle.putString("path", path)
            EditFileFragment().arguments = bundle
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
            navHostFragment!!.findNavController().navigate(R.id.editFileFragment, bundle)
        }

    }

    override fun onDestroy() {
        AppDatabase.destroyInstance()
        super.onDestroy()
    }

}