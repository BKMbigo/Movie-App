package com.bkmbigo.movieapp.ui.main.home.latest

import androidx.lifecycle.*
import com.bkmbigo.movieapp.api.PSAripsAPI
import com.bkmbigo.movieapp.api.dto.PSAripsFeed
import com.bkmbigo.movieapp.api.dto.PSAItem
import com.bkmbigo.movieapp.domain.model.Movie
import com.bkmbigo.movieapp.domain.repository.MovieRepository
import com.bkmbigo.movieapp.utils.WebApiCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LatestViewModel(
    private val movieRepository: MovieRepository
): ViewModel()  {

    private val _latestResults = MutableLiveData<List<Movie>>()
    val latestResults: LiveData<List<Movie>> = _latestResults

    private val _errorMessage = Channel<String>()
    val errorMessage get() = _errorMessage.receiveAsFlow()

    private val _loading = MutableStateFlow(false)
    fun changeLoading(loading: Boolean){_loading.value = loading}
    val loading: StateFlow<Boolean> get() = _loading

    private val psaCallback = object: WebApiCallback<List<Movie>>{
        override fun onFailure(e: Exception?) {
            _errorMessage.trySend(e?.message?:"Error Encountered")
        }

        override fun onResponse(data: List<Movie>) {
            _latestResults.value = data
            _loading.value = false
        }

    }

    suspend fun getLatestResults(){
        withContext(Dispatchers.IO){
            movieRepository.getLatest(MovieRepository.Companion.LatestMovieSite.PSArips, psaCallback)
        }
    }

    init {
        viewModelScope.launch {
            getLatestResults()
        }
    }


    class LatestViewModelFactory(private val movieRepository: MovieRepository): ViewModelProvider.NewInstanceFactory(){
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = LatestViewModel(movieRepository) as T
    }
}