package com.bkmbigo.movieapp.domain.model

data class Series (
    var startYear: Int? = null,
    var endYear: Int? = null,
    var totalSeasons: Int = 1,

    override var imdbID: String? = null,
    override var title: String,
    override var plot: String? = null,
    override var type: MovieType = MovieType.Series,
    override var genres: List<String> = ArrayList(),
    //override var producer: List<String> = ArrayList(),
    override var writers: List<String> = ArrayList(),
    override var actors: List<String>  = ArrayList(),
    override var posterURL: String,
    override var languages: List<String>  = ArrayList(),
    override var country: String? = null,
    override var rated: String? = null,
    override var watched: Boolean = false,
    override var bookmarked: Boolean = false,
    override var favorite: Boolean = false,
    override var downloaded: Boolean = false,
    override var onDownloadList: Boolean = false,

    ) : Movie()