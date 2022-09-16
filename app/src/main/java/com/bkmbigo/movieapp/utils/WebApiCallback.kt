package com.bkmbigo.movieapp.utils

interface WebApiCallback<A> {
    fun onResponse(data: A)
    fun onFailure(e:Exception?)
}