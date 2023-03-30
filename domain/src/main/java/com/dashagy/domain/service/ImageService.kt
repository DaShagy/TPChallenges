package com.dashagy.domain.service

import com.dashagy.domain.utils.Result

interface ImageService {
    fun uploadImage(
        imageUri: String,
        fileName: String,
        callback: (Result<String>) -> Unit
    )
}
