package com.dashagy.tpchallenges.presentation.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dashagy.domain.entities.Picture
import com.dashagy.tpchallenges.databinding.RecyclerviewPictureItemBinding
import com.dashagy.tpchallenges.presentation.utils.loadImageFromUri

class PictureListAdapter: RecyclerView.Adapter<PictureListAdapter.PictureViewHolder>() {

    private var dataset: List<Picture> = listOf()

    fun addPicture(picture: Picture) {
        val newPictureList = dataset.toMutableList().apply {
            if (!any { pic -> pic.localUri == picture.localUri }) add(picture)
        }
        updateDataset(newPictureList)
    }

    private fun updateDataset(pictureList: List<Picture>){
        val oldPictureList = dataset
        val diffResult: DiffUtil.DiffResult = DiffUtil.calculateDiff(
            PictureListsDiffCallback(oldPictureList, pictureList)
        )
        dataset = pictureList
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

        fun bind(picture: Picture) {
            with(binding){
                ivPicture.loadImageFromUri(
                    ivPicture.context,
                    Uri.parse(picture.localUri)
                )
            }
        }
    }

    private class PictureListsDiffCallback(
        val oldList: List<Picture>,
        val newList: List<Picture>
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