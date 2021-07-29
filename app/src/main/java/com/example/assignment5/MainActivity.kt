package com.example.assignment5

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.assignment5.databinding.ActivityMainBinding
import com.kakao.sdk.user.UserApiClient

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var backPressedTime: Long = 0L

    companion object {
        const val PLAY = "play"
        const val CHARGER = "charger"
        const val STORE = "store"
        const val STORAGE = "storage"
        const val SETTING = "setting"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //시작시 play프레그먼트 선택
        binding.mainBottomNavigationView.menu.findItem(R.id.main_play).isChecked = true
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
                        //addToBackStack(PLAY)
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
                        //addToBackStack(SETTING)
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

    override fun onBackPressed() {
        if (System.currentTimeMillis() > backPressedTime + 2000) {
            backPressedTime = System.currentTimeMillis()
            Toast.makeText(this, "뒤로 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show()
        } else {
            finish()
            System.exit(0)
            android.os.Process.killProcess(android.os.Process.myPid())
        }
    }
}