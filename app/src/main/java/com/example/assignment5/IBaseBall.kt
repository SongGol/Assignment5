package com.example.assignment5

import com.example.assignment5.models.baseball.BaseBall
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface IBaseBall {

    @GET("games")
    fun getBaseBallGame(@Query("date") date: String
    ) : Call<BaseBall>
}