package com.dashagy.tpchallenges.presentation.fragments

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.dashagy.domain.entities.Location
import com.dashagy.tpchallenges.databinding.FragmentMapBinding
import com.dashagy.tpchallenges.presentation.viewmodel.LocationViewModel
import com.dashagy.tpchallenges.service.LocationAndroidService
import com.dashagy.tpchallenges.service.LocationAndroidService.Companion.ACTION_START
import com.dashagy.tpchallenges.service.LocationAndroidService.Companion.ACTION_STOP
import com.dashagy.tpchallenges.service.ServiceCallback
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapFragment : Fragment(), ServiceCallback {

    private var locationAndroidService: LocationAndroidService? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            locationAndroidService = (service as LocationAndroidService.LocationAndroidServiceBinder).getService()

            locationAndroidService?.registerCallback(this@MapFragment)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            locationAndroidService?.unregisterCallback()

            locationAndroidService = null
        }

    }


    private val viewModel: LocationViewModel by viewModels()

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var locationPermissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationPermissionLauncher = registerLocationPermission()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMapBinding.inflate(inflater, container, false)

        binding.btnStartLocationService.setOnClickListener { onStartLocationServiceButtonPressed() }
        binding.btnStopLocationService.setOnClickListener { onStopLocationServiceButtonPressed() }
        binding.btnSaveLocation.setOnClickListener { onSaveLocationButtonPressed() }

        viewModel.isLocationAndroidServiceRunning.observe(viewLifecycleOwner){
            binding.btnStopLocationService.isEnabled = it
        }

        val intent = Intent(requireContext(), LocationAndroidService::class.java)
        requireActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        requireActivity().unbindService(serviceConnection)
    }

    private fun onStopLocationServiceButtonPressed(){
        stopLocationUpdates()
    }

    private fun onStartLocationServiceButtonPressed(){
        launchLocationPermission()
    }

    private fun onSaveLocationButtonPressed() {
        viewModel.saveLocation(::showToast)
    }

    private fun registerLocationPermission() =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            val areAllPermissionsGranted = it.any { (_, value) -> value }
            if (areAllPermissionsGranted) {
                startLocationUpdates()
            } else {
                Toast.makeText(requireActivity(), "Permissions rejected", Toast.LENGTH_SHORT).show()
            }
    }

    private fun launchLocationPermission() {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }

    private fun startLocationUpdates() {
        requireContext().startService(
            Intent(requireContext(), LocationAndroidService::class.java).apply {
                action = ACTION_START
            }
        )
    }


    private fun stopLocationUpdates() {
        requireContext().startService(
            Intent(requireContext(), LocationAndroidService::class.java).apply {
                action = ACTION_STOP
            }
        )
        viewModel.updateLocation(null)
    }

    private fun showToast(string: String) {
        Toast.makeText(requireActivity(), string, Toast.LENGTH_SHORT).show()
    }

    override fun updateIsServiceRunning(isServiceRunning: Boolean) {
        viewModel.updateIsLocationRunning(isServiceRunning)
    }

    override fun setLocationFromService(location: Location?) {
        viewModel.updateLocation(location)
    }

    companion object {

        @JvmStatic
        fun newInstance() = MapFragment()
    }
}