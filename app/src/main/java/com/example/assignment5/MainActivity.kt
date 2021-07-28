package com.example.assignment5

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.assignment5.databinding.ActivityMainBinding
import com.kakao.sdk.common.util.Utility

//HashKey: xOMqKG1vx5QFEHO0mLRnpCQ+CSQ=

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        var keyHash = Utility.getKeyHash(this)
        Log.d("HashKey", keyHash)
    }
}