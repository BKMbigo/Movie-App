package com.bkmbigo.movieapp.data.mapper

import com.bkmbigo.movieapp.api.dto.PSAItem
import com.bkmbigo.movieapp.api.dto.ParticularMovieDto
import com.bkmbigo.movieapp.api.dto.SearchMovieDto
import com.bkmbigo.movieapp.domain.model.Film
import com.bkmbigo.movieapp.domain.model.Game
import com.bkmbigo.movieapp.domain.model.Movie
import com.bkmbigo.movieapp.domain.model.Series

fun PSAItem.toMovie() : Movie{
    var posterURL = description
    posterURL = posterURL.substringAfter("src=").substringAfter('"').substringBefore('"')


    if (category.contains("Movie")){
        return Film(
            title = title,
            year = null,
            posterURL = posterURL,
            genres = category,
            type = Movie.MovieType.Movie,
        )
    }else{
        return Series(
            title = title,
            type = Movie.MovieType.Series,
            posterURL = posterURL,
            genres = category,

        )
    }

}

fun ParticularMovieDto.toMovie(): Movie{
    return when (type?.lowercase()?.trim()){
        "movie" -> toFilm()
        "series" -> toSeries()
        "episode" -> toSeries()
        "game" -> toGame()
        else -> throw IllegalStateException()
    }
}

fun ParticularMovieDto.toFilm() : Film{
    if(type?.lowercase() != "movie"){
        throw IllegalStateException("Cannot cast $type to Movie")
    }

    return Film(
        title = title?:"",
        posterURL = poster?: "",
        plot = plot,
        year = year?.toIntOrNull(),
        writers = writer?.split(",")?.map{ it.trim() }?: emptyList(),
        actors = actors?.split(",")?.map{ it.trim() }?: emptyList(),
        director = director?.split(",")?.map{ it.trim() }?: emptyList(),
        genres = genre?.split(",")?.map { it.trim() }?: emptyList(),
        country = country,
        type = Movie.MovieType.Movie,
        imdbID = imdbID,
        languages = language?.split(",")?.map { it.trim() }?: emptyList(),
        rated = rated,
    )
}
fun ParticularMovieDto.toSeries() : Series{
    if(type?.lowercase() != "series"){
        throw IllegalStateException("Cannot cast $type to Series")
    }

    val years = year?.split("-")?.map { it }
    val startYear = years?.get(0)?.toIntOrNull()
    var endYear: Int? = null
    if(years != null){
         endYear = if(years.size > 1) {
             years[1].toIntOrNull()
         }else{ null }
    }


    return Series(
        title = title?:"",
        posterURL = poster?:"",
        plot = plot,
        startYear = startYear,
        endYear = endYear,
        writers = writer?.split(",")?.map{ it.trim() }?: emptyList(),
        actors = actors?.split(",")?.map{ it.trim() }?: emptyList(),
        genres = genre?.split(",")?.map { it.trim() }?: emptyList(),
        country = country,
        type = Movie.MovieType.Movie,
        imdbID = imdbID,
        languages = language?.split(",")?.map { it.trim() }?: emptyList(),
        rated = rated,
    )
}
fun ParticularMovieDto.toGame(): Game{
    if(type?.lowercase() != "game"){
        throw IllegalStateException("Cannot cast $type to Game")
    }

    val genres = genre?.split(",")?.map{ it.trim() }?: emptyList()
    val writers = writer?.split(",")?.map{ it.trim() }?: emptyList()
    val languages = language?.split(",")?.map{ it.trim() }?: emptyList()
    val actor = actors?.split(",")?.map { it.trim() }?: emptyList()


    return Game(
        title = title?:"",
        posterURL = poster?:"",
        imdbID = imdbID,
        plot = plot,
        genres = genres,
        writers = writers,
        languages = languages,
        actors = actor,
        year = year?.toIntOrNull(),
        country = country,
        rated = rated
    )
}

fun SearchMovieDto.toMovie(): Movie{
     when (type?.lowercase()){
        "movie" -> {
            return Film(
                title = title?:"",
                posterURL = posterURL?:"",
                type = Movie.MovieType.Movie,
                imdbID = imdbID,
                year = year?.toIntOrNull()
            )
        }
        "series" -> {
            return Series(
                title = title?:"",
                posterURL = posterURL?:"",
                imdbID = imdbID
            )
        }
        "episode" -> {
            return Series(
                title = title?:"",
                posterURL = posterURL?:"",
                imdbID = imdbID
            )
        }
        "game" -> {
            return Game(
                title = title?:"",
                posterURL = posterURL?:"",
                imdbID = imdbID
            )
        }
         else -> {
              throw IllegalStateException("Movie cannot be cast to options available")
         }
    }
}