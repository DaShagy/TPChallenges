package com.dashagy.tpchallenges.presentation.viewmodel.pictures

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dashagy.domain.useCases.UploadImageToServiceUseCase
import com.dashagy.domain.utils.Result
import com.dashagy.tpchallenges.presentation.viewmodel.pictures.model.ViewModelPicture
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PictureViewModel @Inject constructor(
    private val uploadImageToServiceUseCase: UploadImageToServiceUseCase
): ViewModel() {

    private var _picturesState: MutableLiveData<PicturesState> = MutableLiveData()
    val picturesState: LiveData<PicturesState>
        get() = _picturesState

    private val addedPictures: MutableList<ViewModelPicture> = mutableListOf()

    fun uploadImages() = viewModelScope.launch(Dispatchers.IO) {
        _picturesState.postValue(PicturesState.Loading)
        addedPictures.forEach {picture ->
            uploadImageToServiceUseCase(
                picture.localUri.toString(),
                picture.storagePath,
                ::updateStateOnUploadPicture
            )
        }
    }

    private fun updateStateOnUploadPicture(result: Result<String>) {
        when (result) {
            is Result.Success -> _picturesState.postValue(
                PicturesState.UploadSuccess(
                    result.data
                )
            )
            is Result.Error -> _picturesState.postValue(
                PicturesState.UploadError(
                    result.exception
                )
            )
        }
    }

    fun updateStateOnAddPicture(exception: Exception? = null) {
        _picturesState.value = PicturesState.Loading
        exception?.let {
            _picturesState.value = PicturesState.AddPictureError(it)
            return
        }

        if (addedPictures.isNotEmpty()){
            _picturesState.value = PicturesState.AddPictureSuccess(addedPictures)
            return
        }

        _picturesState.value = PicturesState.AddPictureError(
            Exception("Couldn't add picture")
        )

    }

    fun addPicture(uri: Uri?, path: String) {
        _picturesState.value = PicturesState.Loading
        uri?.let {
            val picture = ViewModelPicture(it, path)
            if (!addedPictures.any { addedPicture -> addedPicture.localUri == it }) {
                addedPictures.add(picture)
                updateStateOnAddPicture()
            } else {
                updateStateOnAddPicture(Exception("Picture already loaded"))
            }
        }
    }

    fun getLastAddedPicture(): ViewModelPicture? {
        return if (addedPictures.isNotEmpty()) addedPictures.last() else null
    }


    sealed class PicturesState {
        class UploadSuccess(val downloadUrl: String): PicturesState()
        class UploadError(val exception: Exception): PicturesState()
        class AddPictureSuccess(val pictures: List<ViewModelPicture>): PicturesState()
        class AddPictureError(val exception: Exception): PicturesState()
        object Loading: PicturesState()
    }
}