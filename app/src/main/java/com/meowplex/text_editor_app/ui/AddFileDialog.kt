package com.meowplex.text_editor_app.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.meowplex.text_editor_app.R
import com.meowplex.text_editor_app.databinding.AddFileDialogBinding
import com.meowplex.text_editor_app.extensions.showToastAndRequirePermissions
import com.meowplex.text_editor_app.utils.FileUtils
import com.meowplex.text_editor_app.utils.PermissionManager
import com.meowplex.text_editor_app.viewmodel.MainViewModel


class AddFileDialog : DialogFragment(R.layout.add_file_dialog){

    private lateinit var binding: AddFileDialogBinding

    var hasStoragePermission: Boolean = false

    companion object {
        const val TAG = "AddFileDialog"
    }

    private val pickFileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                if (uri != null) {
                    val path = FileUtils.getPath(requireContext(), uri)
                    binding.viewmodel!!.onAddFile(path!!)

                }
            }
            this.dismiss()
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.add_file_dialog, container, false)
        binding.viewmodel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        hasStoragePermission = PermissionManager().checkStoragePermission()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val createFileView = view.findViewById<LinearLayout>(R.id.create_file_view)
        val addExistingFileView = view.findViewById<LinearLayout>(R.id.add_existing_file_view)

        createFileView.setOnClickListener{
            if (!hasStoragePermission)
                context?.showToastAndRequirePermissions()
            else
                binding.viewmodel!!.onCreateFile()
            this.dismiss()
        }

        addExistingFileView.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "*/*"
            val mimetypes = arrayOf("application/javascript", "application/json", "text/*", "application/octet-stream")
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)
            pickFileLauncher.launch(intent)
        }
    }

}