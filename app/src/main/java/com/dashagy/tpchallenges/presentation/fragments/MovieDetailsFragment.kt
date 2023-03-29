package com.dashagy.tpchallenges.presentation.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dashagy.tpchallenges.databinding.FragmentMovieDetailsBinding
import com.dashagy.tpchallenges.presentation.utils.loadPosterImage
import com.dashagy.tpchallenges.presentation.viewmodel.movies.model.ViewModelMovie

class MovieDetailsFragment : Fragment() {

    private var _binding: FragmentMovieDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)

        @Suppress("DEPRECATION") val movie =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) arguments?.getParcelable(MOVIE_KEY, ViewModelMovie::class.java)
            else arguments?.getParcelable(MOVIE_KEY) as? ViewModelMovie

        movie?.let {
            binding.apply {
                tvMovieTitle.text = it.title
                tvMovieOverview.text = it.overview
                ivMoviePoster.loadPosterImage(requireContext(), it.poster)
            }
        }

        return binding.root
    }

    companion object {
        private const val MOVIE_KEY = "movie_id"

        @JvmStatic
        fun newInstance(movie: ViewModelMovie) = MovieDetailsFragment().apply {
            arguments = Bundle().apply {
                putParcelable(MOVIE_KEY, movie)
            }
        }
    }
}