package com.example.btvn_nkh.network

import retrofit2.Response
import retrofit2.http.GET

interface SignatureApiService {

    @GET("/api/v5/image-ai/test")
    suspend fun testAuthentication(): Response<TestResponse>
}

data class TestResponse(
    val message: String,
    val timestamp: Long?,
    val authenticated: Boolean
)