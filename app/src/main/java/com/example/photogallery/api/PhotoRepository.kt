package com.example.photogallery.api


import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

private const val PAGE_SIZE = 30
class PhotoRepository {
    private val flickrApi: FlickrApi
    init {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(PhotoInterceptor())
            .build()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.flickr.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .build()
        flickrApi = retrofit.create()
    }

    fun fetchPhotos(): Flow<PagingData<GalleryItem>> {
        return Pager(
            config = PagingConfig(
                PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                FlickrPageSource(flickrApi, null, PAGE_SIZE)
            }).flow
    }
    fun searchPhotos(query: String): Flow<PagingData<GalleryItem>>{
        return Pager(
            config = PagingConfig(
                PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                FlickrPageSource(flickrApi, query, PAGE_SIZE)
            }).flow
    }

}