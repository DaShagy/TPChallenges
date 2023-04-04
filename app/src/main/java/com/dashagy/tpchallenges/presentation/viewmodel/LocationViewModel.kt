package com.dashagy.tpchallenges.presentation.viewmodel

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dashagy.domain.entities.Location
import com.dashagy.domain.useCases.SaveLocationToServiceUseCase
import com.dashagy.domain.utils.Result
import com.dashagy.tpchallenges.service.LocationAndroidService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val saveLocationToServiceUseCase: SaveLocationToServiceUseCase
): ViewModel(), LocationAndroidService.Callback {

    private var locationAndroidService: WeakReference<LocationAndroidService>? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            locationAndroidService = WeakReference((service as LocationAndroidService.LocationAndroidServiceBinder).getService())
            locationAndroidService?.get()?.registerCallback(this@LocationViewModel)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            locationAndroidService?.get()?.unregisterCallback()
            locationAndroidService = null
        }
    }
    fun startService(context: Context){
        context.startService(
            Intent(context, LocationAndroidService::class.java).apply {
                action = LocationAndroidService.ACTION_START
            }
        )
    }

    fun stopService(context: Context){
        context.startService(
            Intent(context, LocationAndroidService::class.java).apply {
                action = LocationAndroidService.ACTION_STOP
            }
        )
    }
    fun bindService(context: Context) {
        context.bindService(Intent(context, LocationAndroidService::class.java), serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun unbindService(context: Context) {
        context.unbindService(serviceConnection)
    }

    private var _state: MutableLiveData<LocationState> = MutableLiveData()
    val state: LiveData<LocationState>
        get() = _state

    fun saveLocation() = viewModelScope.launch(Dispatchers.IO) {
        val previousState = state
        val location = (state.value as? LocationState.Running)?.location

        _state.postValue(LocationState.Loading)

        location?.let {
            saveLocationToServiceUseCase(it) { result ->
                when (result) {
                    is Result.Error -> _state.postValue(LocationState.Failure(result.exception))
                    is Result.Success ->_state.postValue(
                        LocationState.Success(it, result.data)
                    )
                }
            }
        }

        _state.postValue(LocationState.Failure(Exception("Couldn't save the location")))

        _state.postValue(previousState.value)
    }


    override fun onCallback(service: LocationAndroidService, location: Location?, isServiceRunning: Boolean) {
        Log.d("VIEWMODEL", locationAndroidService?.get().toString())
        Log.d("VIEWMODEL", service.toString())
        _state.postValue(
            if (isServiceRunning) LocationState.Running(location)
            else LocationState.Idle
        )
    }

    override fun onCleared() {
        super.onCleared()
        locationAndroidService = null
    }

    fun clearViewModel(){
        onCleared()
    }

    sealed class LocationState{
        object Loading: LocationState()
        class Running(val location: Location?): LocationState()
        object Idle: LocationState()
        class Success(val location: Location, val callbackResult: String): LocationState()
        class Failure(val exception: Exception): LocationState()
    }

}