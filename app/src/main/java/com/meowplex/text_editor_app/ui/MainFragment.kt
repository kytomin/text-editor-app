package com.meowplex.text_editor_app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.meowplex.text_editor_app.R
import com.meowplex.text_editor_app.adapters.MainAdapter
import com.meowplex.text_editor_app.databinding.FragmentMainBinding
import com.meowplex.text_editor_app.utils.checkStoragePermission
import com.meowplex.text_editor_app.viewmodel.EditFileViewModel
import com.meowplex.text_editor_app.viewmodel.MainViewModel


class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding

    private lateinit var toolBar: Toolbar
    private lateinit var recyclerView: RecyclerView
    private lateinit var floatingActionButton: FloatingActionButton

    private var hasStoragePermission: Boolean = false

    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if ((recyclerView.adapter as MainAdapter).isSelectMode) {
                    stopSelectMode()
                } else {
                    this.isEnabled = false
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        binding.viewmodel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        binding.viewmodel?.loadFiles()
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )
        hasStoragePermission = activity?.checkStoragePermission() ?: false

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(view)
        pushItemsToRecyclerView(view)
        observeFloatingActionButton(view)
    }

    private fun setToolBar(view: View) {
        toolBar = view.findViewById(R.id.main_toolbar)
        toolBar.title = getString(R.string.app_name)
        toolBar.inflateMenu(R.menu.main_menu)
        toolBar.menu.findItem(R.id.action_search).isVisible = false
        toolBar.menu.findItem(R.id.action_delete).isVisible = false
        toolBar.menu.findItem(R.id.action_clear_all).isVisible = false
        toolBar.menu.findItem(R.id.action_select_all).isVisible = false

        toolBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_delete -> {
                    showDeleteConfirmDialog(view)
                }
                R.id.action_clear_all -> {
                    stopSelectMode()
                }
                R.id.action_select_all -> {
                    (recyclerView.adapter as MainAdapter).selectAll()
                    onChangeSelection()
                }
            }
            true
        }
    }

    private fun pushItemsToRecyclerView(view: View) {

        recyclerView = view.findViewById(R.id.main_recycler_view)
        val noFilesTextView: TextView = view.findViewById(R.id.no_recent_files_textview)

        binding.viewmodel!!.files.observe(viewLifecycleOwner) { files ->

            if (files.isEmpty()) noFilesTextView.visibility = View.VISIBLE
            else noFilesTextView.visibility = View.GONE

            recyclerView.adapter = MainAdapter(files,
                { file ->
                    binding.viewmodel?.onOpenFile(file)
                    ViewModelProvider(requireActivity())[EditFileViewModel::class.java].setFile(file)
                    findNavController().navigate(R.id.editFileFragment)
                },
                {
                    onChangeSelection()
                }
            )
        }
    }

    private fun observeFloatingActionButton(view: View) {
        floatingActionButton =
            view.findViewById(R.id.main_floating_action_button)
        floatingActionButton.setOnClickListener {
            AddFileDialog().show(childFragmentManager, AddFileDialog.TAG)
        }

    }

    private fun showDeleteConfirmDialog(view: View) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(view.context)
        builder.setMessage(getString(R.string.want_delete_files))
        builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
            binding.viewmodel!!.onDeleteFiles((recyclerView.adapter as MainAdapter).getSelectedItems())
            stopSelectMode()
        }
        builder.setNegativeButton(getString(R.string.no)) { _, _ ->
            stopSelectMode()
        }
        builder.show()
    }

    private fun stopSelectMode() {
        (recyclerView.adapter as MainAdapter).clearSelection()
        toolBar.menu.findItem(R.id.action_delete).isVisible = false
        toolBar.menu.findItem(R.id.action_clear_all).isVisible = false
        toolBar.menu.findItem(R.id.action_select_all).isVisible = false
        toolBar.title = getString(R.string.app_name)
        floatingActionButton.show()
    }

    private fun onChangeSelection() {

        val adapter = recyclerView.adapter as MainAdapter
        val selectedItemsCount = adapter.selectedItemCount
        val itemsCount = adapter.itemCount
        toolBar.title = "$selectedItemsCount/$itemsCount"
        if (!adapter.isSelectMode) return stopSelectMode()



        toolBar.menu.findItem(R.id.action_delete).isVisible = true
        toolBar.menu.findItem(R.id.action_clear_all).isVisible =
            adapter.selectedItemCount == adapter.itemCount
        toolBar.menu.findItem(R.id.action_select_all).isVisible =
            adapter.selectedItemCount != adapter.itemCount
        floatingActionButton.hide()
    }


}
