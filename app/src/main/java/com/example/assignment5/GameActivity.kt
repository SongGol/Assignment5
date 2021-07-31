package com.example.assignment5

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assignment5.databinding.ActivityGameBinding
import com.example.assignment5.models.baseball.BaseBall
import com.example.assignment5.models.baseball.Response
import com.example.assignment5.models.basketball.*

import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback

class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding
    private lateinit var customAdapter: CustomAdapter

    companion object {
        var time = 0L
    }

    private var basketBallArray = ArrayList<com.example.assignment5.models.basketball.Response>()
    private var baseBallArray = ArrayList<com.example.assignment5.models.baseball.Response>()
    private var gameArray = ArrayList<MyResource>()

    private var prevTime = System.currentTimeMillis()
    private var mHandler = Handler(Looper.getMainLooper())
    private var mThread = Thread {
        try {
            while (true) {
                if (System.currentTimeMillis() > prevTime + 1000) {
                    Log.d("GameActivity", System.currentTimeMillis().toString())
                    prevTime = System.currentTimeMillis()
                    time++
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

        //
        Log.d("GameActivity", gameArray.size.toString())

        customAdapter = CustomAdapter(gameArray, this)
        binding.gameRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.gameRecyclerView.adapter = customAdapter
        binding.gameRecyclerView.addItemDecoration(RecyclerViewDecoration(60))

        //tablayout에 리스너
        binding.gameTabLo.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val pos = tab?.position
                Log.d("GameActivity", if(pos == null) "null" else pos.toString())

                when (pos) {
                    0 -> 1
                    1 -> {
                        gameArray.clear()
                        getBaseBallGame("2019-11-26")
                    }
                    2 -> 2
                    3 -> {
                        gameArray.clear()
                        getBasketBallGame("2019-11-26")
                    }
                    4 -> 4
                    5 -> 5
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }
        })
    }

    override fun onResume() {
        super.onResume()
        mThread.start()
    }

    override fun onPause() {
        super.onPause()
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
                        basketBallArray.add(
                            Response(
                            item.country, item.date, item.id,
                            item.league, item.scores, item.stage,
                            item.status, item.teams, item.time,
                            item.timestamp, item.timezone, item.week,
                                getRemaining(item.time)
                        ))
                        gameArray.add(
                            MyResource(
                                item.league.name, item.teams.home.name, item.teams.away.name,
                                item.date, item.time, item.scores.home.total, item.scores.away.total,
                                1, getRemaining(item.time)
                            )
                        )
                    }

                    customAdapter.notifyDataSetChanged()
                    Log.d("GameActivity", basketBallArray.size.toString())
                    Log.d("GameActivity","getBasketBall data - ${basketBallArray[0].teams.home.name}")
                } else {
                    Log.d("GameActivity","getBasketBall data - Error code ${response}")
                }
            }
            override fun onFailure(call: Call<BasketBall>, t: Throwable) {
                Log.d("GameActivity",t.message ?: "통신오류")
            }
        })
    }

    private fun getBaseBallGame(date: String) {
        val baseballInterface = RetrofitClient
            .getRetrofit(0, getString(R.string.x_rapidapi_key), getString(R.string.x_rapidapi_baseball_host))
            .create(IBaseBall::class.java)
        baseballInterface.getBaseBallGame(date).enqueue(object :  Callback<BaseBall>{
            override fun onResponse(call: Call<BaseBall>, response: retrofit2.Response<BaseBall>) {
                if (response.isSuccessful) {
                    val result = response.body() as BaseBall
                    for (item in result.response) {
                        if (item.time == "00:00") continue
                        gameArray.add(
                            MyResource(
                                item.league.name, item.teams.home.name, item.teams.away.name,
                                item.date, item.time, item.scores.home.total, item.scores.away.total,
                                0, getRemaining(item.time)
                            )
                        )
                    }

                    customAdapter.notifyDataSetChanged()
                } else {
                    Log.d("GameActivity","getBasketBall data - Error code ${response}")
                }
            }
            override fun onFailure(call: Call<BaseBall>, t: Throwable) {
                Log.d("GameActivity",t.message ?: "통신오류")
            }
        })
    }

    private fun getRemaining(time: String) : Long{
        val timeHM = time.split(":")
        val hour = timeHM[0].toLong()
        val minute = timeHM[1].toLong()

        return hour * 3600 + minute * 60
    }

    private fun deepCopy(item: com.example.assignment5.models.baseball.Response): com.example.assignment5.models.baseball.Response {
        val json = Gson().toJson(item)
        return Gson().fromJson(json, com.example.assignment5.models.baseball.Response::class.java)
    }
}