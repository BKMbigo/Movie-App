package com.bkmbigo.movieapp.data.api

import com.bkmbigo.movieapp.data.dto.PSAripsFeed
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import retrofit2.http.GET

interface PSAripsAPI {

    @GET("/feed")
    fun getPSAripsFeed(): Call<PSAripsFeed>

    companion object{
        @Volatile
        private var INSTANCE: PSAripsAPI? = null

        fun getClient(): PSAripsAPI{
            return INSTANCE?: synchronized(this){
                val instance = Retrofit.Builder()
                    .baseUrl("https://psa.pm/")
                    .addConverterFactory(SimpleXmlConverterFactory.create())
                    .build()
                    .create(PSAripsAPI::class.java)
                INSTANCE = instance
                instance
            }
        }
    }

}