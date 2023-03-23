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

    fun onLoadFiles(callback: (() -> Unit)? = null) {
        viewModelScope.launch {
            setFiles(dbRepository.getAllFiles())
            if (callback != null) {
                callback()
            }
        }
    }

    fun onCreateFile() {
        val path = FileRepository().createFile()
        onAddFile(path)
    }

    fun onAddFile(path: String) {

        if (isFileExists(path, searchManager?.getFiles() ?: _files.value!!)) {
            return updateFile(path)
        }

        addFile(FileModel(path))
    }

    fun onDeleteFiles(deleteFiles: List<FileModel>) {
        removeFiles(deleteFiles)
    }

    fun onStartSearching() {
        searchManager = SearchManager(_files.value!!)
    }

    fun onSearch(query: String) {
        if (searchManager != null)
            _files.value = searchManager!!.search(query)
    }

    fun onStopSearching(): Boolean {
        searchManager = null
        return false
    }

    private fun setFiles(newValue: List<FileModel>) {
        if (searchManager == null) {
            _files.value = sortFiles(newValue)
        } else {
            searchManager!!.setFiles(sortFiles(newValue))
            _files.value = searchManager!!.research()
        }
    }

    private fun addFile(newFile: FileModel) {
        if (searchManager == null) {
            _files.value = sortFiles(listOf(newFile) + _files.value!!)
        } else {
            searchManager!!.setFiles(sortFiles(listOf(newFile) + searchManager!!.getFiles()))
            _files.value = searchManager!!.research()
        }
        viewModelScope.launch {
            dbRepository.insertFile(newFile)
        }
    }

    private fun updateFile(path: String) {
        val nowDate = Date()
        if (searchManager == null) {
            for (f in _files.value!!) {
                if (f.path == path) {
                    f.lastOpeningDate = nowDate
                }
            }
            _files.value = sortFiles(_files.value!!)
        } else {
            val temp = searchManager!!.getFiles()
            for (f in temp) {
                if (f.path == path) {
                    f.lastOpeningDate = nowDate
                }
            }
            searchManager!!.setFiles(sortFiles(temp))
            _files.value = searchManager!!.research()
        }
        viewModelScope.launch {
            dbRepository.updateFile(FileModel(path, nowDate))
        }
    }

    private fun removeFiles(files: List<FileModel>) {
        if (searchManager == null) {
            val temp = _files.value!!.toMutableList()
            temp.removeAll(files)
            _files.value = temp.toList()
        } else {
            val temp = searchManager!!.getFiles().toMutableList()
            temp.removeAll(files)
            searchManager!!.setFiles(sortFiles(temp))
            _files.value = searchManager!!.research()
        }
        viewModelScope.launch {
            dbRepository.deleteFiles(files)
            fileRepository.deleteFiles(files)
        }
    }


    private fun sortFiles(files: List<FileModel>): List<FileModel> {
        return files.sortedByDescending { f -> f.lastOpeningDate }
    }

    private fun isFileExists(path: String, files: List<FileModel>): Boolean {
        for (f in files) {
            if (f.path == path)
                return true
        }
        return false
    }

}

