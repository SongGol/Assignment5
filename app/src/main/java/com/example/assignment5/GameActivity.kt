package com.example.assignment5

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assignment5.databinding.ActivityGameBinding
import com.example.assignment5.models.baseball.BaseBall
import com.example.assignment5.models.basketball.*
import com.example.assignment5.models.soccer.Soccer

import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback

class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding
    private lateinit var customAdapter: CustomAdapter
    private lateinit var email: String
    private var coin = 10000
    private var jewel = 0

    private var prevTime = System.currentTimeMillis()
    private var mHandler = Handler(Looper.getMainLooper())
    private var mThread = Thread {
        try {
            while (!Thread.interrupted()) {
                if (System.currentTimeMillis() > prevTime + 1000) {
                    Log.d("GameActivity", System.currentTimeMillis().toString())
                    prevTime = System.currentTimeMillis()
                    //time++
                    mHandler.post {
                        customAdapter.notifyDataSetChanged()
                    }
                }
                Thread.sleep(200)
            }
        } catch (e: InterruptedException) {
            Log.d("GameActivity", e.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        email = intent.getStringExtra("email")!!
        getTtadaUser(email!!)
        //초기화
        //getBaseBallGame("2019-11-26")
        getBasketBallGame("2019-11-26")
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
                    0 -> {
                        binding.gameNothingIv.visibility = View.GONE
                        binding.gameNothingTv.visibility = View.GONE
                        gameArray.clear()
                        getBaseBallGame("2019-11-26")
                        getBasketBallGame("2019-11-26")
                    }
                    1 -> {
                        binding.gameNothingIv.visibility = View.GONE
                        binding.gameNothingTv.visibility = View.GONE
                        gameArray.clear()
                        getBaseBallGame("2019-11-26")
                    }
                    2 -> {
                        binding.gameNothingIv.visibility = View.VISIBLE
                        binding.gameNothingTv.visibility = View.VISIBLE
                        gameArray.clear()
                    }
                    3 -> {
                        binding.gameNothingIv.visibility = View.GONE
                        binding.gameNothingTv.visibility = View.GONE
                        gameArray.clear()
                        getBasketBallGame("2019-11-26")
                    }
                    4 -> {
                        binding.gameNothingIv.visibility = View.VISIBLE
                        binding.gameNothingTv.visibility = View.VISIBLE
                        gameArray.clear()
                    }
                    5 -> {
                        binding.gameNothingIv.visibility = View.VISIBLE
                        binding.gameNothingTv.visibility = View.VISIBLE
                        gameArray.clear()
                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }
        })

        binding.gameCoinAddIv.setOnClickListener {
            putTtadaUser(TtadaUser(email?:"", coin + 1000, jewel))
        }

        binding.gameJewelAddIv.setOnClickListener {
            putTtadaUser(TtadaUser(email?:"", coin, jewel + 1000))
        }

        mThread.start()
    }

    override fun onDestroy() {
        super.onDestroy()
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
                            MyResource(
                                item.league.name, item.teams.home.name, item.teams.away.name,
                                item.date, item.time, item.scores.home.total, item.scores.away.total,
                                1, getRemaining(item.time)
                            )
                        )
                    }
                    gameArray.sortBy { it.time }
                    customAdapter.notifyDataSetChanged()
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
                    gameArray.sortBy { it.time }
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
/*
    private fun getSoccerGame(date: String) {
        val soccerInterface = RetrofitClient
            .getRetrofit(2, getString(R.string.x_rapidapi_key), getString(R.string.x_rapidapi_soccer_host))
            .create(ISoccer::class.java)
        soccerInterface.getSoccerGame(date).enqueue(object :  Callback<Soccer>{
            override fun onResponse(call: Call<Soccer>, response: retrofit2.Response<Soccer>) {
                if (response.isSuccessful) {
                    val result = response.body() as Soccer
                    for (item in result.response) {
                        if (item.time == "00:00") continue
                        gameArray.add(
                            MyResource(
                                item.league.name, item.teams.home.name, item.teams.away.name,
                                item.date, item.time, item.scores.home.total, item.scores.away.total,
                                2, getRemaining(item.time)
                            )
                        )
                    }

                    customAdapter.notifyDataSetChanged()
                } else {
                    Log.d("GameActivity","getBasketBall data - Error code ${response}")
                }
            }
            override fun onFailure(call: Call<Soccer>, t: Throwable) {
                Log.d("GameActivity",t.message ?: "통신오류")
            }
        })
    }
*/
    private fun getTtadaUser(email: String) {
        val ttadaInterface = RetrofitClientTtada.tRetrofit.create(ITtadaUser::class.java)
        ttadaInterface.getUser(email).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: retrofit2.Response<JsonObject>) {
                if (response.isSuccessful) {
                    Log.d("PlayFragment GET", response.body().toString())
                    val result = Gson().fromJson(response.body(), TtadaUser::class.java)

                    coin = result.userCoin
                    jewel = result.userJewel

                    binding.gameCoinTv.text = makeDecimal(coin)
                    binding.gameJewelTv.text = makeDecimal(jewel)

                    Log.d("PlayFragment", result.toString())
                } else {
                    //초기에 값이 없을 때
                    putTtadaUser(TtadaUser(email))
                    Log.d("PlayFragment","User Data - Error code ${response}")
                }
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d("PlayFragment",t.message ?: "통신오류")
            }
        })
    }

    private fun putTtadaUser(user: TtadaUser){
        val ttadaInterface = RetrofitClientTtada.tRetrofit.create(ITtadaUser::class.java)
        ttadaInterface.putUser(user).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: retrofit2.Response<JsonObject>) {
                if (response.isSuccessful) {
                    Log.d("PlayFragment reponse body", response.body().toString())

                    getTtadaUser(user.userEmail)
                } else {
                    Log.d("PlayFragment","getBasketBall data - Error code ${response}")
                }
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d("PlayFragment",t.message ?: "통신오류")
            }
        })
    }

    private fun deepCopy(item: com.example.assignment5.models.baseball.Response): com.example.assignment5.models.baseball.Response {
        val json = Gson().toJson(item)
        return Gson().fromJson(json, com.example.assignment5.models.baseball.Response::class.java)
    }
}

fun getRemaining(time: String) : Long{
    val timeHM = time.split(":")
    val hour = timeHM[0].toLong()
    val minute = timeHM[1].toLong()

    return hour * 3600 + minute * 60
}