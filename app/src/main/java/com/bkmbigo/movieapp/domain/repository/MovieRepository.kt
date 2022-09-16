package com.bkmbigo.movieapp.domain.repository

import com.bkmbigo.movieapp.domain.model.Movie
import com.bkmbigo.movieapp.utils.FirebaseReadCallback
import com.bkmbigo.movieapp.utils.FirebaseWriteCallback
import com.bkmbigo.movieapp.utils.WebApiCallback
import retrofit2.Call

interface MovieRepository {

    suspend fun getLatest(site: LatestMovieSite, callback: WebApiCallback<List<Movie>>)

    suspend fun searchMovie(query: String, type: String?, callback: WebApiCallback<List<Movie>>)

    suspend fun getMovieParticulars(movie: Movie, callback: WebApiCallback<Movie>)

    suspend fun getWatchList(callback: FirebaseReadCallback<List<Movie>>)
    suspend fun getDownloadList(callback: FirebaseReadCallback<List<Movie>>)

    suspend fun addToDownloadList(imdbID: String, callback: FirebaseWriteCallback)
    suspend fun removeFromDownloadList(imdbID: String, callback: FirebaseWriteCallback)

    suspend fun addToWatchList(imdbID: String, callback: FirebaseWriteCallback)
    suspend fun removeFromWatchList(imdbID: String, callback: FirebaseWriteCallback)

    suspend fun addBookmarked(imdbID: String, callback: FirebaseWriteCallback)
    suspend fun removeBookmark(imdbID: String, callback: FirebaseWriteCallback)

    suspend fun addFavorite(imdbID: String, callback: FirebaseWriteCallback)
    suspend fun removeFavorite(imdbID: String, callback: FirebaseWriteCallback)

    companion object{
        enum class LatestMovieSite{
            PSArips,
            Pahe
        }
    }
}