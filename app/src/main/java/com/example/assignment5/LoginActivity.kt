package com.example.assignment5

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.assignment5.databinding.ActivityLoginBinding
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.naverlogin.OAuthLoginHandler


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    companion object {
        //네이버 로그인 인스턴스
        val mOAuthLoginModule = OAuthLogin.getInstance()
        var naverAccessToken = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        var keyHash = Utility.getKeyHash(this)
        Log.d("HashKey", keyHash)

        //카카오 로그인 공통 callback 구성
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e(TAG, "로그인 실패", error)
            }
            else if (token != null) {
                Log.i(TAG, "로그인 성공 ${token.accessToken}")

                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("login", "kakao")
                startActivity(intent)
            }
        }

        //네이버 로그인 인스턴스 초기화
        mOAuthLoginModule.init(
            this
            ,getString(R.string.naverLoginClientKey)
            ,getString(R.string.naverClientSecret)
            ,getString(R.string.app_name)
        )
        //네이버 로그인 핸들러
        val mOAuthLoginHandler: OAuthLoginHandler = @SuppressLint("HandlerLeak")
        object : OAuthLoginHandler() {
            override fun run(success: Boolean) {
                if (success) {
                    naverAccessToken = mOAuthLoginModule.getAccessToken(this@LoginActivity)
                    val refreshToken = mOAuthLoginModule.getRefreshToken(this@LoginActivity)
                    val expiresAt = mOAuthLoginModule.getExpiresAt(this@LoginActivity)
                    val tokenType = mOAuthLoginModule.getTokenType(this@LoginActivity)
                    //mOauthAT.setText(accessToken)
                    //mOauthRT.setText(refreshToken)
                    //mOauthExpires.setText(expiresAt.toString())
                    //mOauthTokenType.setText(tokenType)
                    //mOAuthState.setText(mOAuthLoginModule.getState(this@LoginActivity).toString())

                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.putExtra("login", "naver")
                    startActivity(intent)
                    Log.d("LoginActivity", "naver login success")
                } else {
                    val errorCode = mOAuthLoginModule.getLastErrorCode(this@LoginActivity).code
                    val errorDesc = mOAuthLoginModule.getLastErrorDesc(this@LoginActivity)
                    Toast.makeText(
                        this@LoginActivity, "errorCode:" + errorCode
                                + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT
                    ).show()
                }
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

        binding.startNaverLoginTv.setOnClickListener {

            mOAuthLoginModule.startOauthLoginActivity(this@LoginActivity, mOAuthLoginHandler)
        }
    }

    override fun onStart() {
        super.onStart()
        //자동 로그인 구현

    }
}