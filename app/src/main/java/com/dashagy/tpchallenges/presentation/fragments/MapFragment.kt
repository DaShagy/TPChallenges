package com.dashagy.tpchallenges.presentation.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Looper
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
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapFragment : Fragment() {

    private val viewModel: LocationViewModel by viewModels()

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var locationPermissionLauncher: ActivityResultLauncher<Array<String>>

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var lastLocation: LocationResult? = null

    private var isServiceStarted: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationPermissionLauncher = registerLocationPermission()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)

        binding.btnStartLocationService.setOnClickListener { launchLocationPermission() }
        binding.btnStopLocationService.setOnClickListener { stopLocationUpdates() }
        binding.btnSaveLocation.setOnClickListener { onSaveLocationBtnPressed() }

        return binding.root
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

    @SuppressLint("MissingPermission", "VisibleForTests")
    private fun startLocationUpdates() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult?) {
                updateLastLocation(result)
            }
        }

        locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10_000
            fastestInterval = 5_000
        }

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

        updateIsServiceStarted(true)
    }

    private fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        updateIsServiceStarted(false)
        updateLastLocation(null)
    }

    private fun updateIsServiceStarted(value: Boolean){
        isServiceStarted = value
        binding.btnStopLocationService.isEnabled = value
    }

    private fun updateLastLocation(location: LocationResult?){
        lastLocation = location
        binding.tvLocation.text = location?.let { "Lat: ${it.lastLocation.latitude}, Lon: ${it.lastLocation.longitude}" } ?: "Lat: , Lon:"
    }

    private fun onSaveLocationBtnPressed() {
        lastLocation?.lastLocation?.let { location ->
            viewModel.saveLocation(
                Location(
                    Build.ID,
                    location.latitude,
                    location.longitude
                ),
                ::showToast
            )
        }
    }

    private fun showToast(string: String) {
        Toast.makeText(requireActivity(), string, Toast.LENGTH_SHORT).show()
    }

    companion object {

        @JvmStatic
        fun newInstance() = MapFragment()
    }
}