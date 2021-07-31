package com.example.assignment5

import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//https://7vpw0cirub.execute-api.ap-northeast-2.amazonaws.com/test_stage
//user?email="" GET
//put은 body에 user_email, user_coin, user_jewel 넣어서

object RetrofitClientTtada {
    private const val ttadaUserURL = "https://7vpw0cirub.execute-api.ap-northeast-2.amazonaws.com/test_stage/"
    val tRetrofit = initRetrofit()

    private fun initRetrofit() : Retrofit {
        return Retrofit.Builder()
            .baseUrl(ttadaUserURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}