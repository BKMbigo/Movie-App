package com.bkmbigo.movieapp.domain.model

data class Game(
    val year: Int?=null,

    override var imdbID: String?= null,
    override var title: String,
    override var plot: String?=null,
    override var type: MovieType = MovieType.Game,
    override var genres: List<String> = ArrayList(),
    override var writers: List<String> = ArrayList(),
    override var actors: List<String> = ArrayList(),
    override var posterURL: String,
    override var languages: List<String> = ArrayList(),
    override var country: String?= null,
    override var rated: String?= null,

    override var watched: Boolean = false,
    override var bookmarked: Boolean = false,
    override var favorite: Boolean = false,
    override var downloaded: Boolean = false,
    override var onDownloadList: Boolean = false,
): Movie()
