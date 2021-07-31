package com.example.assignment5

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITtadaUser {
    @GET("user")
    fun getUser(@Query("user_email") email: String
    ) : Call<JsonObject>


}