package com.skku_summer.food_info

import retrofit2.Call
import retrofit2.http.GET

interface API {

    @GET("/news")
    fun getSearchNews(
    ): Call<List<MainActivity.ResultGetSearchNews>>

}

