package com.dashagy.tpchallenges.presentation.viewmodel.places

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dashagy.domain.useCases.UploadImageToServiceUseCase
import com.dashagy.domain.utils.Result
import com.dashagy.tpchallenges.presentation.viewmodel.places.model.ViewModelPicture
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPlaceViewModel @Inject constructor(
    private val uploadImageToServiceUseCase: UploadImageToServiceUseCase
): ViewModel() {

    private var _myPlaceState: MutableLiveData<MyPlaceState> = MutableLiveData()
    val myPlaceState: LiveData<MyPlaceState>
        get() = _myPlaceState

    private val addedPictures: MutableList<ViewModelPicture> = mutableListOf()

    fun uploadImages() = viewModelScope.launch(Dispatchers.IO) {
        _myPlaceState.postValue(MyPlaceState.Loading)
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
            is Result.Success -> _myPlaceState.postValue(
                MyPlaceState.UploadSuccess(
                    result.data
                )
            )
            is Result.Error -> _myPlaceState.postValue(
                MyPlaceState.UploadError(
                    result.exception
                )
            )
        }
    }

    fun updateStateOnAddPicture(exception: Exception? = null) {
        exception?.let {
            _myPlaceState.postValue(
                MyPlaceState.AddPictureError(it)
            )
            return
        }

        if (addedPictures.isNotEmpty()){
            _myPlaceState.postValue(
                MyPlaceState.AddPictureSuccess(addedPictures.map { it.localUri })
            )
            return
        }

        _myPlaceState.postValue(
            MyPlaceState.AddPictureError(Exception("Couldn't add picture"))
        )
    }

    fun addPicture(uri: Uri?, path: String) {
        uri?.let {
            addedPictures.add(
                ViewModelPicture(it, path)
            )
        }
    }

    fun getLastAddedPicture(): ViewModelPicture? {
        return if (addedPictures.isNotEmpty()) addedPictures.last() else null
    }

    sealed class MyPlaceState {
        class UploadSuccess(val downloadUrl: String): MyPlaceState()
        class UploadError(val exception: Exception): MyPlaceState()
        class AddPictureSuccess(val uris: List<Uri>): MyPlaceState()
        class AddPictureError(val exception: Exception): MyPlaceState()
        object Loading: MyPlaceState()
    }
}