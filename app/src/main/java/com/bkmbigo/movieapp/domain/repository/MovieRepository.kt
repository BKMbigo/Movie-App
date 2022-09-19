package com.bkmbigo.movieapp.domain.repository

import com.bkmbigo.movieapp.domain.model.Movie
import com.bkmbigo.movieapp.utils.FirebaseCallback
import com.bkmbigo.movieapp.utils.WebApiCallback

interface MovieRepository {

    suspend fun getLatest(site: LatestMovieSite, callback: WebApiCallback<List<Movie>>)

    suspend fun searchMovie(query: String, type: String?, callback: WebApiCallback<List<Movie>>)

    suspend fun getMovieParticulars(movie: Movie, callback: WebApiCallback<Movie>)

    suspend fun getMovies(imdbIDs: List<String>, callback: WebApiCallback<List<Movie>>)

    suspend fun getMovieStatus(movies: List<Movie>, callback: FirebaseCallback<List<Movie>>)

    suspend fun getWatchList(callback: FirebaseCallback<List<String>>, type: Movie.MovieType?=null, retainObserver:Boolean = true)
    suspend fun getDownloadList(callback: FirebaseCallback<List<String>>, type: Movie.MovieType?=null, retainObserver:Boolean = true)
    suspend fun getBookmarked(callback: FirebaseCallback<List<String>>, type: Movie.MovieType?=null, retainObserver:Boolean = true)
    suspend fun getFavorites(callback: FirebaseCallback<List<String>>, type: Movie.MovieType?=null, retainObserver:Boolean = true)

    suspend fun toggleDownloadList(movie: Movie, callback: FirebaseCallback<Movie>)
    suspend fun toggleWatchList(movie: Movie, callback: FirebaseCallback<Movie>)
    suspend fun toggleBookmarked(movie: Movie, callback: FirebaseCallback<Movie>)
    suspend fun toggleFavorite(movie: Movie, callback: FirebaseCallback<Movie>)

    companion object{
        enum class LatestMovieSite{
            PSArips
        }
    }
}