package com.dashagy.tpchallenges.presentation.fragments

import android.Manifest
import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dashagy.tpchallenges.databinding.FragmentMyPlaceBinding
import com.dashagy.tpchallenges.presentation.activity.MyPlacesActivity
import com.dashagy.tpchallenges.presentation.utils.loadImageFromUri
import com.dashagy.tpchallenges.presentation.viewmodel.places.MyPlaceViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class MyPlaceFragment : Fragment() {

    private val viewModel: MyPlaceViewModel by viewModels()

    private var _binding: FragmentMyPlaceBinding? = null
    private val binding get() = _binding!!

    private lateinit var pickVisualMediaLauncher: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var cameraRequestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pickVisualMediaLauncher = registerPickVisualMedia()
        cameraRequestPermissionLauncher = registerCameraPermission()
        takePictureLauncher = registerTakePicture()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyPlaceBinding.inflate(inflater, container, false)

        binding.btnAddMyPlaceImage.setOnClickListener { onAddImageButtonPressed() }
        binding.btnUploadMyPlaceImage.setOnClickListener { onUploadImageButtonPressed() }

        viewModel.myPlaceState.observe(viewLifecycleOwner, ::updateImagePreview)

        return binding.root
    }

    private fun registerPickVisualMedia() = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let {
            addPicture(uri)
            viewModel.updateStateOnAddPicture()
        } ?: viewModel.updateStateOnAddPicture(Exception("Picture was not added"))
    }

    private fun registerCameraPermission() = registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted ->
        if (isGranted) {
            addPicture()
            val picture = viewModel.getLastAddedPicture()
            picture?.let { takePictureLauncher.launch(picture.localUri) }
        } else {
            Toast.makeText(requireActivity(), "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addPicture(uri: Uri? = null) {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val createdUri = createPictureFilePath()?.let { file ->
            FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileProvider",
                file
            )
        }
        val path = "JPEG_${timeStamp}_.jpg"

        viewModel.addPicture(uri = uri ?: createdUri, path = path)
    }

    private fun registerTakePicture() = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            viewModel.updateStateOnAddPicture()
        } else {
            viewModel.updateStateOnAddPicture(Exception("Picture was not taken"))
        }
    }


    private fun takePictureFromCamera() {
        cameraRequestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun selectPictureFromGallery() {
        pickVisualMediaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun createPictureFilePath(): File? =
        try {
            val timeStamp: String =
                SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir: File? = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
        } catch (e: IOException) {
            null
        }

    private fun updateImagePreview(state: MyPlaceViewModel.MyPlaceState) {
        when (state) {
            is MyPlaceViewModel.MyPlaceState.AddPictureError -> {
                (activity as MyPlacesActivity).hideProgressBar()
                Toast.makeText(requireActivity(), state.exception.message, Toast.LENGTH_SHORT).show()
            }
            is MyPlaceViewModel.MyPlaceState.AddPictureSuccess -> {
                (activity as MyPlacesActivity).hideProgressBar()
                binding.ivMyPlace.loadImageFromUri(requireActivity(), state.uris.lastOrNull())
            }
            MyPlaceViewModel.MyPlaceState.Loading -> {
                (activity as MyPlacesActivity).showProgressBar()
            }
            is MyPlaceViewModel.MyPlaceState.UploadError -> {
                (activity as MyPlacesActivity).hideProgressBar()
                Toast.makeText(requireActivity(), state.exception.message, Toast.LENGTH_SHORT).show()
            }
            is MyPlaceViewModel.MyPlaceState.UploadSuccess -> {
                (activity as MyPlacesActivity).hideProgressBar()
                Toast.makeText(requireActivity(), state.downloadUrl, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onAddImageButtonPressed() {
        AlertDialog.Builder(requireActivity()).apply {
            setTitle("Select Action")
            setItems(
                arrayOf(
                    "Select image from gallery",
                    "Take photo from camera"
                )
            ) { _, which ->
                when (which) {
                    0 -> selectPictureFromGallery()
                    1 -> takePictureFromCamera()
                }
            }
        }.show()
    }

    private fun onUploadImageButtonPressed() {
        viewModel.uploadImages()
    }

    companion object {
        @JvmStatic
        fun newInstance() = MyPlaceFragment()
    }
}