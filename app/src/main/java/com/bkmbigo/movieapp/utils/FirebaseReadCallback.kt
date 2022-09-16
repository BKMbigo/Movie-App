package com.bkmbigo.movieapp.utils

interface FirebaseReadCallback<A> {
    fun onSuccess(data:A)
    fun onError(e: Exception)
}

//Write Callback for write