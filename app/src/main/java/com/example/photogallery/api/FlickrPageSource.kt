package com.example.photogallery.api

import android.app.DownloadManager.Query
import android.net.http.HttpException
import androidx.annotation.RequiresApi
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import java.io.IOException
//response.isEmpty() or (response.size < params.loadSize)
private const val STARTING_PAGE = 1
class FlickrPageSource(
    private val flickrApi:FlickrApi,
    private val query: String?,
    private val pageSize: Int
): PagingSource<Int, GalleryItem>() {
    override fun getRefreshKey(state: PagingState<Int, GalleryItem>): Int? {
        val anchorPosition= state.anchorPosition?:return null
        val page = state.closestPageToPosition(anchorPosition)?:return null
        return page.prevKey?.plus(1)?:page.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GalleryItem > {
        val page = params.key?: STARTING_PAGE
        val response:List<GalleryItem>
        return try {
            response = if (query.isNullOrEmpty()){
                flickrApi.fetchPhotos(page, params.loadSize).photos.galleryItems
            }else{
                flickrApi.searchPhotos(query, page, params.loadSize).photos.galleryItems
            }
             LoadResult.Page(
                data = response,
                prevKey = if (page == STARTING_PAGE) null else page -1,
                nextKey = if (response.size == params.loadSize){
                    page + (params.loadSize/pageSize)
                }
                else null
            )
        } catch (ex: IOException){
            return LoadResult.Error(ex)
        }catch (es:CancellationException){
            throw es
        }catch (ex: Exception){
            return LoadResult.Error(ex)
        }
    }
}