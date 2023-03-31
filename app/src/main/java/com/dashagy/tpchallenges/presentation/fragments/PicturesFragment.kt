package com.dashagy.tpchallenges.presentation.fragments

import android.Manifest
import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.dashagy.domain.entities.Picture
import com.dashagy.tpchallenges.databinding.FragmentPicturesBinding
import com.dashagy.tpchallenges.presentation.activity.PicturesActivity
import com.dashagy.tpchallenges.presentation.adapters.PictureListAdapter
import com.dashagy.tpchallenges.presentation.viewmodel.pictures.PictureViewModel
import com.dashagy.tpchallenges.utils.FileManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PicturesFragment : Fragment() {

    private val viewModel: PictureViewModel by viewModels()

    private var _binding: FragmentPicturesBinding? = null
    private val binding get() = _binding!!

    private lateinit var pickVisualMediaLauncher: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var cameraRequestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>

    private lateinit var pictureListAdapter: PictureListAdapter

    private lateinit var fileManager: FileManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerActivityResultContracts()
        fileManager = FileManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPicturesBinding.inflate(inflater, container, false)

        pictureListAdapter = PictureListAdapter()

        binding.rvPictures.apply {
            adapter = pictureListAdapter
            layoutManager = GridLayoutManager(requireActivity(), 3)
        }

        binding.btnAddPictures.setOnClickListener { onAddImageButtonPressed() }
        binding.btnUploadPictures.setOnClickListener { onUploadImageButtonPressed() }
        binding.btnGoToMap.setOnClickListener { onGoToMapButtonPressed() }

        viewModel.picturesState.observe(viewLifecycleOwner, ::updateImagePreview)
        viewModel.pictureList.observe(viewLifecycleOwner, ::updatePictureList)

        return binding.root
    }

    private fun registerActivityResultContracts(){
        pickVisualMediaLauncher = registerPickVisualMedia()
        cameraRequestPermissionLauncher = registerCameraPermission()
        takePictureLauncher = registerTakePicture()
    }

    private fun registerPickVisualMedia() = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
        if (uris.isNotEmpty()) {
            uris.forEachIndexed { index, uri ->
                addPicture(uri, index)
            }
        } else viewModel.updateStateOnAddPicture(null, Exception("Picture was not added"))
    }

    private fun registerCameraPermission() = registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted ->
        if (isGranted) {
            with(fileManager.createImageLocalUri()) {
                first?.let { uri ->
                    val path = createStoragePath(second)
                    viewModel.updateCameraPicture(uri, path)
                    launchCamera(uri)
                }
            }
        } else {
            Toast.makeText(requireActivity(), "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun registerTakePicture() = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            viewModel.addCameraPicture()
        } else {
            viewModel.updateStateOnAddPicture(null, Exception("Picture was not taken"))
        }
    }

    private fun launchCameraPermission() {
        cameraRequestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun launchCamera(uri: Uri){
        takePictureLauncher.launch(uri)
    }

    private fun launchGalleryPicker() {
        pickVisualMediaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun addPicture(uri: Uri? = null, counter: Int = 0) {
        with (fileManager.createImageLocalUri()) {
            val path = createStoragePath(second, counter)
            viewModel.addPicture(uri = uri ?: first, path = path)
        }
    }

    private fun createStoragePath(timestamp: String, counter: Int = 0) =
        "JPEG_${timestamp}_${counter}_.jpg"


    private fun updateImagePreview(state: PictureViewModel.PicturesState) {
        when (state) {
            is PictureViewModel.PicturesState.Error -> {
                (activity as PicturesActivity).hideProgressBar()
                Toast.makeText(requireActivity(), state.exception.message, Toast.LENGTH_SHORT).show()
            }
            is PictureViewModel.PicturesState.Success -> {
                (activity as PicturesActivity).hideProgressBar()
            }
            PictureViewModel.PicturesState.Loading -> {
                (activity as PicturesActivity).showProgressBar()
            }
        }
    }

    private fun updatePictureList(pictures: List<Picture>){
        pictureListAdapter.updateDataset(pictures)
        binding.btnUploadPictures.isEnabled = pictures.isNotEmpty()
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
                    0 -> launchGalleryPicker()
                    1 -> launchCameraPermission()
                }
            }
        }.show()
    }

    private fun onUploadImageButtonPressed() {
        viewModel.uploadImages()
    }


    private fun onGoToMapButtonPressed() {
        (requireActivity() as PicturesActivity).replaceFragment(MapFragment.newInstance())
    }

    companion object {
        @JvmStatic
        fun newInstance() = PicturesFragment()
    }
}