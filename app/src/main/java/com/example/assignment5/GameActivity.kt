package com.example.assignment5

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assignment5.databinding.ActivityGameBinding
import com.example.assignment5.models.basketball.*
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback

class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding
    private lateinit var customAdapter: CustomAdapter
    private var gameArray = ArrayList<Response>()

    private var prevTime = System.currentTimeMillis()
    private var mHandler = Handler(Looper.getMainLooper())
    private var mThread = Thread {
        try {
            while (true) {
                if (System.currentTimeMillis() > prevTime + 1000) {
                    Log.d("GameActivity", System.currentTimeMillis().toString())
                    prevTime = System.currentTimeMillis()
                    for (item in gameArray) {
                        item.remaining -= 1
                    }
                    mHandler.post {
                        customAdapter.notifyDataSetChanged()
                    }
                }
                Thread.sleep(200)
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        getBasketBallGame("2019-11-26")
        Log.d("GameActivity", gameArray.size.toString())

        customAdapter = CustomAdapter(gameArray, this)
        binding.gameRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.gameRecyclerView.adapter = customAdapter
        binding.gameRecyclerView.addItemDecoration(RecyclerViewDecoration(60))

        //레트로핏 테스트용 버튼
        binding.gameCartTv.setOnClickListener {
            Log.d("GameActivity", "test clicked")
        }
    }

    override fun onResume() {
        super.onResume()
        mThread.start()
    }

    override fun onStop() {
        super.onStop()
        mThread.interrupt()
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
                        if (item.time == "00:00") continue
                        gameArray.add(
                            Response(
                            item.country, item.date, item.id,
                            item.league, item.scores, item.stage,
                            item.status, item.teams, item.time,
                            item.timestamp, item.timezone, item.week,
                                getRemaining(item.time)
                        ))
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

    private fun getRemaining(time: String) : Long{
        var res: Long = 0L
        //res += time[0].code.toLong()
        val timeHM = time.split(":")
        val hour = timeHM[0].toLong()
        val minute = timeHM[1].toLong()

        return hour * 3600 + minute * 60
    }

    private fun deepCopy(item: Response): Response {
        val json = Gson().toJson(item)
        return Gson().fromJson(json, Response::class.java)
    }
}