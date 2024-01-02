package com.example.photogallery.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.http.GET
import retrofit2.http.Query


interface FlickrApi {

    @GET("services/rest/?method=flickr.interestingness.getList")
    suspend fun fetchPhotos(
        @Query("page") page:Int,
        @Query("per_page") pageSize: Int)
    :FlickrResponse
    @GET("services/rest?method=flickr.photos.search")
    suspend fun searchPhotos(
        @Query("text") query: String,
        @Query("page") page:Int,
        @Query("per_page") pageSize: Int)
    : FlickrResponse
}