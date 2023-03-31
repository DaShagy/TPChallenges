package com.dashagy.domain.useCases

import com.dashagy.domain.entities.Picture
import com.dashagy.domain.service.ImageService
import com.dashagy.domain.utils.Result

class UploadImageToServiceUseCase(
    private val service: ImageService
) {
    operator fun invoke(
        picture: Picture,
        callback: (Result<Picture>) -> Unit
    ) = service.uploadImage(picture, callback)
}