package com.dashagy.domain.service

import com.dashagy.domain.utils.Result

interface ImageService {
    fun uploadImage(
        filepath: String,
        callback: (Result<String>) -> Unit
    )
}
