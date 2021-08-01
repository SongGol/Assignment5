package com.example.assignment5

import android.content.Intent
import android.os.Bundle
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

class PlayFragment : Fragment() {
    private lateinit var binding: FragmentPlayBinding
    private var name: String? = null
    private var email: String? = null
    private var id: Long = 0
    private var coin = 10000
    private var jewel = 0

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
        binding.playMessageTv.text = if (email == null) "큰 거 한방 터져라" else email

        if (email != null) {
            Log.d("PlayFragment", "onCreateView email not null ${email!!}")
            getTtadaUser(email!!)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.button.setOnClickListener {
            val intent = Intent(activity, GameActivity::class.java)
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

    fun makeDecimal(amount: Int) : String {
        val value = if (amount < 0) -amount else amount
        val formatter = DecimalFormat("###,###,###")
        return formatter.format(value)
    }
}