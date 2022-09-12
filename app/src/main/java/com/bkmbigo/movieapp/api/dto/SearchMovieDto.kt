package com.bkmbigo.movieapp.api.dto

import com.google.gson.annotations.SerializedName
import java.net.URL

data class SearchMovieDto(
    @SerializedName("Title")
    val title: String?,

    @SerializedName("Year")
    val year: String?,

    @SerializedName("imdbID")
    val imdbID: String?,

    @SerializedName("Type")
    val type: String?,

    @SerializedName("Poster")
    val posterURL: String?
)
