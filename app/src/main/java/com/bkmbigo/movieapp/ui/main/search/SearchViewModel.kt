package com.bkmbigo.movieapp.ui.main.search

import androidx.lifecycle.*
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

class SearchViewModel(
    private val movieRepository: MovieRepository,
) : ViewModel() {

    private val _movieResults = MutableLiveData<List<Movie>>()
    val movieResults: LiveData<List<Movie>> = _movieResults

    private val _loading = MutableStateFlow(false)
    fun changeLoading(loading: Boolean) {
        _loading.value = loading
    }

    val loading: StateFlow<Boolean> = _loading

    private val _error = Channel<String>()
    val error = _error.receiveAsFlow()


    val callbackMovie = object : WebApiCallback<List<Movie>> {
        override fun onResponse(data: List<Movie>) {
            _movieResults.value = data
            _loading.value = false
        }

        override fun onFailure(e: Exception?) {
            _error.trySend("Failed to get list of movies")
            _loading.value = false
        }

    }

    suspend fun searchMovies(query: String, type: String) {
        if (query.isEmpty()) {
            _error.send("Query is empty")
        }
        _loading.value = true
        withContext(Dispatchers.IO) {
            movieRepository.searchMovie(query, type, callbackMovie)
        }
    }

    suspend fun getParticularMovie(movie: Movie, callback: WebApiCallback<Movie>) {

            if (movie.imdbID == null) {
                _error.trySend("Movie does not have an IMDB ID")
            } else {
                movieRepository.getMovieParticulars(movie, callback)
            }

    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            searchMovies("thor", "movie")
        }
    }

    class HomeViewModelFactory(
        private val movieRepository: MovieRepository
    ) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            SearchViewModel(movieRepository) as T
    }
}