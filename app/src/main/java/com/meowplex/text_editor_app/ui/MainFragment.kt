package com.meowplex.text_editor_app.ui

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.meowplex.text_editor_app.R
import com.meowplex.text_editor_app.adapters.MainAdapter
import com.meowplex.text_editor_app.databinding.FragmentMainBinding
import com.meowplex.text_editor_app.extensions.showToastAndRequirePermissions
import com.meowplex.text_editor_app.model.FileModel
import com.meowplex.text_editor_app.repository.PermissionRepository
import com.meowplex.text_editor_app.viewmodel.MainViewModel


class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding

    private lateinit var toolBar: Toolbar
    private lateinit var recyclerView: RecyclerView
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var swipeRefresh: SwipeRefreshLayout

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
            viewLifecycleOwner, onBackPressedCallback
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(view)
        setSearchView()
        pushItemsToRecyclerView(view)
        observeFloatingActionButton(view)
        observeSwipeRefresh(view)
    }

    private fun setToolBar(view: View) {
        toolBar = view.findViewById(R.id.main_toolbar)
        toolBar.title = getString(R.string.app_name)
        toolBar.inflateMenu(R.menu.main_menu)
        toolBar.menu.findItem(R.id.action_search).isVisible = true
        toolBar.menu.findItem(R.id.action_delete).isVisible = false
        toolBar.menu.findItem(R.id.action_clear_all).isVisible = false
        toolBar.menu.findItem(R.id.action_select_all).isVisible = false

        toolBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_delete -> {
                    if (!PermissionRepository().checkStoragePermission()) context?.showToastAndRequirePermissions()
                    else showConfirmDeletionDialog(view)
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

    private fun setSearchView() {

        val searchView = toolBar.menu.findItem(R.id.action_search).actionView as SearchView
        searchView.queryHint = getString(R.string.filename)

        searchView.setOnQueryTextListener(
            object : OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    binding.viewmodel!!.onSearch(newText!!)
                    return true
                }
            }
        )

        searchView.setOnSearchClickListener {
            toolBar.title = getString(R.string.search)
            floatingActionButton.hide()
        }

        searchView.setOnCloseListener {
            toolBar.title = getString(R.string.app_name)
            floatingActionButton.show()
            binding.viewmodel!!.onStopSearching()
        }
    }

    private fun pushItemsToRecyclerView(view: View) {

        fun onItemClick(file: FileModel) {
            if (!PermissionRepository().checkStoragePermission()) context?.showToastAndRequirePermissions()
            else {
                val bundle = Bundle()
                bundle.putString("path", file.path)
                findNavController().navigate(R.id.editFileFragment, bundle)
            }
        }

        recyclerView = view.findViewById(R.id.main_recycler_view)
        val noFilesTextView: TextView = view.findViewById(R.id.no_recent_files_textview)

        recyclerView.adapter = MainAdapter(
            binding.viewmodel!!.files.value!!,
            { onItemClick(it) },
            { onChangeSelection() }
        )

        binding.viewmodel!!.files.observe(viewLifecycleOwner) { files ->

            if (files.isEmpty()) noFilesTextView.visibility = View.VISIBLE
            else noFilesTextView.visibility = View.GONE
            (recyclerView.adapter as MainAdapter).updateDataset(files)
        }
    }

    private fun observeFloatingActionButton(view: View) {
        floatingActionButton = view.findViewById(R.id.main_floating_action_button)
        floatingActionButton.setOnClickListener {
            AddFileDialog().show(childFragmentManager, AddFileDialog.TAG)
        }
    }

    private fun observeSwipeRefresh(view: View) {
        val typedValue = TypedValue()
        view.context.theme.resolveAttribute(
            com.google.android.material.R.attr.colorSecondaryVariant,
            typedValue,
            true
        )
        val color = typedValue.data
        swipeRefresh = view.findViewById(R.id.main_swipe_refresh)
        swipeRefresh.setColorSchemeColors(color)
        swipeRefresh.setOnRefreshListener {
            binding.viewmodel!!.onRefresh {
                swipeRefresh.isRefreshing = false
                stopSelectMode()
            }
        }
    }

    private fun showConfirmDeletionDialog(view: View) {
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

    private fun onChangeSelection() {
        val adapter = recyclerView.adapter as MainAdapter
        val selectedItemsCount = adapter.selectedItemCount
        val itemsCount = adapter.itemCount
        toolBar.title = "$selectedItemsCount/$itemsCount"
        if (!adapter.isSelectMode) return stopSelectMode()

        toolBar.menu.findItem(R.id.action_delete).isVisible = true
        toolBar.menu.findItem(R.id.action_search).isVisible = false
        toolBar.menu.findItem(R.id.action_clear_all).isVisible =
            adapter.selectedItemCount == adapter.itemCount
        toolBar.menu.findItem(R.id.action_select_all).isVisible =
            adapter.selectedItemCount != adapter.itemCount
        floatingActionButton.hide()
    }

    private fun stopSelectMode() {
        (recyclerView.adapter as MainAdapter).clearSelection()
        toolBar.menu.findItem(R.id.action_delete).isVisible = false
        toolBar.menu.findItem(R.id.action_clear_all).isVisible = false
        toolBar.menu.findItem(R.id.action_select_all).isVisible = false
        toolBar.menu.findItem(R.id.action_search).isVisible = true
        toolBar.title = getString(R.string.app_name)
        floatingActionButton.show()
    }

}
