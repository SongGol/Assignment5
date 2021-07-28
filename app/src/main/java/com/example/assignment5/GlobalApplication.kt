package com.example.assignment5

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import java.lang.IllegalStateException

public class GlobalApplication : Application() {
    companion object {
        var mInstance: GlobalApplication? = null
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this

        KakaoSdk.init(this, getString(R.string.kakao_app_key))
    }
}