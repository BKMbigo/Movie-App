package com.bkmbigo.movieapp.ui.main.home.latest

import androidx.lifecycle.*
import com.bkmbigo.movieapp.domain.model.Movie
import com.bkmbigo.movieapp.domain.repository.MovieRepository
import com.bkmbigo.movieapp.utils.WebApiCallback
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LatestViewModel(
    private val movieRepository: MovieRepository
) : ViewModel() {

    private val _latestResults = MutableLiveData<List<Movie>>()
    val latestResults: LiveData<List<Movie>> = _latestResults

    private val _latestState = MutableStateFlow(MovieRepository.Companion.LatestMovieSite.PSArips)
    fun changeLatestState(state: MovieRepository.Companion.LatestMovieSite) {
        _latestState.value = state; viewModelScope.launch { retrieveLatestResults() }
    }

    val latestState: StateFlow<MovieRepository.Companion.LatestMovieSite> get() = _latestState

    private val _errorMessage = Channel<String>()
    val errorMessage get() = _errorMessage.receiveAsFlow()

    private val _loading = MutableStateFlow(false)
    fun changeLoading(loading: Boolean) {
        _loading.value = loading
    }

    val loading: StateFlow<Boolean> get() = _loading

    private val psaCallback = object : WebApiCallback<List<Movie>> {
        override fun onFailure(e: Exception?) {
            _errorMessage.trySend(e?.message ?: "Error Encountered")
            _loading.value = false
        }

        override fun onResponse(data: List<Movie>) {
            _latestResults.value = data
            _loading.value = false
        }

    }

     fun retrieveLatestResults() {
        viewModelScope.launch {
            _loading.value = true
            movieRepository.getLatest(_latestState.value, psaCallback)
        }
    }

    init {
        retrieveLatestResults()
    }


    class LatestViewModelFactory(private val movieRepository: MovieRepository) :
        ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            LatestViewModel(movieRepository) as T
    }
}