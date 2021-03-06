package com.example.assignment5

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.assignment5.databinding.FragmentPlayBinding
import com.example.assignment5.models.baseball.BaseBall
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import java.text.DecimalFormat

var gameArray = ArrayList<MyResource>()
var time = 0L

class PlayFragment : Fragment() {
    private lateinit var binding: FragmentPlayBinding
    private var name: String? = null
    private var email: String? = null
    private var id: Long = 0
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
                    time++
                    mHandler.post {
                        binding.remainingTimeTv.text = makeRemaining(getRemaining(binding.endTimeTv.text.toString()) - time)
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

        val bundle = arguments
        if (bundle != null) {
            name = bundle.getString("name")
            email = bundle.getString("email")
            id = bundle.getLong("id")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("PlayFragment", "onCreateView")
        binding = FragmentPlayBinding.inflate(inflater, container, false)

        binding.playNameTv.text = if (name == null) "#$id" else name
        binding.playMessageTv.text = if (email == null) "??? ??? ?????? ?????????" else email

        if (email != null) {
            Log.d("PlayFragment", "onCreateView email not null ${email!!}")
            getTtadaUser(email!!)
        }

        getBaseBallGame("2019-11-26")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.playGameCv.setOnClickListener {
            val intent = Intent(activity, GameActivity::class.java)
            intent.putExtra("email", email)
            startActivity(intent)
        }

        binding.gameCoinAddIv.setOnClickListener {
            putTtadaUser(TtadaUser(email?:"", coin + 1000, jewel))
        }

        binding.gameJewelAddIv.setOnClickListener {
            putTtadaUser(TtadaUser(email?:"", coin, jewel + 1000))
        }
    }

    override fun onStop() {
        super.onStop()
        if (email != null) putTtadaUser(TtadaUser(email!!, coin, jewel))
    }

    override fun onDestroy() {
        super.onDestroy()
        mThread.interrupt()
    }

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
                    //????????? ?????? ?????? ???
                    putTtadaUser(TtadaUser(email))
                    Log.d("PlayFragment","User Data - Error code ${response}")
                }
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d("PlayFragment",t.message ?: "????????????")
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
                Log.d("PlayFragment",t.message ?: "????????????")
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

                    mHandler.post {
                        //?????????, ??????
                        binding.athleticsNameTv.text = gameArray[0].leagueName
                        binding.homeTeamTv.text = gameArray[0].homeName
                        binding.awayTeamTv.text = gameArray[0].awayName
                        //??????, ??????
                        val days = gameArray[0].date.split("T")[0].split("-")
                        binding.endDateTv.text = "${days[1]}/${days[2]}"
                        binding.endTimeTv.text = gameArray[0].time

                        mThread.start()
                    }
                } else {
                    Log.d("GameActivity","getBasketBall data - Error code ${response}")
                }
            }
            override fun onFailure(call: Call<BaseBall>, t: Throwable) {
                Log.d("GameActivity",t.message ?: "????????????")
            }
        })
    }
}

fun makeDecimal(amount: Int) : String {
    val value = if (amount < 0) -amount else amount
    val formatter = DecimalFormat("###,###,###")
    return formatter.format(value)
}