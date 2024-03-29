package com.bkmbigo.movieapp.data.dto

import com.google.gson.annotations.SerializedName

data class SearchResultsDto(
    @SerializedName("Search")
    val search: List<SearchMovieDto>,
    @SerializedName("totalResults")
    val totalResults: String,
    @SerializedName("Response")
    val response: String
)

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
