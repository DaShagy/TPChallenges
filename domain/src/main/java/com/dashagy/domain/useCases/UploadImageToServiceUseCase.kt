package com.dashagy.domain.useCases

import com.dashagy.domain.service.ImageService
import com.dashagy.domain.utils.Result

class UploadImageToServiceUseCase(
    private val service: ImageService
) {
    operator fun invoke(
        filepath: String,
        callback: (Result<String>) -> Unit
    ) = service.uploadImage(filepath, callback)
}