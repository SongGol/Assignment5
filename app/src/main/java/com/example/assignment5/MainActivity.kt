package com.example.assignment5

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.assignment5.databinding.ActivityMainBinding
import com.kakao.sdk.user.UserApiClient

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportFragmentManager.beginTransaction().apply {
            replace(binding.mainFrameLayout.id, PlayFragment())
            addToBackStack(null)
            commit()
        }

        binding.mainBottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.main_play -> {
                    Log.d("bottom navigation", "play")
                    supportFragmentManager.beginTransaction().apply {
                        replace(binding.mainFrameLayout.id, PlayFragment())
                        addToBackStack(null)
                        commit()
                    }
                    return@setOnItemSelectedListener true
                }
                R.id.main_store -> {
                    Log.d("bottom navigation", "store")
                    return@setOnItemSelectedListener true
                }
                R.id.main_charger -> {
                    Log.d("bottom navigation", "charger")
                    return@setOnItemSelectedListener true
                }
                R.id.main_storage -> {
                    Log.d("bottom navigation", "storage")
                    return@setOnItemSelectedListener true
                }
                R.id.main_setting -> {
                    Log.d("bottom navigation", "setting")
                    supportFragmentManager.beginTransaction().apply {
                        replace(binding.mainFrameLayout.id, SettingFragment())
                        addToBackStack(null)
                        commit()
                    }
                    return@setOnItemSelectedListener true
                }
                else -> {
                    Log.d("bottom navigation", "fail")
                    return@setOnItemSelectedListener false
                }
            }
        }




        // 사용자 정보 요청 (기본)
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e(ContentValues.TAG, "사용자 정보 요청 실패", error)
            }
            else if (user != null) {
                Log.i(
                    ContentValues.TAG, "사용자 정보 요청 성공" +
                        "\n회원번호: ${user.id}" +
                        "\n이메일: ${user.kakaoAccount?.email}" +
                        "\n닉네임: ${user.kakaoAccount?.profile?.nickname}" +
                        "\n프로필사진: ${user.kakaoAccount?.profile?.thumbnailImageUrl}")
            }
        }
    }
}