package com.bkmbigo.movieapp.api.dto

import com.google.gson.annotations.SerializedName

data class SearchResultsDto(
    @SerializedName("Search")
    val search: List<SearchMovieDto>,
    @SerializedName("totalResults")
    val totalResults: String,
    @SerializedName("Response")
    val response: String
)
