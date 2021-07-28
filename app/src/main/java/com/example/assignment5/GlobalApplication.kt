package com.example.assignment5

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import java.lang.IllegalStateException

public class GlobalApplication : Application() {
    companion object {
        var mInstance: GlobalApplication? = null

        fun getGlobalApplicationContext(): GlobalApplication {
            if (mInstance == null) {
                throw IllegalStateException("This Application does not inherit com.kakao.GlobalApplication")
            }
            return mInstance!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this

        KakaoSdk.init(this, "424911e2e818f9ac62579b37a61be40a")
    }

    override fun onTerminate() {
        super.onTerminate()
        mInstance = null
    }
}