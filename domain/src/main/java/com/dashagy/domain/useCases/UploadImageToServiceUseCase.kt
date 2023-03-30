package com.dashagy.domain.useCases

import com.dashagy.domain.service.ImageService
import com.dashagy.domain.utils.Result

class UploadImageToServiceUseCase(
    private val service: ImageService
) {
    operator fun invoke(
        imageUri: String,
        fileName: String,
        callback: (Result<String>) -> Unit
    ) = service.uploadImage(imageUri, fileName, callback)
}