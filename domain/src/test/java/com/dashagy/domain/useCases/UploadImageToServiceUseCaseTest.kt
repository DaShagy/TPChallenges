package com.dashagy.domain.useCases

import com.dashagy.domain.entities.Picture
import com.dashagy.domain.service.ImageService
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.invoke
import com.dashagy.domain.utils.Result
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class UploadImageToServiceUseCaseTest {

    private lateinit var uploadImageToServiceUseCase: UploadImageToServiceUseCase

    @MockK
    private lateinit var imageService: ImageService

    private var picture = Picture("URI", "STORAGE_PATH", "DOWNLOAD_URI")
    private var exception = Exception("Error")

    private var successResult = Result.Success(picture)
    private var errorResult = Result.Error(exception)

    @Before
    fun init() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        uploadImageToServiceUseCase = UploadImageToServiceUseCase(imageService)
    }

    @Test
    fun `invoke should call success callback on successful image upload`() {

        every {
            imageService.uploadImage(picture, captureLambda())
        } answers {
            lambda<(Result<Picture>) -> Unit>().invoke(successResult)
        }

        val callback: (Result<Picture>) -> Unit = { result ->
            Assert.assertEquals(result, successResult)
        }

        uploadImageToServiceUseCase(picture, callback)
    }

    @Test
    fun `invoke should call error callback on failed image upload`() {

        every {
            imageService.uploadImage(picture, captureLambda())
        } answers {
            lambda<(Result<Picture>) -> Unit>().invoke(errorResult)
        }

        val callback: (Result<Picture>) -> Unit = { result ->
            Assert.assertEquals(result, errorResult)
        }

        uploadImageToServiceUseCase(picture, callback)
    }

}