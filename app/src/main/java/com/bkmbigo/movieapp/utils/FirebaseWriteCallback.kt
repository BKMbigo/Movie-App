package com.bkmbigo.movieapp.utils

interface FirebaseWriteCallback {
    fun onSuccess()
    fun onFailure(e:Exception)
}