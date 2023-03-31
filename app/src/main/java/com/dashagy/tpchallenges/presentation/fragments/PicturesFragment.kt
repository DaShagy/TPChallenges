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
import androidx.recyclerview.widget.GridLayoutManager
import com.dashagy.domain.entities.Picture
import com.dashagy.tpchallenges.databinding.FragmentPicturesBinding
import com.dashagy.tpchallenges.presentation.activity.PicturesActivity
import com.dashagy.tpchallenges.presentation.adapters.PictureListAdapter
import com.dashagy.tpchallenges.presentation.viewmodel.pictures.PictureViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class PicturesFragment : Fragment() {

    private val viewModel: PictureViewModel by viewModels()

    private var _binding: FragmentPicturesBinding? = null
    private val binding get() = _binding!!

    private lateinit var pickVisualMediaLauncher: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var cameraRequestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>

    private lateinit var pictureListAdapter: PictureListAdapter

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
        _binding = FragmentPicturesBinding.inflate(inflater, container, false)

        pictureListAdapter = PictureListAdapter()

        binding.rvPictures.apply {
            adapter = pictureListAdapter
            layoutManager = GridLayoutManager(requireActivity(), 3)
        }

        binding.btnAddPictures.setOnClickListener { onAddImageButtonPressed() }
        binding.btnUploadPictures.setOnClickListener { onUploadImageButtonPressed() }

        viewModel.picturesState.observe(viewLifecycleOwner, ::updateImagePreview)
        viewModel.pictureList.observe(viewLifecycleOwner, ::updatePictureList)

        return binding.root
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
            addPicture()
            val picture = viewModel.getLastAddedPicture()
            picture?.let { takePictureLauncher.launch(Uri.parse(picture.localUri)) }
        } else {
            Toast.makeText(requireActivity(), "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }


    private fun registerTakePicture() = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            viewModel.updateStateOnAddPicture(viewModel.getLastAddedPicture())
        } else {
            viewModel.updateStateOnAddPicture(null, Exception("Picture was not taken"))
        }
    }

    private fun addPicture(uri: Uri? = null, counter: Int = 0) {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val createdUri = createPictureFilePath()?.let { file ->
            FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileProvider",
                file
            )
        }
        val path = "JPEG_${timeStamp}_$counter.jpg"

        viewModel.addPicture(uri = uri ?: createdUri, path = path)
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
        fun newInstance() = PicturesFragment()
    }
}