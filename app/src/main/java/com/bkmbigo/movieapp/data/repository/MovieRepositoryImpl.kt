package com.bkmbigo.movieapp.data.repository

import com.bkmbigo.movieapp.api.OmdbApi
import com.bkmbigo.movieapp.api.PSAripsAPI
import com.bkmbigo.movieapp.api.dto.PSAripsFeed
import com.bkmbigo.movieapp.api.dto.ParticularMovieDto
import com.bkmbigo.movieapp.api.dto.SearchResultsDto
import com.bkmbigo.movieapp.data.mapper.toMovie
import com.bkmbigo.movieapp.domain.model.Movie
import com.bkmbigo.movieapp.domain.repository.MovieRepository
import com.bkmbigo.movieapp.utils.FirebaseReadCallback
import com.bkmbigo.movieapp.utils.FirebaseWriteCallback
import com.bkmbigo.movieapp.utils.WebApiCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MovieRepositoryImpl(
    private val omdbApiKey: String? = null,
): MovieRepository {
    override suspend fun getLatest(
        site: MovieRepository.Companion.LatestMovieSite,
        callback: WebApiCallback<List<Movie>>
    ) {
        if(site == MovieRepository.Companion.LatestMovieSite.PSArips) {

            val call = PSAripsAPI.getClient().getPSAripsFeed()
            call.enqueue(object : Callback<PSAripsFeed> {
                override fun onResponse(call: Call<PSAripsFeed>, response: Response<PSAripsFeed>) {
                    val feed = response.body()
                    val itemList = feed?.channel?.items?.map { it.toMovie() }
                    if (itemList != null) {
                        callback.onResponse(itemList)
                    } else {
                        callback.onFailure(IllegalStateException("Feed is null"))
                    }

                }

                override fun onFailure(call: Call<PSAripsFeed>, t: Throwable) {
                    callback.onFailure(Exception(t.cause))
                }

            })
        }else if(site == MovieRepository.Companion.LatestMovieSite.Pahe){
            callback.onFailure(IllegalStateException("Not yet implemented"))
            //TODO Implement method for scraping from Pahe.li
        }
    }

    override suspend fun searchMovie(
        query: String,
        type: String?,
        callback: WebApiCallback<List<Movie>>
    ) {
        if(omdbApiKey != null){
            val call = OmdbApi.getClient().searchMovie(omdbApiKey, query, type?: "null")
            call.enqueue(object: Callback<SearchResultsDto>{
                override fun onResponse(
                    call: Call<SearchResultsDto>,
                    response: Response<SearchResultsDto>
                ) {
                    val searchResponse = response.body()
                    if(searchResponse != null){
                        if(searchResponse.response.toBoolean()){
                            if(searchResponse.search.isEmpty() || searchResponse.totalResults.toInt() == 0){
                                callback.onResponse(emptyList())
                            }else{
                                val movieList = searchResponse.search.map{ it.toMovie() }
                                callback.onResponse(movieList)
                            }
                        }else{
                            callback.onFailure(IllegalStateException("Invalid response from the API"))
                        }
                    }else{
                        callback.onFailure(IllegalStateException("Error getting a response from the API"))
                    }
                }

                override fun onFailure(call: Call<SearchResultsDto>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
        }else{
            callback.onFailure(IllegalStateException("OMDB API Key not defined"))
        }

    }

    override suspend fun getMovieParticulars(movie: Movie, callback: WebApiCallback<Movie>) {
        if(movie.imdbID != null){
            if(omdbApiKey != null){
                val call = OmdbApi.getClient().getParticularMovie(apikey = omdbApiKey, imdbID = movie.imdbID, type = movie.type.name.lowercase())
                call.enqueue(object: Callback<ParticularMovieDto>{
                    override fun onResponse(
                        call: Call<ParticularMovieDto>,
                        response: Response<ParticularMovieDto>
                    ) {
                        val body = response.body()
                        if(body != null){
                            val item = body.toMovie()
                            callback.onResponse(item)
                        }else{
                            callback.onFailure(IllegalStateException())
                        }
                    }

                    override fun onFailure(call: Call<ParticularMovieDto>, t: Throwable) {
                        callback.onFailure(Exception(t))
                    }

                })
            }else{
                return callback.onFailure(IllegalStateException("OMDB Api Key not defined"))
            }
        }else{
            return callback.onFailure(IllegalStateException("ImdbID not found"))
        }
    }

    override suspend fun getWatchList(callback: FirebaseReadCallback<List<Movie>>) {
        TODO("Not yet implemented")
    }

    override suspend fun getDownloadList(callback: FirebaseReadCallback<List<Movie>>) {
        TODO("Not yet implemented")
    }

    override suspend fun addToDownloadList(imdbID: String, callback: FirebaseWriteCallback) {
        TODO("Not yet implemented")
    }

    override suspend fun removeFromDownloadList(imdbID: String, callback: FirebaseWriteCallback) {
        TODO("Not yet implemented")
    }

    override suspend fun addToWatchList(imdbID: String, callback: FirebaseWriteCallback) {
        TODO("Not yet implemented")
    }

    override suspend fun removeFromWatchList(imdbID: String, callback: FirebaseWriteCallback) {
        TODO("Not yet implemented")
    }

    override suspend fun addBookmarked(imdbID: String, callback: FirebaseWriteCallback) {
        TODO("Not yet implemented")
    }

    override suspend fun removeBookmark(imdbID: String, callback: FirebaseWriteCallback) {
        TODO("Not yet implemented")
    }

    override suspend fun addFavorite(imdbID: String, callback: FirebaseWriteCallback) {
        TODO("Not yet implemented")
    }

    override suspend fun removeFavorite(imdbID: String, callback: FirebaseWriteCallback) {
        TODO("Not yet implemented")
    }


}