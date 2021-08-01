package com.example.assignment5

import android.content.ContentValues
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.assignment5.databinding.ActivityMainBinding
import com.google.gson.Gson
import com.kakao.sdk.user.UserApiClient

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var mHandler = Handler(Looper.getMainLooper())
    private var name: String? = null
    private var email: String? = null
    private var id: Long = 0
    private var backPressedTime: Long = 0L
    private var bundle = Bundle(4)

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

        //카카오나 네이버에서 데이터 가져와 초기화
        initializeFragment(intent?.extras?.getString("login"))

        binding.mainBottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.main_play -> {
                    Log.d("bottom navigation", "play")
                    val fragment = PlayFragment()
                    fragment.arguments = bundle
                    supportFragmentManager.beginTransaction().apply {
                        replace(binding.mainFrameLayout.id, fragment)
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
                    val fragment = SettingFragment()
                    fragment.arguments = bundle
                    supportFragmentManager.beginTransaction().apply {
                        replace(binding.mainFrameLayout.id, fragment)
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
    }

    private fun initializeFragment(login: String?) {
        if (login == "naver") {
            initWithNaver()
        } else if (login == "kakao") {
            initWithKakao()
        }
    }

    private fun initWithNaver() {
        Thread {
            NaverProfile.setToken(LoginActivity.naverAccessToken)
            val response = NaverProfile.getProfile()
            val result = Gson().fromJson(response, NaverResult::class.java)

            name = result.response.name
            email = result.response.email

            //시작시 play프레그먼트 선택
            mHandler.post {
                binding.mainBottomNavigationView.menu.findItem(R.id.main_play).isChecked = true
            }
            //id = result.response.id.toLong()
            id = 0L
            bundle.putString("email", email)
            bundle.putString("name", name)
            bundle.putLong("id", id)
            bundle.putString("login", "naver")

            val fragment = PlayFragment()
            fragment.arguments = bundle
            supportFragmentManager.beginTransaction().apply {
                replace(binding.mainFrameLayout.id, fragment)
                //addToBackStack(null)
                commit()
            }
        }.start()
    }

    private fun initWithKakao() {
        //카카오 사용자 정보 요청 (기본)
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
                name = user.kakaoAccount?.profile?.nickname
                email = user.kakaoAccount?.email
                id = user.id

                //시작시 play프레그먼트 선택
                mHandler.post {
                    binding.mainBottomNavigationView.menu.findItem(R.id.main_play).isChecked = true
                }
                bundle.putString("email", email)
                bundle.putString("name", name)
                bundle.putLong("id", id)
                bundle.putString("login", "kakao")

                val fragment = PlayFragment()
                fragment.arguments = bundle
                supportFragmentManager.beginTransaction().apply {
                    replace(binding.mainFrameLayout.id, fragment)
                    //addToBackStack(null)
                    commit()
                }
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