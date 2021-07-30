package com.example.assignment5

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assignment5.databinding.ActivityGameBinding
import com.example.assignment5.models.basketball.BasketBall
import com.example.assignment5.models.basketball.Response
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback

class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding
    private lateinit var customAdapter: CustomAdapter
    private var gameArray = ArrayList<Response>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        getBasketBallGame("2019-11-26")
        Log.d("GameActivity", gameArray.size.toString())

        customAdapter = CustomAdapter(gameArray)
        binding.gameRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.gameRecyclerView.adapter = customAdapter
        binding.gameRecyclerView.addItemDecoration(RecyclerViewDecoration(60))

        //레트로핏 테스트용 버튼
        binding.gameCartTv.setOnClickListener {
            Log.d("GameActivity", "test clicked")
        }
    }

    private fun getBasketBallGame(date: String) {
        val basketballInterface = RetrofitClient
                            .getRetrofit(1, getString(R.string.x_rapidapi_key), getString(R.string.x_rapidapi_basket_host))
                            .create(IBasketBall::class.java)
        basketballInterface.getBasketBallGame(date).enqueue(object :  Callback<BasketBall>{
            override fun onResponse(call: Call<BasketBall>, response: retrofit2.Response<BasketBall>) {
                if (response.isSuccessful) {
                    val result = response.body() as BasketBall
                    for (item in result.response) {
                        gameArray.add(deepCopy(item))
                    }

                    customAdapter.notifyDataSetChanged()
                    Log.d("GameActivity", gameArray.size.toString())
                    Log.d("GameActivity","getBasketBall data - ${gameArray[0].teams.home.name}")
                } else {
                    Log.d("GameActivity","getBasketBall data - Error code ${response}")
                }
            }
            override fun onFailure(call: Call<BasketBall>, t: Throwable) {
                Log.d("GameActivity",t.message ?: "통신오류")
            }
        })
    }

    private fun deepCopy(item: Response): Response {
        val json = Gson().toJson(item)
        return Gson().fromJson(json, Response::class.java)
    }
}