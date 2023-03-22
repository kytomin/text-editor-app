package com.meowplex.text_editor_app.utils

import com.meowplex.text_editor_app.model.FileModel

class SearchManager(private val files: List<FileModel>) {

    fun search(query: String): List<FileModel> {
        val response = mutableListOf<FileModel>()
        for (file in files){
            if ((file.fileName + file.path).contains(query)){
                response.add(file)
            }
        }
        return response
    }

}