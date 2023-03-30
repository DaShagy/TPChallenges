package com.dashagy.domain.entities

data class Picture(
    val localUri: String,
    val storageFilepath: String,
    var downloadUri: String = ""
)
