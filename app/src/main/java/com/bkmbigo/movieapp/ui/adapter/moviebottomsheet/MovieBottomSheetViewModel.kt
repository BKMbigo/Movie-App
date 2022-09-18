package com.bkmbigo.movieapp.ui.adapter.moviebottomsheet

import androidx.lifecycle.*
import com.bkmbigo.movieapp.domain.model.Movie
import com.bkmbigo.movieapp.domain.repository.MovieRepository
import com.bkmbigo.movieapp.utils.FirebaseCallback
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class MovieBottomSheetViewModel(
    private val initialMovie: Movie,
    private val movieRepository: MovieRepository
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    private val _movie = MutableLiveData<Movie>(initialMovie)
    val movie: LiveData<Movie> = _movie

    private val _error = Channel<String> { }
    val error = _error.receiveAsFlow()

    fun toggleBookmark() {
        _loading.value = true

        val bookmarkCallback = object : FirebaseCallback<Movie> {
            override fun onSuccess(data: Movie) {
                _loading.value = false
                _movie.value = data
            }

            override fun onError(e: Exception) {
                _loading.value = false
                e.message?.let { _error.trySend(it) }
            }
        }
        val movie = movie.value!!
        viewModelScope.launch {
            movieRepository.toggleBookmarked(movie, bookmarkCallback)
        }
    }

    fun toggleDownloadList() {
        _loading.value = true

        val downloadCallback = object : FirebaseCallback<Movie> {
            override fun onSuccess(data: Movie) {
                _loading.value = false
                _movie.value = data
            }

            override fun onError(e: Exception) {
                _loading.value = false
                e.message?.let { _error.trySend(it) }
            }
        }
        val movie = movie.value!!
        viewModelScope.launch {
            movieRepository.toggleDownloadList(movie, downloadCallback)
        }
    }

    fun toggleFavorite() {
        _loading.value = true

        val favoriteCallback = object : FirebaseCallback<Movie> {
            override fun onSuccess(data: Movie) {
                _loading.value = false
                _movie.value = data
            }

            override fun onError(e: Exception) {
                _loading.value = false
                e.message?.let { _error.trySend(it) }
            }
        }
        val movie = movie.value!!
        viewModelScope.launch {
            movieRepository.toggleFavorite(movie, favoriteCallback)
        }
    }

    fun toggleWatchList() {
        _loading.value = true

        val watchListCallback = object : FirebaseCallback<Movie> {
            override fun onSuccess(data: Movie) {
                _loading.value = false
                _movie.value = data
            }

            override fun onError(e: Exception) {
                _loading.value = false
                e.message?.let { _error.trySend(it) }
            }
        }
        val movie = movie.value!!
        viewModelScope.launch {
            movieRepository.toggleWatchList(movie, watchListCallback)
        }
    }


    class MovieBottomSheetViewModelFactory(
        private val movie: Movie,
        private val movieRepository: MovieRepository
    ) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            MovieBottomSheetViewModel(movie, movieRepository) as T
    }
}