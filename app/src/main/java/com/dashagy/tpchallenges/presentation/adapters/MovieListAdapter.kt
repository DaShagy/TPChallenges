package com.dashagy.tpchallenges.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dashagy.tpchallenges.databinding.RecyclerviewMovieItemBinding
import com.dashagy.tpchallenges.domain.entities.Movie
import com.dashagy.tpchallenges.presentation.utils.loadImage

class MovieListAdapter: RecyclerView.Adapter<MovieListAdapter.MovieViewHolder>() {

    private var dataset: List<Movie> = listOf()
    private var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(movie: Movie)
    }

    fun updateDataset(movieList: List<Movie>){
        val oldMovieList = dataset
        val diffResult: DiffUtil.DiffResult = DiffUtil.calculateDiff(
            MovieListsDiffCallback(oldMovieList, movieList)
        )
        dataset = movieList
        diffResult.dispatchUpdatesTo(this)
    }

    fun setListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = RecyclerviewMovieItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(dataset[position], listener)
    }

    override fun getItemCount(): Int = dataset.size

    inner class MovieViewHolder(
        private val binding: RecyclerviewMovieItemBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie, listener: OnItemClickListener?) {
            with(binding) {
                ivMovieItemPoster.loadImage(
                    ivMovieItemPoster.context,
                    movie.poster
                )
                listener?.let { listener ->
                    root.setOnClickListener {
                        listener.onItemClick(movie)
                    }
                }
            }
        }
    }

    private class MovieListsDiffCallback(
        val oldList: List<Movie>,
        val newList: List<Movie>
    ) : DiffUtil.Callback(){

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

    }
}