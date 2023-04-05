package com.dashagy.tpchallenges.presentation.fragments

import android.Manifest
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.dashagy.domain.entities.Location
import com.dashagy.tpchallenges.BuildConfig
import com.dashagy.tpchallenges.R
import com.dashagy.tpchallenges.databinding.FragmentLocationBinding
import com.dashagy.tpchallenges.presentation.activity.FirebaseActivity
import com.dashagy.tpchallenges.presentation.viewmodel.LocationViewModel
import com.dashagy.tpchallenges.utils.TimeUtil.toDateString
import com.tomtom.sdk.location.GeoPoint
import com.tomtom.sdk.map.display.MapOptions
import com.tomtom.sdk.map.display.camera.CameraOptions
import com.tomtom.sdk.map.display.image.ImageFactory
import com.tomtom.sdk.map.display.marker.MarkerOptions
import com.tomtom.sdk.map.display.ui.MapFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LocationFragment : Fragment() {

    private val viewModel: LocationViewModel by viewModels()

    private var _binding: FragmentLocationBinding? = null
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

        _binding = FragmentLocationBinding.inflate(inflater, container, false)

        binding.btnStartLocationService.setOnClickListener { onStartLocationServiceButtonPressed() }
        binding.btnStopLocationService.setOnClickListener { onStopLocationServiceButtonPressed() }
        binding.btnSaveLocation.setOnClickListener { onSaveLocationButtonPressed() }
        binding.btnShowMap.setOnClickListener { onShowMapButtonPressed() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.state.observe(viewLifecycleOwner, ::updateUI)
    }

    private fun updateUI(locationState: LocationViewModel.LocationState) {
        when (locationState) {
            is LocationViewModel.LocationState.Failure -> {
                (activity as FirebaseActivity).hideProgressBar()
                Toast.makeText(requireContext(), locationState.exception.message, Toast.LENGTH_SHORT).show()
            }
            LocationViewModel.LocationState.Idle -> {
                (activity as FirebaseActivity).hideProgressBar()
                binding.tvLocation.text = ""
                binding.btnStartLocationService.isEnabled = true
                binding.btnStopLocationService.isEnabled = false
                binding.btnSaveLocation.isEnabled = false
            }
            LocationViewModel.LocationState.Loading -> {
                (activity as FirebaseActivity).showProgressBar()
            }
            is LocationViewModel.LocationState.Running -> {
                (activity as FirebaseActivity).hideProgressBar()
                binding.tvLocation.text = "Lat: ${locationState.location?.latitude}, Lon: ${locationState.location?.longitude}"
                binding.btnStartLocationService.isEnabled = false
                binding.btnStopLocationService.isEnabled = true
                binding.btnSaveLocation.isEnabled = true
            }
            is LocationViewModel.LocationState.Success -> {
                (activity as FirebaseActivity).hideProgressBar()
                Toast.makeText(requireContext(), locationState.callbackResult, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.clearViewModel()
    }

    private fun onStopLocationServiceButtonPressed(){
        stopLocationService()
    }

    private fun onStartLocationServiceButtonPressed(){
        launchLocationPermission()
    }

    private fun onSaveLocationButtonPressed() {
        viewModel.saveLocation()
    }

    private fun onShowMapButtonPressed() {
        val locations = mutableListOf<Location>()

        viewModel.getLocations(requireContext()) { deviceLocations ->
            deviceLocations?.locations?.let { locations.addAll(it) }
        }

        (activity as FirebaseActivity).replaceFragment(
            MapFragment.newInstance(
                mapOptions = MapOptions(
                    mapKey = BuildConfig.TOMTOM_API_KEY,
                    cameraOptions = CameraOptions(
                        zoom = 12.0
                    )
                )
            ).apply {
                getMapAsync { map ->
                    locations.forEach{ location ->
                        val markerOptions = MarkerOptions(
                            coordinate = GeoPoint(location.latitude, location.longitude),
                            pinImage = ImageFactory.fromResource(R.drawable.baseline_push_pin_24),
                            balloonText = "Timestamp: ${location.timestamp.toDateString()}, ${location.latitude}, ${location.longitude}"
                        )
                        map.addMarker(markerOptions)

                        map.addMarkerClickListener { marker ->
                            if(!marker.isSelected()) marker.select()
                        }


                    }
                    map.moveCamera(
                        CameraOptions(
                            position = GeoPoint(
                                if (locations.isNotEmpty()) locations.map { it.latitude }.average() else 0.0,
                                if (locations.isNotEmpty()) locations.map { it.longitude }.average() else 0.0,
                            )
                        )
                    )
                }
            }
        )
    }

    private fun registerLocationPermission() =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            val areAllPermissionsGranted = it.any { (_, value) -> value }
            if (areAllPermissionsGranted) {
                startLocationService()
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

    private fun startLocationService() {
        viewModel.startService(requireContext())
        viewModel.bindService(requireContext())
    }


    private fun stopLocationService() {
        viewModel.unbindService(requireContext())
        viewModel.stopService(requireContext())
    }

    companion object {

        @JvmStatic
        fun newInstance() = LocationFragment()
    }
}