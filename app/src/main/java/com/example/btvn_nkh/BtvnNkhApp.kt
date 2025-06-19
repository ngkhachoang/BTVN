package com.example.btvn_nkh

import android.app.Application
import com.example.btvn_nkh.network.SignatureApiManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BtvnNkhApp : Application() {
    
    override fun onCreate() {
        super.onCreate()

        SignatureApiManager.init(
            apiKey = "sk-ePKj7HupzKwrm0BBDpKgbcptFg6zhJL7Fx0ZpfOMzhTa0w2efS",
            appName = "BTVN_NKH",
            bundleId = BuildConfig.APPLICATION_ID
        )
    }
} 