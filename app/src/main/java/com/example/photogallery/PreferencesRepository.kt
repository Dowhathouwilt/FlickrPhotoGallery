package com.example.photogallery

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class PreferencesRepository private constructor(
    private val dataStore: DataStore<Preferences>
) {
    val storedQuery: Flow<String> = dataStore.data.map {
        it[SEARCH_QUERY_KEY] ?: ""
    }.distinctUntilChanged()

    val searchedQueries: Flow<Set<String>> = dataStore.data.map {
        it[SEARCH_QUERIES_KEY]?: emptySet()
    }.distinctUntilChanged()

    suspend fun setStoredQuery(query: String) {
        dataStore.edit {
            it[SEARCH_QUERY_KEY] = query
            val searchedQueries:Set<String> = it[SEARCH_QUERIES_KEY]?: emptySet()
            it[SEARCH_QUERIES_KEY] = searchedQueries.plusElement(query)
        }
    }


    companion object {
        private val SEARCH_QUERY_KEY = stringPreferencesKey("search_query")
        private val SEARCH_QUERIES_KEY = stringSetPreferencesKey("searched_queries")
        private var INSTANCE: PreferencesRepository? = null
        fun initialize(context: Context) {
            if (INSTANCE == null) {
                val dataStore = PreferenceDataStoreFactory.create {
                    context.preferencesDataStoreFile("settings")
                }
                INSTANCE = PreferencesRepository(dataStore)
            }
        }

        fun get(): PreferencesRepository {
            return INSTANCE ?: throw IllegalStateException(
                "PreferencesRepository must be initialized"
            )
        }
    }
}