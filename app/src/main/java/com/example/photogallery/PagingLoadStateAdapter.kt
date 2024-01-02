package com.example.photogallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.photogallery.databinding.LoadStateBinding


class PagingLoadStateViewHolder(private val binding: LoadStateBinding): RecyclerView.ViewHolder(binding.root){
    fun bind(loadState: LoadState){
        binding.progressBar.visibility = toVisibility(loadState is LoadState.Loading)
    }
    private fun toVisibility(constraint: Boolean): Int = if (constraint) {
        View.VISIBLE
    } else {
        View.GONE
    }
}
class PagingLoadStateAdapter(): LoadStateAdapter<PagingLoadStateViewHolder>() {
    override fun onBindViewHolder(holder: PagingLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

        override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): PagingLoadStateViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LoadStateBinding.inflate(inflater,parent,false)
        return PagingLoadStateViewHolder(binding)
    }

}
