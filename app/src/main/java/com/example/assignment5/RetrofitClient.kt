package com.example.assignment5

import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASEBALL_URL = "https://api-baseball.p.rapidapi.com/"
    private const val BASKETBALL_URL = "https://api-basketball.p.rapidapi.com/"
    private const val SOCCER_URL = "https://api-football-v1.p.rapidapi.com/v3/odds/"

    fun getRetrofit(type: Int, apiKey: String, apiHost: String) : Retrofit {
        return initRetrofit(type, apiKey, apiHost)
    }

    private fun initRetrofit(type: Int = 1, apiKey: String, apiHost: String) : Retrofit {
        val url = when(type) {
            0 -> BASEBALL_URL
            1 -> BASKETBALL_URL
            2 -> SOCCER_URL
            else -> BASEBALL_URL
        }

        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor { chain ->
            val request: Request =
                chain
                    .request()
                    .newBuilder()
                    .addHeader("x-rapidapi-key", apiKey)
                    .addHeader("x-rapidapi-host", apiHost)
                    .build()
            chain.proceed(request)
        }

        return Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()
    }
}