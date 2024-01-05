package com.example.photogallery

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.photogallery.api.GalleryItem
import com.example.photogallery.databinding.ListItemGalleryBinding

class PhotoViewHolder(private val binding:ListItemGalleryBinding):RecyclerView.ViewHolder(binding.root){
    fun bind(galleryItem: GalleryItem, onItemClicked: (Uri) -> Unit){
        binding.itemImageView.load(galleryItem.url){
            placeholder(R.drawable.ic_launcher_background)
        }
        binding.root.setOnClickListener {
            onItemClicked(galleryItem.photoPageUri)
        }
    }
}

class PhotoListAdapter (private val onItemClicked: (Uri) -> Unit): PagingDataAdapter<GalleryItem, PhotoViewHolder>(COMPARATOR){
    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val item:GalleryItem? = getItem(position)
        holder.bind(checkNotNull(item), onItemClicked)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemGalleryBinding.inflate(inflater,parent,false)
        return PhotoViewHolder(binding)
}
    companion object{
        private val COMPARATOR = object : DiffUtil.ItemCallback<GalleryItem>() {
            override fun areItemsTheSame(oldItem: GalleryItem, newItem: GalleryItem): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areContentsTheSame(oldItem: GalleryItem, newItem: GalleryItem): Boolean {
                return oldItem == newItem
            }
        }
    }

}