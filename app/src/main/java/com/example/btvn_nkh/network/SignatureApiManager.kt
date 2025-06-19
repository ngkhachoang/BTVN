package com.example.btvn_nkh.network

object SignatureApiManager {
    internal var API_KEY = ""
    internal var APP_NAME = ""
    internal var BUNDLE_ID = ""
    internal var SERVICE_URL = "https://api-img-gen-wrapper.apero.vn"
    private var timeDiff: Long = 0L
    internal val timeStamp: Long get() = System.currentTimeMillis() + timeDiff

    fun setTimeStamp(serverTimestamp: Long?) {
        val clientTimestamp = System.currentTimeMillis()
        timeDiff = (serverTimestamp ?: 0) - clientTimestamp
    }

    fun init(apiKey: String, appName: String, bundleId: String) {
        API_KEY = apiKey
        APP_NAME = appName
        BUNDLE_ID = bundleId
    }
}