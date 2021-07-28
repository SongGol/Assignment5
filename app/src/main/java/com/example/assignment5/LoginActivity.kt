package com.example.assignment5

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.assignment5.databinding.ActivityLoginBinding
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        var keyHash = Utility.getKeyHash(this)
        Log.d("HashKey", keyHash)

        // 로그인 공통 callback 구성
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e(TAG, "로그인 실패", error)
            }
            else if (token != null) {
                Log.i(TAG, "로그인 성공 ${token.accessToken}")

                finish()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
        binding.startKakaoLoginTv.setOnClickListener {
            // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
            Log.d("mainActivity", "click")
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                Log.d("mainActivity", "click1")
                UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
            } else {
                Log.d("mainActivity", "click2")
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }
    }
}