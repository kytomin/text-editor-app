package com.meowplex.text_editor_app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meowplex.text_editor_app.model.FileModel
import com.meowplex.text_editor_app.repository.DbRepository
import com.meowplex.text_editor_app.repository.FileRepository
import com.meowplex.text_editor_app.utils.SearchManager
import kotlinx.coroutines.launch
import java.util.*

class MainViewModel : ViewModel() {

    private val dbRepository = DbRepository()
    private val fileRepository = FileRepository()
    private var searchManager: SearchManager? = null

    private val _files = MutableLiveData<List<FileModel>>(listOf())
    val files: LiveData<List<FileModel>> = _files

    fun loadFiles() {
        viewModelScope.launch {
            _files.value = dbRepository.getAllFiles()
            sortFiles()
        }
    }

    fun onCreateFile() {
        val path = FileRepository().createFile()
        onAddFile(path)
    }

    fun onRefresh(callback: () -> Unit) {
        viewModelScope.launch {
            if (searchManager == null) {
                _files.value = dbRepository.getAllFiles()
                sortFiles()
            } else {
                searchManager!!.setFiles(dbRepository.getAllFiles())
                _files.value = searchManager!!.research()
            }
            callback()
        }
    }

    fun onAddFile(path: String) {
        val existingFile = findFileInFiles(path)

        if (existingFile != null) {
            existingFile.lastOpeningDate = Date()
            sortFiles()
            viewModelScope.launch {
                dbRepository.updateFile(existingFile)
            }
            return
        }

        val newFile = FileModel(path)
        if (searchManager == null) {
            _files.value = listOf(newFile) + _files.value!!
            sortFiles()
        } else {
            searchManager!!.setFiles(listOf(newFile) + searchManager!!.getFiles())
            _files.value = searchManager!!.research()
        }

        viewModelScope.launch {
            dbRepository.insertFile(newFile)
        }
    }

    fun onDeleteFiles(deleteFiles: List<FileModel>) {

        if (searchManager == null) {
            val temp = _files.value!!.toMutableList()
            temp.removeAll(deleteFiles)
            _files.value = temp.toList()
        } else {
            val temp = searchManager!!.getFiles().toMutableList()
            temp.removeAll(deleteFiles)
            searchManager!!.setFiles(temp)
            _files.value = searchManager!!.research()
        }

        viewModelScope.launch {
            dbRepository.deleteFiles(deleteFiles)
            fileRepository.deleteFiles(deleteFiles)
        }
    }

    fun onStartSearching() {
        searchManager = SearchManager(_files.value!!)
    }

    fun onSearch(query: String) {
        _files.value = searchManager!!.search(query)
    }

    fun onStopSearching(): Boolean {
        searchManager = null
        return false
    }


    private fun sortFiles() {
        _files.value = _files.value?.sortedByDescending { f -> f.lastOpeningDate }
    }

    private fun findFileInFiles(path: String): FileModel? {
        for (f in _files.value!!) {
            if (f.path == path)
                return f
        }
        return null
    }

}

