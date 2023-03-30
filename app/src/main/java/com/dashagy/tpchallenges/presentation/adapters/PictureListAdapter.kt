package com.dashagy.tpchallenges.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dashagy.tpchallenges.databinding.RecyclerviewPictureItemBinding
import com.dashagy.tpchallenges.presentation.utils.loadImageFromUri
import com.dashagy.tpchallenges.presentation.viewmodel.pictures.model.ViewModelPicture

class PictureListAdapter: RecyclerView.Adapter<PictureListAdapter.PictureViewHolder>() {

    private var dataset: List<ViewModelPicture> = listOf()

    fun updateDataset(movieList: List<ViewModelPicture>){
        val oldMovieList = dataset
        val diffResult: DiffUtil.DiffResult = DiffUtil.calculateDiff(
            PictureListsDiffCallback(oldMovieList, movieList)
        )
        dataset = movieList
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictureViewHolder {
        val binding = RecyclerviewPictureItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PictureViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PictureViewHolder, position: Int) {
        holder.bind(dataset[position])
    }

    override fun getItemCount(): Int = dataset.size

    inner class PictureViewHolder(
        private val binding: RecyclerviewPictureItemBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(picture: ViewModelPicture) {
            with(binding){
                ivPicture.loadImageFromUri(
                    ivPicture.context,
                    picture.localUri
                )
            }
        }
    }

    private class PictureListsDiffCallback(
        val oldList: List<ViewModelPicture>,
        val newList: List<ViewModelPicture>
    ) : DiffUtil.Callback(){

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].localUri == newList[newItemPosition].localUri
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

    }
}