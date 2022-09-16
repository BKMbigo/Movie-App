package com.bkmbigo.movieapp.domain.model

import java.net.URL

abstract class Movie{
    abstract var imdbID: String?
    abstract var title: String
    abstract var plot: String?

    abstract var type: MovieType
    abstract var genres: List<String>

    //abstract var producer: List<String>
    abstract var writers: List<String>
    abstract var actors: List<String>

    abstract var posterURL: String
    abstract var languages: List<String>
    abstract var country: String?
    abstract var rated: String?

    //Saved on Database
    abstract var watched: Boolean
    abstract var bookmarked: Boolean
    abstract var favorite: Boolean
    abstract var downloaded: Boolean
    abstract var onDownloadList: Boolean

    enum class MovieType{
        Movie,
        Series,
        Episode,
        Game
    }
}

data class Film(

    val year: Int?, //Movies have a single
    val director: List<String> = ArrayList(),

    override var imdbID: String? = null,
    override var title: String,
    override var plot: String? = null,
    override var type: MovieType,
    override var genres: List<String> = ArrayList(),
    //override var producer: List<String>,
    override var writers: List<String> = ArrayList(),
    override var actors: List<String> = ArrayList(),
    override var posterURL: String,
    override var languages: List<String>  = ArrayList(),
    override var country: String? = null,
    override var rated: String? = null,

    override var watched: Boolean = false,
    override var bookmarked: Boolean = false,
    override var favorite: Boolean = false,
    override var downloaded: Boolean = false,
    override var onDownloadList: Boolean = false
): Movie()
