package com.example.photogallery


import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.photogallery.api.GalleryItem
import com.example.photogallery.api.PhotoRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PhotoGalleryViewModel:ViewModel() {
    private val photoRepository = PhotoRepository()
    private val preferencesRepository = PreferencesRepository.get()
    private val _uiState: MutableStateFlow<PhotoGalleryUiState> = MutableStateFlow(PhotoGalleryUiState())
    val uiState: StateFlow<PhotoGalleryUiState>
        get() = _uiState.asStateFlow()
    private val _searchedQueries: MutableStateFlow<Set<String>> = MutableStateFlow(emptySet())
    val searchedQueries
    get() = _searchedQueries.asStateFlow()


    init {
        viewModelScope.launch{
            preferencesRepository.storedQuery.collectLatest { storedQuery ->
                try {
                    fetchGalleryItems(storedQuery).cachedIn(viewModelScope).collect { fetchedItems ->
                        _uiState.update { oldState ->
                            oldState.copy(
                                images = fetchedItems,
                                query = storedQuery
                            )
                        }
                    }
                }catch (ce: CancellationException){
                    throw ce
                }
                catch (ex: Exception) {
                    Log.e(TAG, "Failed to fetch  items", ex)
                }
            }
            preferencesRepository.searchedQueries.collectLatest { searchedQueries ->
                _searchedQueries.value = searchedQueries
            }
        }
    }

    fun setQuery(query: String) {
           viewModelScope.launch {
               preferencesRepository.setStoredQuery(query)
           }
    }

    private fun fetchGalleryItems(query: String): Flow<PagingData<GalleryItem>> {
        return if (query.isEmpty()){
            photoRepository.fetchPhotos()
        } else{
            photoRepository.searchPhotos(query)
        }

    }
}

data class PhotoGalleryUiState(
    val images: PagingData<GalleryItem> = PagingData.empty(),
    val query: String = "",
)