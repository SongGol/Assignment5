package com.example.assignment5

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.assignment5.databinding.FragmentSettingBinding
import com.kakao.sdk.user.UserApiClient


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
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.button.setOnClickListener {
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
}