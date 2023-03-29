package com.dashagy.tpchallenges.presentation.viewmodel.places

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dashagy.domain.useCases.UploadImageToServiceUseCase
import com.dashagy.domain.utils.Result
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

    fun uploadImage(filepath: String) = viewModelScope.launch(Dispatchers.IO) {
        _myPlaceState.postValue(MyPlaceState.Loading)
        uploadImageToServiceUseCase(filepath, ::updateStateOnUploadPicture)
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

    fun updateStateOnAddPicture(uri: Uri?, exception: Exception? = null) {
        uri?.let {
            _myPlaceState.postValue(
                MyPlaceState.AddPictureSuccess(it)
            )
        } ?: exception?.let {
            _myPlaceState.postValue(
                MyPlaceState.AddPictureError(it)
            )
        } ?: _myPlaceState.postValue(
            MyPlaceState.AddPictureError(Exception("Couldn't add picture"))
        )
    }

    sealed class MyPlaceState {
        class UploadSuccess(val downloadUrl: String): MyPlaceState()
        class UploadError(val exception: Exception): MyPlaceState()
        class AddPictureSuccess(val uri: Uri): MyPlaceState()
        class AddPictureError(val exception: Exception): MyPlaceState()
        object Loading: MyPlaceState()
    }
}