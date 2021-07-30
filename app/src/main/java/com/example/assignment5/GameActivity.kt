package com.example.assignment5

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assignment5.databinding.ActivityGameBinding
import com.example.assignment5.models.basketball.BasketBall
import retrofit2.Call
import retrofit2.Callback

class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding
    private lateinit var customAdapter: CustomAdapter
    private var gameArray = ArrayList<Game>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        for (i in 0..4) {
            gameArray.add(Game(fullName = "", nickName = "", shortName = ""))
        }

        customAdapter = CustomAdapter(gameArray)
        binding.gameRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.gameRecyclerView.adapter = customAdapter
        binding.gameRecyclerView.addItemDecoration(RecyclerViewDecoration(10))



        //레트로핏 테스트용 버튼
        binding.gameCartTv.setOnClickListener {
            Log.d("GameActivity", "test clicked")
            getBasketBallGame("2019-11-26")
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

                    Log.d("GameActivity","getBasketBall data - ${result.response[0].teams.home.name}")
                } else {
                    Log.d("GameActivity","getBasketBall data - Error code ${response}")
                }
            }
            override fun onFailure(call: Call<BasketBall>, t: Throwable) {
                Log.d("GameActivity",t.message ?: "통신오류")
            }
        })
    }
}