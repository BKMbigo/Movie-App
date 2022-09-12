package com.bkmbigo.movieapp.ui.main.home

import android.util.Log
import androidx.lifecycle.*
import com.bkmbigo.movieapp.api.OmdbApi
import com.bkmbigo.movieapp.api.dto.SearchMovieDto
import com.bkmbigo.movieapp.api.dto.SearchResultsDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(
    private val omdbApiKey: String,
    private val omdbApi: OmdbApi
) : ViewModel() {

    private val _movieResults = MutableLiveData<List<SearchMovieDto>>()
    val movieResults: LiveData<List<SearchMovieDto>> = _movieResults

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    suspend fun searchMovies(query: String, type: String) {
        _loading.value = true
        withContext(Dispatchers.IO) {
            val call = omdbApi.searchMovie(omdbApiKey, query, type)
            call.enqueue(object : Callback<SearchResultsDto> {
                override fun onResponse(
                    call: Call<SearchResultsDto>,
                    response: Response<SearchResultsDto>
                ) {
                    if(response.body() != null) {
                        response.body()!!.search.let {
                            _movieResults.value = it
                        }
                    }
                    _loading.value = false
                }

                override fun onFailure(call: Call<SearchResultsDto>, t: Throwable) {
                    _loading.value = false
                    //TODO("Not yet implemented")
                }


            })
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            searchMovies("thor", "null")
        }
    }

    class HomeViewModelFactory(
        private val omdbApiKey: String,
        private val omdbApi: OmdbApi
    ) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            HomeViewModel(omdbApiKey, omdbApi) as T
    }
}