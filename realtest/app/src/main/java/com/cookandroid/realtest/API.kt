package com.cookandroid.realtest

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface API {

    @GET("/news")
    fun getSearchNews(
    ): Call<List<MainActivity.ResultGetSearchNews>>

}

