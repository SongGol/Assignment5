package com.example.assignment5

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ISoccer {

    @GET("games")
    fun getSoccerGame(@Query("date") date: String
    ) : Call<MyResource>
}