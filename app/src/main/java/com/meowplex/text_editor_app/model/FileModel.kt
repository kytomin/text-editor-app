package com.meowplex.text_editor_app.model

import java.util.*

class FileModel {

    var path: String
    var lastOpeningDate: Date
    var fileName: String
    var extension: String

    constructor(path: String, lastOpeningDate: Date = Date()) {
        this.path = path
        this.lastOpeningDate = lastOpeningDate
        val startFileNameIndex = path.lastIndexOf("/")
        val startExtensionIndex = path.lastIndexOf(".")
        this.fileName = path.substring(startFileNameIndex + 1)
        if (startExtensionIndex == -1)
            this.extension = ""
        else
            this.extension = path.substring(startExtensionIndex + 1)
    }

}