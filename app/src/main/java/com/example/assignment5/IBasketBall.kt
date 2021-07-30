package com.example.assignment5

import com.example.assignment5.models.basketball.BasketBall
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface IBasketBall {

    @GET("games")
    fun getBasketBallGame(@Query("date") date: String
    ) : Call<BasketBall>
}