package com.example.assignment5

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.assignment5.databinding.FragmentSettingBinding
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.kakao.sdk.user.UserApiClient
import retrofit2.Call
import retrofit2.Callback


class SettingFragment : Fragment() {
    private lateinit var binding: FragmentSettingBinding
    private var name: String? = null
    private var email: String? = null
    private var login: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater, container, false)

        val bundle = arguments
        if (bundle != null) {
            name = bundle.getString("name")
            email = bundle.getString("email")
            login = SharedPreferencesManager.getStrValue(activity, LOGIN, "none")
            //coin, 보석 설정
            getTtadaUser(email!!)
        }

        if (login == "kakao") {
            binding.settingLogoutTv.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.kakao_yellow))
        } else if (login == "naver") {
            binding.settingLogoutTv.text = "네이버 로그아웃"
            binding.settingLogoutTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.settingLogoutTv.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.naver_green))
            binding.logoutIv.setImageResource(R.drawable.ic_start_naver)
        }
        //이름, email 설정
        binding.settingNameTv.text = name
        binding.settingMessageTv.text = email

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.settingLogoutTv.setOnClickListener {
            if (login == "naver") {
                //네이버 로그아웃
                if (LoginActivity.mOAuthLoginModule != null) {
                    LoginActivity.mOAuthLoginModule.logout(activity)
                    //DeleteTokenTask(activity, mOAuthLoginModule).execute()
                }
            } else if (login == "kakao") {
                //카카오 로그아웃
                UserApiClient.instance.logout { error ->
                    if (error != null) {
                        Log.e(TAG, "로그아웃 실패. SDK에서 토큰 삭제됨", error)
                    }
                    else {
                        Log.i(TAG, "로그아웃 성공. SDK에서 토큰 삭제됨")
                    }
                }
            }
            //sharedPreference저장
            SharedPreferencesManager.putStrValue(activity, LOGIN, "none")

            Toast.makeText(activity, "로그아웃 하셨습니다.", Toast.LENGTH_SHORT).show()
            activity?.finish()
        }
    }

    private fun getTtadaUser(email: String) {
        val ttadaInterface = RetrofitClientTtada.tRetrofit.create(ITtadaUser::class.java)
        ttadaInterface.getUser(email).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: retrofit2.Response<JsonObject>) {
                if (response.isSuccessful) {
                    Log.d("PlayFragment GET", response.body().toString())
                    val result = Gson().fromJson(response.body(), TtadaUser::class.java)

                    binding.gameCoinTv.text = makeDecimal(result.userCoin)
                    binding.gameJewelTv.text = makeDecimal(result.userJewel)

                    Log.d("PlayFragment", result.toString())
                } else {
                    Log.d("PlayFragment","User Data - Error code ${response}")
                }
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d("PlayFragment",t.message ?: "통신오류")
            }
        })
    }
}