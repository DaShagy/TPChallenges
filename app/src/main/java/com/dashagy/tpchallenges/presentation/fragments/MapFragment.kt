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
import com.dashagy.tpchallenges.databinding.FragmentMapBinding
import com.dashagy.tpchallenges.presentation.activity.PicturesActivity
import com.dashagy.tpchallenges.presentation.viewmodel.LocationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapFragment : Fragment() {

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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.state.observe(viewLifecycleOwner, ::updateUI)
    }


    override fun onResume() {
        super.onResume()
        //Toast.makeText(requireActivity(), "Service: ${viewModel.service()?.get().toString()}", Toast.LENGTH_SHORT).show()
    }

    override fun onPause() {
        super.onPause()
        //.makeText(requireActivity(), "Service: ${viewModel.service()?.get().toString()}", Toast.LENGTH_SHORT).show()
    }

    private fun updateUI(locationState: LocationViewModel.LocationState) {
        when (locationState) {
            is LocationViewModel.LocationState.Failure -> {
                (activity as PicturesActivity).hideProgressBar()
                Toast.makeText(requireContext(), locationState.exception.message, Toast.LENGTH_SHORT).show()
            }
            LocationViewModel.LocationState.Idle -> {
                (activity as PicturesActivity).hideProgressBar()
                binding.tvLocation.text = ""
                binding.btnStartLocationService.isEnabled = true
                binding.btnStopLocationService.isEnabled = false
                binding.btnSaveLocation.isEnabled = false
            }
            LocationViewModel.LocationState.Loading -> {
                (activity as PicturesActivity).showProgressBar()
            }
            is LocationViewModel.LocationState.Running -> {
                (activity as PicturesActivity).hideProgressBar()
                binding.tvLocation.text = "${locationState.location?.id}, Lat: ${locationState.location?.latitude}, Lon: ${locationState.location?.longitude}"
                binding.btnStartLocationService.isEnabled = false
                binding.btnStopLocationService.isEnabled = true
                binding.btnSaveLocation.isEnabled = true
            }
            is LocationViewModel.LocationState.Success -> {
                (activity as PicturesActivity).hideProgressBar()
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
        fun newInstance() = MapFragment()
    }
}