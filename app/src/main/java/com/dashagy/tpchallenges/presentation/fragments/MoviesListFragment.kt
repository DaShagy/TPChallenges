package com.dashagy.tpchallenges.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.dashagy.tpchallenges.databinding.FragmentMoviesListBinding
import com.dashagy.tpchallenges.presentation.activity.MainActivity
import com.dashagy.tpchallenges.presentation.adapters.MovieListAdapter
import com.dashagy.tpchallenges.presentation.utils.clean
import com.dashagy.tpchallenges.presentation.viewmodel.movies.MoviesViewModel
import com.dashagy.tpchallenges.presentation.viewmodel.movies.model.ViewModelMovie
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MoviesListFragment : Fragment() {

    private val moviesViewModel: MoviesViewModel by viewModels()

    private var _binding: FragmentMoviesListBinding? = null
    private val binding get() = _binding!!

    private lateinit var movieListAdapter: MovieListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoviesListBinding.inflate(inflater, container, false)

        movieListAdapter = MovieListAdapter().apply {
            setListener(
                object : MovieListAdapter.OnItemClickListener {
                    override fun onItemClick(movie: ViewModelMovie) {
                        (activity as MainActivity).replaceFragment(MovieDetailsFragment.newInstance(movie))
                    }
                }
            )
        }

        binding.rvMovies.apply {
            adapter = movieListAdapter
            layoutManager = GridLayoutManager(requireActivity(), 3)
        }

        moviesViewModel.movieState.observe(viewLifecycleOwner, ::updateUI)

        binding.svMovie.apply {
            setOnQueryTextListener(
                object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        searchMovie(query.orEmpty())
                        cleanSearchView(this@apply)
                        (activity as MainActivity).hideKeyboard()
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        return false
                    }
                })
        }

        binding.btnGetPopularMovies.setOnClickListener { getPopularMovies() }

        return binding.root
    }

    private fun searchMovie(query: String) = moviesViewModel.searchMovie(query)

    private fun getMovieById(id: Int) = moviesViewModel.getMovieById(id)

    private fun getPopularMovies() = moviesViewModel.getPopularMovies()

    private fun updateUI(state: MoviesViewModel.MovieState) {
        when (state) {
            is MoviesViewModel.MovieState.Error -> {
                (activity as MainActivity).hideProgressBar()
                Toast.makeText(activity, state.exception.message, Toast.LENGTH_SHORT).show()
            }
            MoviesViewModel.MovieState.Loading -> (activity as MainActivity).showProgressBar()
            is MoviesViewModel.MovieState.Success -> {
                (activity as MainActivity).hideProgressBar()
                movieListAdapter.updateDataset(state.movies)
            }
        }
    }

    private fun cleanSearchView(sv: SearchView) {
        sv.clean()
    }

    companion object {

        @JvmStatic
        fun newInstance() = MoviesListFragment()
    }
}