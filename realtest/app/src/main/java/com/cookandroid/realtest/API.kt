package com.cookandroid.realtest

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface API {
    @GET("/고기")
    fun getSearch(
    ): Call<List<ResultActivity.ResultGetSearch>>

    @GET("/news")
    fun getSearchNews(
    ): Call<List<MainActivity.ResultGetSearchNews>>

}
