package com.bkmbigo.movieapp.ui.main.home.dowloadlist

import androidx.lifecycle.*
import com.bkmbigo.movieapp.domain.model.Movie
import com.bkmbigo.movieapp.domain.repository.MovieRepository
import com.bkmbigo.movieapp.utils.FirebaseCallback
import com.bkmbigo.movieapp.utils.WebApiCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DownloadListViewModel(
    private val movieRepository: MovieRepository
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    fun changeLoading(loading: Boolean) {
        _loading.value = loading
    }

    val loading: StateFlow<Boolean> get() = _loading

    private val _error = Channel<String>()
    val error = _error.receiveAsFlow()

    private val _downloadList = MutableLiveData<List<Movie>>()
    val downloadList: LiveData<List<Movie>> = _downloadList

    private val _state = MutableStateFlow(DownloadListState.All)
    fun changeState(state: DownloadListState) {
        _state.value = state
        refreshDownloadList()
    }

    val state: StateFlow<DownloadListState> = _state

    fun refreshDownloadList() {
        viewModelScope.launch {
            getDownloadList()
        }
    }


    init {
        refreshDownloadList()
    }

    private suspend fun getDownloadList() {
        _loading.value = true

        withContext(Dispatchers.IO) {
            movieRepository.getDownloadList(downloadCallback, getState(), true)
        }
    }

    private val downloadCallback = object : FirebaseCallback<List<String>> {
        override fun onSuccess(data: List<String>) {
            //downloadListSize = data.size
            if (data.isEmpty()) {
                _loading.value = false
                _downloadList.value = emptyList()
                _error.trySend("Empty Download List")
            } else {
                viewModelScope.launch {
                    movieRepository.getMovies(data, getMoviesCallback)
                }
            }
        }

        override fun onError(e: Exception) {
            e.message?.let { _error.trySend(it) }; _loading.value = false
        }
    }

    private val getMoviesCallback = object : WebApiCallback<List<Movie>> {
        override fun onResponse(data: List<Movie>) {
            _downloadList.value = data
            _loading.value = false
        }

        override fun onFailure(e: Exception?) {
            e?.message?.let { _error.trySend(it) }; _loading.value = false
        }
    }

    private fun getState(): Movie.MovieType? {
        return when (_state.value) {
            DownloadListState.All -> null
            DownloadListState.Movie -> Movie.MovieType.Movie
            DownloadListState.Series -> Movie.MovieType.Series
            DownloadListState.Episode -> Movie.MovieType.Episode
            DownloadListState.Game -> Movie.MovieType.Game

        }
    }

    enum class DownloadListState {
        All,
        Movie,
        Series,
        Episode,
        Game
    }

    class DownloadListViewModelFactory(private val movieRepository: MovieRepository) :
        ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            DownloadListViewModel(movieRepository) as T
    }
}