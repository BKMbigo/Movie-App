package com.bkmbigo.movieapp.data.repository

import com.bkmbigo.movieapp.data.api.OmdbApi
import com.bkmbigo.movieapp.data.api.PSAripsAPI
import com.bkmbigo.movieapp.data.dto.PSAripsFeed
import com.bkmbigo.movieapp.data.dto.ParticularMovieDto
import com.bkmbigo.movieapp.data.dto.SearchResultsDto
import com.bkmbigo.movieapp.data.mapper.toMovie
import com.bkmbigo.movieapp.domain.model.Movie
import com.bkmbigo.movieapp.domain.repository.MovieRepository
import com.bkmbigo.movieapp.utils.FirebaseCallback
import com.bkmbigo.movieapp.utils.WebApiCallback
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MovieRepositoryImpl(
    private val omdbApiKey: String? = null,
    private val databaseReference: DatabaseReference? = null,
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

    //Firebase
    override suspend fun getWatchList(callback: FirebaseCallback<List<Movie>>) {
        if(databaseReference == null){
            callback.onError(IllegalStateException("Database Reference is null"))
            return
        }
        val watchReference: DatabaseReference =
            databaseReference.child("movie").child("watch")

        val valueEventListener= object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val resultList = ArrayList<Movie>()
                val list = snapshot.children.map{
                    it.getValue(Movie::class.java)
                }
                list.forEach { movie -> if(movie != null) { resultList.add(movie) } }

                callback.onSuccess(resultList)
            }

            override fun onCancelled(error: DatabaseError) { callback.onError(error.toException()) }
        }
        watchReference.addValueEventListener(valueEventListener)
    }
    override suspend fun getDownloadList(callback: FirebaseCallback<List<Movie>>) {
        if(databaseReference == null){
            callback.onError(IllegalStateException("Database Reference is null"))
            return
        }

        val downloadReference: DatabaseReference =
            databaseReference.child("movie").child("download")

        val valueEventListener= object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val resultList = ArrayList<Movie>()
                val list = snapshot.children.map{
                    it.getValue(Movie::class.java)
                }
                list.forEach { movie -> if(movie != null) { resultList.add(movie) } }

                callback.onSuccess(resultList)
            }

            override fun onCancelled(error: DatabaseError) { callback.onError(error.toException()) }
        }
        downloadReference.addValueEventListener(valueEventListener)
    }
    override suspend fun getBookmarked(callback: FirebaseCallback<List<Movie>>) {
        if(databaseReference == null){
            callback.onError(IllegalStateException("Database Reference is null"))
            return
        }

        val bookmarkedReference: DatabaseReference =
            databaseReference.child("movie").child("bookmark")

        val valueEventListener= object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val resultList = ArrayList<Movie>()
                val list = snapshot.children.map{
                    it.getValue(Movie::class.java)
                }
                list.forEach { movie -> if(movie != null) { resultList.add(movie) } }

                callback.onSuccess(resultList)
            }

            override fun onCancelled(error: DatabaseError) { callback.onError(error.toException()) }
        }
        bookmarkedReference.addValueEventListener(valueEventListener)
    }
    override suspend fun getFavorites(callback: FirebaseCallback<List<Movie>>) {
        if(databaseReference == null){
            callback.onError(IllegalStateException("Database Reference is null"))
            return
        }

        val favoriteReference: DatabaseReference =
            databaseReference.child("movie").child("favorite")

        val valueEventListener= object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val resultList = ArrayList<Movie>()
                val list = snapshot.children.map{
                    it.getValue(Movie::class.java)
                }
                list.forEach { movie -> if(movie != null) { resultList.add(movie) } }

                callback.onSuccess(resultList)
            }

            override fun onCancelled(error: DatabaseError) { callback.onError(error.toException()) }
        }
        favoriteReference.addValueEventListener(valueEventListener)
    }


    override suspend fun toggleDownloadList(movie: Movie, callback: FirebaseCallback<Movie>) {
        if(databaseReference == null){
            callback.onError(IllegalStateException("Database Reference is null"))
            return
        }

        if(movie.imdbID == null){
            callback.onError(IllegalStateException("Cannot set movie without IMDB ID"))
            return
        }

        val downloadReference: DatabaseReference =
            databaseReference.child("movie").child("download")

        if(movie.onDownloadList){
            downloadReference.child(movie.imdbID!!).removeValue().addOnCompleteListener { task->
                if(task.isSuccessful){
                    movie.onDownloadList = false
                    callback.onSuccess(movie)
                }else{
                    task.exception?.let{ callback.onError(it) }
                }
            }
        }else {
            downloadReference.child(movie.imdbID!!).let {
                it.child("imdbID").setValue(movie.imdbID!!)
                it.child("title").setValue(movie.title)
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    movie.onDownloadList = true
                    callback.onSuccess(movie)
                } else {
                    task.exception?.let { callback.onError(it) }
                }
            }
        }
    }
    override suspend fun toggleWatchList(movie: Movie, callback: FirebaseCallback<Movie>) {
        if(databaseReference == null){
            callback.onError(IllegalStateException("Database Reference is null"))
            return
        }

        if(movie.imdbID == null){
            callback.onError(IllegalStateException("Cannot set movie without IMDB ID"))
            return
        }

        val downloadReference: DatabaseReference =
            databaseReference.child("movie").child("watch")


        if(movie.watched){
            downloadReference.child(movie.imdbID!!).removeValue().addOnCompleteListener { task->
                if(task.isSuccessful){
                    movie.watched = false
                    callback.onSuccess(movie)
                }else{
                    task.exception?.let{ callback.onError(it) }
                }
            }
        }else {
            downloadReference.child(movie.imdbID!!).let {
                it.child("imdbID").setValue(movie.imdbID!!)
                it.child("title").setValue(movie.title)
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    movie.watched = true
                    callback.onSuccess(movie)
                } else {
                    task.exception?.let { callback.onError(it) }
                }
            }
        }
    }
    override suspend fun toggleBookmarked(movie: Movie, callback: FirebaseCallback<Movie>) {
        if(databaseReference == null){
            callback.onError(IllegalStateException("Database Reference is null"))
            return
        }

        if(movie.imdbID == null){
            callback.onError(IllegalStateException("Cannot set movie without IMDB ID"))
            return
        }

        val downloadReference: DatabaseReference =
            databaseReference.child("movie").child("bookmark")

        if(movie.bookmarked){
            downloadReference.child(movie.imdbID!!).removeValue().addOnCompleteListener { task->
                if(task.isSuccessful){
                    movie.bookmarked = false
                    callback.onSuccess(movie)
                }else{
                    task.exception?.let{ callback.onError(it) }
                }
            }
        }else {
            downloadReference.child(movie.imdbID!!).let {
                it.child("imdbID").setValue(movie.imdbID!!)
                it.child("title").setValue(movie.title)
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    movie.bookmarked = true
                    callback.onSuccess(movie)
                } else {
                    task.exception?.let { callback.onError(it) }
                }
            }
        }
    }
    override suspend fun toggleFavorite(movie: Movie, callback: FirebaseCallback<Movie>) {
        if(databaseReference == null){
            callback.onError(IllegalStateException("Database Reference is null"))
            return
        }

        if(movie.imdbID == null){
            callback.onError(IllegalStateException("Cannot set movie without IMDB ID"))
            return
        }

        val downloadReference: DatabaseReference =
            databaseReference.child("movie").child("favorite")

        if(movie.favorite){
            downloadReference.child(movie.imdbID!!).removeValue().addOnCompleteListener { task->
                if(task.isSuccessful){
                    movie.favorite = false
                    callback.onSuccess(movie)
                }else{
                    task.exception?.let{ callback.onError(it) }
                }
            }
        }else {
            downloadReference.child(movie.imdbID!!).let {
                it.child("imdbID").setValue(movie.imdbID!!)
                it.child("title").setValue(movie.title)
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    movie.favorite = true
                    callback.onSuccess(movie)
                } else {
                    task.exception?.let { callback.onError(it) }
                }
            }
        }
    }


}