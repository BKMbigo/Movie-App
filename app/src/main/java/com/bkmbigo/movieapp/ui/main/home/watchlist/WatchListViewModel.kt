package com.bkmbigo.movieapp.ui.main.home.watchlist

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

class WatchListViewModel(
    private val movieRepository: MovieRepository
): ViewModel() {

    private val _loading = MutableStateFlow(false)
    fun changeLoading(loading: Boolean){_loading.value = loading}
    val loading: StateFlow<Boolean> get() = _loading

    private val _error = Channel<String>()
    val error = _error.receiveAsFlow()

    private val _state = MutableStateFlow(WatchListState.All)
    fun changeState(state: WatchListState){
        _state.value = state
        viewModelScope.launch { getWatchList() }
    }
    val state: StateFlow<WatchListState> = _state

    private val _watchList = MutableLiveData<List<Movie>>()
    val watchList: LiveData<List<Movie>> = _watchList

    var watchListSize = 0

    private val watchCallback = object: FirebaseCallback<List<String>>{
        override fun onSuccess(data: List<String>) {
            watchListSize = data.size
            if(data.isEmpty()){
                _loading.value = false
                _error.trySend("Empty Watch List")
            }else {
                viewModelScope.launch {
                    movieRepository.getMovies(data, getMoviesCallback)
                }
            }

        }
        override fun onError(e: Exception) { e.message?.let { _error.trySend(it) }; _loading.value = false }
    }

    private val getMoviesCallback = object : WebApiCallback<List<Movie>>{
        override fun onResponse(data: List<Movie>) {
            _watchList.value = data
            _loading.value = false
        }

        override fun onFailure(e: Exception?) { e?.message?.let { _error.trySend(it) }; _loading.value = false}
    }

    private suspend fun getWatchList(){
        _loading.value = true

        withContext(Dispatchers.IO){
            movieRepository.getWatchList(watchCallback, getStateName())
        }

    }

    init {
        refresh()
    }
    fun refresh(){
        viewModelScope.launch {
            getWatchList()
        }
    }

    private fun getStateName(): Movie.MovieType?{
        return when(_state.value){
            WatchListState.All -> null
            WatchListState.Movie -> Movie.MovieType.Movie
            WatchListState.Series -> Movie.MovieType.Series
            WatchListState.Episode -> Movie.MovieType.Episode
            WatchListState.Game -> Movie.MovieType.Game
        }
    }

    enum class WatchListState{
        All,
        Movie,
        Series,
        Episode,
        Game
    }

    class WatchListViewModelFactory(private val movieRepository: MovieRepository): ViewModelProvider.NewInstanceFactory(){
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = WatchListViewModel(movieRepository) as T
    }
}