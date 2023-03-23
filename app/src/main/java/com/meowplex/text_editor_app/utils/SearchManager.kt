package com.meowplex.text_editor_app.utils

import com.meowplex.text_editor_app.model.FileModel

class SearchManager(files: List<FileModel>) {

    private var files: List<FileModel> = listOf()
    private var lastQuery: String = ""

    init {
        setFiles(files)
    }

    fun setFiles(files: List<FileModel>) {
        this.files = files
    }

    fun getFiles(): List<FileModel> = this.files

    fun search(query: String): List<FileModel> {
        lastQuery = query
        val response = mutableListOf<FileModel>()
        for (file in files) {
            if (file.fileName.contains(query)) {
                response.add(file)
            }
        }
        return response
    }

    fun research(): List<FileModel> {
        return search(lastQuery)
    }

}