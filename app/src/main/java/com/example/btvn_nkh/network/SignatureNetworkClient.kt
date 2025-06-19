package com.example.btvn_nkh.network

import com.apero.signature.SignatureParser
import com.example.btvn_nkh.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SignatureNetworkClient @Inject constructor() {

    fun createHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(createSignatureInterceptor())
            .addInterceptor(createLoggingInterceptor())
            .build()
    }

    private fun createSignatureInterceptor(): Interceptor {
        return Interceptor { chain ->
            val signatureData = SignatureParser.parseData(
                SignatureApiManager.API_KEY,
                BuildConfig.PUBLIC_KEY,
                SignatureApiManager.timeStamp
            )
            val tokenIntegrity = signatureData.tokenIntegrity.ifEmpty { "not_get_api_token" }

            val headers = mapOf(
                "Accept" to "application/json",
                "Content-Type" to "application/json",
                "device" to "android",
                "x-api-signature" to signatureData.signature,
                "x-api-timestamp" to signatureData.timeStamp.toString(),
                "x-api-keyid" to signatureData.keyId,
                "x-api-token" to tokenIntegrity,
                "x-api-bundleId" to SignatureApiManager.BUNDLE_ID,
                "App-name" to SignatureApiManager.APP_NAME,
            )

            val requestBuilder = chain.request().newBuilder()
            headers.forEach { (key, value) ->
                requestBuilder.addHeader(key, value)
            }
            chain.proceed(requestBuilder.build())
        }
    }

    private fun createLoggingInterceptor(): Interceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    companion object {
        private const val TIMEOUT_SECONDS = 30L
    }
}