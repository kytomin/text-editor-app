package com.meowplex.text_editor_app.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.meowplex.text_editor_app.R
import com.meowplex.text_editor_app.databinding.FragmentEditFileBinding
import com.meowplex.text_editor_app.utils.FileUtils
import com.meowplex.text_editor_app.viewmodel.EditFileViewModel


class EditFileFragment : Fragment() {
    private lateinit var binding: FragmentEditFileBinding

    private val saveFileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                if (uri != null) {
                    val path = FileUtils.getPath(requireContext(), uri)
                    binding.viewmodel!!.onSaveAs(path!!)
                    findNavController().popBackStack()
                }
            }
        }


    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!binding.viewmodel!!.isSaved()) {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(context!!)
                    builder.setMessage(getString(R.string.want_save_changes))
                    builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
                        binding.viewmodel!!.onSave()
                        closeFragment()
                    }
                    builder.setNegativeButton(getString(R.string.no)) { _, _ ->
                        closeFragment()
                    }
                    builder.show()
                } else {
                    closeFragment()
                }
            }

            private fun closeFragment() {
                this.isEnabled = false
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_file, container, false)
        binding.viewmodel = ViewModelProvider(requireActivity())[EditFileViewModel::class.java]
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )
        val path = requireArguments().getString("path")
        if (path != null)
            binding.viewmodel!!.setFileByPath(path)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(view)
        observeEditText(view)
    }


    private fun setToolBar(view: View) {
        val toolBar = view.findViewById<Toolbar>(R.id.edit_file_toolbar)
        toolBar.title = binding.viewmodel!!.getFileName()
        toolBar.inflateMenu(R.menu.edit_file_menu)

        val editText = view.findViewById<EditText>(R.id.edit_text)
        editText.setText(binding.viewmodel!!.getFileContent())

        toolBar.setOnMenuItemClickListener {
            editText.clearFocus()
            when (it.itemId) {
                R.id.action_save -> binding.viewmodel!!.onSave()
                R.id.action_undo -> {
                    binding.viewmodel!!.onUndo()
                    editText.setText(binding.viewmodel!!.getFileContent())
                }
                R.id.action_redo -> {
                    binding.viewmodel!!.onRedo()
                    editText.setText(binding.viewmodel!!.getFileContent())
                }
                R.id.action_save_as -> {
                    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
                    intent.type = "*/*"
                    saveFileLauncher.launch(intent)
                }
            }
            true
        }
    }

    private fun observeEditText(view: View) {
        val editText = view.findViewById<EditText>(R.id.edit_text)
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                binding.viewmodel!!.onChangedText(editable.toString())
            }
        })
    }


}

