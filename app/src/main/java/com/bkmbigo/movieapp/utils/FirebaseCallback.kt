package com.bkmbigo.movieapp.utils

interface FirebaseCallback<A> {
    fun onSuccess(data:A)
    fun onError(e: Exception)
}

//Write Callback for write