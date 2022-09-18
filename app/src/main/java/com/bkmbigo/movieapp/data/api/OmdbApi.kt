package com.bkmbigo.movieapp.data.api

import com.bkmbigo.movieapp.data.dto.ParticularMovieDto
import com.bkmbigo.movieapp.data.dto.SearchResultsDto
import retrofit2.http.GET
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Query

interface OmdbApi{

    @GET("/")
    fun searchMovie(
        @Query("apikey") apikey: String,
        @Query("s") query: String,
        @Query("type") type: String,
    ): Call<SearchResultsDto>

    @GET("/")
    fun getParticularMovie(
        @Query("apikey") apikey: String,
        @Query("i") imdbID: String? = null,
        @Query("type") type: String? = null,
        @Query("t") title: String? = null,
        @Query("plot") plot: String = "full"
    ): Call<ParticularMovieDto>

    companion object{
        @Volatile
        private var INSTANCE: OmdbApi? = null

        fun getClient(): OmdbApi{
            return INSTANCE?: synchronized(this){
                val instance = Retrofit.Builder()
                    .baseUrl("https://www.omdbapi.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(OmdbApi::class.java)
                INSTANCE = instance
                instance
            }
        }
    }
}