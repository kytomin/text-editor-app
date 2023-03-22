package com.meowplex.text_editor_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meowplex.text_editor_app.model.FileModel
import com.meowplex.text_editor_app.repository.DbRepository
import com.meowplex.text_editor_app.repository.FileRepository
import kotlinx.coroutines.launch
import java.util.*


class EditFileViewModel : ViewModel() {

    private val bufferSize: Int = 25

    private val fileRepository = FileRepository()
    private val dbRepository = DbRepository()

    private var fileChangeHistory: MutableList<String> = mutableListOf()
    private var index: Int = 0
    private var name: String = ""
    private var path: String = ""
    private var savedContent: String = ""

    fun setFile(file: FileModel) {
        name = file.fileName
        path = file.path
        savedContent = fileRepository.readFile(file.path)
        fileChangeHistory = mutableListOf(savedContent)
        index = 0

        viewModelScope.launch {
            file.lastOpeningDate = Date()
            dbRepository.updateOrInsertFile(file)
        }
    }

    fun setFileByPath(path: String) {
        setFile(FileModel(path))
    }

    fun getFileContent(): String {
        return fileChangeHistory[index]
    }

    fun getFileName(): String {
        return name
    }

    fun isSaved(): Boolean {
        return fileChangeHistory[index] == savedContent
    }

    fun onSaveAs(newPath: String) {

        val newFile = FileModel(newPath)
        fileRepository.writeFile(newPath, fileChangeHistory[index])

        viewModelScope.launch {
            dbRepository.updateFileByPath(path, newFile)
            setFile(newFile)
        }
    }

    fun onSave() {
        savedContent = fileChangeHistory[index]
        fileRepository.writeFile(path, fileChangeHistory[index])
    }

    fun onChangedText(newText: String) {
        if (newText != fileChangeHistory[index]) {
            index++
            if (index < fileChangeHistory.size) {
                fileChangeHistory[index] = newText
                fileChangeHistory = fileChangeHistory.subList(0, index + 1)
            } else {
                fileChangeHistory.add(newText)
                if (fileChangeHistory.size > bufferSize) {
                    fileChangeHistory.removeFirst()
                    index--
                }
            }
        }
    }

    fun onUndo() {
        if (index > 0) {
            index--
        }
    }

    fun onRedo() {
        if (index < fileChangeHistory.size - 1) {
            index++
        }
    }


}